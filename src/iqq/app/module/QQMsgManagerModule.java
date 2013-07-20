 /*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 /**
 * Project  : IQQ_V2.1
 * Package  : iqq.app.module
 * File     : UIMsgManagerModule.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-4-10
 * License  : Apache License 2.0 
 */
package iqq.app.module;

import iqq.app.bean.UIMsg;
import iqq.app.bean.UIMsg.State;
import iqq.app.core.IMService;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventHandler;
import iqq.app.event.IMEventType;
import iqq.app.service.IMEventService;
import iqq.app.ui.content.chat.conversation.UIMsgUtils;
import iqq.app.ui.content.chat.picloader.PicLoadState;
import iqq.app.ui.content.chat.picloader.PicLoader;
import iqq.app.ui.content.chat.rich.UILoaderPicItem;
import iqq.app.ui.content.chat.rich.UIRichItem;
import iqq.app.ui.content.chat.rich.UISkinPicItem;
import iqq.app.ui.content.chat.rich.UITextItem;
import iqq.app.util.I18NUtil;
import iqq.im.bean.QQAccount;
import iqq.im.bean.QQDiscuz;
import iqq.im.bean.QQGroup;
import iqq.im.bean.QQMsg;
import iqq.im.bean.QQStranger;
import iqq.im.bean.QQUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

/**
 *
 * 消息管理，处理消息的接收和发送
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class QQMsgManagerModule extends AbstractModule{
	private static Logger LOG = Logger.getLogger(QQMsgManagerModule.class);
	//有图片，待发送的消息内容
	private List<UIMsg> pendSendUIMsgs = new ArrayList<UIMsg>();
	private List<UIMsg> recvUIMsgs = new ArrayList<UIMsg>();
	private Map<Object, List<UIMsg>> unreadUIMsgs = new HashMap<Object, List<UIMsg>>();
	private QQAccount self;
	
	/**由于没有获取到用户信息，等待等待信息获取成功*/
	private Queue<QQMsg> pendRecvQQMsgQueue = new LinkedList<QQMsg>();
	private long mnemonicId = 0L;
	
	@IMEventHandler(IMEventType.UPLOAD_OFFPIC_ERROR)
	public void processUploadOffPicError(IMEvent event){
		//需要等当前事件所有的回调处理完毕后才执行，
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				purgePendUIMsgQueue();
			}
		});
	}
	
	@IMEventHandler(IMEventType.UPLOAD_OFFPIC_SUCCESS)
	public void processUploadOffPicSuccess(IMEvent event){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				purgePendUIMsgQueue();
			}
		});
	}
	
	@IMEventHandler(IMEventType.UPLOAD_CFACE_ERROR)
	public void processUploadCPicError(IMEvent event){
		//需要等当前事件所有的回调处理完毕后才执行，
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				purgePendUIMsgQueue();
			}
		});
	}
	
	@IMEventHandler(IMEventType.UPLOAD_CFACE_SUCCESS)
	public void processUploadCPicSuccess(IMEvent event){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				purgePendUIMsgQueue();
			}
		});
	}
	
	@IMEventHandler(IMEventType.GET_GROUPPIC_SUCCESS)
	public void processGetGroupPicSuccess(final IMEvent event){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				IMEvent related = event.getRelatedEvent();
				updateUIMsgByPicLoader((PicLoader) related.getTarget());
			}
		});
	}
	
	@IMEventHandler(IMEventType.LOGIN_SUCCESS)
	protected void processIMLoginSuccess(IMEvent event){
		self = (QQAccount) event.getTarget();
	}
	
	@IMEventHandler(IMEventType.GET_USERPIC_SUCCESS)
	public void processGetUserPicSuccess(final IMEvent event){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				IMEvent related = event.getRelatedEvent();
				updateUIMsgByPicLoader((PicLoader) related.getTarget());
			}
		});
	}
	
	@IMEventHandler(IMEventType.GET_OFFPIC_SUCCESS)
	public void processGetOffPicPicSuccess(final IMEvent event){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				IMEvent related = event.getRelatedEvent();
				updateUIMsgByPicLoader((PicLoader) related.getTarget());
			}
		});
	}
	
	
	@IMEventHandler(IMEventType.RECV_QQ_MSG)
	protected void processIMRecvQQMsg(IMEvent event) {
		QQMsg msg = (QQMsg) event.getTarget();
		// 过虑消息
		if(checkIMQQMsgFilter(msg)) return ;
		
		switch (msg.getType()) {
		case GROUP_MSG:
			QQGroup group = msg.getGroup();
			if(group.getMembers().isEmpty()) {
				broadcastIMEvent(new IMEvent(IMEventType.GROUP_INFO_REQUEST, group));
			}
			break;
		case DISCUZ_MSG:
			QQDiscuz discuz = msg.getDiscuz();
			if(discuz.getMembers().isEmpty()) {
				broadcastIMEvent(new IMEvent(IMEventType.DISCUZ_INFO_REQUEST, discuz));
			}
			break;
		}
		
		QQUser user = msg.getFrom();
		// 填充用户信息
		if(user.getNickname() == null) {
			if(user instanceof QQStranger) {
				pendRecvQQMsgQueue.add(msg);
				broadcastIMEvent(new IMEvent(IMEventType.STRANGER_INFO_REQUEST, user));
			} else {
				new IllegalArgumentException("Unkown QQUser type : " + user);
			}
		} else {
			processIMRecvQQMsg(msg);
		}
	}
	
	protected void processIMRecvQQMsg(QQMsg msg) {
		UIMsg uiMsg = UIMsgUtils.toUIMsg(msg, self);
		recvUIMsgs.add(uiMsg);
		broadcastIMEvent(IMEventType.RECV_CHAT_MSG, uiMsg);
		updateUnReadMsgList(uiMsg);
	}
	
	@IMEventHandler(IMEventType.STRANGER_INFO_UPDATE)
	protected void processIMUserQQUpdate(IMEvent event){
		QQUser user = (QQUser) event.getTarget();
		Iterator<QQMsg> it = pendRecvQQMsgQueue.iterator();
		while(it.hasNext()) {
			QQMsg msg = it.next();
			if(msg.getFrom() == user) {
				processIMRecvQQMsg(msg);
				break;
			}
		}
	}
	
	protected boolean checkIMQQMsgFilter(QQMsg msg) {
		if(mnemonicId == msg.getId()) {
			return true;
		}
		mnemonicId = msg.getId();	// 记住上一条信息ID，以防重复
		
		if(msg.getType() == QQMsg.Type.GROUP_MSG) {
			QQGroup group = msg.getGroup();
			if(group.getMask() != 0) {
				return true;
			}
		}
		return false;
	}
	
	@IMEventHandler(IMEventType.UPDATE_UIMSG)
	protected void processIMUpdateUIMsg(IMEvent event){
		updateUnReadMsgList((UIMsg) event.getTarget());
	}
	
	@IMEventHandler(IMEventType.SEND_CHAT_MSG)
	protected void processIMSendUIMsg(IMEvent event){
		UIMsg uiMsg = (UIMsg) event.getTarget();
		//判断如果有离线图片消息就发送完图片再继续
		List<UILoaderPicItem> picList = new ArrayList<UILoaderPicItem>();
		for(UIRichItem item: uiMsg.getContents()){
			if(item instanceof UILoaderPicItem){
				picList.add((UILoaderPicItem) item);
			}
		}
		
		if( picList.size()>0 ){
			pendSendUIMsgs.add(uiMsg);
			for(UILoaderPicItem item: picList){
				item.getPicLoader().sendToSever();
			}
		}else{
			doSendUIMsg(uiMsg);
		}
	}
	
	@IMEventHandler(IMEventType.SEND_MSG_SUCCESS)
	protected void processIMSendMsgSuccess(IMEvent event){
		UIMsg uiMsg = event.getRelatedEvent().getData("uiMsg");
		uiMsg.setState(UIMsg.State.SENT);
		broadcastIMEvent(IMEventType.UPDATE_UIMSG, uiMsg);
	}
	
	@IMEventHandler(IMEventType.SEND_MSG_ERROR)
	protected void processIMSendMsgError(IMEvent event){
		UIMsg uiMsg = event.getRelatedEvent().getData("uiMsg");
		uiMsg.setState(UIMsg.State.ERROR);
		broadcastIMEvent(IMEventType.UPDATE_UIMSG, uiMsg);
	}
	
	@IMEventHandler(IMEventType.SHOW_CHAT)
	protected void processIMShowChat(final IMEvent event){
		IMEvent imEvent = new IMEvent(IMEventType.FLASH_USER_STOP);
		imEvent.setTarget(event.getTarget());
		imEvent.putData("unread", 0);
		broadcastIMEvent(imEvent);
	}
	
	private void purgePendUIMsgQueue(){
		Iterator<UIMsg> it = pendSendUIMsgs.iterator();
		while(it.hasNext()){
			UIMsg uiMsg = it.next();
			int all = 0, error = 0, success = 0;
			for(UIRichItem item: uiMsg.getContents()){
				if(item instanceof UILoaderPicItem){
					all++;
					UILoaderPicItem pic = (UILoaderPicItem) item;
					PicLoader loader = pic.getPicLoader();
					if(loader.getState()==PicLoadState.SUCCESS){
						success++;
					}else if(loader.getState() == PicLoadState.ERROR){
						error++;
					}
				}
			}
			
			if(all == error){
				//所有图片发送失败。。
				uiMsg.setState(UIMsg.State.ERROR);
				broadcastIMEvent(IMEventType.UPDATE_UIMSG, uiMsg);
				it.remove();
			}else if(all == success){
				//所有图片都已经上传完毕，直接发送
				doSendUIMsg(uiMsg);
				it.remove();
			}else{
				//还有未上传的图片。。
			}
		}
	}
	
	private void updateUIMsgByPicLoader(PicLoader loader){
		List<UIMsg> msgs = new ArrayList<UIMsg>();
		msgs.addAll(pendSendUIMsgs);
		msgs.addAll(recvUIMsgs);
		for(UIMsg msg: msgs){
			for(UIRichItem richItem: msg.getContents()){
				if(richItem instanceof UILoaderPicItem){
					UILoaderPicItem loaderItem = (UILoaderPicItem) richItem;
					PicLoader tmpLoader = loaderItem.getPicLoader();
					if(tmpLoader == loader){
						broadcastIMEvent(IMEventType.UPDATE_UIMSG, msg);
						return;
					}
				}
			}
		}
	}
	
	private void doSendUIMsg(UIMsg uiMsg){
		try {
			QQMsg qqMsg = UIMsgUtils.toQQMsg(uiMsg, self);
			uiMsg.setState(UIMsg.State.PENDING);
			
			IMEventService events = getContext().getSerivce(IMService.Type.EVENT);
			IMEvent imEvent = new IMEvent(IMEventType.SEND_MSG_REQUEST, qqMsg);
			imEvent.putData("uiMsg", uiMsg);
			events.broadcast(imEvent);
			
		} catch (Exception e) {
			LOG.warn("send QQMsg Error!", e);
		}
	}
	
	@IMEventHandler(IMEventType.SHAKE_WINDOW)
	public void processIMRecvShakeWindow(IMEvent event){
		UIMsg msg = new UIMsg();
		msg.setMsgId(UIMsg.genMsgId());
		msg.setSender((QQUser)event.getTarget());
		msg.setCategory(UIMsg.Category.INFO);
		msg.setDate(new Date());
		msg.setDirection(UIMsg.Direction.RECV);
		msg.setOwner(event.getTarget());
		List<UIRichItem> items = new ArrayList<UIRichItem>();
		items.add(new UISkinPicItem("chat/toolbar/shake"));
		items.add(new UITextItem(I18NUtil.getMessage("chat.recvShake")));
		msg.setContents(items);
		msg.setState(UIMsg.State.UNREAD);
		msg.setDialogType(UIMsgUtils.getDialogType(event.getTarget()));
		broadcastIMEvent(IMEventType.SHOW_INFO_MSG, msg);
		updateUnReadMsgList(msg);
	}
	
	@IMEventHandler(IMEventType.SEND_SHAKE_REQUEST)
	protected void processIMSendShakeWIndow(IMEvent event){
		UIMsg msg = new UIMsg();
		msg.setMsgId(UIMsg.genMsgId());
		msg.setSender(self);
		msg.setCategory(UIMsg.Category.INFO);
		msg.setDate(new Date());
		msg.setDirection(UIMsg.Direction.SEND);
		msg.setOwner(event.getTarget());
		List<UIRichItem> items = new ArrayList<UIRichItem>();
		items.add(new UISkinPicItem("chat/toolbar/shake"));
		items.add(new UITextItem(I18NUtil.getMessage("chat.sendShake")));
		msg.setContents(items);
		msg.setState(UIMsg.State.SENT);
		msg.setDialogType(UIMsgUtils.getDialogType(event.getTarget()));
		broadcastIMEvent(IMEventType.SHOW_INFO_MSG, msg);
	}
	
	private void updateUnReadMsgList(UIMsg msg){
		if(msg.getState() == State.UNREAD){
			List<UIMsg> unreadList = unreadUIMsgs.get(msg.getOwner());
			if(unreadList == null){
				unreadList = new ArrayList<UIMsg>();
				unreadUIMsgs.put(msg.getOwner(), unreadList);
			}
			if(!unreadList.contains(msg)){
				unreadList.add(msg);
				IMEvent imEvent = new IMEvent(IMEventType.FLASH_USER_START);
				imEvent.setTarget(msg.getOwner());
				imEvent.putData("unread", unreadList.size());
				broadcastIMEvent(imEvent);
			}
		}
	}

}

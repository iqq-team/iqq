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
 * File     : QQMsgHistoryModule.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-4-5
 * License  : Apache License 2.0 
 */
package iqq.app.module;

import iqq.app.bean.UIDialog;
import iqq.app.bean.UIDialog.Type;
import iqq.app.bean.UIMsg;
import iqq.app.core.IMContext;
import iqq.app.core.IMException;
import iqq.app.core.IMService;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventHandler;
import iqq.app.event.IMEventType;
import iqq.app.service.IMResourceService;
import iqq.app.service.IMSkinService;
import iqq.app.service.IMTaskService;
import iqq.app.util.Benchmark;
import iqq.im.bean.QQAccount;
import iqq.im.bean.QQDiscuz;
import iqq.im.bean.QQGroup;
import iqq.im.bean.QQStranger;
import iqq.im.bean.QQUser;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentNavigableMap;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.mapdb.DB;
import org.mapdb.DBMaker;

/**
 *
 *  聊天消息记录模块
 *  
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class QQMsgHistoryModule extends AbstractModule{
	private static final Logger LOG = Logger.getLogger(QQMsgHistoryModule.class);
	private IMResourceService resources;
	private long uin;
	private DB msgDb;
	private ConcurrentNavigableMap<String, UIDialog> dialogMap;
	/**由于没有获取到QQ号或者群号，等待被写入数据库的消息列表*/
	private Queue<UIMsg> pendMsgQueue;
	/**由于没有获取QQ号或者群号，等待被处理的消息历史查找事件*/
	private List<IMEvent> historyFindEvents;
	
	@Override
	public void init(IMContext context) throws IMException {
		super.init(context);
		resources = context.getSerivce(IMService.Type.RESOURCE);
		historyFindEvents = new ArrayList<IMEvent>();
		pendMsgQueue = new LinkedList<UIMsg>();
	}
	
	@Override
	public void destroy() throws IMException {
		if(msgDb != null){
			msgDb.commit();
			msgDb.close();
		}
		super.destroy();
	}
	
	@IMEventHandler(IMEventType.LOGIN_SUCCESS)
	protected void processIMLoginSuccess(IMEvent event) throws URISyntaxException{
		QQAccount account = (QQAccount) event.getTarget();
		uin = account.getUin();
		
		File dbDir = resources.getFile("user/" + uin +"/db/");
		if(!dbDir.exists()){
			dbDir.mkdirs();
		}
		
		File picDir = resources.getFile("user/" + uin +"/pic/");
		if(!picDir.exists()){
			picDir.mkdirs();
		}
		
		msgDb = DBMaker.newFileDB(resources.getFile("user/" + uin +"/db/msg.db"))
					   .closeOnJvmShutdown()
					   .asyncWriteFlushDelay(1000)
					   .make();
		dialogMap = msgDb.getTreeMap("dialogMap");
	}
	
	@IMEventHandler(IMEventType.SHOW_INFO_MSG)
	protected void processIMSaveUIInfoMsg(IMEvent event){
		UIMsg msg = (UIMsg) event.getTarget();
		handleMsgSave(msg);
	}
	
	@IMEventHandler(IMEventType.SEND_CHAT_MSG)
	protected void processIMSaveUIChatMsg(IMEvent event){
		UIMsg msg = (UIMsg) event.getTarget();
		handleMsgSave(msg);
	}

	@IMEventHandler(IMEventType.RECV_CHAT_MSG)
	protected void processIMRecvUIChatMsg(IMEvent event){
		UIMsg msg = (UIMsg) event.getTarget();
		handleMsgSave(msg);
	}

	@IMEventHandler(IMEventType.UPDATE_UIMSG)
	protected void processIMUpdateUIMsg(IMEvent event){
		UIMsg msg = (UIMsg) event.getTarget();
		handleMsgUpdate(msg);
	}
	
	@IMEventHandler(IMEventType.USER_QQ_UPDATE)
	protected void processIMUserQQUpdate(IMEvent event){
		QQUser user = (QQUser) event.getTarget();
		
		//保存所有接收到的消息到数据库
		Iterator<UIMsg> it = pendMsgQueue.iterator();
		while(it.hasNext()){
			UIMsg uiMsg = it.next();
			if( uiMsg.getDialogType()==UIDialog.Type.BUDDY_CHAT ||
					uiMsg.getDialogType()==UIDialog.Type.STRANGER_CHAT ){
				QQUser current = (QQUser) uiMsg.getOwner();
				if(current.getUin() == user.getUin()){
					saveMsgToDb(uiMsg);
					it.remove();
				}
			
			}
		}
		
		//是否有查找消息历史记录请求事件，如果有就处理
		Iterator<IMEvent> eit = historyFindEvents.iterator();
		while(eit.hasNext()){
			IMEvent find = eit.next();
			if(find.getTarget() instanceof QQUser){
				QQUser pend = (QQUser) event.getTarget();
				if(pend.getUin() == user.getUin()){
					handleMsgFind(user, find);
					eit.remove();
				}
			}
		}
	}
	
	@IMEventHandler(IMEventType.GROUP_GID_UPDATE)
	protected void processIMGroupGidUpdate(IMEvent event){
		QQGroup group = (QQGroup) event.getTarget();
		
		//保存所有接收到的消息到数据库
		Iterator<UIMsg> it = pendMsgQueue.iterator();
		while(it.hasNext()){
			UIMsg uiMsg = it.next();
			if( uiMsg.getDialogType()==Type.GROUP_CHAT){
				QQGroup current = (QQGroup) uiMsg.getOwner();
				if(current.getCode() ==group.getCode()){
					saveMsgToDb(uiMsg);
					it.remove();
				}
			}
		}
		
		//是否有查找消息历史记录请求事件，如果有就处理
		Iterator<IMEvent> eit = historyFindEvents.iterator();
		while(eit.hasNext()){
			IMEvent find = eit.next();
			if(find.getTarget() instanceof QQGroup){
				QQGroup pend = (QQGroup) event.getTarget();
				if(pend.getCode() == group.getCode()){
					handleMsgFind(group, find);
					eit.remove();
				}
			}
		}
	}
	
	@IMEventHandler(IMEventType.MSG_HISTORY_FIND)
	protected void processIMMsgHistoryFind(IMEvent event){
		if(event.getTarget() instanceof QQUser){
			QQUser user = (QQUser) event.getTarget();
			if(user.getQQ() > 0){
				handleMsgFind(user, event);
			}else{
				broadcastIMEvent(new IMEvent(IMEventType.USER_QQ_REQUEST, user));
				historyFindEvents.add(event);
			}
		}else if(event.getTarget() instanceof QQGroup){
			QQGroup group = (QQGroup) event.getTarget();
			if(group.getGid() > 0){
				handleMsgFind(group, event);
			}else{
				broadcastIMEvent(new IMEvent(IMEventType.GROUP_GID_REQUEST, group));
				historyFindEvents.add(event);
			}
		}else if(event.getTarget() instanceof QQDiscuz){
			handleMsgFind(event.getTarget(), event);
		}else{
			LOG.warn("processIMMsgHistoryFind: unknown event target type: " + event.getTarget());
		}
	}
	
	/***
	 * 查找相对于KEY更旧的消息
	 * @param msgMap
	 * @param key
	 * @param state
	 * @param limit
	 * @return
	 */
	private List<UIMsg> findOlderMsg(
			ConcurrentNavigableMap<String, UIMsg> msgMap, 
			String key, UIMsg.State state,  Integer limit){
		List<UIMsg> msgList = new ArrayList<UIMsg>(); 
		if(msgMap.size() > 0){
			if(limit == null){
				limit = 20;
			}
			
			if(key == null ){
				key = msgMap.lastKey();
			}else{
				key = msgMap.lowerKey(key);
			}
			
			//查找符合条件的UIMsg对象
			int count = 0;
			while(count < limit && key != null){
				UIMsg msg = msgMap.get(key);
				if(state == null || state == msg.getState()){
					msgList.add(msg);
					count++;
				}
				key = msgMap.lowerKey(key);
			}
		}
		return msgList;
	}
	
	/**
	 *  * 查找相对于KEY更老的消息
	 * @param msgMap
	 * @param key
	 * @param state
	 * @param limit
	 * @return
	 */
	private List<UIMsg> findNewerMsg(
				ConcurrentNavigableMap<String, UIMsg> msgMap,
				String key, UIMsg.State state, Integer limit) {
		List<UIMsg> msgList = new ArrayList<UIMsg>();
		if (msgMap.size() > 0) {
			if (limit == null) {
				limit = 20;
			}
			
			if (key == null) {
				key = msgMap.firstKey();
			} else {
				key = msgMap.higherKey(key);
			}

			// 查找符合条件的UIMsg对象
			int count = 0;
			while (count < limit && key != null) {
				UIMsg msg = msgMap.get(key);
				if (state == null || state == msg.getState()) {
					msgList.add(msg);
					count++;
				}
				key = msgMap.higherKey(key);
			}
		}
		//倒序排列，新消息的排在前面
		Collections.sort(msgList, new Comparator<UIMsg>() {
			public int compare(UIMsg o1, UIMsg o2) {
				return o2.getMsgId().compareTo(o1.getMsgId());
			}
		});
		return msgList;
	}
	
	/**
	 * 从数据库文件中查找历史消息记录
	 * @param target
	 * @param event
	 */
	private void handleMsgFind(Object target, IMEvent event){
		String dialogId = getDialogIdByObject(target);
		ConcurrentNavigableMap<String, UIMsg> msgMap = msgDb.getTreeMap(dialogId);
		
		//执行过滤，比如只看未读的消息，
		UIMsg.State state = event.getData("state");
		String key = event.getData("start");
		String direct = event.getData("direct");	//["newer","older"]
		Integer limit = event.getData("limit");
		int total =  msgMap.size();
		List<UIMsg> msgList = null;
		if("newer".equals(direct)){
			msgList = findNewerMsg(msgMap, key, state, limit);
		}else{
			msgList = findOlderMsg(msgMap, key, state, limit);
		}
		
		//取出来的只是之前存储的User对象，头像和UIN都已经无效了，所以这里需要采取一些规则来匹配
		//只有通过nick来匹配登陆之后的好友列表，对应UIN，但可能会有些好友的nick完全相同。。。
		if( msgList.size() > 0){
			
			//取出来是倒序的，重新按时间顺序排序
			Collections.reverse(msgList);
			try {
				//获取登录账号和群，讨论组成员列表，建立<nick, User>映射关系，只保留唯一映射
				QQAccount account = queryIMEvent(new IMEvent(IMEventType.QUERY_ACCOUNT));
				
				List<QQUser> members = new ArrayList<QQUser>();
				if(target instanceof QQGroup ){
					members.addAll(((QQGroup) target).getMembers());
				}else if(target instanceof QQDiscuz){
					members.addAll(((QQDiscuz) target).getMembers());
				}
				
				Map<String, QQUser> nickMap = new HashMap<String, QQUser>();
				for(QQUser user: members){
					if(nickMap.containsKey(user.getNickname())){
						nickMap.remove(user.getNickname());
					}else{
						nickMap.put(user.getNickname(), user);
					}
				}
				
				//由于从历史记录中取出来的消息接收和发送对象中的uin和头像都无效了，所以这里需要查找在线好友列表，填充uin和头像
				//因为读取出来的数据中QQ始终是有效的，uin无效，但好友列表中可能只有一部分好友有QQ号，所以在 populateQQUser函数执行如下的匹配规则：
				//  1. 目标用户是否为当前登录的用户，如果是设置为当前登录用户；
				//  2. 目标用户是不是查询的用户，如果是设置为查询的用户（对于查询单个好友聊天记录，始终有效，因为查询之前必须获取到QQ号）；
				//  3. 群消息 讨论组消息？？？
				for(UIMsg uiMsg: msgList){
					uiMsg.setOwner(event.getTarget());
					QQUser sender = uiMsg.getSender();
					sender = populateQQUser(sender.getQQ(), sender.getNickname(), account, target, nickMap);
					uiMsg.setSender(sender);
				}
			} catch (IMException e) {
				LOG.warn("query IMEvent Error", e);
			}
		}
		//触发事件
		IMEvent result = new IMEvent(IMEventType.MSG_HISTORY_UPDATE, msgList);
		result.putData("total", total);
		result.setRelatedEvent(event);
		broadcastIMEvent(result);
		LOG.info("MsgHistoryFindResult: found " + msgList.size() + " UIMsgs for " + event.getTarget());
	}
	
	/**
	 *  通过nick和QQ号来查找从登陆的好友列表查找有效的用户对象
	 *  主要是填充uin和头像
	 * @param user
	 * @param account
	 * @param buddyNickMap
	 * @param buddyQQMap
	 * @return
	 */
	private QQUser populateQQUser(long qq, String nick, QQAccount account, Object target, Map<String, QQUser> nickMap){
		//这条消息可能是自己发送出去
		if( qq > 0 && qq == account.getUin()){
			return account;
		}
		
		//对于好友聊天和临时会话，因为对方QQ号获得之后才去查询记录，所以这里也一定会满足条件。。
		if(target instanceof QQUser){
			QQUser user = (QQUser) target;
			if(qq > 0 && qq == user.getQQ()){
				return user;
			}
		}
		
		//剩下就是群和临时消息啊啊啊啊啊 
		//尝试查找Nick对象
		if(nickMap != null){
			QQUser user = nickMap.get(nick);
			if(user != null){
				return user;
			}
		}
		
		//如果nick都找不到，三种可能
		// 1. 群或讨论组的列表没有下载完..
		// 2. 群或讨论组这个用户不存在，可能已经退出该群
		// 3. 群里存在两个相同nick的用户
		//，悲剧了只有新建一个用户对象
		QQUser user = new QQStranger();
		user.setQQ(qq);
		user.setNickname(nick);
		return user;
	}
	
	
	/**
	 * 把接收和发送的消息记录保存到数据库中
	 * @param msg
	 * @param user
	 */
	private void handleMsgSave(UIMsg msg){
		switch(msg.getDialogType()){
		case BUDDY_CHAT:
		case STRANGER_CHAT: {
			QQUser user = (QQUser) msg.getOwner();
			if(user.getQQ() == 0){
				pendMsgQueue.add(msg);
				broadcastIMEvent(new IMEvent(IMEventType.USER_QQ_REQUEST, user));
			}else{
				saveMsgToDb(msg);
			}
		}break;
		
		case GROUP_CHAT: {
			QQGroup group = (QQGroup) msg.getOwner();
			if(group.getGid() == 0){
				pendMsgQueue.add(msg);
				broadcastIMEvent(new IMEvent(IMEventType.GROUP_GID_REQUEST, group));
			}else{
				saveMsgToDb(msg);
			}
		} break;
		
		default:{
			saveMsgToDb(msg);
		}
		}

	}
	
	/**
	 * 把消息写入数据库，前提必须满足QQ号或者群号都已经获取完毕了
	 * @param msg
	 * @param key
	 */
	private void saveMsgToDb(final UIMsg uiMsg){
		IMTaskService tasks = getContext().getSerivce(IMService.Type.TASK);
		tasks.submit(new Runnable() {
			public void run() {
				Benchmark.start("saveMsgToDb");
				UIDialog dialog = getDialogByMsg(uiMsg);
				ConcurrentNavigableMap<String, UIMsg> msgMap =  msgDb.getTreeMap(dialog.getId());
				msgMap.put(uiMsg.getMsgId(), uiMsg);
				
				dialog.setLastMsg(uiMsg);
				dialog.incMsgCount();
				flushDialog(dialog);
				
				LOG.debug("Saved UIMsg: " + uiMsg);
				Benchmark.end("saveMsgToDb");
			}
		});
	}
	
	private void handleMsgUpdate(final UIMsg uiMsg){
		IMTaskService tasks = getContext().getSerivce(IMService.Type.TASK);
		tasks.submit(new Runnable() {
			public void run() {
				Benchmark.start("handleMsgUpdate");
				UIDialog dialog = getDialogByMsg(uiMsg);
				ConcurrentNavigableMap<String, UIMsg> msgMap =  msgDb.getTreeMap(dialog.getId());
				//必须要先保存了才能更新，如果没有保存，可能是在等待获取QQ号或者群号
				if(msgMap.containsKey(uiMsg.getMsgId())){
					msgMap.replace(uiMsg.getMsgId(), uiMsg);
					LOG.debug("Updated UIMsg: " + uiMsg);
				}
				Benchmark.end("handleMsgUpdate");
			}
		});
		
	}
	
	
	/***
	 * 获取保存在数据库中对话消息KEY
	 * 一个KEY就代表了一个对话，如好友聊天，群聊天，讨论组
	 * 必须保证每次登陆都完全一致
	 * 
	 * QQGroup:  QQGroup.{gid}
	 * QQUser:   QQUser.{qq}
	 * QQDiscuz: QQDiscuz.{did}
	 * @param obj
	 * @return
	 */
	private String getDialogIdByMsg(UIMsg msg){
		Object obj = msg.getOwner();
		switch(msg.getDialogType()){
		case BUDDY_CHAT:
		case STRANGER_CHAT: return "QQUser." + ((QQUser) obj).getQQ();
		case GROUP_CHAT: return "QQGroup." + ((QQGroup) obj).getGid();
		case DISCUZ_CHAT: return "QQDiscuz." + ((QQDiscuz) obj).getDid();
		case BUDDY_NOTIFY: return "BuddyNotify.10000";
		case GROUP_NOTIFY: return "GroupNotify.10000";
		case SYSTEM_NOTIFY: return "SystemNotify.10000";
		default:
			throw new IllegalArgumentException("unknown object type: " + obj);
		}
	}
	
	
	private String getDialogIdByObject(Object obj){
		if(obj instanceof QQUser){
			return "QQUser." + ((QQUser) obj).getQQ();
		}else if(obj instanceof QQGroup){
			return "QQGroup." + ((QQGroup) obj).getGid();
		}else if(obj instanceof QQDiscuz){
			 return "QQDiscuz." + ((QQDiscuz) obj).getDid();
		}else if(obj instanceof String){
			return (String) obj;
		}else {
			throw new IllegalArgumentException("unknown object type: " + obj);
		}
	}
	
	private UIDialog createDialogByMsg(UIMsg msg){
		UIDialog dialog = new UIDialog();
		IMSkinService skins = getContext().getSerivce(IMService.Type.SKIN);
		switch (msg.getDialogType()) {
		case BUDDY_CHAT:
		case STRANGER_CHAT:{
			QQUser user = (QQUser) msg.getOwner();
			dialog.setName(user.getNickname());
			dialog.setFace(getFaceBytes(user.getFace()));
		} break;
		
		case GROUP_CHAT: {
			QQGroup group = (QQGroup) msg.getOwner();
			dialog.setName(group.getName());
			dialog.setFace(getFaceBytes(group.getFace()));
		} break;
		
		case DISCUZ_CHAT: {
			QQDiscuz discuz = (QQDiscuz) msg.getOwner();
			dialog.setName(discuz.getName());
			dialog.setFace(getFaceBytes(skins.getBufferedImage("discuzIcon")));
		} break;
		
		case BUDDY_NOTIFY: {
			dialog.setName("BuddyNotify");
			dialog.setFace(getFaceBytes(skins.getBufferedImage("discuzIcon")));
		} break;
		
		case GROUP_NOTIFY: {
			dialog.setName("GroupNotify");
			dialog.setFace(getFaceBytes(skins.getBufferedImage("discuzIcon")));
		} break;
		
		case SYSTEM_NOTIFY: {
			dialog.setName("SystemNotify");
			dialog.setFace(getFaceBytes(skins.getBufferedImage("discuzIcon")));
		}break;
		}
		
		//默认属性
		dialog.setId(getDialogIdByMsg(msg));
		dialog.setType(msg.getDialogType());
		dialog.setMsgCount(0);
		dialog.setUnreadCount(0);
		return dialog;
	}

	
	/**
	 * 根据一条消息获取对话信息
	 * @param msg
	 * @return
	 */
	private UIDialog getDialogByMsg(UIMsg msg){
		if(msg.getDialogId() == null) {
			msg.setDialogId(getDialogIdByMsg(msg));
		}
		String dbKey = msg.getDialogId();
		UIDialog dialog = dialogMap.get(dbKey);
		if(dialog == null){
			dialog = createDialogByMsg(msg);
			dialogMap.put(dbKey, dialog);
		}
		return dialog;
	}

	/**
	 * 把QQ对话信息重新写入数据库
	 * @param dialog
	 */
	private void flushDialog(UIDialog dialog){
		dialogMap.put(dialog.getId(), dialog);
	}
	
	private byte[] getFaceBytes(BufferedImage face){
		if(face != null){
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				ImageIO.write(face, "png", out);
				return out.toByteArray();
			} catch (IOException e) {
				LOG.warn("getFaceBytes error!!", e);
			}
		}
		return null;
	}
	
	
}

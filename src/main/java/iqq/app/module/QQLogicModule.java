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
 * File     : QQLogic.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-3-13
 * License  : Apache License 2.0 
 */
package iqq.app.module;

import iqq.app.core.IMContext;
import iqq.app.core.IMException;
import iqq.app.core.IMService;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventHandler;
import iqq.app.event.IMEventType;
import iqq.app.service.IMEventService;
import iqq.app.service.IMResourceService;
import iqq.app.ui.IMFrameView;
import iqq.app.util.I18NUtil;
import iqq.im.QQActionListener;
import iqq.im.QQClient;
import iqq.im.QQException;
import iqq.im.QQException.QQErrorCode;
import iqq.im.WebQQClient;
import iqq.im.actor.SwingActorDispatcher;
import iqq.im.bean.QQAccount;
import iqq.im.bean.QQBuddy;
import iqq.im.bean.QQDiscuz;
import iqq.im.bean.QQDiscuzMember;
import iqq.im.bean.QQGroup;
import iqq.im.bean.QQGroupMember;
import iqq.im.bean.QQMsg;
import iqq.im.bean.QQStatus;
import iqq.im.bean.QQStranger;
import iqq.im.bean.QQUser;
import iqq.im.bean.content.CFaceItem;
import iqq.im.bean.content.OffPicItem;
import iqq.im.event.QQActionEvent;
import iqq.im.event.QQActionEventArgs.ProgressArgs;
import iqq.im.event.QQActionFuture;
import iqq.im.event.QQNotifyEvent;
import iqq.im.event.QQNotifyEventArgs;
import iqq.im.event.QQNotifyHandler;
import iqq.im.event.QQNotifyHandlerProxy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;


/**
 *
 * QQ逻辑处理，和webqq-core底层通信，保持连接，接受消息等。。
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class QQLogicModule extends AbstractModule{
	private static final Logger LOG = Logger.getLogger(QQLogicModule.class);
	private QQClient client;
	private QQActionFuture loginFuture;
	private boolean buddyListFetched;
	private boolean groupListFetched;
	private boolean discuzListFetched;
	private boolean pollMsgStarted;

	
	@Override
	public void destroy() throws IMException {
		if(client != null){
			client.destroy();
		}
		super.destroy();
	}

	@IMEventHandler(IMEventType.LOGOUT_REQUEST)
	protected void processLogoutRequest(IMEvent imEvent) {
		if(client != null && client.isOnline()) {
			client.logout(new QQActionListener() {
				public void onActionEvent(QQActionEvent event) {
					switch(event.getType()){
					case EVT_OK:
					case EVT_ERROR:{
						IMEvent imEvent = new IMEvent(IMEventType.LOGOUT_SUCCESS);
						broadcastIMEvent(imEvent);
					}
					}
				}
			});
		}else{
			IMEvent event = new IMEvent(IMEventType.LOGOUT_SUCCESS);
			broadcastIMEvent(event);
		}
	}

	@IMEventHandler(IMEventType.LOGIN_CANCEL_REQUEST)
	protected void processIMLoginCancelRequest(IMEvent imEvent) {
		if (loginFuture != null) {
			try {
				loginFuture.cancel();
			} catch (QQException e) {
				LOG.warn("cancel login error!", e);
			}
		}
	}

	@IMEventHandler(IMEventType.SUBMIT_VERIFY_REQUEST)
	protected void processIMSubmitVerifyRequest(IMEvent imEvent) {
		IMEvent relatedEvent = imEvent.getRelatedEvent();
		QQNotifyEvent notifyEvent = (QQNotifyEvent) relatedEvent.getTarget();
		client.submitVerify(imEvent.getString("code"), notifyEvent);
	}
	
	@IMEventHandler(IMEventType.FRESH_VERIFY_REQUEST)
	protected void processIMFreshVerifyRequest(IMEvent imEvent) {
		IMEvent relatedEvent = imEvent.getRelatedEvent();
		QQNotifyEvent notifyEvent = (QQNotifyEvent) relatedEvent.getTarget();
		client.freshVerify(notifyEvent, new QQActionListener() {
			public void onActionEvent(QQActionEvent event) {
				switch(event.getType()){
				case EVT_OK:{
					IMEvent imEvent = new IMEvent(IMEventType.FRESH_VERIFY_SUCCESS);
					imEvent.setRelatedEvent(imEvent);
					imEvent.setTarget(event.getTarget());
					broadcastIMEvent(imEvent);
				} break;
				case EVT_ERROR:{
					IMEvent imEvent = new IMEvent(IMEventType.FRESH_VERIFY_FAILED);
					imEvent.setRelatedEvent(imEvent);
					imEvent.setTarget(event.getTarget());
					broadcastIMEvent(imEvent);
				} break;
				}
				
			}
		});
	}

	@IMEventHandler(IMEventType.VERIFY_CANCEL)
	protected void processIMVerifyCancel(IMEvent event){
		IMEvent related = event.getRelatedEvent();
		QQNotifyEvent notify = (QQNotifyEvent) related.getTarget();
		try {
			client.cancelVerify(notify);
		} catch (QQException e) {
			LOG.warn("cancelVerify error!", e);
		}
	}

	@IMEventHandler(IMEventType.RELOGIN_REQEUST)
	protected void processIMReLoginRequest(IMEvent imEvent){
		//TODO ..判断是否可以重新登录
		QQStatus status = (QQStatus) imEvent.getTarget();
		client.relogin(status, new QQActionListener() {
			public void onActionEvent(QQActionEvent event) {
				switch(event.getType()){
				case EVT_OK:{
					broadcastIMEvent(IMEventType.RELOGIN_SUCCESS, client.getAccount());
					broadcastIMEvent(IMEventType.CLIENT_ONLINE, client.getAccount());
					doGetOnlineBuddy();
					client.beginPollMsg();
				} break;
				case EVT_ERROR:{
					broadcastIMEvent(IMEventType.RELOGIN_ERROR, client.getAccount());
					LOG.warn("relogin Error!!", (QQException) event.getTarget());
				}
				}
			}
		});
	}
	
	@IMEventHandler(IMEventType.LOGIN_REQUEST)
	protected void processLoginRequest(IMEvent imEvent){
		buddyListFetched = false;
		groupListFetched = false;
		discuzListFetched = false;
		pollMsgStarted = false;
		
		if(client != null){
			client.destroy();
		}
		
		client = new WebQQClient(
				imEvent.getString("username"), 
				imEvent.getString("password"),
				new QQNotifyHandlerProxy(this), 
				new SwingActorDispatcher());
		//client.setHttpUserAgent("IQQ App/2.1 dev");
		String ua = "Mozilla/5.0 (@os.name; @os.version; @os.arch) AppleWebKit/537.36 (KHTML, like Gecko) IQQ App-dev/2.1 Safari/537.36";
		ua = ua.replaceAll("@os.name", System.getProperty("os.name"));
		ua = ua.replaceAll("@os.version", System.getProperty("os.version"));
		ua = ua.replaceAll("@os.arch", System.getProperty("os.arch"));
		client.setHttpUserAgent(ua);
		loginFuture = client.login(
				(QQStatus)imEvent.getData("status"), 
				new QQActionListener() {
			public void onActionEvent(QQActionEvent event) {
				switch(event.getType()){
				case EVT_OK:{
					IMResourceService resources = getContext().getSerivce(IMService.Type.RESOURCE);
					resources.setActiveUser(client.getAccount());
					
					broadcastIMEvent(new IMEvent(IMEventType.LOGIN_SUCCESS, client.getAccount()));
					broadcastIMEvent(IMEventType.CLIENT_ONLINE, client.getAccount());
					
					doGetBuddyList();
					doGetGroupList();
					doGetDiscuzList();
					doGetSelfFace();
					doGetSelfInfo();
					doGetSelfSign();
				} break;
				case EVT_ERROR:{
					//登录失败
					QQException exc = (QQException) event.getTarget();
					IMEvent imEvent = new IMEvent(IMEventType.LOGIN_ERROR);
					imEvent.setTarget(exc);
					imEvent.putData("reason", exc.getMessage()); //TODO ..转换为友好的字符串
					broadcastIMEvent(imEvent);
					LOG.warn("Login Error!!", exc);
				} break;
				
				case EVT_CANCELED: {
					//取消登录
					IMEvent imEvent = new IMEvent(IMEventType.LOGIN_CANCELED);
					broadcastIMEvent(imEvent);
					LOG.warn("Login canceled!!!!!!");
				} break;
				}
			}
		});
	}
	
	@IMEventHandler(IMEventType.USER_FACE_REQUEST)
	protected void processIMUserFaceRuquest(IMEvent event){
		final QQUser user = (QQUser) event.getTarget();
		client.getUserFace(user, new QQActionListener() {
			public void onActionEvent(QQActionEvent event) {
				switch(event.getType()){
				case EVT_OK:{
					IMEvent imEvent = new IMEvent(IMEventType.USER_FACE_UPDATE);
					imEvent.setTarget(user);
					broadcastIMEvent(imEvent);
				} break;
				case EVT_ERROR:{
					//TODO ...
				}
				}
				
			}
		});
	}
	
	@IMEventHandler(IMEventType.SEND_MSG_REQUEST)
	protected void processSendMsgRequest(final IMEvent event){
		final QQMsg msg = (QQMsg) event.getTarget();
		client.sendMsg(msg, new QQActionListener() {
			public void onActionEvent(QQActionEvent actEvent) {
				switch(actEvent.getType()){
				case EVT_OK:{
					IMEvent imEvent = new IMEvent(IMEventType.SEND_MSG_SUCCESS);
					imEvent.setRelatedEvent(event);
					imEvent.setTarget(msg);
					broadcastIMEvent(imEvent);
				} break;
				case EVT_ERROR:{
					LOG.warn("send msg Error", (QQException) actEvent.getTarget());
					IMEvent imEvent = new IMEvent(IMEventType.SEND_MSG_ERROR);
					imEvent.setRelatedEvent(event);
					imEvent.setTarget(actEvent.getTarget());
					broadcastIMEvent(imEvent);
				}
				}
				
			}
		});
	}
	
	@IMEventHandler(IMEventType.SEND_SHAKE_REQUEST)
	protected void processSendShakeRequest(IMEvent event){
		QQUser user = (QQUser) event.getTarget();
		client.sendShake(user, new QQActionListener() {
			public void onActionEvent(QQActionEvent event) {
				switch(event.getType()){
				case EVT_OK:{
					//TODO ..
				} break;
				case EVT_ERROR:{
					LOG.warn("send shake message error!", (QQException) event.getTarget());
				}
				}
			}
		});
	}
	
	@IMEventHandler(IMEventType.CHANGE_STATUS_REQUEST)
	protected void processIMChangeStatusReqeust(IMEvent event){
		final QQStatus status = (QQStatus) event.getTarget();
		client.changeStatus(status, new QQActionListener() {
			public void onActionEvent(QQActionEvent event) {
				switch(event.getType()){
				case EVT_OK:{
					//TODO ..
					if(status == QQStatus.OFFLINE){
						doClearOnlineStatus();
						IMEvent imEvent = new IMEvent(IMEventType.CLIENT_OFFLINE);
						imEvent.putData("force", false);
						broadcastIMEvent(imEvent);
					}
					broadcastIMEvent(IMEventType.CHANGE_STATUS_SUCCESS, client.getAccount());
				} break;
				case EVT_ERROR:{
					LOG.warn("self change status request error!", (QQException) event.getTarget());
				}
				}
			}
		});
	}
	
	@IMEventHandler(IMEventType.GET_OFFPIC_REQUEST)
	protected void processIMGetOffPicRequest(final IMEvent event){
		final File file = event.getData("localFile");
		FileOutputStream picOut = null;
		try {
			picOut = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			broadcastIMEvent(new IMEvent(IMEventType.GET_OFFPIC_ERROR,
							new QQException(QQErrorCode.IO_ERROR, e), event) );
			return;
		}
		
		//下面的代码有待商榷。。TODO ...接口定义？
		final OffPicItem item = event.getData("offpic");
		
		QQUser user = new QQBuddy();
		user.setUin(event.getLong("fromUin"));
		QQMsg msg = new QQMsg();
		msg.setFrom(user);
		
		client.getOffPic(item, msg, picOut, new QQActionListener() {
			@Override
			public void onActionEvent(QQActionEvent actEvent) {
				switch(actEvent.getType()){
				case EVT_OK:{
					IMEvent imEvent = new IMEvent(IMEventType.GET_OFFPIC_SUCCESS);
					imEvent.setTarget(event.getTarget());
					imEvent.setRelatedEvent(event);
					broadcastIMEvent(imEvent);
				} break;
				
				case EVT_ERROR:{
					file.delete();
					LOG.warn("getOffPic error!", (QQException) actEvent.getTarget());
					IMEvent imEvent = new IMEvent(IMEventType.GET_OFFPIC_ERROR);
					imEvent.setTarget(event.getTarget());
					imEvent.setRelatedEvent(event);
					broadcastIMEvent(imEvent);
				} break;
				
				case EVT_READ : {
					IMEvent imEvent = new IMEvent(IMEventType.GET_OFFPIC_PROGRESS);
					ProgressArgs progress = (ProgressArgs) actEvent.getTarget();
					imEvent.putData("current", progress.current);
					imEvent.putData("total", progress.total);
					imEvent.setTarget(event.getTarget());
					imEvent.setRelatedEvent(event);
					broadcastIMEvent(imEvent);
				} break;
				}
				
			}
		});
	}
	
	@IMEventHandler(IMEventType.UPLOAD_OFFPIC_REQUEST)
	protected void processIMUploadOffPicRequest(final IMEvent event){
		final File file = event.getData("localFile");
		if(!file.exists() || !file.canRead()){
			broadcastIMEvent(new IMEvent(IMEventType.GET_OFFPIC_ERROR,
							new QQException(QQErrorCode.IO_ERROR, file + "not found!!!"), event) );
		}
		
		QQUser user = new QQBuddy();
		user.setUin(event.getLong("fromUin"));
		client.uploadOffPic(user, file, new QQActionListener() {
			@Override
			public void onActionEvent(QQActionEvent actEvent) {
				switch(actEvent.getType()){
				case EVT_OK:{
					IMEvent imEvent = new IMEvent(IMEventType.UPLOAD_OFFPIC_SUCCESS);
					imEvent.setTarget(actEvent.getTarget());
					imEvent.setRelatedEvent(event);
					broadcastIMEvent(imEvent);
				} break;
				
				case EVT_ERROR:{
					LOG.warn("UploadOffPic error!", (QQException) actEvent.getTarget());
					IMEvent imEvent = new IMEvent(IMEventType.UPLOAD_OFFPIC_ERROR);
					imEvent.setTarget(event.getTarget());
					imEvent.setRelatedEvent(event);
					broadcastIMEvent(imEvent);
				} break;
				
				case EVT_WRITE : {
					IMEvent imEvent = new IMEvent(IMEventType.UPLOAD_OFFPIC_PROGRESS);
					ProgressArgs progress = (ProgressArgs) actEvent.getTarget();
					imEvent.putData("current", progress.current);
					imEvent.putData("total", progress.total);
					imEvent.setTarget(event.getTarget());
					imEvent.setRelatedEvent(event);
					broadcastIMEvent(imEvent);
				} break;
				}
				
			}
		});
	}
	
	@IMEventHandler(IMEventType.UPLOAD_CFACE_REQUEST)
	protected void processIMUploadGroupPicRequest(final IMEvent event){
		final File file = event.getData("localFile");
		if(!file.exists() || !file.canRead()){
			broadcastIMEvent(new IMEvent(IMEventType.GET_GROUPPIC_ERROR,
							new QQException(QQErrorCode.IO_ERROR, file + "not found!!!"), event) );
		}
		client.uploadCustomPic(file, new QQActionListener() {
			@Override
			public void onActionEvent(QQActionEvent actEvent) {
				switch(actEvent.getType()){
				case EVT_OK:{
					IMEvent imEvent = new IMEvent(IMEventType.UPLOAD_CFACE_SUCCESS);
					imEvent.setTarget(actEvent.getTarget());
					imEvent.setRelatedEvent(event);
					broadcastIMEvent(imEvent);
				} break;
				
				case EVT_ERROR:{
					LOG.warn("UploadCustomPic error!", (QQException) actEvent.getTarget());
					IMEvent imEvent = new IMEvent(IMEventType.UPLOAD_CFACE_ERROR);
					imEvent.setTarget(actEvent.getTarget());
					imEvent.setRelatedEvent(event);
					broadcastIMEvent(imEvent);
				} break;
				
				case EVT_WRITE : {
					IMEvent imEvent = new IMEvent(IMEventType.UPLOAD_CFACE_PROGRESS);
					ProgressArgs progress = (ProgressArgs) actEvent.getTarget();
					imEvent.putData("current", progress.current);
					imEvent.putData("total", progress.total);
					imEvent.setTarget(event.getTarget());
					imEvent.setRelatedEvent(event);
					broadcastIMEvent(imEvent);
				} break;
				}
				
			}
		});
	}
	
	
	@IMEventHandler(IMEventType.GET_USERPIC_REQUEST)
	protected void processIMGetUserPicRequest(final IMEvent event){
		final File file = event.getData("localFile");
		FileOutputStream picOut = null;
		try {
			picOut = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			broadcastIMEvent(new IMEvent(IMEventType.GET_USERPIC_ERROR,
							new QQException(QQErrorCode.IO_ERROR, e), event) );
			return;
		}
		
		//下面的代码有待商榷。。TODO ...接口定义？
		final CFaceItem item = new CFaceItem();
		item.setFileName(event.getString("guid"));
		
		QQUser user = new QQBuddy();
		user.setUin(event.getLong("fromUin"));
		QQMsg msg = new QQMsg();
		msg.setFrom(user);
		msg.setId(event.getLong("msgId"));
		
		client.getUserPic(item, msg, picOut, new QQActionListener() {
			@Override
			public void onActionEvent(QQActionEvent actEvent) {
				switch(actEvent.getType()){
				case EVT_OK:{
					IMEvent imEvent = new IMEvent(IMEventType.GET_USERPIC_SUCCESS);
					imEvent.setTarget(actEvent.getTarget());
					imEvent.setRelatedEvent(event);
					broadcastIMEvent(imEvent);
				} break;
				
				case EVT_ERROR:{
					file.delete();
					LOG.warn("getUserPic error!", (QQException) actEvent.getTarget());
					IMEvent imEvent = new IMEvent(IMEventType.GET_USERPIC_ERROR);
					imEvent.setTarget(actEvent.getTarget());
					imEvent.setRelatedEvent(event);
					broadcastIMEvent(imEvent);
				} break;
				
				case EVT_READ : {
					IMEvent imEvent = new IMEvent(IMEventType.GET_USERPIC_PROGRESS);
					ProgressArgs progress = (ProgressArgs) actEvent.getTarget();
					imEvent.putData("current", progress.current);
					imEvent.putData("total", progress.total);
					imEvent.setTarget(actEvent.getTarget());
					imEvent.setRelatedEvent(event);
					broadcastIMEvent(imEvent);
				} break;
				}
				
			}
		});
	}
	
	
	@IMEventHandler(IMEventType.GET_GROUPPIC_REQUEST)
	protected void processIMGetGroupPicRequest(final IMEvent event){
		final File file = event.getData("localFile");
		FileOutputStream picOut = null;
		try {
			picOut = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			broadcastIMEvent(new IMEvent(IMEventType.GET_USERPIC_ERROR,
							new QQException(QQErrorCode.IO_ERROR, e), event) );
			return;
		}
		
		//下面的代码有待商榷。。TODO ...接口定义？
		final CFaceItem item = new CFaceItem();
		item.setFileName(event.getString("fileName"));
		item.setFileId(event.getLong("fileId"));
		item.setServer(event.getString("server"));
		
		QQUser user = new QQBuddy();
		user.setUin(event.getLong("fromUin"));
		
		
		QQMsg msg = new QQMsg();
		msg.setFrom(user);
		int type = event.getData("type");
		if(type == 0){
			QQGroup group = new QQGroup();
			group.setCode(event.getLong("groupId"));
			msg.setGroup(group);
			msg.setType(QQMsg.Type.GROUP_MSG);
		}else{
			QQDiscuz discuz = new QQDiscuz();
			discuz.setDid(event.getLong("groupId"));
			msg.setDiscuz(discuz);
			msg.setType(QQMsg.Type.DISCUZ_MSG);
		}
		
		client.getGroupPic(item, msg, picOut, new QQActionListener() {
			@Override
			public void onActionEvent(QQActionEvent actEvent) {
				switch(actEvent.getType()){
				case EVT_OK:{
					IMEvent imEvent = new IMEvent(IMEventType.GET_GROUPPIC_SUCCESS);
					imEvent.setTarget(actEvent.getTarget());
					imEvent.setRelatedEvent(event);
					broadcastIMEvent(imEvent);
				} break;
				
				case EVT_ERROR:{
					file.delete();
					LOG.warn("getGroupPic error!", (QQException) actEvent.getTarget());
					IMEvent imEvent = new IMEvent(IMEventType.GET_GROUPPIC_ERROR);
					imEvent.setTarget(actEvent.getTarget());
					imEvent.setRelatedEvent(event);
					broadcastIMEvent(imEvent);
				} break;
				
				case EVT_READ : {
					IMEvent imEvent = new IMEvent(IMEventType.GET_GROUPPIC_PROGRESS);
					ProgressArgs progress = (ProgressArgs) actEvent.getTarget();
					imEvent.putData("current", progress.current);
					imEvent.putData("total", progress.total);
					imEvent.setTarget(actEvent.getTarget());
					imEvent.setRelatedEvent(event);
					broadcastIMEvent(imEvent);
				} break;
				}
				
			}
		});
	}
	
	
	@IMEventHandler(IMEventType.SEND_INPUT_REQUEST)
	protected void processIMSendInputRequest(IMEvent event){
		QQUser user = (QQUser) event.getTarget();
		client.sendInputNotify(user, new QQActionListener() {
			@Override
			public void onActionEvent(QQActionEvent event) {
				switch(event.getType()){
				case EVT_OK:{
					//TODO ....
				} break;
				case EVT_ERROR:{
					LOG.warn("sendInputNotify error!", (QQException) event.getTarget());
				}
				}
			}
		});
	}
	
	
	@IMEventHandler(IMEventType.GROUP_GID_REQUEST)
	protected void processIMGroupGidRequest(IMEvent event){
		QQGroup group = (QQGroup) event.getTarget();
		client.getGroupGid(group, new QQActionListener() {
			public void onActionEvent(QQActionEvent event) {
				switch(event.getType()){
				case EVT_OK:{
					broadcastIMEvent(new IMEvent(IMEventType.GROUP_GID_UPDATE, event.getTarget()));
				} break;
				case EVT_ERROR:{
					//TODO ...
				}
				}
				
			}
		});
	}
	
	@IMEventHandler(IMEventType.GET_SESSION_MSG_REQUEST)
	protected void processIMSessionMsgSigRequest(IMEvent event){
		QQStranger member = (QQStranger) event.getTarget();
		client.getSessionMsgSig(member, new QQActionListener() {
			@Override
			public void onActionEvent(QQActionEvent event) {
				switch(event.getType()){
				case EVT_OK:{
					broadcastIMEvent(IMEventType.GET_SESSION_MSG_SUCCESS, event.getTarget());
				} break;
				case EVT_ERROR:{
					//TODO ...
				}
				}
			}
		});
	}
	
	@IMEventHandler(IMEventType.USER_CACHE_MISS)
	protected void processIMUserCacheMiss(IMEvent event){
		final QQUser user = (QQUser) event.getTarget();
		client.getUserFace(user, new QQActionListener() {
			public void onActionEvent(QQActionEvent event) {
				switch(event.getType()){
				case EVT_OK:{
					broadcastIMEvent(new IMEvent(IMEventType.USER_FACE_UPDATE, user));
				} break;
				case EVT_ERROR:{
					//TODO ..
				}
				}
			}
		});
		
		client.getUserSign(user, new QQActionListener() {
			public void onActionEvent(QQActionEvent event) {
				switch(event.getType()){
				case EVT_OK:{
					broadcastIMEvent(new IMEvent(IMEventType.USER_SIGN_UPDATE, user));
				} break;
				case EVT_ERROR:{
					//TODO ..
				}
				}
			}
		});
	}
	
	@IMEventHandler(IMEventType.QUERY_BUDDY_LIST)
	protected void processIMBuddyListQuery(IMEvent event){
		event.setResult(client.getBuddyList());
		event.setQueryHandled(true);
		IMContext context = ((IMFrameView)event.getData("view")).getContext();
		IMEventService events = context.getSerivce(IMService.Type.EVENT);
		IMEvent show_event = new IMEvent(IMEventType.SHOW_SEARCHBUDDY_WINDOW, null); 
		show_event.putData("view", event.getData("view"));
		show_event.putData("nodeBounds", event.getData("nodeBounds"));
		show_event.putData("comp", event.getData("comp"));
		show_event.putData("searchKeyWord", event.getData("searchKeyWord"));
		show_event.setResult(event.getResult());
		events.broadcast(show_event);
	}
	
	@IMEventHandler(IMEventType.QUERY_ACCOUNT)
	protected void processIMAccountQuery(IMEvent event){
		event.setResult(client.getAccount());
		event.setQueryHandled(true);
	}
	
	@IMEventHandler(IMEventType.QUERY_SELF_IS_ONLINE)
	protected void processIMSelfIsOnlineQuery(IMEvent event){
		event.setResult(client.isOnline());
		event.setQueryHandled(true);
	}
	
	private void doClearOnlineStatus(){
		for(QQBuddy buddy: client.getBuddyList()){
			buddy.setStatus(QQStatus.OFFLINE);
		}
		for(QQGroup group: client.getGroupList()){
			for(QQGroupMember mem: group.getMembers()){
				mem.setStatus(QQStatus.OFFLINE);
			}
		}
		for(QQDiscuz discuz: client.getDiscuzList()){
			for(QQDiscuzMember mem: discuz.getMembers()){
				mem.setStatus(QQStatus.OFFLINE);
			}
		}
	}
	
	private void doGetBuddyList(){
		client.getBuddyList(new QQActionListener() {
			public void onActionEvent(QQActionEvent event) {
				switch(event.getType()){
				case EVT_OK:{
					IMEvent imEvent = new IMEvent(IMEventType.BUDDY_LIST_UPDATE);
					imEvent.setTarget(event.getTarget());
					broadcastIMEvent(imEvent);
					
					buddyListFetched = true;
					
					doGetOnlineBuddy();
					//这个可能要优化，比如用户看到的首先下载，折叠起来可以稍后下载，特别是在好友很多情况下用户体验完全就不一样了
					//应该由UI部分发起请求。。。。
					//从缓存中查找用信息
					doFindAllBuddyInfoFromCache();
					doTryGetRecentListAndPollMsg();
					
				} break;
				}
			}
		});
	}
	
	private void doGetGroupList(){
		client.getGroupList(new QQActionListener() {
			public void onActionEvent(QQActionEvent event) {
				switch(event.getType()){
				case EVT_OK:{
					IMEvent imEvent = new IMEvent(IMEventType.GROUP_LIST_UPDATE);
					imEvent.setTarget(event.getTarget());
					broadcastIMEvent(imEvent);
					
					doGetAllGroupFace();
					doGetAllGroupInfo();
					
					groupListFetched = true;
					doTryGetRecentListAndPollMsg();
				} break;
				}
			}
		});
	}
	
	private void doGetDiscuzList(){
		client.getDiscuzList(new QQActionListener() {
			public void onActionEvent(QQActionEvent event) {
				switch(event.getType()){
				case EVT_OK:{
					IMEvent imEvent = new IMEvent(IMEventType.DISCUZ_LIST_UPDATE);
					imEvent.setTarget(event.getTarget());
					broadcastIMEvent(imEvent);
					
					doGetAllDiscuzInfo();
					
					discuzListFetched = true;
					doTryGetRecentListAndPollMsg();
				} break;
				}
			}
		});
	}
	
	private void doGetAllDiscuzInfo() {
		for(final QQDiscuz discuz : client.getDiscuzList()) {
			client.getDiscuzInfo(discuz, new QQActionListener() {
				
				@Override
				public void onActionEvent(QQActionEvent event) {
					switch(event.getType()){
					case EVT_OK:{
						//TODO ..
						IMEvent imEvent = new IMEvent(IMEventType.DISCUZ_INFO_UPDATE);
						imEvent.setTarget(discuz);
						broadcastIMEvent(imEvent);
					} break;
					case EVT_ERROR:{
						//TODO ..
						LOG.error("", (QQException)event.getTarget());
					}
					}
				}
			});
		}
	}
	
	private void doGetOnlineBuddy(){
		client.getOnlineList(new QQActionListener() {
			public void onActionEvent(QQActionEvent event) {
				switch(event.getType()){
				case EVT_OK:{
					broadcastIMEvent(new IMEvent(IMEventType.ONLINE_LIST_UPDATE, event.getTarget()));
				} break;
				case EVT_ERROR:{
					//TODO ..
					LOG.warn("doGetOnlineBuddy Error!", (QQException) event.getTarget());
				}
				}
			}
		});
	}
	
	private void doFindAllBuddyInfoFromCache(){
		broadcastIMEvent(new IMEvent(IMEventType.USER_CACHE_BATCH_FIND, client.getBuddyList()));
	}
	
	private void doTryGetRecentListAndPollMsg(){
		if(buddyListFetched && groupListFetched && discuzListFetched && !pollMsgStarted){
			pollMsgStarted = true;
			//好友列表，群列表，讨论组列表获取完后，就可以开始poll消息
			client.beginPollMsg();
			
			//获取最近联系人列表
			client.getRecentList(new QQActionListener() {
				public void onActionEvent(QQActionEvent event) {
					switch(event.getType()){
					case EVT_OK:{
						broadcastIMEvent(new IMEvent(IMEventType.RECENT_LIST_UPDATE, event.getTarget()));
					} break;
					case EVT_ERROR:{
						//TODO ..
					}
					}
					
				}
			});
		}
	}
	
	private void doGetAllGroupFace(){
		for(final QQGroup group: client.getGroupList()){
			client.getGroupFace(group, new QQActionListener() {
				public void onActionEvent(QQActionEvent event) {
					switch(event.getType()){
					case EVT_OK:{
						IMEvent imEvent = new IMEvent(IMEventType.GROUP_FACE_UPDATE);
						imEvent.setTarget(group);
						broadcastIMEvent(imEvent);
					} break;
					case EVT_ERROR:{
						//TODO ...
					}
					}
					
				}
			});
		}
	}
	
	private void doGetSelfSign(){
		final QQAccount account = client.getAccount();
		client.getUserSign(account, new QQActionListener() {
			public void onActionEvent(QQActionEvent event) {
				switch(event.getType()){
				case EVT_OK:{
					IMEvent imEvent = new IMEvent(IMEventType.SELF_SIGN_UPDATE);
					imEvent.setTarget(account);
					broadcastIMEvent(imEvent);
				} break;
				case EVT_ERROR:{
					//TODO ...
				}
				}
			}
		});
	}
	
	private void doGetSelfFace(){
		final QQAccount account = client.getAccount();
		client.getUserFace(account, new QQActionListener() {
			public void onActionEvent(QQActionEvent event) {
				switch(event.getType()){
				case EVT_OK:{
					IMEvent imEvent = new IMEvent(IMEventType.SELF_FACE_UPDATE);
					imEvent.setTarget(account);
					broadcastIMEvent(imEvent);
				} break;
				case EVT_ERROR:{
					//TODO ...
				}
				}
			}
		});
	}
	
	private void doGetSelfInfo(){
		final QQAccount account = client.getAccount();
		client.getUserInfo(account, new QQActionListener() {
			public void onActionEvent(QQActionEvent event) {
				switch(event.getType()){
				case EVT_OK:{
					IMEvent imEvent = new IMEvent(IMEventType.SELF_INFO_UPDATE);
					imEvent.setTarget(account);
					broadcastIMEvent(imEvent);
				} break;
				case EVT_ERROR:{
					//TODO ...
				}
				}
			}
		});
	}
	
	private void doGetAllGroupInfo(){
		for(final QQGroup group: client.getGroupList()){
			client.getGroupInfo(group, new QQActionListener() {
				public void onActionEvent(QQActionEvent event) {
					switch(event.getType()){
					case EVT_OK:{
						IMEvent imEvent = new IMEvent(IMEventType.GROUP_INFO_UPDATE);
						imEvent.setTarget(group);
						broadcastIMEvent(imEvent);
					} break;
					case EVT_ERROR:{
						QQException.QQErrorCode ex = (QQException.QQErrorCode) event.getTarget();
						LOG.warn("getGroupInfo Error! " + ex);
						//TODO ...
					}
					}
				}
			});
		}
	}
	
	@IMEventHandler(IMEventType.USER_INFO_REQUEST)
	protected void processGetUserInfoRequest(IMEvent imEvent){
		final QQUser user = (QQUser) imEvent.getTarget();
		client.getUserInfo(user, new QQActionListener() {
			public void onActionEvent(QQActionEvent event) {
				switch(event.getType()){
				case EVT_OK:{
					IMEvent imEvent = new IMEvent(IMEventType.USER_INFO_UPDATE);
					imEvent.setTarget(user);
					broadcastIMEvent(imEvent);
				} break;
				case EVT_ERROR:{
					//TODO ...
				}
				}
			}
		});
	}
	
	@IMEventHandler(IMEventType.USER_LEVEL_REQUEST)
	protected void processGetUserLevelRequest(IMEvent imEvent){
		final QQUser user = (QQUser) imEvent.getTarget();
		client.getUserLevel(user, new QQActionListener() {
			public void onActionEvent(QQActionEvent event) {
				switch(event.getType()){
				case EVT_OK:{
					IMEvent imEvent = new IMEvent(IMEventType.USER_LEVEL_UPDATE);
					imEvent.setTarget(user);
					broadcastIMEvent(imEvent);
				} break;
				case EVT_ERROR:{
					//TODO ...
				}
				}
			}
		});
	}
	
	@IMEventHandler(IMEventType.GROUP_INFO_REQUEST)
	protected void processIMGroupInfoRequest(IMEvent imEvent) {
		final QQGroup group = (QQGroup) imEvent.getTarget();
		client.getGroupInfo(group, new QQActionListener() {
			public void onActionEvent(QQActionEvent event) {
				switch(event.getType()){
				case EVT_OK:{
					IMEvent imEvent = new IMEvent(IMEventType.GROUP_INFO_UPDATE);
					imEvent.putData("reqeust", "true");
					imEvent.setTarget(group);
					broadcastIMEvent(imEvent);
				} break;
				case EVT_ERROR:{
					QQException.QQErrorCode  ex = (QQException.QQErrorCode) event.getTarget();
					LOG.warn("getGroupInfo Error!" + ex);
					//TODO ...
				}
				}
			}
		});
	}
	
	@IMEventHandler(IMEventType.DISCUZ_INFO_REQUEST)
	protected void processIMDiscuzInfoRequest(IMEvent event) {
		final QQDiscuz discuz = (QQDiscuz) event.getTarget();
		client.getDiscuzInfo(discuz, new QQActionListener() {
			public void onActionEvent(QQActionEvent event) {
				switch(event.getType()){
				case EVT_OK:{
					IMEvent imEvent = new IMEvent(IMEventType.DISCUZ_INFO_UPDATE);
					imEvent.putData("reqeust", "true");
					imEvent.setTarget(discuz);
					broadcastIMEvent(imEvent);
				} break;
				case EVT_ERROR:{
					QQException ex = (QQException) event.getTarget();
					LOG.warn("getGroupInfo Error!", ex);
					//TODO ...
				}
				}
			}
		});
	}
	
	@IMEventHandler(IMEventType.USER_QQ_REQUEST)
	protected void processIMUserQQRequest(IMEvent event){
		QQUser user = (QQUser) event.getTarget();
		client.getUserQQ(user, new QQActionListener() {
			public void onActionEvent(QQActionEvent event) {
				switch(event.getType()){
				case EVT_OK:{
					IMEvent imEvent = new IMEvent(IMEventType.USER_QQ_UPDATE);
					imEvent.setTarget(event.getTarget()); //QQUser
					broadcastIMEvent(imEvent);
				} break;
				case EVT_ERROR:{
					//TODO ..
				}
				}
				
			}
		});
	}
	
	@IMEventHandler(IMEventType.STRANGER_INFO_REQUEST)
	protected void processIMStrangerRequest(IMEvent event){
		QQUser user = (QQUser) event.getTarget();
		client.getStrangerInfo(user, new QQActionListener() {
			public void onActionEvent(QQActionEvent event) {
				switch(event.getType()){
				case EVT_OK:{
					IMEvent imEvent = new IMEvent(IMEventType.STRANGER_INFO_UPDATE);
					imEvent.setTarget(event.getTarget()); //QQUser
					broadcastIMEvent(imEvent);
				} break;
				case EVT_ERROR:{
					//TODO ..
				}
				}
				
			}
		});
	}
	
	@IMEventHandler(IMEventType.GROUP_MSG_FILTER_REQUEST)
	protected void processIMUpdateGroupMsgFilterRequest(IMEvent imEvent){
		client.updateGroupMessageFilter(new QQActionListener() {
			
			@Override
			public void onActionEvent(QQActionEvent event) {
				switch(event.getType()){
				case EVT_OK:{
					IMEvent imEvent = new IMEvent(IMEventType.GROUP_MSG_FILTER_SUCCESS);
					imEvent.setTarget(event.getTarget());
					broadcastIMEvent(imEvent);
				} break;
				case EVT_ERROR:{
					//TODO ..
					LOG.warn("UpdateGroupMsgFilterRequest Error! " + event.getTarget());
				}
				}
			}
		});
	}
	

	@QQNotifyHandler(QQNotifyEvent.Type.CAPACHA_VERIFY)
	protected void processQQNotifyCaptchaVerify(QQNotifyEvent event){
		IMEvent imEvent = new IMEvent(IMEventType.IMAGE_VERIFY_NEED);
		QQNotifyEventArgs.ImageVerify verify = (
				QQNotifyEventArgs.ImageVerify) event.getTarget();
		imEvent.putData("reason", verify.reason);
		imEvent.putData("image", verify.image);
		imEvent.setTarget(event);
		broadcastIMEvent(imEvent);
	}
	
	@QQNotifyHandler(QQNotifyEvent.Type.CHAT_MSG)
	protected void processQQNotifyBuddyMsg(QQNotifyEvent event){
		QQMsg msg = (QQMsg) event.getTarget();
		IMEvent imEvent = new IMEvent(IMEventType.RECV_QQ_MSG, msg);
		broadcastIMEvent(imEvent);
	}
	
	@QQNotifyHandler(QQNotifyEvent.Type.BUDDY_STATUS_CHANGE)
	protected void processQQNotifyBuddyStateChange(QQNotifyEvent event){
		IMEvent imEvent = new IMEvent(IMEventType.USER_STATUS_UPDATE, event.getTarget());
		broadcastIMEvent(imEvent);
	}
	
	@QQNotifyHandler(QQNotifyEvent.Type.SHAKE_WINDOW)
	protected void processQQNotifyShakeWindow(QQNotifyEvent event){
		IMEvent imEvent = new IMEvent(IMEventType.SHAKE_WINDOW, event.getTarget());
		broadcastIMEvent(imEvent);
	}
	
	@QQNotifyHandler(QQNotifyEvent.Type.BUDDY_INPUT)
	protected void processQQNotifyBuddyInput(QQNotifyEvent event){
		broadcastIMEvent(IMEventType.USER_INPUTTING, event.getTarget());
	}
	
	@QQNotifyHandler(QQNotifyEvent.Type.NET_ERROR)
	protected void processQQNotifyNetError(QQNotifyEvent event){
		doClearOnlineStatus();
		IMEvent imEvent = new IMEvent(IMEventType.CLIENT_OFFLINE);
		imEvent.putData("msg", I18NUtil.getMessage("netOffline"));
		imEvent.putData("force", true);
		broadcastIMEvent(imEvent);
	}
	
	@QQNotifyHandler(QQNotifyEvent.Type.KICK_OFFLINE)
	protected void processKickOff(QQNotifyEvent event){
		doClearOnlineStatus();
		IMEvent imEvent = new IMEvent(IMEventType.CLIENT_OFFLINE);
		imEvent.putData("msg", event.getTarget());
		imEvent.putData("force", true);
		broadcastIMEvent(imEvent);
	}
	
	/* 已经在CORE自动处理重新自动登录
	@QQNotifyHandler(QQNotifyEvent.Type.NEED_REAUTH)
	protected void processQQNotifyNeedReauth(QQNotifyEvent event){
		processIMReLoginRequest(new IMEvent(IMEventType.RELOGIN_REQEUST, 
											client.getAccount().getStatus()));
	}
	*/
}

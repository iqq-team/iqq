package iqq.app.core.module;
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

import iqq.api.annotation.IMEventHandler;
import iqq.api.bean.IMAccount;
import iqq.api.bean.IMBuddy;
import iqq.api.bean.IMBuddyCategory;
import iqq.api.bridge.IMApp;
import iqq.api.bridge.IMBridge;
import iqq.api.event.IMEvent;
import iqq.api.event.IMEventDispatcher;
import iqq.api.event.IMEventType;
import iqq.api.event.args.LoginRequest;
import iqq.app.core.query.AccountQuery;
import iqq.app.core.query.BuddyQuery;
import iqq.app.core.query.GroupQuery;
import iqq.app.core.service.EventService;
import iqq.app.ui.event.UIEvent;
import iqq.app.ui.event.UIEventDispatcher;
import iqq.app.ui.event.UIEventHandler;
import iqq.app.ui.event.UIEventType;
import iqq.app.ui.event.args.LoginInfoParam;
import iqq.bridge.IMBridgeFactory;
import iqq.im.QQException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

//import iqq.im.QQException;

/**
 * QQ主模块，负责底层和QQ核心通信，如QQ登陆，发送消息，接受消息等
 * Project  : iqq
 * Author   : solosky < solosky772@qq.com >
 * Created  : 4/13/14
 * License  : Apache License 2.0
 */
@Service
public class LogicModule extends IMEventDispatcher implements AccountQuery, BuddyQuery, GroupQuery, IMApp {
    //    @Inject
    private IMBridge imBridge;
    @Resource
    private EventService eventService;
    private IMAccount account;

    @PostConstruct
    public void init() {
        imBridge = IMBridgeFactory.getIMBridge();
        imBridge.setApp(this);

        UIEventDispatcher uiEventDispatcher = new UIEventDispatcher(this);
        eventService.register(uiEventDispatcher.getEventTypes(), uiEventDispatcher);
    }

    @Override
    public IMAccount getOwner() {
        return account;
    }

    @Override
    public IMBuddy findById(long id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<IMBuddy> findAll() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @UIEventHandler(UIEventType.SEND_MSG_REQUEST)
    private void onSendMsgEvent(UIEvent uiEvent) {
        imBridge.onIMEvent(new IMEvent(IMEventType.SEND_MSG_REQUEST, uiEvent.getTarget()));
    }

    @UIEventHandler(UIEventType.LOGIN_REQUEST)
    private void onLoginEvent(UIEvent uiEvent) {
        LoginInfoParam param = (LoginInfoParam) uiEvent.getTarget();

        LoginRequest req = new LoginRequest();
        req.setUsername(param.getUsername());
        req.setPassword(param.getPassword());
        req.setStatus(param.getStatus());

        imBridge.onIMEvent(new IMEvent(IMEventType.LOGIN_REQUEST, req));
    }

    @IMEventHandler(IMEventType.LOGIN_SUCCESS)
    protected void processLoginSuccess(IMEvent imEvent) {
        account = (IMAccount) imEvent.getTarget();
        eventService.broadcast(new UIEvent(UIEventType.LOGIN_SUCCESS));
    }

    @IMEventHandler(IMEventType.LOGIN_ERROR)
    protected void processLoginError(IMEvent imEvent) {
        QQException ex = (QQException) imEvent.getTarget();
        eventService.broadcast(new UIEvent(UIEventType.LOGIN_ERROR, ex.getMessage()));
    }

    @IMEventHandler(IMEventType.IMAGE_VERIFY_NEED)
    protected void processNeedVerify(IMEvent imEvent) {
        eventService.broadcast(new UIEvent(UIEventType.IMAGE_VERIFY_NEED, imEvent.getTarget()));
    }

    @IMEventHandler(IMEventType.SELF_FACE_UPDATE)
    protected void processSelfFaceUpdate(IMEvent imEvent) {
        eventService.broadcast(new UIEvent(UIEventType.SELF_FACE_UPDATE, imEvent.getTarget()));
    }

    @IMEventHandler(IMEventType.SELF_INFO_UPDATE)
    protected void processSelfInfoUpdate(IMEvent imEvent) {
        eventService.broadcast(new UIEvent(UIEventType.SELF_INFO_UPDATE, imEvent.getTarget()));
    }

    @IMEventHandler(IMEventType.SELF_SIGN_UPDATE)
    protected void processSelfSignUpdate(IMEvent imEvent) {
        eventService.broadcast(new UIEvent(UIEventType.SELF_SIGN_UPDATE, imEvent.getTarget()));
    }

    @IMEventHandler(IMEventType.BUDDY_LIST_UPDATE)
    protected void processBuddyListUpdate(IMEvent imEvent) {
        eventService.broadcast(new UIEvent(UIEventType.BUDDY_LIST_UPDATE, imEvent.getTarget()));
    }

    @IMEventHandler(IMEventType.USER_FACE_UPDATE)
    protected void processUserFaceUpdate(IMEvent imEvent) {
        eventService.broadcast(new UIEvent(UIEventType.USER_FACE_UPDATE, imEvent.getTarget()));
    }

    @IMEventHandler(IMEventType.GROUP_LIST_UPDATE)
    protected void processGroupListUpdate(IMEvent imEvent) {
        eventService.broadcast(new UIEvent(UIEventType.GROUP_LIST_UPDATE, imEvent.getTarget()));
    }

    @IMEventHandler(IMEventType.RECENT_LIST_UPDATE)
    protected void processRecentListUpdate(IMEvent imEvent) {
        eventService.broadcast(new UIEvent(UIEventType.RECENT_LIST_UPDATE, imEvent.getTarget()));
    }

    @IMEventHandler(IMEventType.RECV_RAW_MSG)
    protected void processRecvRawMsg(IMEvent imEvent) {
        eventService.broadcast(new UIEvent(UIEventType.RECV_RAW_MSG, imEvent.getTarget()));
    }

}

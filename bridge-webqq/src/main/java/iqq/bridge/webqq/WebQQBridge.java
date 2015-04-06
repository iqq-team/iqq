package iqq.bridge.webqq;
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
import iqq.api.bean.IMBuddy;
import iqq.api.bean.IMBuddyCategory;
import iqq.api.bean.IMUser;
import iqq.api.bridge.IMApp;
import iqq.api.bridge.IMBridge;
import iqq.api.event.IMEvent;
import iqq.api.event.IMEventDispatcher;
import iqq.api.event.IMEventType;
import iqq.api.event.args.LoginRequest;
import iqq.im.QQActionListener;
import iqq.im.QQException;
import iqq.im.WebQQClient;
import iqq.im.actor.SwingActorDispatcher;
import iqq.im.bean.*;
import iqq.im.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Project  : iqq
 * Author   : solosky < solosky772@qq.com >
 * Created  : 5/1/14
 * License  : Apache License 2.0
 */
public class WebQQBridge extends IMEventDispatcher implements IMBridge {
    private static final Logger LOG = LoggerFactory.getLogger(WebQQBridge.class);
    private WebQQClient client;
    private IMApp imApp;
    private QQActionFuture loginFuture;
    private boolean buddyListFetched;
    private boolean groupListFetched;
    private boolean discuzListFetched;
    private boolean pollMsgStarted;

    @Override
    public void setApp(IMApp imApp) {
        this.imApp = imApp;
    }

    @IMEventHandler(IMEventType.LOGIN_REQUEST)
    private void processLoginRequestEvent(IMEvent imEvent) {
        if (client != null) {
            client.destroy();
        }

        buddyListFetched = false;
        groupListFetched = false;
        discuzListFetched = false;
        pollMsgStarted = false;

        LoginRequest loginRequest = (LoginRequest) imEvent.getTarget();

        client = new WebQQClient(
                loginRequest.getUsername(),
                loginRequest.getPassword(),
                new QQNotifyHandlerProxy(this),
                new SwingActorDispatcher());
        //client.setHttpUserAgent("IQQ App/2.1 dev");
        String ua = "Mozilla/5.0 (@os.name; @os.version; @os.arch) AppleWebKit/537.36 (KHTML, like Gecko) IQQ App-dev/2.1 Safari/537.36";
        ua = ua.replaceAll("@os.name", System.getProperty("os.name"));
        ua = ua.replaceAll("@os.version", System.getProperty("os.version"));
        ua = ua.replaceAll("@os.arch", System.getProperty("os.arch"));
        client.setHttpUserAgent(ua);
        loginFuture = client.login(
                QQStatus.valueOf(loginRequest.getStatus().name()),
                new QQActionListener() {
                    public void onActionEvent(QQActionEvent event) {
                        switch (event.getType()) {
                            case EVT_OK: {

                                broadcastIMEvent(new IMEvent(IMEventType.LOGIN_SUCCESS, client.getAccount()));
                                broadcastIMEvent(IMEventType.CLIENT_ONLINE, client.getAccount());

                                doGetBuddyList();
                                doGetGroupList();
                                doGetDiscuzList();
                                doGetSelfFace();
                                doGetSelfInfo();
                                doGetSelfSign();
                            }
                            break;
                            case EVT_ERROR: {
                                //登录失败
                                QQException exc = (QQException) event.getTarget();
                                IMEvent imEvent = new IMEvent(IMEventType.LOGIN_ERROR);
                                imEvent.setTarget(exc);
                                imEvent.putData("reason", exc.getMessage()); //TODO ..转换为友好的字符串
                                broadcastIMEvent(imEvent);
                                LOG.warn("Login Error!!", exc);
                            }
                            break;

                            case EVT_CANCELED: {
                                //取消登录
                                IMEvent imEvent = new IMEvent(IMEventType.LOGIN_CANCELED);
                                broadcastIMEvent(imEvent);
                                LOG.warn("Login canceled!!!!!!");
                            }
                            break;
                        }
                    }
                });
    }


    private void doGetBuddyList() {
        client.getBuddyList(new QQActionListener() {
            public void onActionEvent(QQActionEvent event) {
                switch (event.getType()) {
                    case EVT_OK: {
                        List<QQCategory> qqCategories = (List<QQCategory>) event.getTarget();
                        List<IMBuddyCategory> imCategories = new LinkedList<IMBuddyCategory>();
                        for (QQCategory qqCategory : qqCategories) {
                            IMBuddyCategory imCategory = new IMBuddyCategory();
                            imCategory.setName(qqCategory.getName());
                            for (QQBuddy qqBuddy : qqCategory.getBuddyList()) {
                                IMBuddy imBuddy = new IMBuddy();
                                imBuddy.setId(qqBuddy.getUin());
                                imBuddy.setNick(qqBuddy.getNickname());
                                imBuddy.setSign(qqBuddy.getSign());
                                imBuddy.setRemark(qqBuddy.getMarkname());
                                imCategory.getBuddyList().add(imBuddy);
                            }
                            imCategories.add(imCategory);
                        }

                        IMEvent imEvent = new IMEvent(IMEventType.BUDDY_LIST_UPDATE);
                        imEvent.setTarget(imCategories);
                        broadcastIMEvent(imEvent);

                        buddyListFetched = true;

                        doGetOnlineBuddy();
                        //这个可能要优化，比如用户看到的首先下载，折叠起来可以稍后下载，特别是在好友很多情况下用户体验完全就不一样了
                        //应该由UI部分发起请求。。。。
                        //从缓存中查找用信息
                        doFindAllBuddyInfoFromCache(imCategories);
                        doTryGetRecentListAndPollMsg();

                    }
                    break;
                }
            }
        });
    }

    private void doGetGroupList() {
        client.getGroupList(new QQActionListener() {
            public void onActionEvent(QQActionEvent event) {
                switch (event.getType()) {
                    case EVT_OK: {
                        IMEvent imEvent = new IMEvent(IMEventType.GROUP_LIST_UPDATE);
                        imEvent.setTarget(event.getTarget());
                        broadcastIMEvent(imEvent);

                        doGetAllGroupFace();
                        doGetAllGroupInfo();

                        groupListFetched = true;
                        doTryGetRecentListAndPollMsg();
                    }
                    break;
                }
            }
        });
    }

    private void doGetDiscuzList() {
        client.getDiscuzList(new QQActionListener() {
            public void onActionEvent(QQActionEvent event) {
                switch (event.getType()) {
                    case EVT_OK: {
                        IMEvent imEvent = new IMEvent(IMEventType.DISCUZ_LIST_UPDATE);
                        imEvent.setTarget(event.getTarget());
                        broadcastIMEvent(imEvent);

                        doGetAllDiscuzInfo();

                        discuzListFetched = true;
                        doTryGetRecentListAndPollMsg();
                    }
                    break;
                }
            }
        });
    }

    private void doGetAllDiscuzInfo() {
        for (final QQDiscuz discuz : client.getDiscuzList()) {
            client.getDiscuzInfo(discuz, new QQActionListener() {

                @Override
                public void onActionEvent(QQActionEvent event) {
                    switch (event.getType()) {
                        case EVT_OK: {
                            //TODO ..
                            IMEvent imEvent = new IMEvent(IMEventType.DISCUZ_INFO_UPDATE);
                            imEvent.setTarget(discuz);
                            broadcastIMEvent(imEvent);
                        }
                        break;
                        case EVT_ERROR: {
                            //TODO ..
                            LOG.error("", (QQException) event.getTarget());
                        }
                    }
                }
            });
        }
    }

    private void doGetOnlineBuddy() {
        client.getOnlineList(new QQActionListener() {
            public void onActionEvent(QQActionEvent event) {
                switch (event.getType()) {
                    case EVT_OK: {
                        broadcastIMEvent(new IMEvent(IMEventType.ONLINE_LIST_UPDATE, event.getTarget()));
                    }
                    break;
                    case EVT_ERROR: {
                        //TODO ..
                        LOG.warn("doGetOnlineBuddy Error!", (QQException) event.getTarget());
                    }
                }
            }
        });
    }

    private void doFindAllBuddyInfoFromCache(List<IMBuddyCategory> imCategories) {
        //broadcastIMEvent(new IMEvent(IMEventType.USER_CACHE_BATCH_FIND, client.getBuddyList()));
        for (IMBuddyCategory category : imCategories) {
            for (IMBuddy buddy : category.getBuddyList()) {
                IMEvent imEvent = new IMEvent(IMEventType.USER_FACE_REQUEST);
                imEvent.setTarget(buddy);
                processUserFaceRuquest(imEvent);
            }
        }

    }

    private void doTryGetRecentListAndPollMsg() {
        if (buddyListFetched && groupListFetched && discuzListFetched && !pollMsgStarted) {
            pollMsgStarted = true;
            //好友列表，群列表，讨论组列表获取完后，就可以开始poll消息
            client.beginPollMsg();

            //获取最近联系人列表
            client.getRecentList(new QQActionListener() {
                public void onActionEvent(QQActionEvent event) {
                    switch (event.getType()) {
                        case EVT_OK: {
                            broadcastIMEvent(new IMEvent(IMEventType.RECENT_LIST_UPDATE, event.getTarget()));
                        }
                        break;
                        case EVT_ERROR: {
                            //TODO ..
                        }
                    }

                }
            });
        }
    }

    private void doGetAllGroupFace() {
        for (final QQGroup group : client.getGroupList()) {
            client.getGroupFace(group, new QQActionListener() {
                public void onActionEvent(QQActionEvent event) {
                    switch (event.getType()) {
                        case EVT_OK: {
                            IMEvent imEvent = new IMEvent(IMEventType.GROUP_FACE_UPDATE);
                            imEvent.setTarget(group);
                            broadcastIMEvent(imEvent);
                        }
                        break;
                        case EVT_ERROR: {
                            //TODO ...
                        }
                    }

                }
            });
        }
    }

    private void doGetSelfSign() {
        final QQAccount account = client.getAccount();
        client.getUserSign(account, new QQActionListener() {
            public void onActionEvent(QQActionEvent event) {
                switch (event.getType()) {
                    case EVT_OK: {
                        IMEvent imEvent = new IMEvent(IMEventType.SELF_SIGN_UPDATE);
                        IMUser imUser = new IMUser();
                        imUser.setSign(account.getSign());
                        imEvent.setTarget(imUser);
                        broadcastIMEvent(imEvent);
                    }
                    break;
                    case EVT_ERROR: {
                        //TODO ...
                    }
                }
            }
        });
    }

    private void doGetSelfFace() {
        final QQAccount account = client.getAccount();
        client.getUserFace(account, new QQActionListener() {
            public void onActionEvent(QQActionEvent event) {
                switch (event.getType()) {
                    case EVT_OK: {
                        IMEvent imEvent = new IMEvent(IMEventType.SELF_FACE_UPDATE);
                        imEvent.setTarget(account.getFace());
                        broadcastIMEvent(imEvent);
                    }
                    break;
                    case EVT_ERROR: {
                        //TODO ...
                    }
                }
            }
        });
    }

    private void doGetSelfInfo() {
        final QQAccount account = client.getAccount();
        client.getUserInfo(account, new QQActionListener() {
            public void onActionEvent(QQActionEvent event) {
                switch (event.getType()) {
                    case EVT_OK: {
                        IMEvent imEvent = new IMEvent(IMEventType.SELF_INFO_UPDATE);
                        IMUser imUser = new IMUser();
                        imUser.setNick(account.getNickname());
                        imUser.setSign(account.getSign());
                        imEvent.setTarget(imUser);
                        broadcastIMEvent(imEvent);
                    }
                    break;
                    case EVT_ERROR: {
                        LOG.warn("doGetSelfInfo Error!", (QQException) event.getTarget());
                    }
                }
            }
        });
    }

    private void doGetAllGroupInfo() {
        for (final QQGroup group : client.getGroupList()) {
            client.getGroupInfo(group, new QQActionListener() {
                public void onActionEvent(QQActionEvent event) {
                    switch (event.getType()) {
                        case EVT_OK: {
                            IMEvent imEvent = new IMEvent(IMEventType.GROUP_INFO_UPDATE);
                            imEvent.setTarget(group);
                            broadcastIMEvent(imEvent);
                        }
                        break;
                        case EVT_ERROR: {
                            QQException.QQErrorCode ex = (QQException.QQErrorCode) event.getTarget();
                            LOG.warn("getGroupInfo Error! " + ex);
                            //TODO ...
                        }
                    }
                }
            });
        }
    }

    @QQNotifyHandler(QQNotifyEvent.Type.CAPACHA_VERIFY)
    protected void processQQNotifyCaptchaVerify(QQNotifyEvent event) {
        IMEvent imEvent = new IMEvent(IMEventType.IMAGE_VERIFY_NEED);
        QQNotifyEventArgs.ImageVerify verify = (
                QQNotifyEventArgs.ImageVerify) event.getTarget();
        imEvent.putData("reason", verify.reason);
        imEvent.putData("image", verify.image);
        imEvent.setTarget(event);
        broadcastIMEvent(imEvent);
    }


    @IMEventHandler(IMEventType.USER_FACE_REQUEST)
    protected void processUserFaceRuquest(IMEvent event) {
        final IMUser user = (IMUser) event.getTarget();
        final QQUser qqUser = client.getStore().searchUserByUin(user.getId());
        client.getUserFace(qqUser, new QQActionListener() {
            public void onActionEvent(QQActionEvent event) {
                switch (event.getType()) {
                    case EVT_OK: {
                        IMEvent imEvent = new IMEvent(IMEventType.USER_FACE_UPDATE);
                        IMUser imUser = new IMUser();
                        imUser.setId(qqUser.getUin());
                        imUser.setAvatar(qqUser.getFace());
                        imEvent.setTarget(imUser);
                        broadcastIMEvent(imEvent);
                    }
                    break;
                    case EVT_ERROR: {
                        //TODO ...
                    }
                }

            }
        });
    }

    protected void broadcastIMEvent(IMEvent event) {
        imApp.onIMEvent(event);
    }

    protected void broadcastIMEvent(IMEventType type, Object target) {
        imApp.onIMEvent(new IMEvent(type, target));
    }
}

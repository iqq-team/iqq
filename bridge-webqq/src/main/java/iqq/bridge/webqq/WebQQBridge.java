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
import iqq.api.bridge.IMApp;
import iqq.api.bridge.IMBridge;
import iqq.api.event.IMEvent;
import iqq.api.event.IMEventDispatcher;
import iqq.api.event.IMEventType;
import iqq.api.event.args.LoginRequest;
import iqq.im.QQActionListener;
import iqq.im.QQNotifyListener;
import iqq.im.WebQQClient;
import iqq.im.actor.SwingActorDispatcher;
import iqq.im.bean.QQStatus;
import iqq.im.event.QQActionEvent;
import iqq.im.event.QQNotifyEvent;

/**
 * Project  : iqq
 * Author   : solosky < solosky772@qq.com >
 * Created  : 5/1/14
 * License  : Apache License 2.0
 */
public class WebQQBridge extends IMEventDispatcher implements IMBridge, QQNotifyListener {
    private WebQQClient client;
    private IMApp imApp;
    @Override
    public void setApp(IMApp imApp) {
        this.imApp = imApp;
    }

   @IMEventHandler(IMEventType.LOGIN_REQUEST)
    private void processLoginRequestEvent(IMEvent imEvent){
       LoginRequest login = (LoginRequest) imEvent.getTarget();

       client = new WebQQClient(login.getUsername(), login.getPassword(),
               this, new SwingActorDispatcher());

       client.login(QQStatus.valueOf(login.getStatus().name()), new QQActionListener() {
           @Override
           public void onActionEvent(QQActionEvent event) {
               //To change body of implemented methods use File | Settings | File Templates.
           }
       });
    }

    @Override
    public void onNotifyEvent(QQNotifyEvent event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

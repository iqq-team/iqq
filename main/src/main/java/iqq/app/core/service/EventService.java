package iqq.app.core.service;
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

import iqq.app.ui.event.UIEvent;
import iqq.app.ui.event.UIEventListener;
import iqq.app.ui.event.UIEventType;

/**
 * Project  : iqq
 * Author   : solosky < solosky772@qq.com >
 * Created  : 5/3/14
 * License  : Apache License 2.0
 */
public interface EventService {

    /***
     * 注册自己感兴趣的事件到事件中心
     * 如果该模块被卸载或者禁用了,请务必取消注册
     * @param intrestedEvents	感兴趣的事件ID，可以是多个
     * @param listener			监听器
     */
    public void register(UIEventType[] intrestedEvents, UIEventListener listener);
    /***
     * 只注销已经注册特定的事件监听器
     * @param intrestedEvents
     * @param listener
     */
    public void unregister(UIEventType[] intrestedEvents, UIEventListener listener);

    /**
     * 注销和指定监听器所有感兴趣的事件
     * @param listener
     */
    public void unregister(UIEventListener listener);


    /***
     * 广播一个事件，所有对这个感兴趣的监听器都会被调用
     * @param event
     */
    public void broadcast(UIEvent event);
}

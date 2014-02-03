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
 * Package  : iqq.app.service
 * File     : EventService.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-3-13
 * License  : Apache License 2.0 
 */
package iqq.app.service;

import iqq.app.core.IMException;
import iqq.app.core.IMService;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventListener;
import iqq.app.event.IMEventType;

/**
 *
 * 事件服务
 * 提供事件的集中注册和分发
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public interface IMEventService extends IMService{
	/***
	 * 注册自己感兴趣的事件到事件中心
	 * 如果该模块被卸载或者禁用了,请务必取消注册
	 * @param intrestedEvents	感兴趣的事件ID，可以是多个
	 * @param listener			监听器
	 */
	public void register(IMEventType[] intrestedEvents, IMEventListener listener);
	/***
	 * 只注销已经注册特定的事件监听器
	 * @param intrestedEvents
	 * @param listener
	 */
	public void unregister(IMEventType[] intrestedEvents, IMEventListener listener);
	
	/**
	 * 注销和指定监听器所有感兴趣的事件
	 * @param listener
	 */
	public void unregister(IMEventListener listener);
	
	
	/***
	 * 广播一个事件，所有对这个感兴趣的监听器都会被调用
	 * @param event
	 */
	public void broadcast(IMEvent event);
	
	/***
	 * 发出一个查询事件
	 * 如果某个模块可以响应这个查询事件，需要把结果放入事件的result属性，
	 * 或者以Key=>Value的形式放入dataMAP里，并且设置queryHandled为true
	 */
	public Object query(IMEvent event) throws IMException;
	
}

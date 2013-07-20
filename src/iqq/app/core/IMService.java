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
 * Project  : WebQQCore
 * Package  : iqq.im.module
 * File     : QQModule.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2012-7-31
 * License  : Apache License 2.0 
 */
package iqq.app.core;



/**
 *
 * QQ服务
 * 
 * 提供和模块与协议无关的公共服务，供模块调用，如定时服务，网络连接服务，异步任务服务等
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public interface IMService extends IMLifeCycle {
	public enum Type{
		EVENT,			//事件服务，负责事件的分发
		RESOURCE,		//资源服务，负责本地资源的获取
		MODULE,			//模块管理服务
		TIMER,			//定时服务
		HTTP,			//HTTP
		TASK,			//异步任务，可以执行比较耗时的操作
		I18N,			//国际化支持
		PROP,			//配置
		SKIN,			//皮肤服务
		ACTION			//Swing Action(Button等)
	}
}

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
 * File     : IMTimerService.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-4-10
 * License  : Apache License 2.0 
 */
package iqq.app.service;

import iqq.app.core.IMService;

/**
 *
 * 定时器服务
 * 执行的方法都是在EventDispatcher线程中执行的
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public interface IMTimerService extends IMService {
	public void setInterval(Runnable runnable, long interval);
	public void setTimeout(Runnable runnable, long delay);
	public void killTimer(Runnable runnable);
}

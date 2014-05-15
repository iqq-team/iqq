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
 * File     : IMTaskService.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-3-25
 * License  : Apache License 2.0 
 */
package iqq.app.core.service;


/**
 * 
 *  异步任务执行
 *  可以把一些可能比较耗时的任务单独放在另外一个线程处理，如IO操作等
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public interface TaskService {
	public void submit(Runnable runnable);
	public void submit(Object target, String method, Object... args);
}

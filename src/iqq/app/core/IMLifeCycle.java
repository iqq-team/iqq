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
 * Package  : iqq.im.core
 * File     : QQLifeCycle.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-2-23
 * License  : Apache License 2.0 
 */
package iqq.app.core;


/**
 *
 * 生命周期管理
 * 实现了这个接口就可以支持对象的重复使用
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public interface IMLifeCycle {
	
	/**
	 * 初始化，在使用之前调用
	 * @param context	初始化失败抛出
	 */
	public void init(IMContext context) throws IMException;
	
	/**
	 * 销毁，在使用完毕之后调用
	 * @throws QQException 销毁失败抛出
	 */
	public void destroy() throws IMException;
}

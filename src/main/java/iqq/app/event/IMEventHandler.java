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
 * Package  : iqq.app.event
 * File     : IMeventHandler.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-3-16
 * License  : Apache License 2.0 
 */
package iqq.app.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * 可以使用这个注解来简化事件的分发
 * 加在方法上，参数为IM事件类型
 * 如 @IMEventHandler(IMEventType.LOGIN_PROGRESS)
 *
 * @author solosky <solosky772@qq.com>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IMEventHandler {
	/**标明这个方法处理的事件类型*/
	IMEventType value();
}

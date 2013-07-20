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
 * Project  : IQQ_V2
 * Package  : iqq.event
 * File     : IMEventListener.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-3-10
 * License  : Apache License 2.0 
 */
package iqq.app.event;

import iqq.app.core.IMException;

/**
 *
 * IMEvent回调方法，需要注册在IMEventHub里面
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public interface IMEventListener {
	public void onIMEvent(IMEvent imEvent) throws IMException;
}

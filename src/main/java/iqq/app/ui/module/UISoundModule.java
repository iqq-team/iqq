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
 * Package  : iqq.app.ui.module
 * File     : UISoundModule.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-3-29
 * License  : Apache License 2.0 
 */
package iqq.app.ui.module;

import iqq.app.core.IMService;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventHandler;
import iqq.app.event.IMEventType;
import iqq.app.module.AbstractModule;
import iqq.app.service.IMResourceService;
import iqq.app.service.IMTaskService;
import iqq.app.util.AePlayWave;

/**
 *
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class UISoundModule extends AbstractModule {

	private long interval;
	
	@IMEventHandler(IMEventType.RECV_CHAT_MSG)
	protected void processIMRecvMsg(IMEvent event){
		if((System.currentTimeMillis() - interval) < 500) {
			return ;
		}
		IMTaskService tasks = getContext().getSerivce(IMService.Type.TASK);
		IMResourceService resources = getContext().getSerivce(IMService.Type.RESOURCE);
		tasks.submit(new AePlayWave(resources.getFile("res/sound/office/msg.wav")));
		interval = System.currentTimeMillis();
	}

}

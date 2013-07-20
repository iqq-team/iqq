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
 * File     : AbstractModule.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2012-8-10
 * License  : Apache License 2.0 
 */
package iqq.app.module;

import iqq.app.core.IMContext;
import iqq.app.core.IMException;
import iqq.app.core.IMModule;
import iqq.app.core.IMService;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventHandlerProxy;
import iqq.app.event.IMEventType;
import iqq.app.service.IMEventService;

/**
 *
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class AbstractModule implements IMModule {
	private IMContext context;

	@Override
	public void init(IMContext context) throws IMException{
		this.context = context;
		IMEventHandlerProxy.register(this, context);
	}

	@Override
	public void destroy() throws IMException{
		IMEventHandlerProxy.unregister(this);
	}
	
	protected IMContext getContext(){
		return this.context;
	}
	
	protected void broadcastIMEvent(IMEvent event){
		IMEventService eventHub = context.getSerivce(IMService.Type.EVENT);
		eventHub.broadcast(event);
	}
	
	protected void broadcastIMEvent(IMEventType type, Object target){
		IMEventService eventHub = context.getSerivce(IMService.Type.EVENT);
		eventHub.broadcast(new IMEvent(type, target));
	}
	
	protected <T> T queryIMEvent(IMEvent event) throws IMException{
		IMEventService eventHub = context.getSerivce(IMService.Type.EVENT);
		return (T) eventHub.query(event);
	}
	
}

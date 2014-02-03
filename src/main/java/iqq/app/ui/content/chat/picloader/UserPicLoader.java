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
 * Package  : iqq.app.ui.content.chat.picloader
 * File     : CFacePicLoader.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-4-20
 * License  : Apache License 2.0 
 */
package iqq.app.ui.content.chat.picloader;

import iqq.app.core.IMService;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventHandler;
import iqq.app.event.IMEventType;
import iqq.app.service.IMResourceService;
import iqq.im.bean.content.CFaceItem;

import java.io.File;

/**
 *
 * 用户图片，其实也是CFace，但获取的地址和方式不同
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class UserPicLoader extends AbstractPicLoader{
	private static final long serialVersionUID = 1L;
	private String guid;
	private long msgId;
	private long fromUin;
	
	public UserPicLoader(File file){
		this.localFile = file.getAbsolutePath();
	}
	
	public UserPicLoader(CFaceItem item, long msgId, long fromUin){
		this.guid = item.getFileName();
		this.msgId = msgId;
		this.fromUin = fromUin;
	}

	@Override
	protected void doLoadFromServer() throws Exception {
		IMEvent event = new IMEvent(IMEventType.GET_USERPIC_REQUEST);
		String tmpFile = genLocalFileName();
		
		IMResourceService resources = context.getSerivce(IMService.Type.RESOURCE);
		
		event.setTarget(this);
		event.putData("guid", guid);
		event.putData("fromUin", fromUin);
		event.putData("msgId", msgId);
		event.putData("cachedFile", tmpFile);
		event.putData("localFile", resources.getUserFile("pic/" + tmpFile));
		broadcastIMEvent(event);
		
	}

	@Override
	protected void doSendToServer() throws Exception {
		throw new UnsupportedOperationException("not supported!!");
	}
	
	@IMEventHandler(IMEventType.GET_USERPIC_PROGRESS)
	protected void processGetOffpicProgress(IMEvent event){
		processGetPicProgress(event);
	}
	
	@IMEventHandler(IMEventType.GET_USERPIC_ERROR)
	protected void processGetOffpicError(IMEvent event){
		processGetPicError(event);
	}
	
	@IMEventHandler(IMEventType.GET_USERPIC_SUCCESS)
	protected void processGetOffpicSuccess(IMEvent event){
		processGetPicSuccess(event);
	}

	@Override
	protected boolean isMyEvent(IMEvent event) {
		IMEvent related = event.getRelatedEvent();
		return related.getTarget() == this;
	}

}

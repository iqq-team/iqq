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
 * File     : OffPicLoader.java
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
import iqq.im.bean.content.OffPicItem;

import java.io.File;

/**
 * 离线图片加载器
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class OffPicLoader extends AbstractPicLoader{
	private static final long serialVersionUID = 1L;
	private long fromUin;
	public OffPicLoader(File file, long fromUin){
		this.fromUin = fromUin;
		this.localFile = file.getAbsolutePath();
	}
	
	public OffPicLoader(OffPicItem item, long fromUin){
		this.fromUin = fromUin;
		this.picItem = item;
	}
	
	
	public long getFromUin() {
		return fromUin;
	}

	@Override
	protected void doLoadFromServer() throws Exception {
		IMEvent event = new IMEvent(IMEventType.GET_OFFPIC_REQUEST);
		String tmpFile = genLocalFileName();
		
		IMResourceService resources = context.getSerivce(IMService.Type.RESOURCE);
		event.setTarget(this);
		event.putData("offpic", picItem);
		event.putData("fromUin", fromUin);
		event.putData("cachedFile", tmpFile);
		event.putData("localFile", resources.getUserFile("pic/" + tmpFile));
		broadcastIMEvent(event);
	}
	
	@IMEventHandler(IMEventType.GET_OFFPIC_PROGRESS)
	protected void processGetOffpicProgress(IMEvent event){
		processGetPicProgress(event);
	}
	
	@IMEventHandler(IMEventType.GET_OFFPIC_ERROR)
	protected void processGetOffpicError(IMEvent event){
		processGetPicError(event);
	}
	
	@IMEventHandler(IMEventType.GET_OFFPIC_SUCCESS)
	protected void processGetOffpicSuccess(IMEvent event){
		processGetPicSuccess(event);
	}
	
	@IMEventHandler(IMEventType.UPLOAD_OFFPIC_PROGRESS)
	protected void processUploadOffpicProgress(IMEvent event){
		processUploadPicProgress(event);
	}
	
	@IMEventHandler(IMEventType.UPLOAD_OFFPIC_ERROR)
	protected void processUploadOffpicError(IMEvent event){
		processUploadPicError(event);
	}
	
	@IMEventHandler(IMEventType.UPLOAD_OFFPIC_SUCCESS)
	protected void processUploadOffpicSuccess(IMEvent event){
		processUploadPicSuccess(event);
		picItem = (OffPicItem) event.getTarget();
	}
	
	@Override
	protected void doSendToServer() throws Exception {
		IMEvent event = new IMEvent(IMEventType.UPLOAD_OFFPIC_REQUEST);
		event.setTarget(this);
		event.putData("fromUin", fromUin);
		event.putData("localFile", new File(localFile));
		broadcastIMEvent(event);
	}
	
	protected boolean isMyEvent(IMEvent event){
		IMEvent related = event.getRelatedEvent();
		return related.getTarget() == this;
	}
}

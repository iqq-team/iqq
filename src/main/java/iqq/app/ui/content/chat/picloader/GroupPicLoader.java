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
 * File     : GroupPicLoader.java
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
 * 群图片，其实也是CFace，只不过上传接口不同
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class GroupPicLoader extends AbstractPicLoader {
	private static final long serialVersionUID = 1L;
	private long groupId;
	private long fromUin;
	private int type;
	
	
	public GroupPicLoader(File file, long groupId, long fromUin){
		this.localFile = file.getAbsolutePath();
	}
	
	public GroupPicLoader(CFaceItem item, int type, long groupId, long fromUin){
		this.groupId = groupId;
		this.type = type;
		this.fromUin = fromUin;
		this.picItem = item;
	}

	@Override
	protected void doLoadFromServer() throws Exception {
		IMEvent event = new IMEvent(IMEventType.GET_GROUPPIC_REQUEST);
		String tmpFile = genLocalFileName();
		
		IMResourceService resources = context.getSerivce(IMService.Type.RESOURCE);
		
		CFaceItem item = (CFaceItem) picItem;
		event.setTarget(this);
		event.putData("fileId", item.getFileId());
		event.putData("fromUin", fromUin);
		event.putData("server", item.getServer());
		event.putData("fileName", item.getFileName());
		event.putData("groupId", groupId);
		event.putData("type", type);
		event.putData("cachedFile", tmpFile);
		event.putData("localFile", resources.getUserFile("pic/" + tmpFile));
		broadcastIMEvent(event);
		
	}

	@Override
	protected void doSendToServer() throws Exception {
		IMEvent event = new IMEvent(IMEventType.UPLOAD_CFACE_REQUEST);
		event.setTarget(this);
		event.putData("fromUin", fromUin);
		event.putData("localFile", new File(localFile));
		broadcastIMEvent(event);
	}
	
	@IMEventHandler(IMEventType.GET_GROUPPIC_PROGRESS)
	protected void processGetOffpicProgress(IMEvent event){
		processGetPicProgress(event);
	}
	
	@IMEventHandler(IMEventType.GET_GROUPPIC_ERROR)
	protected void processGetOffpicError(IMEvent event){
		processGetPicError(event);
	}
	
	@IMEventHandler(IMEventType.GET_GROUPPIC_SUCCESS)
	protected void processGetOffPicSuccess(IMEvent event){
		processGetPicSuccess(event);
	}
	
	@IMEventHandler(IMEventType.UPLOAD_CFACE_PROGRESS)
	protected void processUploadCPicProgress(IMEvent event){
		processUploadPicProgress(event);
	}
	
	@IMEventHandler(IMEventType.UPLOAD_CFACE_ERROR)
	protected void processUploadCPicError(IMEvent event){
		processUploadPicError(event);
	}
	
	@IMEventHandler(IMEventType.UPLOAD_CFACE_SUCCESS)
	protected void processUploadCPicSuccess(IMEvent event){
		processUploadPicSuccess(event);
		if(isMyEvent(event)){
			this.picItem = (CFaceItem) event.getTarget();
		}
	}
	
	@Override
	protected boolean isMyEvent(IMEvent event) {
		IMEvent related = event.getRelatedEvent();
		return related.getTarget() == this;
	}
}

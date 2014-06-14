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
package iqq.app.ui.frame.panel.chat.picloader;

import iqq.api.bean.IMEntity;
import iqq.api.bean.content.IMPictureItem;
import iqq.api.event.IMEvent;

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
	private File file;
	private IMEntity owner;

	public UserPicLoader(IMPictureItem item, IMEntity owner){
		this.file = item.getFile();
        this.owner = owner;
	}

	@Override
	protected void doLoadFromServer() throws Exception {

		
	}

	@Override
	protected void doSendToServer() throws Exception {
		throw new UnsupportedOperationException("not supported!!");
	}

    @Override
    protected boolean isMyEvent(IMEvent event) {
        return false;
    }


    @Override
    public void sendToServer() {

    }
}

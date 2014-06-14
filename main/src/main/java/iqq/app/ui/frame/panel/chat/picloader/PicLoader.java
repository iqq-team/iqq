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
 * File     : PicLoader.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-4-20
 * License  : Apache License 2.0 
 */
package iqq.app.ui.frame.panel.chat.picloader;

 import iqq.api.bean.content.IMContentItem;

import java.awt.*;
import java.io.Serializable;

/**
 *
 * 图片加载或者上传接口
 * 可以是加载网络上的图片，也可以是加载本地图片
 * 需序列话保存在本地，实现是注意考虑序列号问题
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public interface PicLoader extends Serializable {
	public void loadPic();
	public void sendToServer();
	public void addListener(PicLoadListener listener);
	public void removeListener(PicLoadListener listener);
	public IMContentItem getContentItem();
	public PicLoadState getState();
	public Image getPic();
}

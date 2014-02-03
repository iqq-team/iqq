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
 * Package  : iqq.app.bean
 * File     : UICategory.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-4-7
 * License  : Apache License 2.0 
 */
package iqq.app.bean;

import iqq.im.bean.QQCategory;

import java.awt.image.BufferedImage;

/**
 * 
 * 分组
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class UICategory implements UINamedObject {
	private QQCategory category;
	

	public UICategory(QQCategory category) {
		this.category = category;
	}

	@Override
	public String getName() {
		return category.getName() + " [" + category.getOnlineCount() +"/" + category.getBuddyCount() + "]";
	}

	@Override
	public String getSubname() {
		return null;
	}

	@Override
	public String getExtra() {
		return null;
	}

	@Override
	public BufferedImage getIcon() {
		return null;
	}

	@Override
	public Object getEntity() {
		return null;
	}

}

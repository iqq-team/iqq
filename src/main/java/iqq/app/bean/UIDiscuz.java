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
 * File     : UIDiscuz.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-4-5
 * License  : Apache License 2.0 
 */
package iqq.app.bean;

import iqq.app.util.SkinUtils;
import iqq.im.bean.QQDiscuz;

import java.awt.image.BufferedImage;

import org.apache.commons.lang3.StringUtils;

import com.alee.utils.ImageUtils;

/**
 *
 * 讨论组
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class UIDiscuz implements UINamedObject{
	private QQDiscuz discuz;
	
	public UIDiscuz(QQDiscuz discuz) {
		this.discuz = discuz;
	}

	@Override
	public String getName() {
		return StringUtils.isNotEmpty(discuz.getName()) ? discuz.getName() : "No Title";
	}

	@Override
	public String getSubname() {
		return "";
	}

	@Override
	public BufferedImage getIcon() {
		return ImageUtils.copy(SkinUtils.getImageIcon("discuzIcon").getImage());
	}

	@Override
	public Object getEntity() {
		return discuz;
	}

	@Override
	public String getExtra() {
		return "";
	}

}

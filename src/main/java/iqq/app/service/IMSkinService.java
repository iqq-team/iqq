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
 * Package  : iqq.app.service
 * File     : IMPropService.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-3-13
 * License  : Apache License 2.0 
 */
package iqq.app.service;

import iqq.app.core.IMService;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ImageIcon;

import com.alee.extended.painter.Painter;

/**
 * 
 * 配置相关服务
 * 
 * @author solosky <solosky772@qq.com>
 * 
 */
public interface IMSkinService extends IMService {
	public Painter<?> getPainter(Type type, String key);

	public ImageIcon getImageIcon(String key);
	
	public BufferedImage getBufferedImage(String key);
	
	public ImageIcon getImageIcon(String key, Object... params);

	public void setString(Type type, String key, String value);
	
	public Font getFont();

	public enum Type {
		/**
		 * 普通图标
		 */
		ICON("icon"),
		/**
		 * 九格图片
		 */
		NPICON("npicon"),
		/**
		 * XML资源文件
		 */
		RESOURCE("resource");
		private String name;

		Type(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public static Type valueOfRaw(String txt) {
			if (txt.equals(NPICON.getName())) {
				return NPICON;
			} else if (txt.equals(RESOURCE.getName())) {
				return RESOURCE;
			} else {
				return ICON;
			}
		}
	}
}

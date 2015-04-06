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
 * Package  : iqq.app.ui.content.chat.rich
 * File     : UIResPicItem.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-4-9
 * License  : Apache License 2.0 
 */
package iqq.app.ui.frame.panel.chat.rich;


 import iqq.app.core.context.IMContext;
 import iqq.app.core.service.SkinService;
 import iqq.app.core.service.impl.SkinServiceImpl;

 import javax.swing.*;

/**
 *
 * 插入程序皮肤本地资源
 * 通常这些图片都较小，并且都有缓存，直接添加即可
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class UISkinPicItem extends UIBasicItem{
	private static final long serialVersionUID = 1L;
	private String picName;
	public UISkinPicItem(String picName){
		this.picName = picName;
	}
	
	@Override
	public void insertTo(JTextPane pane) throws Exception {
        SkinService skins = IMContext.getBean(SkinServiceImpl.class);
        ImageIcon pic = skins.getIconByKey(picName);
		pane.insertComponent(new UISkinComponent(pic));
	}
	
	private class UISkinComponent extends JLabel implements UIRichComponent{
		private static final long serialVersionUID = 1L;
		UISkinComponent(ImageIcon pic){
			super();
			setIcon(pic);
		}
		@Override
		public UIRichItem getRichItem() {
			return UISkinPicItem.this;
		}
	}
}

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
 * File     : UIFaceItem.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-4-8
 * License  : Apache License 2.0 
 */
package iqq.app.ui.frame.panel.chat.rich;

 import javax.swing.*;

/**
 *
 * 表情对象
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class UIFaceItem extends UIBasicItem{
	private static final long serialVersionUID = 1L;
	private int faceId;

	public UIFaceItem(int faceId){
		this.faceId = faceId;
	}
	
	public int getFaceId() {
		return faceId;
	}

	@Override
	public void insertTo(JTextPane pane) throws Exception {
		//FaceIcon icon = FaceWindow.getFaceIconById(faceId);
		//pane.insertComponent(new UIFaceComponent(icon));
	}

	private class UIFaceComponent extends JLabel implements UIRichComponent{
		/*
        UIFaceComponent(FaceIcon icon){
			super();
			setIcon(icon.getIcon());
		}
		*/
		@Override
		public UIRichItem getRichItem() {
			return UIFaceItem.this;
		}
	}

}

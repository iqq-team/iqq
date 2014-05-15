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
 * File     : UILinkItem.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-4-9
 * License  : Apache License 2.0 
 */
package iqq.app.ui.frame.panel.chat.rich;

 import javax.swing.*;
 import javax.swing.text.Document;
 import javax.swing.text.SimpleAttributeSet;
 import javax.swing.text.StyleConstants;
 import javax.swing.text.html.HTML;
 import java.awt.*;

/**
 *
 * 超链接组件
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class UILinkItem extends UIBasicItem {
	private static final long serialVersionUID = 1L;
	private String link;
	private transient Action action;
	
	public UILinkItem(String link){
		this.link = link;
	}
	public UILinkItem(String link,  Action action){
		this.link = link;
		this.action = action;
	}
	@Override
	public void insertTo(final JTextPane pane) throws Exception {
		Document doc = pane.getDocument();
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setUnderline(attrs, true);
		StyleConstants.setForeground(attrs, Color.BLUE);// unvisited color
		// StyleConstants.setFontSize(null, 13);
		attrs.addAttribute(HTML.Attribute.HREF, link);
		if(action != null){
			attrs.addAttribute("action", action);
			attrs.addAttribute("self", this);
		}
		doc.insertString(doc.getLength(), link, attrs);
	}
	
	public String getLink() {
		return link;
	}
}

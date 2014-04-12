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
 * Package  : iqq.app.ui.content.chat.conversation
 * File     : UIMsgUtils.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-4-9
 * License  : Apache License 2.0 
 */
package iqq.app.ui.content.chat.conversation;

import iqq.app.bean.UIDialog;
import iqq.app.bean.UIMsg;
import iqq.app.ui.bean.FaceIcon;
import iqq.app.ui.content.chat.picloader.PicLoader;
import iqq.app.ui.content.chat.picloader.PicLoaderFactory;
import iqq.app.ui.content.chat.rich.UIFaceItem;
import iqq.app.ui.content.chat.rich.UILinkItem;
import iqq.app.ui.content.chat.rich.UILoaderPicItem;
import iqq.app.ui.content.chat.rich.UIRichItem;
import iqq.app.ui.content.chat.rich.UITextItem;
import iqq.im.bean.QQAccount;
import iqq.im.bean.QQBuddy;
import iqq.im.bean.QQDiscuz;
import iqq.im.bean.QQGroup;
import iqq.im.bean.QQHalfStranger;
import iqq.im.bean.QQMsg;
import iqq.im.bean.QQStranger;
import iqq.im.bean.QQUser;
import iqq.im.bean.content.ContentItem;
import iqq.im.bean.content.FaceItem;
import iqq.im.bean.content.TextItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 工具类，实现UIMsg 和 QQMsg的互转
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class UIMsgUtils {
	private static final Logger LOG = LoggerFactory.getLogger(UIMsgUtils.class);
	private static final String LINK_REGXP = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	public static QQMsg toQQMsg(UIMsg uiMsg, QQAccount self) throws Exception{
		QQMsg qqMsg = new QQMsg();
		Object target = uiMsg.getOwner();
		if (target instanceof QQBuddy || target instanceof QQHalfStranger) {
			qqMsg.setType(QQMsg.Type.BUDDY_MSG);
			qqMsg.setTo((QQUser) target);
		} else if (target instanceof QQGroup) {
			qqMsg.setType(QQMsg.Type.GROUP_MSG);
			qqMsg.setGroup((QQGroup) target);
		} else if (target instanceof QQDiscuz) {
			qqMsg.setType(QQMsg.Type.DISCUZ_MSG);
			qqMsg.setDiscuz((QQDiscuz) target);
		}else if(target instanceof QQStranger){
			qqMsg.setType(QQMsg.Type.SESSION_MSG);
			qqMsg.setTo((QQUser) target);
		}
		
		qqMsg.setFrom(self);
		qqMsg.setContentList(toContentItems(uiMsg));
		qqMsg.getContentList().add(uiMsg.getFont());
		qqMsg.setDate(new Date());
		return qqMsg;
	}
	
	public static UIMsg toUIMsg(QQMsg qqMsg, QQAccount self){
		UIMsg uiMsg = new UIMsg();
		uiMsg.setMsgId(UIMsg.genMsgId(qqMsg.getDate()));
		uiMsg.setDate(qqMsg.getDate());
		if(qqMsg.getFrom().getUin() == self.getUin()){
			uiMsg.setSender(self);
			uiMsg.setState(UIMsg.State.PENDING);
			uiMsg.setDirection(UIMsg.Direction.SEND);
		}else{
			uiMsg.setSender(qqMsg.getFrom());
			uiMsg.setState(UIMsg.State.UNREAD);
			uiMsg.setDirection(UIMsg.Direction.RECV);
		}
		
		switch(qqMsg.getType()){
			case BUDDY_MSG:{
				uiMsg.setDialogType(UIDialog.Type.BUDDY_CHAT);
				uiMsg.setOwner(uiMsg.getDirection() == UIMsg.Direction.SEND ? qqMsg.getTo() : qqMsg.getFrom());
			} break;
			
			case SESSION_MSG: {
				uiMsg.setDialogType(UIDialog.Type.STRANGER_CHAT);
				uiMsg.setOwner(uiMsg.getDirection() == UIMsg.Direction.SEND ? qqMsg.getTo() : qqMsg.getFrom());
			}break;
			
			case DISCUZ_MSG: {
				uiMsg.setDialogType(UIDialog.Type.DISCUZ_CHAT);
				uiMsg.setOwner(qqMsg.getDiscuz());
			}break;
			
			case GROUP_MSG: {
				uiMsg.setDialogType(UIDialog.Type.GROUP_CHAT);
				uiMsg.setOwner(qqMsg.getGroup());
			}
		}
		
		uiMsg.setCategory(UIMsg.Category.CHAT);
		uiMsg.setContents(toRichItems(qqMsg));
		return uiMsg;
	}
	
	public static UIDialog.Type getDialogType(Object obj){
			if(obj instanceof QQBuddy){
				return UIDialog.Type.BUDDY_CHAT;
			}else if(obj instanceof QQStranger){
				return UIDialog.Type.STRANGER_CHAT;
			}else if(obj instanceof QQGroup){
				return UIDialog.Type.GROUP_CHAT;
			}else if(obj instanceof QQDiscuz){
				return UIDialog.Type.DISCUZ_CHAT;
			}else {
				throw new IllegalArgumentException("unknown object type: " + obj);
			}
	}
	
	public static List<ContentItem> toContentItems(UIMsg msg) throws Exception{
		List<ContentItem> contentItems = new ArrayList<ContentItem>();
		for(UIRichItem item: msg.getContents()){
			if(item instanceof UITextItem){
				contentItems.add(new TextItem(((UITextItem) item).getText()));
			}else if(item instanceof UILoaderPicItem){
				contentItems.add(((UILoaderPicItem) item).getPicLoader().getContentItem());
			}else if(item instanceof UIFaceItem){
				contentItems.add(new FaceItem(((UIFaceItem) item).getFaceId()));
			}else if(item instanceof UILinkItem){
				contentItems.add(new TextItem(((UILinkItem) item).getLink()));
			}else {
				LOG.warn("toContentItems: unsupported UIRichItem " + item);
			}
		}
		return contentItems;
	}
	
	public static List<UIRichItem> parseLink(String text){
		text = text.replaceAll("\r", "\n");
		List<UIRichItem> items = new ArrayList<UIRichItem>();
		Pattern pt = Pattern.compile(LINK_REGXP);
		Matcher mc = pt.matcher(text);
		int current = 0;
		while(mc.find()){
			if(mc.start() > current){
				items.add(new UITextItem(text.substring(current, mc.start())));
			}
			current = mc.end();
			items.add(new UILinkItem(mc.group(0)));
		}
		if(current < text.length()){
			items.add(new UITextItem(text.substring(current)));
		}
		return items;
	}
	
	public static List<UIRichItem> toRichItems(QQMsg msg) {
		ArrayList<UIRichItem> richItems = new ArrayList<UIRichItem>();
		for (ContentItem item : msg.getContentList()) {
			switch (item.getType()) {
			case FACE: {
				FaceItem faceItem = (FaceItem) item;
				FaceIcon faceIcon = FaceWindow
						.getFaceIconById(faceItem.getId());
				if (faceIcon != null) {
					richItems.add(new UIFaceItem(faceItem.getId()));
				} else {
					richItems.add(new UITextItem("[表情: " + faceItem.getId()+ "]"));
				}
			}
				break;

			case TEXT: {
				TextItem textItem = (TextItem) item;
				richItems.addAll(parseLink(textItem.getContent()));
			} break;
			
			case OFFPIC: 
			case CFACE: {
				PicLoader picLoader = PicLoaderFactory.createLoader(item, msg);
				richItems.add(new UILoaderPicItem(picLoader));
			} break;
			}
		}
		return richItems;
	}
}

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
 * File     : UIMsg.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-4-8
 * License  : Apache License 2.0 
 */
package iqq.app.bean;

import iqq.app.ui.content.chat.rich.UIRichItem;
import iqq.im.bean.QQUser;
import iqq.im.bean.content.FontItem;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 *
 * 聊天消息，直接在聊天框内展示的消息，可以直接存储
 * 比QQMsg存储了更多的状态信息
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class UIMsg implements Serializable{
	private static final long serialVersionUID = 1L;
	/**主题ID，标志这个消息归属的对话*/
	private String dialogId;
	/**所属对话类别*/
	private UIDialog.Type dialogType;
	/**消息ID，全局唯一，标志唯一条消息*/
	private String msgId;
	/**消息发送者*/
	private QQUser sender;
	/**消息内容*/
	private List<UIRichItem> contents;
	/**消息接收或者发送时间*/
	private Date date;
	/**消息状态*/
	private State state;
	/**消息方向*/
	private Direction direction;
	/**消息组别*/
	private Category category;
	/**字体信息*/
	private FontItem font;
	/**消息拥有者，群还是讨论组*/
	private transient Object owner;
	
	public static String genMsgId(){
		return genMsgId(new Date());
	}
	
	public static String genMsgId(Date date){
		long time = date.getTime();
		long rand = new Random().nextInt(1000);
		return String.format("%d.%03d", time, rand);
	}
	
	/**
	 * @return the dialogId
	 */
	public String getDialogId() {
		return dialogId;
	}

	/**
	 * @param dialogId the dialogId to set
	 */
	public void setDialogId(String dialogId) {
		this.dialogId = dialogId;
	}
	

	/**
	 * @return the dialogType
	 */
	public UIDialog.Type getDialogType() {
		return dialogType;
	}

	/**
	 * @param dialogType the dialogType to set
	 */
	public void setDialogType(UIDialog.Type dialogType) {
		this.dialogType = dialogType;
	}

	/**
	 * @return the msgId
	 */
	public String getMsgId() {
		return msgId;
	}

	/**
	 * @param msgId the msgId to set
	 */
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	/**
	 * @return the sender
	 */
	public QQUser getSender() {
		return sender;
	}

	/**
	 * @param sender the sender to set
	 */
	public void setSender(QQUser sender) {
		this.sender = sender;
	}

	/**
	 * @return the contents
	 */
	public List<UIRichItem> getContents() {
		return contents;
	}

	/**
	 * @param contents the contents to set
	 */
	public void setContents(List<UIRichItem> contents) {
		this.contents = contents;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the state
	 */
	public State getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * @return the direction
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * @param direction the direction to set
	 */
	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	/**
	 * @return the category
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(Category category) {
		this.category = category;
	}

	/**
	 * @return the font
	 */
	public FontItem getFont() {
		return font;
	}

	/**
	 * @param font the font to set
	 */
	public void setFont(FontItem font) {
		this.font = font;
	}

	/**
	 * @return the owner
	 */
	public Object getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(Object owner) {
		this.owner = owner;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((msgId == null) ? 0 : msgId.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UIMsg other = (UIMsg) obj;
		if (msgId == null) {
			if (other.msgId != null)
				return false;
		} else if (!msgId.equals(other.msgId))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UIMsg [dialogId=" + dialogId + ", dialogType=" + dialogType
				+ ", msgId=" + msgId + ", state=" + state + ", direction="
				+ direction + ", category=" + category + "]";
	}


	/***
	 * 
	 * 消息状态枚举
	 *
	 * @author solosky <solosky772@qq.com>
	 *
	 */
	public enum State{
		/**消息已经发送*/
		SENT,
		/**消息*/
		PENDING,
		/**发送失败*/
		ERROR,
		/**已读*/
		READ,
		/**未读*/
		UNREAD
	}
	
	/***
	 * 
	 * 消息方向，接收还是发送
	 *
	 * @author solosky <solosky772@qq.com>
	 *
	 */
	public enum Direction{
		/**接收到的消息*/
		SEND,
		/**发送的消息*/
		RECV
	}
	
	/***
	 * 
	 * 消息类型枚举
	 *
	 * @author solosky <solosky772@qq.com>
	 *
	 */
	public enum Category{
		/**普通的聊天消息*/
		CHAT,
		/**信息提示消息*/
		INFO,
	}
}

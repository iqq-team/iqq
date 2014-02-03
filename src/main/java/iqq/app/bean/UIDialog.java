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
 * File     : IMDialog.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-4-10
 * License  : Apache License 2.0 
 */
package iqq.app.bean;

import java.io.Serializable;

/**
 * 
 * Dialog是指和好友，群，讨论组消息的容器 
 * dialogId命名规则
 *  1. 好友对话 (QQBuddy.{QQ}) 
 *  2. 群对话 (QQGroup.{GID}) 
 *  3. 讨论组对话(QQDiscuz.{DID}) 
 *  4. 临时对话(QQUser.{QQ}) 
 *  5.系统广播(QQSystemNotify) 
 *  6. 群通知 (QQGroupNotify) 
 *  7. 好友通知 (QQBudyNotify)
 *  其中5，6，7是系统默认的，必须登陆成功后创建好
 * 
 * @author solosky <solosky772@qq.com>
 * 
 */
public class UIDialog implements Serializable{
	private static final long serialVersionUID = 1L;
	/***
	 * 对话ID属性，对于不同类型消息,见上面注释
	 */
	private String id;

	/***
	 * 对话标题， 对于不同消息，含义不同 好友消息，临时会话: 对方昵称 群消息: 群名字 讨论组: 讨论组 系统消息: 固定为"系统消息"
	 */
	private String name;
	/** 头像数据 */
	private byte[] face;
	/** 对话类型，使用QQMsg.Type枚举的序号值 */
	private Type type;
	/** 最后一条消息 */
	private UIMsg lastMsg;
	/** 消息总数 */
	private long msgCount;
	/** 未读消息总数 */
	private long unreadCount;

	public enum Type {
		BUDDY_CHAT, STRANGER_CHAT, GROUP_CHAT, DISCUZ_CHAT, SYSTEM_NOTIFY, BUDDY_NOTIFY, GROUP_NOTIFY
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the face
	 */
	public byte[] getFace() {
		return face;
	}

	/**
	 * @param face
	 *            the face to set
	 */
	public void setFace(byte[] face) {
		this.face = face;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * @return the lastMsg
	 */
	public UIMsg getLastMsg() {
		return lastMsg;
	}

	/**
	 * @param lastMsg
	 *            the lastMsg to set
	 */
	public void setLastMsg(UIMsg lastMsg) {
		this.lastMsg = lastMsg;
	}

	/**
	 * @return the msgCount
	 */
	public long getMsgCount() {
		return msgCount;
	}

	/**
	 * @param msgCount
	 *            the msgCount to set
	 */
	public void setMsgCount(long msgCount) {
		this.msgCount = msgCount;
	}

	/**
	 * @return the unreadCount
	 */
	public long getUnreadCount() {
		return unreadCount;
	}

	/**
	 * @param unreadCount
	 *            the unreadCount to set
	 */
	public void setUnreadCount(long unreadCount) {
		this.unreadCount = unreadCount;
	}

	public void incUnreadCount() {
		this.unreadCount++;
	}

	public void incMsgCount() {
		this.msgCount++;
	}
}

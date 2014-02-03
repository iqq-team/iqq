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
 * File     : PicLoaderFactory.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-4-20
 * License  : Apache License 2.0 
 */
package iqq.app.ui.content.chat.picloader;

import iqq.im.bean.QQAccount;
import iqq.im.bean.QQDiscuz;
import iqq.im.bean.QQGroup;
import iqq.im.bean.QQMsg;
import iqq.im.bean.QQUser;
import iqq.im.bean.content.CFaceItem;
import iqq.im.bean.content.ContentItem;
import iqq.im.bean.content.OffPicItem;

import java.io.File;

/**
 *
 * 工厂类，根据QQMsg和ContentItem来创建Loader
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class PicLoaderFactory {
	/***
	 * 通过消息中的一个对象来创建PicLoader
	 * @param item		CFaceItem或OffPicItem,  其他抛出异常 IllegalArgumentException
	 * @param msg		接收到的QQ消息对象
	 * @return
	 */
	public static PicLoader createLoader(ContentItem item, QQMsg msg){
		if(item instanceof OffPicItem){
			return new OffPicLoader((OffPicItem) item, msg.getFrom().getUin());
		}else if(item instanceof CFaceItem){
			CFaceItem cface = (CFaceItem) item;
			if(msg.getType() == QQMsg.Type.BUDDY_MSG){
				return new UserPicLoader(cface, msg.getId(), msg.getFrom().getUin());
			}else if(msg.getType() == QQMsg.Type.GROUP_MSG){
				return new GroupPicLoader(cface, 0, msg.getGroup().getCode(), msg.getFrom().getUin());
			}else if(msg.getType() == QQMsg.Type.DISCUZ_MSG){
				return new GroupPicLoader(cface, 1, msg.getDiscuz().getDid(), msg.getFrom().getUin());
			}
		}
		throw new IllegalArgumentException("invalid content item:" + item);
	}
	
	/***
	 * 通过要发送消息的对象来创建对应的PicLoader
	 * @param picFile			图片文件
	 * @param target			发送对象，QQUser, QQGroup, QQDiscuz， 其他抛出异常 IllegalArgumentException
	 * @param self				用户自己对象
	 * @return
	 */
	public static PicLoader createLoader(File picFile, Object target, QQAccount self){
		if(target instanceof QQUser){
			return new OffPicLoader(picFile, self.getUin());
		}else if(target instanceof QQGroup){
			return new GroupPicLoader(picFile, ((QQGroup) target).getCode(), self.getUin());
		}else if(target instanceof QQDiscuz){
			return new GroupPicLoader(picFile, ((QQDiscuz) target).getDid(), self.getUin());
		}
		throw new IllegalArgumentException("invalid target:" + target);
	}
}

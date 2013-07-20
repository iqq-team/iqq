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
 * File     : IMDataService.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-3-25
 * License  : Apache License 2.0 
 */
package iqq.app.service;

import iqq.app.core.IMService;
import iqq.im.bean.QQMsg;
import iqq.im.bean.QQUser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 
 * QQ数据存储服务
 * 
 * 主要存放和登陆用户相关的数据 , 如
 * 2. QQ号和用户的对应关系
 * 3. QQ头像
 * 4. 聊天发送的图片
 * 5. 接收传输的文件
 * 6. 聊天记录
 * 7. 用户对应真实的QQ号记录
 * 
 * 在程序根目录下，有一个user目录，该目录下按用户uin分别存放用户的信息
 * 设计的目录结构如下（以后可能会有改动）
 *   └─1070772010
 *	    ├─db	[存放数据库文件]
 *	    │      msg.db	[聊天消息记录数据]
 *	    │      user.db	[好友的基本信息缓存数据]
 *	    ├─face	[用户头像文件]
 *	    ├─pic	[聊天接收的图片]
 *	    └─recv	[接收的文件]
 *
 * 数据库底层使用嵌入数据库mapDb存储，KV数据库使用比关系型数据库要方便点
 * 
 * 因为WebQQ协议把QQ号做了一下变换，所以作为查询用户的数据不再是UIN，而是计算出来的一个HASH值
 * hash计算方法 = md5 (category_name/group_name + nickname + mark/card + vip_level + is_vip)
 * 这种计算方法特征：
 * 1. 利用第一次获取的信息即可建立到用户的映射，无需反复请求真实qq号 (反复请求可能会出验证码)
 * 2. hash计算方法可能存在冲突，即多名用户得到相同HASH值，但冲突率应该可以达到10%以下
 * 3. 同一个用户可能存在多个HASH，(比如用户更改了昵称)
 * 
 * 所以Hash适合做用户头像和普通数据缓存，不适合作为聊天记录的索引（聊天记录还需用真实的uin）
 * 
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public interface IMDataService extends IMService{
	public void setActiveUin(long uin);
	public String hashUser(QQUser user);
	
	public BufferedImage findFace(String hash);
	public void putFace(String hash, BufferedImage face);
	
	public BufferedImage findPic(String hash);
	public void putPic(String hash, BufferedImage img);
	 
	public InputStream findRecv(String hash);
	public void putRecv(String hash, InputStream in);
	public OutputStream putRecv(String hash);

	public File getDbFile(String name);
	
	
	public long findQQByHash(String hash);
	public String findHashByQQ(long qq);
	public void putHash(String hash, long qq);
	
	public List<QQMsg> findMsg(QQMsg.Type type, long id, int start, int limit);
	public int countMsg(QQMsg.Type type, long id);
	public void putMsg(QQMsg msg);
}

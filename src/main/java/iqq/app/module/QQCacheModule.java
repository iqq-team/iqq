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
 * Package  : iqq.app.module
 * File     : QQDataModule.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-3-25
 * License  : Apache License 2.0 
 */
package iqq.app.module;

import iqq.app.core.IMContext;
import iqq.app.core.IMException;
import iqq.app.core.IMService;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventHandler;
import iqq.app.event.IMEventType;
import iqq.app.service.IMResourceService;
import iqq.app.service.IMTaskService;
import iqq.app.util.UIUtils;
import iqq.im.bean.QQAccount;
import iqq.im.bean.QQUser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentNavigableMap;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mapdb.DB;
import org.mapdb.DBMaker;

/**
 *
 * QQ数据缓存
 * 1. 用户信息（包括真实的QQ号）
 * 2. 群信息
 * 3. 用户头像
 * 4. 群头像
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class QQCacheModule extends AbstractModule {
	private static final Logger LOG = LoggerFactory.getLogger(QQCacheModule.class);
	private IMResourceService resources;
	private IMTaskService tasks;
	private long uin;
	private DB cacheDb;
	private ConcurrentNavigableMap<String, QQUser> userMap;
	
	@Override
	public void init(IMContext context) throws IMException {
		super.init(context);
		resources = context.getSerivce(IMService.Type.RESOURCE);
		tasks = context.getSerivce(IMService.Type.TASK);
	}
	
	@Override
	public void destroy() throws IMException {
		if(cacheDb != null){
			cacheDb.commit();
			cacheDb.close();
		}
		super.destroy();
	}
	
	@IMEventHandler(IMEventType.USER_CACHE_FIND)
	protected void processIMUserCacheFind(final IMEvent event){
		tasks.submit(new Runnable() {
			public void run() {
				QQUser memUser = (QQUser) event.getTarget();
				QQUser cachedUser = findUserFromCache(memUser);
				if(cachedUser != null){
					broadcastIMEvent(new IMEvent(IMEventType.USER_CACHE_UPDATE,cachedUser));
				}else{
					broadcastIMEvent(new IMEvent(IMEventType.USER_CACHE_MISS, memUser));
				}
			}
		});
	}
	
	
	@IMEventHandler(IMEventType.USER_CACHE_BATCH_FIND)
	protected void processIMUserCacheBatchFind(final IMEvent event){
		tasks.submit(new Runnable() {
			public void run() {
				List<QQUser> userList = (List<QQUser>) event.getTarget();
				List<QQUser> foundList = new ArrayList<QQUser>();
				for(QQUser memUser: userList){
					QQUser cachedUser = findUserFromCache(memUser);
					if(cachedUser != null){
						foundList.add(cachedUser);
					}else{
						broadcastIMEvent(new IMEvent(IMEventType.USER_CACHE_MISS, memUser));
					}
				}
				broadcastIMEvent(new IMEvent(IMEventType.USER_CACHE_BATCH_UPDATE, foundList));
			}
		});
	}
	
	@IMEventHandler(IMEventType.USER_FACE_UPDATE)
	protected void processIMUserFaceUpdate(final IMEvent event) throws URISyntaxException{
		tasks.submit(new Runnable(){
			public void run(){
				QQUser user = (QQUser) event.getTarget();
				String hash = getUserHash(user);
				File file = resources.getFile("user/" + uin + "/face/" + hash + ".png");
				try {
					ImageIO.write(user.getFace(), "png", file);
				} catch (IOException e) {
					LOG.warn("cache user face error!", e);
				}
			}
		});
	}
	
	@IMEventHandler(IMEventType.USER_SIGN_UPDATE)
	protected void processIMUserSignUpdate(IMEvent event){
		QQUser memUser = (QQUser) event.getTarget();
		String hash = getUserHash(memUser);
		QQUser cachedUser = userMap.get(hash);
		if(cachedUser != null){
			cachedUser.setSign(memUser.getSign());
		}else{
			cachedUser = memUser;
		}
		userMap.put(hash, cachedUser);
	}
	
	@IMEventHandler(IMEventType.USER_QQ_UPDATE)
	protected void processIMUserQQUpdate(IMEvent event){
		QQUser memUser = (QQUser) event.getTarget();
		String hash = getUserHash(memUser);
		QQUser cachedUser = userMap.get(hash);
		if(cachedUser != null){
			cachedUser.setQQ(memUser.getQQ());
		}else{
			cachedUser = memUser;
		}
		userMap.put(hash, cachedUser);
	}
	
	@IMEventHandler(IMEventType.LOGIN_SUCCESS)
	protected void processIMLoginSuccess(IMEvent event) throws URISyntaxException{
		QQAccount account = (QQAccount) event.getTarget();
		uin = account.getUin();
		
		File dbDir = resources.getFile("user/" + uin +"/db/");
		if(!dbDir.exists()){
			dbDir.mkdirs();
		}
		
		File faceDir = resources.getFile("user/" + uin +"/face/");
		if(!faceDir.exists()){
			faceDir.mkdirs();
		}
		
		cacheDb = DBMaker.newFileDB(resources.getFile("user/" + uin +"/db/cache.db"))
					   .closeOnJvmShutdown()
					   .asyncWriteFlushDelay(1000)
					   .make();
		userMap  = cacheDb.getTreeMap("userMap");
	}
	
	private String getUserHash(QQUser user){
		return UIUtils.Hash.md5(user.getNickname());
	}
	
	
	private QQUser findUserFromCache(QQUser memUser){
		String hash = getUserHash(memUser);
		
		//缓存的用户数据
		QQUser cachedUser = null;
		try {
			cachedUser = userMap.get(hash);
		} catch (Exception e) {
			LOG.warn("read user from mapdb error!!", e);
		}
		
		if(cachedUser == null){
			return null;
		}
		
		if(StringUtils.isNotEmpty(cachedUser.getSign())){
			memUser.setSign(cachedUser.getSign());
		}
		if(cachedUser.getQQ() > 0){
			memUser.setQQ(cachedUser.getQQ());
		}
		memUser.setLevel(cachedUser.getLevel());
	
		//缓存的头像
		File faceFile = resources.getFile("user/" +uin + "/face/" + hash + ".png");
		if(!faceFile.exists() || !faceFile.canRead()){
			return null;
		}
		
		try {
			BufferedImage faceImg = ImageIO.read(faceFile);
			memUser.setFace(faceImg);
		} catch (IOException e) {
			return null;
		}
		return memUser;
	}
}

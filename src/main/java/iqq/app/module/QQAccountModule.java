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
 * File     : QQAccountModule.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-4-12
 * License  : Apache License 2.0 
 */
package iqq.app.module;

import iqq.app.core.IMService;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventHandler;
import iqq.app.event.IMEventType;
import iqq.app.service.IMResourceService;
import iqq.app.service.IMTaskService;
import iqq.app.util.UIUtils;
import iqq.im.bean.QQAccount;
import iqq.im.bean.QQStatus;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 *
 * 保存最近登录的账号列表
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class QQAccountModule extends AbstractModule{
	private static final Logger LOG = Logger.getLogger(QQAccountModule.class);
	private static final String SALT = "~HelloWord~";
	private static final String FILE = "Account.dat";
	private Map<String, QQAccountEntry> entryMap;
	private QQAccount self;

	@IMEventHandler(IMEventType.RECENT_ACCOUNT_FIND)
	protected void processIMRecentAccountFind(IMEvent event){
		IMTaskService tasks = getContext().getSerivce(IMService.Type.TASK);
		tasks.submit(this, "handleRecentAccountFind", new Object[0]);
	}
	
	@IMEventHandler(IMEventType.RECENT_ACCOUNT_DELETE)
	protected void processIMRecentAccountDelete(IMEvent event){
		IMResourceService resources = getContext().getSerivce(IMService.Type.RESOURCE);
		File userDir = resources.getFile("user" + File.separator + self.getQQ());
		File datFile = new File(userDir, FILE);
		if(datFile.exists()) {
			datFile.delete();
		}
	}
	
	@IMEventHandler(IMEventType.LOGIN_SUCCESS)
	protected void processIMLoginSuccess(IMEvent event){
		QQAccount account = (QQAccount) event.getTarget();
		QQAccountEntry entry = entryMap.get(account.getUsername());
		if(entry != null){
			entry.qq = account.getQQ();
			submitRecentAccountSave(entry);
		}
		self = account;
	}
	
	@IMEventHandler(IMEventType.LOGIN_REQUEST)
	protected void processIMLoginRequest(IMEvent event){
		String username = event.getString("username");
		String password = event.getString("password");
		QQAccountEntry entry = entryMap.get(username);
		if(entry == null){
			entry = new QQAccountEntry();
			entryMap.put(username, entry);
		}
		entry.username = username;
		entry.lastLogin = new Date();
		entry.remPwd = event.getData("rememberPassword");
		entry.status = event.getData("status");
		
		if(entry.remPwd){
			entry.password = password;
		}else{
			entry.password = null;
		}
	}
	
	@IMEventHandler(IMEventType.SELF_INFO_UPDATE)
	protected void processIMSelfInfoUpdate(IMEvent event){
		QQAccount account = (QQAccount) event.getTarget();
		QQAccountEntry entry = entryMap.get(account.getNickname());
		if(entry != null){
			entry.nickname = account.getNickname();
			submitRecentAccountSave(entry);
		}
		
	}
	
	@IMEventHandler(IMEventType.SELF_FACE_UPDATE)
	protected void processIMSelfFaceUpdate(IMEvent event){
		QQAccount account = (QQAccount) event.getTarget();
		QQAccountEntry entry = entryMap.get(account.getUsername());
		if(entry != null){
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				ImageIO.write(account.getFace(), "png", out);
				entry.face = out.toByteArray();
				submitRecentAccountSave(entry);
			} catch (IOException e) {
				LOG.warn("write face error!", e);
			}
		}
	}
	
	private void submitRecentAccountSave(QQAccountEntry entry){
		IMTaskService tasks = getContext().getSerivce(IMService.Type.TASK);
		tasks.submit(this, "handleRecentAccountSave", entry);
	}
	
	private void handleRecentAccountSave(QQAccountEntry entry){
		try {
			IMResourceService resources = getContext().getSerivce(IMService.Type.RESOURCE);
			File userDir = resources.getFile("user" + File.separator + entry.qq);
			File datFile = new File(userDir, FILE);
			AESKeyPair key = genAesKeyPair(entry.qq + "");
			
			ByteArrayOutputStream memOut = new ByteArrayOutputStream();
			ObjectOutputStream objOut = new ObjectOutputStream(memOut);
			objOut.writeObject(entry);
			objOut.close();
			
			byte[] plain = memOut.toByteArray();
			byte[] encrypted = UIUtils.Crypt.AESEncrypt(plain, key.key, key.iv);
			FileUtils.writeByteArrayToFile(datFile, encrypted);
		} catch (IOException e) {
			LOG.warn("save account data Error!", e);
		}
	}
	
	private void handleRecentAccountFind(){
		IMResourceService resources = getContext().getSerivce(IMService.Type.RESOURCE);
		File userDir = resources.getFile("user");
		if(!userDir.exists()){
			userDir.mkdirs();
		}
		entryMap = new HashMap<String, QQAccountEntry>();
		List<QQAccount> result = new ArrayList<QQAccount>();
		for(File dir: userDir.listFiles()){
			if(dir.isDirectory()){
				File datFile = new File(dir, FILE);
				if(datFile.exists()){
					try {
						AESKeyPair key = genAesKeyPair(dir.getName());
						byte[] data = FileUtils.readFileToByteArray(datFile);
						byte[] plain = UIUtils.Crypt.AESDecrypt(data, key.key, key.iv);
						ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(plain));
						QQAccountEntry entry = (QQAccountEntry) in.readObject();
						QQAccount account = new QQAccount();
						account.setNickname(entry.nickname);
						account.setUsername(entry.username);
						account.setPassword(entry.password);
						account.setStatus(entry.status);
						if(entry.face != null){
							try {
								BufferedImage face = ImageIO.read(new ByteArrayInputStream(entry.face));
								account.setFace(face);
							} catch (IOException e) {
								LOG.warn("read account face error!", e);
							}
						}
						result.add(account);
						entryMap.put(account.getUsername(), entry);
					} catch (ClassNotFoundException e) {
						LOG.warn("read account data error!", e);
					} catch (IOException e) {
						LOG.warn("read account data error!", e);
					}
				}
			}
		}
		broadcastIMEvent(IMEventType.RECENT_ACCOUNT_UPDATE, result);
	}
	
	private AESKeyPair genAesKeyPair(String qq){
		AESKeyPair key = new AESKeyPair();
		key.key = UIUtils.Hash.digest("md5", (SALT + qq ).getBytes());
		key.iv = UIUtils.Hash.digest("md5", key.key);
		return key;
	}
	
	private static class AESKeyPair{
		public byte[] iv;
		public byte[] key;
	}
	
	
	private static class QQAccountEntry implements Serializable{
		private static final long serialVersionUID = 1L;
		public long qq;
		public String nickname;
		public String username;
		public String password;
		public QQStatus status;
		public byte[] face;
		public Date lastLogin;
		public boolean remPwd;
		public boolean autoLogin;
	}
}

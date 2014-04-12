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
 * Package  : iqq.app.ui.module
 * File     : UITrayModule.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-3-29
 * License  : Apache License 2.0 
 */
package iqq.app.ui.module;

import iqq.app.core.IMContext;
import iqq.app.core.IMException;
import iqq.app.core.IMService;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventHandler;
import iqq.app.event.IMEventType;
import iqq.app.module.AbstractModule;
import iqq.app.service.IMEventService;
import iqq.app.service.IMPropService;
import iqq.app.service.IMSkinService;
import iqq.app.service.IMTimerService;
import iqq.app.util.UIUtils;
import iqq.im.bean.QQAccount;
import iqq.im.bean.QQDiscuz;
import iqq.im.bean.QQGroup;
import iqq.im.bean.QQUser;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Deque;
import java.util.LinkedList;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alee.utils.ImageUtils;

/**
 *
 * 系统托盘模块
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class UITrayModule extends AbstractModule {
	private static final Logger LOG = LoggerFactory.getLogger(UITrayModule.class);
	public static boolean isLogin = false;
	private SystemTray tray;
	private TrayIcon icon;
	private Runnable flashTimer;
	private Image flashFace;	//当前闪动的头像
	private Image defaultFace; 	//默认头像
	private Image blankFace;	//空白的头像
	private Object flashOwner;	//当前闪烁的用户
	private Deque<Object> flashQueue;	//带闪烁的对象列表
	private QQAccount self;
	
	@Override
	public void init(IMContext context) throws IMException {
		super.init(context);
		IMPropService propService = context.getSerivce(IMService.Type.PROP);
		if(propService.getInt("trayNotification") == 0) {
			return ;
		}
		
		IMSkinService skins = context.getSerivce(IMService.Type.SKIN);
		final IMEventService events = context.getSerivce(IMService.Type.EVENT);
		if(SystemTray.isSupported()){
			ImageIcon img = skins.getImageIcon("defaultFace");
			defaultFace = ImageUtils.getBufferedImage(img);
			blankFace = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
			
			PopupMenu pop = new PopupMenu();
			MenuItem restore = new MenuItem("Restore");
			restore.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					events.broadcast(new IMEvent(IMEventType.SHOW_MAIN_FRAME));
				}
			});
	        pop.add(restore);
			
			// 退出
	        MenuItem exit = new MenuItem("Exit");
	        exit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					events.broadcast(new IMEvent(IMEventType.APP_EXIT_READY));
				}
			});
	        pop.add(exit);
	        
			tray = SystemTray.getSystemTray();
			icon =  new TrayIcon(defaultFace, "IQQ", pop);
			icon.setImageAutoSize(true);
			try {
				tray.add(icon);
			} catch (AWTException e) {
				LOG.warn("add tray icon error!", e);
			}
			icon.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount() == 2) {
						//如果有消息闪动，就弹出消息
						if(flashOwner != null){
							events.broadcast(new IMEvent(IMEventType.SHOW_CHAT, flashOwner));
						}else{
							//显示主窗口
							if(isLogin) {
								events.broadcast(new IMEvent(IMEventType.SHOW_MAIN_FRAME));
							}
						}
					}
				}
			});
			
			flashQueue = new LinkedList<Object>();
			IMTimerService timer = getContext().getSerivce(IMService.Type.TIMER);
			flashTimer = new FlashTrayTimer();
			timer.setInterval(flashTimer, 500);
			
		}
	}

	@Override
	public void destroy() throws IMException {
		super.destroy();
		if(tray != null){
			tray.remove(icon);
		}
		IMTimerService timer = getContext().getSerivce(IMService.Type.TIMER);
		timer.killTimer(flashTimer);
	}
	
	@IMEventHandler(IMEventType.LOGIN_SUCCESS)
	protected void processIMLoginSucess(IMEvent event){
		self = (QQAccount) event.getTarget();
		if(self.getFace() != null) {
			blankFace = self.getFace();
		}
		isLogin = true;
	}
	
	@IMEventHandler(IMEventType.SELF_INFO_UPDATE)
	protected void processIMSelfInfoUpdate(IMEvent event){
		updateSelfFace();
	}
	
	@IMEventHandler(IMEventType.SELF_FACE_UPDATE)
	protected void processIMSelfFaceUpdate(IMEvent event){
		updateSelfFace();
		if(self.getFace() != null) {
			blankFace = self.getFace();
		}
	}
	
	
	@IMEventHandler(IMEventType.FLASH_USER_START)
	protected void processIMFlashUserStart(IMEvent event){
		if(flashQueue == null) return ;
		if(flashQueue.contains(event.getTarget())){
			flashQueue.remove(event.getTarget());
		}
		if(flashOwner != null && flashOwner != event.getTarget()){
			flashQueue.addFirst(flashOwner);
		}
		flashOwner = event.getTarget();
		flashFace = getTrayFace(flashOwner);
		flashTimer.run();
	}
	
	@IMEventHandler(IMEventType.FLASH_USER_STOP)
	protected void processIMFlashUserStop(IMEvent event){
		if(flashQueue == null) return ;
		if(flashQueue.isEmpty()){
			flashOwner = null;
			flashFace  = null;
			icon.setImage(defaultFace);
		}else if(flashOwner != event.getTarget()){
			flashQueue.remove(flashOwner);
		}else{
			flashOwner = flashQueue.poll();
			flashFace  = getTrayFace(flashOwner);
			flashTimer.run();
		}
	}
	
	@IMEventHandler(IMEventType.USER_FACE_UPDATE)
	protected void processIMUserFaceUpdate(IMEvent event){
		if(flashQueue == null) return ;
		QQUser user = (QQUser) event.getTarget();
		if(flashOwner != null && flashOwner == user){
			flashFace = getTrayFace(user);
		}
	}
	
	@IMEventHandler(IMEventType.CLIENT_OFFLINE)
	protected void processIMClientOffline(IMEvent event){
		updateSelfFace();
	}
	
	@IMEventHandler(IMEventType.CLIENT_ONLINE)
	protected void processIMClientOnline(IMEvent event){
		updateSelfFace();
	}
	
	@IMEventHandler(IMEventType.CHANGE_STATUS_SUCCESS)
	protected void processIMChangeSatusSucess(IMEvent event){
		updateSelfFace();
	}
	
	private void updateSelfFace(){
		defaultFace = UIUtils.Bean.drawStatusFace(self, getContext());
		if(flashOwner == null && icon != null){
			icon.setImage(defaultFace);
			icon.setToolTip(self.getUin() + "\n" + self.getNickname() +"\n" + self.getStatus().getValue());
		}
	}
	
	private Image getTrayFace(Object owner){
		BufferedImage face = null;
		IMSkinService skins = getContext().getSerivce(IMService.Type.SKIN);
		if(owner instanceof QQUser){
			face = ((QQUser) owner).getFace();
		}else if(owner instanceof QQGroup){
			face = ((QQGroup) owner).getFace();
		}else if(owner instanceof QQDiscuz){
			face = skins.getBufferedImage("discuzIcon");
		}else{
			throw new IllegalArgumentException("unknown tray face!!!!" + owner);
		}
		
		if(face == null) {
			face = UIUtils.Bean.getDefaultFace(owner);
		}
		return face.getScaledInstance(32, 32, 100);
	}
	
	private class FlashTrayTimer implements Runnable{
		@Override
		public void run() {
			if(flashOwner != null && tray != null 
					&& icon != null && flashFace != null){
				Image curImg = icon.getImage();
				icon.setImage(curImg == flashFace ? blankFace : flashFace);
			}
		}
	}

}

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
 * File     : UIPopupModule.java
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
import iqq.app.event.IMEventHandlerProxy;
import iqq.app.event.IMEventType;
import iqq.app.service.IMEventService;
import iqq.app.ui.IMFrameView;
import iqq.app.util.UIUtils;
import iqq.im.bean.QQGroup;
import iqq.im.bean.QQMsg;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JWindow;

import ch.swingfx.twinkle.NotificationBuilder;
import ch.swingfx.twinkle.event.INotificationEventListener;
import ch.swingfx.twinkle.event.NotificationEvent;
import ch.swingfx.twinkle.manager.INotificationManager;
import ch.swingfx.twinkle.style.theme.DarkDefaultNotification;
import ch.swingfx.twinkle.window.Positions;

/**
 *
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class UIPopupModule extends IMFrameView {
	private static final long serialVersionUID = -4025351091657473825L;
	private static final MyNotificationManager NOTIFICATION_MANAGER = new MyNotificationManager();

	@Override
	public void init(IMContext context) throws IMException {
		super.init(context);
		IMEventHandlerProxy.register(this, context);
	}

	@Override
	public void destroy() throws IMException {
		super.destroy();
		IMEventHandlerProxy.unregister(this);
	}
	
	@IMEventHandler(IMEventType.RECV_QQ_MSG)
	protected void processIMRecvMsg(IMEvent event){
		final QQMsg msg = (QQMsg) event.getTarget();

		// 看看是不是已经禁群的
		if(msg.getType() == QQMsg.Type.GROUP_MSG) {
			QQGroup group = msg.getGroup();
			if(group.getMask() != 0) {
				return ;
			}
		}
		//我更倾向于来消息就显示，以后可以做个配置。。
		ImageIcon face = null;
		if( msg.getFrom().getFace() == null){
			face = UIUtils.Bean.getDefaultFace();
		}else{
			face = new ImageIcon(msg.getFrom().getFace());
		}
		
		String title = UIUtils.Bean.getDisplayName(msg.getFrom());
		if(msg.getGroup() != null){
			title = msg.getGroup().getName() + " - " + title;
		} else if(msg.getDiscuz() != null) {
			title = msg.getDiscuz().getName() + " - " + title;
		}
	
		new NotificationBuilder()
		.withStyle(new DarkDefaultNotification())
		.withTitle(title)
		.withMessage(msg.getText())
		.withIcon(face)
		.withNotificationManager(NOTIFICATION_MANAGER)
		.withDisplayTime(3000)
		.withPosition(Positions.SOUTH_EAST)
		.withListener(new INotificationEventListener() {
			public void opened(NotificationEvent arg0) {}
			public void mouseOver(NotificationEvent arg0) {}
			public void mouseOut(NotificationEvent arg0) {}
			public void closed(NotificationEvent arg0) {}
			public void clicked(NotificationEvent arg0) {
				IMEventService events = getContext().getSerivce(IMService.Type.EVENT);
				IMEvent imEvent = new IMEvent(IMEventType.SHOW_CHAT);
				switch(msg.getType()){
				case BUDDY_MSG: 
					imEvent.setTarget(msg.getFrom()); break;
				case SESSION_MSG: 
					imEvent.setTarget(msg.getFrom()); break;
				case GROUP_MSG: 
					imEvent.setTarget(msg.getGroup()); break;
				case DISCUZ_MSG: 
					imEvent.setTarget(msg.getDiscuz()); break;
				}
				events.broadcast(imEvent);
			}
		})
		.showNotification();
	}
	
	private static class MyNotificationManager implements INotificationManager{
		private static final long MAX_DELAY = 500;
		private static long sLast = 0;
		private static JWindow sWindow;
		 public void showNotification(final JWindow window) {
			 long now = System.currentTimeMillis();
			 if(now - sLast > MAX_DELAY){
				 if(sWindow != null){
					 sWindow.setVisible(false);
				 }
				 sWindow = window;
				 sWindow.addWindowListener(new WindowAdapter() {
						public void windowClosed(WindowEvent e) {
							window.removeWindowListener(this);
						}
					});
				 sWindow.setVisible(true);
				 sLast = System.currentTimeMillis();
			 }
		 }
	}

}

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
 * File     : UILoginModule.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-3-13
 * License  : Apache License 2.0 
 */
package iqq.app.ui.module;

import iqq.app.bean.UIUser;
import iqq.app.core.IMContext;
import iqq.app.core.IMException;
import iqq.app.core.IMService;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventHandler;
import iqq.app.event.IMEventHandlerProxy;
import iqq.app.event.IMEventType;
import iqq.app.service.IMEventService;
import iqq.app.ui.IMFrameView;
import iqq.app.ui.action.IMActionHandler;
import iqq.app.ui.bean.UserListElement;
import iqq.app.ui.content.login.LoginPanel;
import iqq.app.ui.content.login.LoginProcPanel;
import iqq.app.ui.widget.StatusPopup;
import iqq.app.util.I18NUtil;
import iqq.app.util.LocationUtil;
import iqq.app.util.SettingUtils;
import iqq.app.util.SkinUtils;
import iqq.im.QQException;
import iqq.im.bean.QQAccount;
import iqq.im.bean.QQStatus;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JFrame;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.text.WebPasswordField;

/**
 * 
 * 登录界面模块,负责界面的显示和UI事件的处理
 * 
 * @author solosky <solosky772@qq.com>
 * 
 */
public class UILoginModule extends IMFrameView {
	private static final Logger LOG = Logger.getLogger(UILoginModule.class);

	private LoginPanel loginPanel;
	private boolean isVerifyCode = false;

	@Override
	public void init(IMContext context) throws IMException {
		super.init(context);

		// 初始化窗口
		initFrame();
		
		// 初始化显示内容，登录面板
		loginPanel = new LoginPanel(this);
		setContentPanel(loginPanel);
		
		// 初始化用户登录信息
		initAccount();

		// 注册感兴趣的事件
		IMEventHandlerProxy.register(this, context);
	}
	
	@Override
	public void destroy() throws IMException {
		super.destroy();
		IMEventHandlerProxy.unregister(this);
	}
	
	private void initFrame() {
		// 设置标题
        setTitle(I18NUtil.getMessage("app.name"));
        setIconImage(SkinUtils.getImageIcon("appLogo").getImage());

        // 设置程序宽度 和高度
        setSize(SettingUtils.getInt("appWidth"), SettingUtils.getInt("appHeight"));
        
        // 设置在屏幕显示位置，右中
        setLocation(LocationUtil.getScreenRight(getWidth(), getHeight()));
        setAlwaysOnTop(true);
        
        // 关闭后退出程序
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * 初始化用户登录信息
	 */
	private void initAccount() {
		WebComboBox accoutCbx = (WebComboBox) getValue("account");
		final WebPasswordField passwordFld = (WebPasswordField) getValue("password");
		StatusPopup statusPopup = (StatusPopup) getValue("status");
		WebCheckBox rePwdCkb = (WebCheckBox) getValue("rememberPwd");
		
		// 登录信息
		rePwdCkb.setSelected(true);
		statusPopup.setCurrentStatus(QQStatus.ONLINE);
		
		accoutCbx.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getItem() instanceof UserListElement) {
					UserListElement userElt = (UserListElement) e.getItem();
					QQAccount account = (QQAccount) userElt.getUserNamed().getEntity();
					passwordFld.setText(account.getPassword());
				} else {
					passwordFld.clear();
				}
			}
		});
	}

	@IMEventHandler(IMEventType.LOGIN_READY)
	protected void processIMLoginReady(IMEvent event) {
		LOG.info("Ready to login......");
		this.show();
		broadcastIMEvent(new IMEvent(IMEventType.RECENT_ACCOUNT_FIND));
	}
	
	@IMEventHandler(IMEventType.RECENT_ACCOUNT_UPDATE)
	protected void processIMRecentAccountUpdate(IMEvent event){
		List<QQAccount> accounts = (List<QQAccount>) event.getTarget();
		WebComboBox accoutCbx = (WebComboBox) getValue("account");
		for(QQAccount account : accounts) {
			accoutCbx.addItem(new UserListElement(new UIUser(account)));
		}
	}
	
	@IMEventHandler(IMEventType.LOGIN_REQUEST)
	protected void processIMLoginRequest(IMEvent event) {
		LOG.info("login request......");
		setContentPanel(new LoginProcPanel(this));
	}

	@IMEventHandler(IMEventType.IMAGE_VERIFY_NEED)
	protected void processIMLoginVerify(IMEvent event) {
		isVerifyCode = true;
	}

	@IMEventHandler(IMEventType.VERIFY_CANCEL)
	protected void processIMLoginVerifyCancel(IMEvent event) {
		if (isVerifyCode) {
			// 返回登录面板
			setContentPanel(loginPanel);
		}
	}

	@IMEventHandler(IMEventType.LOGIN_PROGRESS)
	protected void processIMLoginProgress(IMEvent event) {
		LOG.info("login progress......");
	}

	@IMEventHandler(IMEventType.LOGIN_ERROR)
	protected void processIMLoginError(IMEvent event) {
		LOG.info("login error......" + event.getData("reason"));
		setContentPanel(loginPanel);	// 返回登录面板
		QQException ex = (QQException) event.getTarget();
		String reason = event.getData("reason");
		switch(ex.getError()){
		case IO_ERROR: reason = I18NUtil.getMessage("login.netError"); break;
		default: //TODO ..
		}
		
		WebOptionPane.showMessageDialog ( this, reason, 
				I18NUtil.getMessage("login.failed"), WebOptionPane.INFORMATION_MESSAGE );
	}
	
	@IMEventHandler(IMEventType.LOGIN_CANCELED)
	protected void processIMLoginCanceled(IMEvent event){
		setContentPanel(loginPanel);	// 返回登录面板
	}

	@IMEventHandler(IMEventType.LOGIN_SUCCESS)
	protected void processIMLoginSuccess(IMEvent event) {
		LOG.info("login success......");
		// 处理登录窗口
		this.dispose();
	}

	/**
	 * 登录
	 */
	@IMActionHandler(name = "login")
	public void login() {
		IMEventService eventHub = getContext().getSerivce(
				IMService.Type.EVENT);
		IMEvent event = new IMEvent(IMEventType.LOGIN_REQUEST);

		WebComboBox accoutCbx = (WebComboBox) getValue("account");
		WebPasswordField passwordFld = (WebPasswordField) getValue("password");
		StatusPopup statusPopup = (StatusPopup) getValue("status");
		WebCheckBox rePwdCkb = (WebCheckBox) getValue("rememberPwd");
		String username = accoutCbx.getSelectedItem().toString().trim();
		String password = new String(passwordFld.getPassword());
		QQStatus status = statusPopup.getStatus();
		boolean isRememberPwd = rePwdCkb.isSelected();
		
		if(StringUtils.isEmpty(username)){
			loginPanel.showErrorTip(accoutCbx, I18NUtil.getMessage("login.inputUsername"));
			return;
		}
		
		if(StringUtils.isEmpty(password)){
			loginPanel.showErrorTip(passwordFld, I18NUtil.getMessage("login.inputPassword"));
			return;
		}
		
		event.putData("username", username);
		event.putData("password", password);
		event.putData("status", status);
		event.putData("rememberPassword", isRememberPwd);
		eventHub.broadcast(event);
	}

	/**
	 * 取消登录
	 */
	@IMActionHandler
	public void cancel() {
		IMEventService eventHub = getContext().getSerivce(IMService.Type.EVENT);
		IMEvent event = new IMEvent(IMEventType.LOGIN_CANCEL_REQUEST);
		eventHub.broadcast(event);
	}

}

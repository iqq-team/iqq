package iqq.app.ui.module;

import iqq.app.core.IMContext;
import iqq.app.core.IMException;
import iqq.app.core.IMService;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventHandler;
import iqq.app.event.IMEventHandlerProxy;
import iqq.app.event.IMEventType;
import iqq.app.service.IMEventService;
import iqq.app.service.IMPropService;
import iqq.app.ui.BackgroundPanel;
import iqq.app.ui.IMDialogView;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.SwingConstants;

import com.alee.extended.panel.CenterPanel;
import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.tabbedpane.WebTabbedPane;

public class UISettingModule extends IMDialogView {
	private static final long serialVersionUID = -5865363942284348747L;
	private SettingContent settingContent;

	@Override
	public void init(IMContext context) throws IMException {
		super.init(context);
		// 注册感兴趣的事件
		IMEventHandlerProxy.register(this, context);
		setTitle("设置（尚未完善）");
		initView();
	}

	@Override
	public void destroy() throws IMException {
		super.destroy();
		IMEventHandlerProxy.unregister(this);
	}

	protected void initView() {
		setAlwaysOnTop(true);
		setLocationRelativeTo(null);
		setSize(600, 450);

		settingContent = new SettingContent(this);
		setContentPanel(settingContent);
	}

	@IMEventHandler(IMEventType.SHOW_SETTING_WINDOW)
	protected void proccessIMShowSettingWindow(IMEvent event) {
		if (!isVisible()) {
			setVisible(true);
		}
	}

	class SettingContent extends BackgroundPanel {
		private static final long serialVersionUID = -8860926370226645331L;

		WebTabbedPane settingTab;

		UISettingModule view;

		public SettingContent(UISettingModule view) {
			super(view);
			this.view = view;
			initComponent();
		}

		private void initComponent() {
			settingTab = new WebTabbedPane();

			settingTab.addTab("常规", getGeneral());
			settingTab.addTab("聊天窗口", new WebLabel("尚未完善"));
			settingTab.addTab("通知方式", getNotification());

			add(settingTab);
		}

		private WebPanel getGeneral() {
			WebPanel generalPl = new WebPanel(new GridLayout(3, 2));

			// 好友列表头像大小
			WebLabel buddyFaceLbl = new WebLabel("列表头像大小：");
			buddyFaceLbl.setHorizontalAlignment(SwingConstants.RIGHT);
			buddyFaceLbl.setMargin(5);
			generalPl.add(buddyFaceLbl);
			generalPl.add(new WebLabel("在好友列表TAB处理右击菜单处理"));

			WebLabel keyAccountLbl = new WebLabel("发送消息快捷键：");
			keyAccountLbl.setHorizontalAlignment(SwingConstants.RIGHT);
			keyAccountLbl.setMargin(5);
			generalPl.add(keyAccountLbl);
			WebComboBox keyComboBox = new WebComboBox();
			keyComboBox.addItem("Enter");
			keyComboBox.addItem("Ctrl + Enter");
			generalPl.add(new CenterPanel(keyComboBox));

			WebLabel clearAccountLbl = new WebLabel("清除账号密码：");
			clearAccountLbl.setHorizontalAlignment(SwingConstants.RIGHT);
			clearAccountLbl.setMargin(5);
			generalPl.add(clearAccountLbl);
			WebButton clearPwd = new WebButton("清除");
			generalPl.add(new CenterPanel(clearPwd));

			IMContext context = view.getContext();
			IMPropService propService = context.getSerivce(IMService.Type.PROP);
			if (propService.getInt("sentMsgKey") == 0) {
				keyComboBox.setSelectedIndex(0);
			} else {
				keyComboBox.setSelectedIndex(1);
			}
			// Action
			keyComboBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					WebComboBox keyComboBox = (WebComboBox) e.getSource();
					int index = keyComboBox.getSelectedIndex();
					IMContext context = view.getContext();
					IMPropService propService = context
							.getSerivce(IMService.Type.PROP);
					if (index == 0) {
						propService.setInt("sentMsgKey", 0);
					} else {
						propService.setInt("sentMsgKey", 1);
					}
				}
			});

			clearPwd.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					IMEventService eventHub = getContext().getSerivce(
							IMService.Type.EVENT);
					eventHub.broadcast(new IMEvent(
							IMEventType.RECENT_ACCOUNT_DELETE));
					WebOptionPane
							.showMessageDialog(SettingContent.this, "消除成功",
									"消除账号信息", WebOptionPane.INFORMATION_MESSAGE);
				}
			});

			return generalPl;
		}

		private WebPanel getNotification() {
			WebPanel panel = new WebPanel(new GridLayout(3, 1));
			final WebCheckBox desktopCbx = new WebCheckBox("桌面悬浮");
			final WebCheckBox trayCbx = new WebCheckBox("系统托盘");
			panel.add(new CenterPanel(new WebLabel("重新打开程序后生效")));
			panel.add(new CenterPanel(desktopCbx));
			panel.add(new CenterPanel(trayCbx));

			IMContext context = view.getContext();
			final IMPropService propService = context
					.getSerivce(IMService.Type.PROP);
			if (propService.getInt("desktopNotification") == 1) {
				desktopCbx.setSelected(true);
			} else {
				desktopCbx.setSelected(false);
			}
			if (propService.getInt("trayNotification") == 1) {
				trayCbx.setSelected(true);
			} else {
				trayCbx.setSelected(false);
			}

			desktopCbx.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (desktopCbx.isSelected()) {
						propService.setInt("desktopNotification", 1);
					} else {
						if (propService.getInt("trayNotification") == 1) {
							propService.setInt("desktopNotification", 0);
						} else {
							WebOptionPane
									.showMessageDialog(view, "您至少选择一样通知方式");
							desktopCbx.setSelected(true);
						}
					}
				}
			});
			trayCbx.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (trayCbx.isSelected()) {
						propService.setInt("trayNotification", 1);
					} else {
						if (propService.getInt("desktopNotification") == 1) {
							propService.setInt("trayNotification", 0);
						} else {
							WebOptionPane
									.showMessageDialog(view, "您至少选择一样通知方式");
							trayCbx.setSelected(true);
						}
					}
				}
			});
			return panel;
		}
	}
}

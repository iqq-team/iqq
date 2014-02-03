/**
 * 
 */
package iqq.app.ui.module;

import iqq.app.core.IMContext;
import iqq.app.core.IMException;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventHandler;
import iqq.app.event.IMEventHandlerProxy;
import iqq.app.event.IMEventType;
import iqq.app.ui.IMDialogView;
import iqq.app.ui.action.IMActionHandler;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;

import com.alee.extended.panel.GroupPanel;
import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.text.WebTextField;

/**
 * 程序代理模块
 * 
 * @author ZhiHui_Chen<6208317@qq.com>
 * @create date 2013-4-15
 */
public class UIProxyModule extends IMDialogView {
	private static final long serialVersionUID = 55508484016418468L;

	@Override
	public void init(IMContext context) throws IMException {
		super.init(context);

		// 初始化窗口
		initView();
		initContent();

		// 注册感兴趣的事件
		IMEventHandlerProxy.register(this, context);
	}

	@Override
	public void destroy() throws IMException {
		super.destroy();
		IMEventHandlerProxy.unregister(this);
	}

	/**
	 * 初始化窗口属性
	 */
	private void initView() {
		setTitle("代理设置");
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
		setSize(500, 300);
	}

	/**
	 * 内容面板
	 */
	private void initContent() {
		setContentPanel(new ProxyPanel(this));
	}

	@IMEventHandler(IMEventType.SHOW_PROXY_WINDOW)
	public void processShowChat(IMEvent event) {
		this.setVisible(true);
	}

	public class ProxyPanel extends WebPanel {
		private static final long serialVersionUID = 9061339914079755255L;
		private UIProxyModule view;

		private WebTextField addressFld;
		private WebTextField portFld;
		private WebTextField usernameFld;
		private WebTextField passwordFld;
		private WebTextField domainFld;
		private WebComboBox typeCbx;

		public ProxyPanel(UIProxyModule view) {
			this.view = view;

			initComponent();

		}

		/**
		 * 初始化组件
		 */
		private void initComponent() {
			WebLabel netSettingLbl = new WebLabel("Network Setting");
			WebLabel typeLbl = new WebLabel("Type:");
			WebLabel addressLbl = new WebLabel("Address:");
			WebLabel portLbl = new WebLabel("Port:");
			WebLabel usernameLbl = new WebLabel("Username:");
			WebLabel passwordLbl = new WebLabel("Password:");
			WebLabel domainLbl = new WebLabel("Domain:");

			typeCbx = new WebComboBox();
			typeCbx.addItem("None");
			typeCbx.addItem("Http");
			typeCbx.addItem("Socks5");
			typeCbx.addItem("Browser");
			typeCbx.setDrawFocus(false);

			// field
			addressFld = new WebTextField();
			portFld = new WebTextField();
			usernameFld = new WebTextField();
			passwordFld = new WebTextField();
			domainFld = new WebTextField();

			// label margin
			netSettingLbl.setMargin(15);
			Insets insets = new Insets(5, 5, 5, 5);
			typeLbl.setMargin(5, 37, 5, 5);
			addressLbl.setMargin(5, 14, 5, 5);
			portLbl.setMargin(5, 27, 5, 5);
			usernameLbl.setMargin(insets);
			passwordLbl.setMargin(insets);
			domainLbl.setMargin(insets);

			// size
			Dimension d1 = new Dimension(80, 30);
			Dimension d2 = new Dimension(100, 30);
			Dimension d3 = new Dimension(50, 30);

			// field properties
			typeCbx.setPreferredSize(d1);
			usernameFld.setPreferredSize(d1);
			addressFld.setPreferredSize(d2);
			passwordFld.setPreferredSize(d2);
			portFld.setPreferredSize(d3);
			domainFld.setPreferredSize(d3);

			// group panel
			GroupPanel g1 = new GroupPanel(true, typeLbl, typeCbx, addressLbl,
					addressFld, portLbl, portFld);
			GroupPanel g2 = new GroupPanel(true, usernameLbl, usernameFld,
					passwordLbl, passwordFld, domainLbl, domainFld);
			g1.setPreferredSize(new Dimension(this.getWidth(), 30));

			// test button
			Dimension dBtn = new Dimension(80, 30);
			WebButton testBtn = new WebButton("Test");
			WebButton okBtn = new WebButton("OK");
			WebButton cancelBtn = new WebButton("Cancel");
			// size
			testBtn.setPreferredSize(dBtn);
			okBtn.setPreferredSize(dBtn);
			cancelBtn.setPreferredSize(dBtn);
			// action
			testBtn.setAction(view.getActionService().getActionMap(this)
					.get("processTestAction"));
			okBtn.setAction(view.getActionService().getActionMap(this)
					.get("processOKAction"));
			cancelBtn.setAction(view.getActionService().getActionMap(this)
					.get("processCancelAction"));
			// reset text
			testBtn.setText("Test");
			okBtn.setText("OK");
			cancelBtn.setText("Cancel");

			// layout panel
			WebPanel testPl = new WebPanel();
			testPl.add(testBtn, BorderLayout.LINE_END);
			testPl.setOpaque(false);
			testPl.setMargin(20, 0, 5, 30);
			WebPanel confimBtnPl = new WebPanel();
			confimBtnPl.add(new GroupPanel(true, okBtn, cancelBtn),
					BorderLayout.LINE_END);
			confimBtnPl.setMargin(10, 0, 5, 30);
			confimBtnPl.setOpaque(false);

			// add to content
			add(new GroupPanel(false, netSettingLbl, g1, g2, testPl,
					confimBtnPl));
		}

		@IMActionHandler
		public void processTestAction() {
			System.out.println("TestAction");
		}

		@IMActionHandler
		public void processOKAction() {
			System.out.println("OkAction");
		}

		@IMActionHandler
		public void processCancelAction() {
			System.out.println("CancelAction");
		}

	}
}

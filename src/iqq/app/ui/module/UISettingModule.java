package iqq.app.ui.module;

import iqq.app.core.IMContext;
import iqq.app.core.IMException;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventHandler;
import iqq.app.event.IMEventHandlerProxy;
import iqq.app.event.IMEventType;
import iqq.app.ui.BackgroundPanel;
import iqq.app.ui.IMDialogView;

import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.SwingConstants;

import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.slider.WebSlider;
import com.alee.laf.tabbedpane.WebTabbedPane;

public class UISettingModule extends IMDialogView {
	private static final long serialVersionUID = -5865363942284348747L;
	private SettingContent settingContent;
	
	@Override
	public void init(IMContext context) throws IMException {
		super.init(context);
		// 注册感兴趣的事件
		IMEventHandlerProxy.register(this, context);
		
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
		if(!isVisible()) {
			setVisible(true);
		}
	}
	
	class SettingContent extends BackgroundPanel {
		private static final long serialVersionUID = -8860926370226645331L;
		
		WebTabbedPane settingTab;
		
		Window view;
		public SettingContent(Window view) {
			super(view);
			
			initComponent();
		}
		
		private void initComponent() {
			settingTab = new WebTabbedPane();
			
			settingTab.addTab("General", getGeneral());
			settingTab.addTab("Chat Window", getGeneral());
			settingTab.addTab("Notification", getGeneral());
			
			add(settingTab);
		}
		
		private WebPanel getGeneral() {
			WebPanel generalPl = new WebPanel(new GridLayout(4, 2));
			
			// 好友列表头像大小
			WebLabel buddyFaceLbl = new WebLabel("好友列表头像大小：");
			buddyFaceLbl.setHorizontalAlignment(SwingConstants.RIGHT);
			buddyFaceLbl.setMargin(5);
			generalPl.add(buddyFaceLbl);
			WebSlider buddySlider = new WebSlider ( WebSlider.HORIZONTAL );
			buddySlider.setMinimum ( 0 );
			buddySlider.setMaximum ( 100 );
			buddySlider.setMinorTickSpacing ( 10 );
			buddySlider.setMajorTickSpacing ( 50 );
			buddySlider.setPaintTicks ( true );
			buddySlider.setPaintLabels ( true );
			generalPl.add(buddySlider);
			
			WebLabel groupFaceLbl = new WebLabel("群列表头像大小：");
			groupFaceLbl.setHorizontalAlignment(SwingConstants.RIGHT);
			groupFaceLbl.setMargin(5);
			generalPl.add(groupFaceLbl);
			generalPl.add(new WebLabel("好友列表头像"));
			
			WebLabel recentFaceLbl = new WebLabel("最近列表头像大小：");
			recentFaceLbl.setHorizontalAlignment(SwingConstants.RIGHT);
			recentFaceLbl.setMargin(5);
			generalPl.add(recentFaceLbl);
			generalPl.add(new WebLabel("好友列表头像"));
			
			WebLabel clearAccountLbl = new WebLabel("清除账号密码：");
			clearAccountLbl.setHorizontalAlignment(SwingConstants.RIGHT);
			clearAccountLbl.setMargin(5);
			generalPl.add(clearAccountLbl);
			generalPl.add(new WebLabel("好友列表头像"));
			
			
			
			return generalPl;
		}
		
	}
}

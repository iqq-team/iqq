package iqq.app.ui.content.login;

import iqq.app.core.IMService;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventType;
import iqq.app.service.IMEventService;
import iqq.app.service.IMSkinService.Type;
import iqq.app.service.IMTimerService;
import iqq.app.ui.BackgroundPanel;
import iqq.app.ui.IMFrameView;
import iqq.app.ui.module.UILoginModule;
import iqq.app.ui.renderer.UserComboBoxCellRenderer;
import iqq.app.ui.widget.StatusPopup;
import iqq.app.util.I18NUtil;
import iqq.app.util.IMImageUtil;
import iqq.app.util.SkinUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.KeyStroke;

import com.alee.extended.image.WebImage;
import com.alee.extended.panel.BorderPanel;
import com.alee.extended.panel.CenterPanel;
import com.alee.extended.panel.FlowPanel;
import com.alee.extended.panel.GroupPanel;
import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.text.WebPasswordField;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.managers.tooltip.TooltipWay;
import com.alee.managers.tooltip.WebCustomTooltip;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-16
 */
public class LoginPanel extends BackgroundPanel {
	private static final long serialVersionUID = -4217925392103603133L;

	private IMFrameView view;
	private WebImage faceImage;
	private WebLabel regAccountLbl;
	private WebLabel forgetPwdLbl;
	private WebLabel settingLbl;
	private WebCustomTooltip errTooltip;
	private HideTooltipTask errHideTask;
	private Color color = Color.GRAY;
	private Color enteredColor = Color.RED;

	public LoginPanel(IMFrameView view) {
		super(view);
		this.view = view;
		this.errHideTask = new HideTooltipTask();

		// add content
		addContent();
	}

	/**
	 * add content
	 */
	private void addContent() {
		// 上面是标题栏，下面为内容显示
		add(view.getIMTitleComponent(), BorderLayout.PAGE_START);
		add(new GroupPanel(false, createHeader(), createCenter(),
				new CenterPanel(createFooter())), BorderLayout.CENTER);
	}

	private WebPanel createHeader() {
		faceImage = new WebImage(IMImageUtil.getScaledInstance(
				SkinUtils.getImageIcon("appLogo"), 100, 100));
		faceImage.setMargin(20, 20, 15, 20);
		faceImage.setPreferredSize(new Dimension(128, 128));
		
		WebLabel shadowLbl = new WebLabel();
		shadowLbl.setPreferredSize(new Dimension(0, 20));
		shadowLbl.setPainter(SkinUtils.getPainter(Type.NPICON, "shadowLine"));
		GroupPanel groupPanel = new GroupPanel(false, faceImage, shadowLbl);
		return groupPanel;
	}

	private WebPanel createCenter() {
		// new component
		Insets insets = new Insets(10, 10, 10, 10);
		Dimension d = new Dimension(this.getWidth(), 30);
		WebLabel accountLbl = new WebLabel(I18NUtil.getMessage("account"));
		accountLbl.setMargin(insets);

		WebLabel passwordLbl = new WebLabel(I18NUtil.getMessage("password"));
		passwordLbl.setMargin(insets);

		WebComboBox accoutCbx = new WebComboBox();
		accoutCbx.setRenderer(new UserComboBoxCellRenderer(accoutCbx));
		accoutCbx.setEditable(true);
		accoutCbx.setPreferredSize(new Dimension(d));
		
		final WebPasswordField passwordFld = new WebPasswordField();
		passwordFld.setPreferredSize(new Dimension(d));
		
		WebCheckBox rePwdCkb = new WebCheckBox(
				I18NUtil.getMessage("rememberPwd"));
		rePwdCkb.setMargin(insets);

		StatusPopup statusPopup = new StatusPopup();

		WebButton loginBtn = new WebButton();
		loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
		loginBtn.setPreferredSize(new Dimension(100, 30));
		loginBtn.setAction(view.getActionService()
				.getActionMap(UILoginModule.class, getView()).get("login"));
		loginBtn.setText(I18NUtil.getMessage("login"));
		loginBtn.registerKeyboardAction(loginBtn.getAction(),  
	              KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,   0),  
	                  JComponent.WHEN_IN_FOCUSED_WINDOW); 

		// add to group panel
		FlowPanel p = new FlowPanel(rePwdCkb, statusPopup);
		p.setOpaque(false);
		p.setMargin(10, 0, 20, 0);
		GroupPanel groupPanel = new GroupPanel(0, false, new BorderPanel(
				accountLbl), new BorderPanel(accoutCbx), new BorderPanel(
				passwordLbl), new BorderPanel(passwordFld), p, new CenterPanel(
				loginBtn));
		groupPanel.setMargin(10, 25, 10, 25);

		view.addValue("account", accoutCbx);
		view.addValue("password", passwordFld);
		view.addValue("status", statusPopup);
		view.addValue("rememberPwd", rePwdCkb);

		return groupPanel;
	}

	private WebPanel createFooter() {
		Insets insets = new Insets(0, 10, 0, 10);
		regAccountLbl = new WebLabel(I18NUtil.getMessage("regAccount"));
		forgetPwdLbl = new WebLabel(I18NUtil.getMessage("forgetPwd"));
		settingLbl = new WebLabel(I18NUtil.getMessage("setting"));

		regAccountLbl.setForeground(color);
		forgetPwdLbl.setForeground(color);
		settingLbl.setForeground(color);

		regAccountLbl.setMargin(insets);
		forgetPwdLbl.setMargin(insets);
		settingLbl.setMargin(insets);

		GroupPanel groupPanel = new GroupPanel(true, regAccountLbl,
				forgetPwdLbl, settingLbl);
		groupPanel.setMargin(60, 0, 0, 0);
		groupPanel.setPainter(SkinUtils.getPainter(Type.NPICON, "shadowLine"));
		LabelMouseListener lblMouseListener = new LabelMouseListener();
		regAccountLbl.addMouseListener(lblMouseListener);
		forgetPwdLbl.addMouseListener(lblMouseListener);
		settingLbl.addMouseListener(lblMouseListener);

		return groupPanel;
	}
	
	public void showErrorTip(JComponent comp, String msg){
		errTooltip =  TooltipManager.showOneTimeTooltip ( comp, null, msg, TooltipWay.up );
		IMTimerService timers = view.getContext().getSerivce(IMService.Type.TIMER);
		timers.killTimer(errHideTask);
		timers.setTimeout(errHideTask, 3000);
	}
	
	private class HideTooltipTask implements Runnable{
		@Override
		public void run() {
			if(errTooltip != null){
				errTooltip.setVisible(false);
				errTooltip = null;
			}
		}
		
	}

	class LabelMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getSource() == LoginPanel.this.settingLbl) {
				IMEventService events  = view.getContext().getSerivce(IMService.Type.EVENT);
				events.broadcast(new IMEvent(IMEventType.SHOW_PROXY_WINDOW));
			} else {
				IMEventService events  = view.getContext().getSerivce(IMService.Type.EVENT);
				events.broadcast(new IMEvent(IMEventType.SHOW_SETTING_WINDOW));
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {

		}

		@Override
		public void mouseReleased(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			if (e.getComponent() == regAccountLbl) {
				regAccountLbl.setForeground(enteredColor);
			} else if (e.getComponent() == forgetPwdLbl) {
				forgetPwdLbl.setForeground(enteredColor);
			} else if (e.getComponent() == settingLbl) {
				settingLbl.setForeground(enteredColor);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (e.getComponent() == regAccountLbl) {
				regAccountLbl.setForeground(color);
			} else if (e.getComponent() == forgetPwdLbl) {
				forgetPwdLbl.setForeground(color);
			} else if (e.getComponent() == settingLbl) {
				settingLbl.setForeground(color);
			}
		}
	}
}

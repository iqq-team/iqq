package iqq.app.ui.content.login;

import iqq.app.ui.BackgroundPanel;
import iqq.app.ui.IMFrameView;
import iqq.app.ui.module.UILoginModule;
import iqq.app.ui.widget.IMTitleComponent;
import iqq.app.util.I18NUtil;
import iqq.app.util.IMImageUtil;
import iqq.app.util.SkinUtils;

import java.awt.BorderLayout;
import java.awt.Dimension;

import com.alee.extended.image.WebImage;
import com.alee.extended.panel.BorderPanel;
import com.alee.extended.panel.CenterPanel;
import com.alee.extended.panel.GroupPanel;
import com.alee.laf.button.WebButton;
import com.alee.laf.progressbar.WebProgressBar;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-17
 */
public class LoginProcPanel extends BackgroundPanel {
	private static final long serialVersionUID = -680568532789078833L;

	private IMFrameView view;
	private WebImage logoImg;
	private WebProgressBar progressBar;
	private WebButton cancelBtn;
	private GroupPanel groupPl;

	/**
	 * @param application
	 */
	public LoginProcPanel(IMFrameView view) {
		super(view);
		this.view = view;

		addContent();
	}

	/**
	 * add content
	 */
	private void addContent() {
		// 上面是标题栏，下面为内容显示
		add(new IMTitleComponent(view, false), BorderLayout.PAGE_START);
		add(createContent(), BorderLayout.CENTER);
	}

	private GroupPanel createContent() {
		logoImg = new WebImage(IMImageUtil.getScaledInstance(
				SkinUtils.getImageIcon("appLogo"), 100, 100));

		progressBar = new WebProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(true);
		progressBar.setString("");
		BorderPanel bp = new BorderPanel(progressBar);
		bp.setMargin(20, 0, 20, 0);
		
		cancelBtn = new WebButton();
		cancelBtn.setPreferredSize(new Dimension(100, 30));
		cancelBtn.setAction(view.getActionService()
				.getActionMap(UILoginModule.class, getView()).get("cancel"));
		cancelBtn.setText(I18NUtil.getMessage("cancel"));

		groupPl = new GroupPanel(false, new BorderPanel(logoImg), bp,
				new CenterPanel(cancelBtn));
		groupPl.setMargin(50, 20, 50, 20);

		return groupPl;
	}

}

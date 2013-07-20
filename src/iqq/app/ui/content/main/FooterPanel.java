package iqq.app.ui.content.main;

import iqq.app.core.IMService;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventType;
import iqq.app.service.IMEventService;
import iqq.app.service.IMSkinService.Type;
import iqq.app.ui.BackgroundPanel;
import iqq.app.ui.IMFrameView;
import iqq.app.util.SkinUtils;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.alee.laf.StyleConstants;
import com.alee.laf.button.WebButton;
import com.alee.laf.toolbar.ToolbarStyle;
import com.alee.laf.toolbar.WebToolBar;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-18
 */
public class FooterPanel extends BackgroundPanel {
	private static final long serialVersionUID = -7550051260283034681L;
	private IMFrameView view;
	private WebToolBar toolBar;

	/**
	 * @param view
	 */
	public FooterPanel(IMFrameView view) {
		super(view);
		this.view = view;
		this.setOpaque(false);
		addContent();
	}

	private void addContent() {
		add(createToolBar());
	}

	/**
	 * @return
	 */
	private Component createToolBar() {
		toolBar = new WebToolBar(WebToolBar.HORIZONTAL);
		toolBar.setFloatable(false);
		toolBar.setToolbarStyle(ToolbarStyle.attached);
		toolBar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		toolBar.setOpaque(false);
		toolBar.setPainter(SkinUtils.getPainter(Type.NPICON, "appBg"));

		WebButton add = WebButton.createIconWebButton(
				SkinUtils.getImageIcon("add"), StyleConstants.smallRound, true);
		WebButton mail = WebButton
				.createIconWebButton(SkinUtils.getImageIcon("mail"),
						StyleConstants.smallRound, true);
		WebButton qzone = WebButton.createIconWebButton(
				SkinUtils.getImageIcon("qzone"), StyleConstants.smallRound,
				true);
		WebButton weibo = WebButton.createIconWebButton(
				SkinUtils.getImageIcon("weibo"), StyleConstants.smallRound,
				true);
		WebButton msg = WebButton.createIconWebButton(
				SkinUtils.getImageIcon("msg"), StyleConstants.smallRound, true);
		WebButton setting = WebButton.createIconWebButton(
				SkinUtils.getImageIcon("setting"), StyleConstants.smallRound,
				true);

		toolBar.add(setting);
		toolBar.add(msg);
		toolBar.addSeparator();
		toolBar.add(qzone);
		toolBar.add(weibo);
		toolBar.add(qzone);
		toolBar.add(mail);
		toolBar.addSeparator();
		toolBar.addToEnd(add);
		toolBar.setPreferredSize(new Dimension(view.getWidth(), 30));
		
		// action
		setting.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				IMEventService events = view.getContext().getSerivce(IMService.Type.EVENT);
				events.broadcast(new IMEvent(IMEventType.SHOW_SETTING_WINDOW));
			}
		});
		return toolBar;
	}

}

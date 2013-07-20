package iqq.app.ui.content.main;

import iqq.app.ui.BackgroundPanel;
import iqq.app.ui.IMFrameView;

import java.awt.BorderLayout;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-18
 */
public class MainPanel extends BackgroundPanel {
	private static final long serialVersionUID = -810346599535079894L;
	private IMFrameView view;
	private HeaderPanel header;
	private MiddlePanel center;
	private FooterPanel footer;

	/**
	 * @param view
	 */
	public MainPanel(IMFrameView view) {
		super(view);
		this.view = view;
		addContent();
	}

	/**
	 * 初始化显示内容
	 */
	private void addContent() {
		header = new HeaderPanel(view);
		center = new MiddlePanel(view);
		footer = new FooterPanel(view);

		add(header, BorderLayout.PAGE_START);
		add(center, BorderLayout.CENTER);
		add(footer, BorderLayout.PAGE_END);
	}

}

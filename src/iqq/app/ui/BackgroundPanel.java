package iqq.app.ui;

import java.awt.Window;

import com.alee.laf.panel.WebPanel;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-16
 */
public class BackgroundPanel extends WebPanel {

	private static final long serialVersionUID = -4326635468326074828L;
	
	private Window view;

	/**
	 * @param application
	 */
	public BackgroundPanel(Window view) {
		super();
		this.view = view;
	}

	/**
	 * @return the frameView
	 */
	public Window getView() {
		return view;
	}

}

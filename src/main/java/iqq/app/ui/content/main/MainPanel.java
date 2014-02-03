package iqq.app.ui.content.main;

import iqq.app.ui.BackgroundPanel;
import iqq.app.ui.IMFrameView;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;

import com.alee.laf.panel.WebPanel;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-18
 */
public class MainPanel extends BackgroundPanel {
	private static final long serialVersionUID = -810346599535079894L;
	public static final String SEARCH_BUDDY_MODEL = "searchBuddyModel";
	public static final String CLOSE_SEARCH_BUDDY_MODEL = "closeSearchBuddyModel";
	private IMFrameView view;
	private HeaderPanel header;
	private static MiddlePanel center;
	private FooterPanel footer;
	private WebPanel srBuddyModel;

	/**
	 * @param view
	 */
	public MainPanel(IMFrameView view) {
		super(view);
		this.view = view;
		addContent();
		view.addPropertyChangeListener(SEARCH_BUDDY_MODEL, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				showSearBuddyPanel((WebPanel)evt.getNewValue());
			}
		});
		view.addPropertyChangeListener(CLOSE_SEARCH_BUDDY_MODEL, new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				closeSearBuddyPanel();
			}
		});
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
	
	private void showSearBuddyPanel(WebPanel sbResustPanel){
		remove(center);
		srBuddyModel = sbResustPanel;
		add(sbResustPanel,BorderLayout.CENTER);
		this.updateUI();
	}
	
	public void closeSearBuddyPanel(){
		remove(srBuddyModel);
		add(center);
		this.updateUI();
	}

}

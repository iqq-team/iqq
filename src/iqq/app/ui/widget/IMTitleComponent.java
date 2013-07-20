package iqq.app.ui.widget;

import iqq.app.service.IMSkinService.Type;
import iqq.app.ui.BackgroundPanel;
import iqq.app.ui.IMFrameView;
import iqq.app.util.I18NUtil;
import iqq.app.util.SettingUtils;
import iqq.app.util.SkinUtils;
import iqq.app.util.SystemUtils;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;

import com.alee.extended.painter.Painter;
import com.alee.extended.panel.WebButtonGroup;
import com.alee.extended.window.ComponentMoveAdapter;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.rootpane.WebRootPaneStyle;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.managers.tooltip.TooltipWay;
import com.alee.utils.SwingUtils;

/**
 * TitleComponent
 * 
 * @author ChenZhiHui
 * @create-time 2013-3-6
 */
public class IMTitleComponent extends BackgroundPanel {

	private static final long serialVersionUID = -7534840790711885140L;

	public static ImageIcon minimizeIcon;
	public static ImageIcon minimizeActiveIcon;
	public static ImageIcon maximizeIcon;
	public static ImageIcon maximizeActiveIcon;
	public static ImageIcon closeIcon;
	public static ImageIcon closeActiveIcon;

	private IMFrameView view;
	private Window window;
	private Frame frame;
	private Dialog dialog;

	private String title;
	private int state;
	private int linuxState;
	private int shadeWidth = 30;

	private WebLabel titleLbl;
	private WebButton minBtn;
	private WebButton maxBtn;
	private WebButton closeBtn;
	private WebButtonGroup groupBtns;

	private Painter<?> headerNpip;

	private StateActionListener mouseListener;
	private PropertyChangeListener titleChangeListener;

	private boolean showMinimizeButton = WebRootPaneStyle.showMinimizeButton;
	private boolean showMaximizeButton = WebRootPaneStyle.showMaximizeButton;
	private boolean showCloseButton = WebRootPaneStyle.showCloseButton;

	public IMTitleComponent(Window win) {
		this(win, true);
	}
	
	public IMTitleComponent(Window win, boolean resizeWindow) {
		super(win);

		if(win instanceof IMFrameView) {
			this.view = (IMFrameView)win;
			this.title = this.view.getTitle();
		}
		
		this.window = win != null ? win : null;
		frame = win instanceof Frame ? (Frame) win : null;
		dialog = win instanceof Dialog ? (Dialog) window : null;

		if (isFrame()) {
			state = frame.getState();
		}

		if (frame instanceof WebFrame) {
			WebFrame f = (WebFrame) frame;
			f.setShowTitleComponent(false);
			f.setShowWindowButtons(false);
			/**
			 * 解决LINUX兼容问题
			 */
			if (SystemUtils.isLinux()) {
				if(!frame.isUndecorated()) {
					// frame.setUndecorated(true);
					this.setVisible(false);
					if(resizeWindow) {
						frame.setSize(f.getWidth() - 40, f.getHeight());
					}
				}
			}
		} else {
			WebDialog d = (WebDialog) window;
			d.setShowTitleComponent(false);
			d.setShowWindowButtons(false);
			/**
			 * 解决LINUX兼容问题
			 */
			if (SystemUtils.isLinux()) {
				if(!d.isUndecorated()) {
					//d.setUndecorated(true);
					this.setVisible(false);
					if(resizeWindow) {
						d.setSize(d.getWidth() - 40, d.getHeight());
					}
				}
			}
		}

		minimizeIcon = (ImageIcon) SkinUtils.getImageIcon("minimizeIcon");
		minimizeActiveIcon = (ImageIcon) SkinUtils
				.getImageIcon("minimizeActiveIcon");
		maximizeIcon = (ImageIcon) SkinUtils.getImageIcon("maximizeIcon");
		maximizeActiveIcon = (ImageIcon) SkinUtils
				.getImageIcon("maximizeActiveIcon");
		closeIcon = (ImageIcon) SkinUtils.getImageIcon("closeIcon");
		closeActiveIcon = (ImageIcon) SkinUtils.getImageIcon("closeActiveIcon");

		headerNpip = (Painter<?>) SkinUtils.getPainter(Type.NPICON,
				"transparent");

		initTitleComponent();
		initButtonsComponent();
		// setPainter(headerNpip);
		setPreferredSize(new Dimension(window.getWidth(), 30));
		setOpaque(false);
	}

	private void initTitleComponent() {
		titleLbl = new WebLabel(title);
		titleLbl.setMargin(4, 5, 4, 10);
		titleLbl.setIconTextGap(5);
		titleLbl.setDrawShade(true);
		titleLbl.setHorizontalAlignment(WebLabel.LEADING);
		SwingUtils.setFontSize(titleLbl, 13);
		titleLbl.setToolTipText(title);

		if (isFrame()) {
			Image logo = frame.getIconImage();
			if (logo != null) {
				logo = logo.getScaledInstance(20, 20, 100);
				titleLbl.setIcon(new ImageIcon(logo));
			}
		}
		this.add(titleLbl, BorderLayout.CENTER);

		// Window move and max/restore listener
		ComponentMoveAdapter cma = new ComponentMoveAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (isFrame() && SwingUtils.isLeftMouseButton(e)
						&& e.getClickCount() == 2) {
					if (isFrameMaximized()) {
						restore();
					} else {
						maximize();
					}
				}
			}

			public void mouseDragged(MouseEvent e) {
				if (dragging && isFrameMaximized()) {
					initialPoint = new Point(initialPoint.x + shadeWidth,
							initialPoint.y + shadeWidth);
					restore();
				}
				super.mouseDragged(e);
			}
		};
		titleLbl.addMouseListener(cma);
		titleLbl.addMouseMotionListener(cma);

		titleChangeListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				titleLbl.setText(evt.getNewValue().toString());
				titleLbl.revalidate();
				titleLbl.repaint();
				titleLbl.setToolTipText(titleLbl.getText());
			}
		};
		window.addPropertyChangeListener("title", titleChangeListener);
		window.addPropertyChangeListener("appLogo", new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				setTitleIcon((Image) evt.getNewValue());
			}
		});
	}

	private void initButtonsComponent() {
		mouseListener = new StateActionListener();

		groupBtns = new WebButtonGroup();
		minBtn = new WebButton(minimizeIcon);
		minBtn.setPainter(headerNpip);
		minBtn.addMouseListener(mouseListener);

		maxBtn = new WebButton(maximizeIcon);
		maxBtn.setPainter(headerNpip);
		maxBtn.addMouseListener(mouseListener);

		closeBtn = new WebButton(closeIcon);
		closeBtn.setPainter(headerNpip);
		closeBtn.addMouseListener(mouseListener);

		groupBtns.add(minBtn);
		groupBtns.add(maxBtn);
		groupBtns.add(closeBtn);

		groupBtns.setButtonsMargin(2);
		groupBtns.setMargin(0, 0, 3, 5);

		this.add(groupBtns, BorderLayout.EAST);
	}
	
	protected void iconify() {
		if (frame != null) {
			frame.setExtendedState(Frame.ICONIFIED);
			state = Frame.ICONIFIED;
		}
	}

	public void maximize() {
		if (frame != null) {
			if (isFrameMaximized()) {
				restore();
			} else {
				if (SystemUtils.isLinux()) {
					frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
				} else {
					frame.setExtendedState(Frame.MAXIMIZED_BOTH);
				}
				state = Frame.MAXIMIZED_BOTH;
				linuxState = Frame.MAXIMIZED_BOTH;
			}
		}
	}

	public void restore() {
		if (frame != null) {
			if (SystemUtils.isLinux()) {
				frame.setSize(SettingUtils.getInt("appWidth") - 40,
						SettingUtils.getInt("appHeight"));
			}
			frame.setExtendedState(Frame.NORMAL);
			state = Frame.NORMAL;
			linuxState = Frame.NORMAL;
		}
	}

	public void close() {
		if (window != null) {
			window.dispatchEvent(new WindowEvent(window,
					WindowEvent.WINDOW_CLOSING));
		}
	}

	protected boolean isFrame() {
		return frame != null;
	}

	protected boolean isDialog() {
		return dialog != null;
	}

	protected boolean isFrameMaximized() {
		return isFrame()
				&& (state == Frame.MAXIMIZED_BOTH || linuxState == Frame.MAXIMIZED_BOTH);
	}

	class StateActionListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			WebButton b = (WebButton) e.getComponent();
			if (b == minBtn) {
				iconify();
			} else if (b == maxBtn) {
				maximize();
			} else {
				close();
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
			WebButton b = (WebButton) e.getComponent();
			if (b == minBtn) {
				minBtn.setIcon(minimizeActiveIcon);
				TooltipManager.setTooltip(b, I18NUtil.getMessage("minimize"), TooltipWay.down, 0);
			} else if (b == maxBtn) {
				maxBtn.setIcon(maximizeActiveIcon);
				TooltipManager.setTooltip(b, I18NUtil.getMessage("maximize"), TooltipWay.down, 0);
			} else {
				closeBtn.setIcon(closeActiveIcon);
				TooltipManager.setTooltip(b, I18NUtil.getMessage("close"), TooltipWay.down, 0);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			WebButton b = (WebButton) e.getComponent();
			if (b == minBtn) {
				minBtn.setIcon(minimizeIcon);
			} else if (b == maxBtn) {
				maxBtn.setIcon(maximizeIcon);
			} else if (b == closeBtn) {
				closeBtn.setIcon(closeIcon);
			}

		}

	}

	public void setTitleIcon(Image image) {
		if (isFrame()) {
			Image logo = frame.getIconImage();
			if (logo != null) {
				logo = logo.getScaledInstance(20, 20, 100);
				titleLbl.setIcon(new ImageIcon(logo));
			}
		} else if (image != null) {
			image = image.getScaledInstance(20, 20, 100);
			titleLbl.setIcon(new ImageIcon(image));
			titleLbl.revalidate();
			titleLbl.repaint();
		}
	}

	/**
	 * @return the showMinimizeButton
	 */
	public boolean isShowMinimizeButton() {
		return showMinimizeButton;
	}

	/**
	 * @param showMinimizeButton
	 *            the showMinimizeButton to set
	 */
	public void setShowMinimizeButton(boolean showMinimizeButton) {
		this.showMinimizeButton = showMinimizeButton;
		if (!showMinimizeButton) {
			groupBtns.remove(minBtn);
		}
	}

	/**
	 * @return the showMaximizeButton
	 */
	public boolean isShowMaximizeButton() {
		return showMaximizeButton;
	}

	/**
	 * @param showMaximizeButton
	 *            the showMaximizeButton to set
	 */
	public void setShowMaximizeButton(boolean showMaximizeButton) {
		this.showMaximizeButton = showMaximizeButton;
		if (!showMaximizeButton) {
			groupBtns.remove(maxBtn);
		}
	}

	/**
	 * @return the showCloseButton
	 */
	public boolean isShowCloseButton() {
		return showCloseButton;
	}

	/**
	 * @param showCloseButton
	 *            the showCloseButton to set
	 */
	public void setShowCloseButton(boolean showCloseButton) {
		this.showCloseButton = showCloseButton;
		if (!showCloseButton) {
			groupBtns.remove(closeBtn);
		}
	}
}

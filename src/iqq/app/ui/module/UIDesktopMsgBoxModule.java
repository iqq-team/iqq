/**
 * 
 */
package iqq.app.ui.module;

import iqq.app.core.IMContext;
import iqq.app.core.IMException;
import iqq.app.core.IMService;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventHandler;
import iqq.app.event.IMEventHandlerProxy;
import iqq.app.event.IMEventType;
import iqq.app.service.IMEventService;
import iqq.app.service.IMSkinService;
import iqq.app.service.IMTimerService;
import iqq.app.ui.IMDialogView;
import iqq.app.ui.renderer.MsgBoxListCellRenderer;
import iqq.app.ui.widget.hideframe.IPosition;
import iqq.app.ui.widget.hideframe.Positions;
import iqq.app.util.SkinUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.SwingConstants;

import com.alee.extended.painter.ColorBackgroundPainter;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.DefaultListModel;
import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;

/**
 * 托盘迷你消息提示框
 * 
 * @author ZhiHui_Chen<6208317@qq.com>
 * @create date 2013-4-3
 */
public class UIDesktopMsgBoxModule extends IMDialogView {
	private static final long serialVersionUID = -3141867331970631022L;
	private int sideWidth = 100; // 边宽度
	private int side = 1; // 左显示多少隐藏后可见，最少为1
	private int gap = 0;
	private int shadeWidthSide = 0;
	private int sideAndShadeWidth = 0;
	private int hide = 2;
	private int msgComingSide = 20;
	private boolean comingAlert = false;

	private Rectangle rect;
	private int frameLeft;// 窗体离屏幕左边的距离
	private int frameRight;// 窗体离屏幕右边的距离；
	private int frameTop;// 窗体离屏幕顶部的距离
	private int frameWidth; // 窗体的宽
	private int frameHeight; // 窗体的高

	private int screenXX;// 屏幕的宽度；
	private Point point; // 鼠标在窗体的位置

	private IPosition position;
	private int xx, yy;
	private boolean isDraging = false;

	private WebPanel rootPanel;
	private WebPanel headerPl;
	private WebPanel footerPl;
	private WebList msgList;
	private DefaultListModel msgModel;

	private DesktopTimerTask msgTimer;

	@Override
	public void init(IMContext context) throws IMException {
		super.init(context);
		// 注册感兴趣的事件
		IMEventHandlerProxy.register(this, context);

		initFrame();
		createContent();
		
		msgTimer = new DesktopTimerTask();
	}

	public void start() {
		frameLeft = (int) getBounds().getX();
		frameTop = (int) getBounds().getY();
		frameWidth = getWidth();
		frameHeight = getHeight();
		screenXX = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
		frameRight = screenXX - frameLeft - frameWidth;

		// 获取窗体的轮廓
		rect = new Rectangle(0, 0, frameWidth, frameHeight);
		// 获取鼠标在窗体的位置
		point = getMousePosition();

		if (position == Positions.LEFT) {
			if (frameLeft < -sideAndShadeWidth && isPtInRect(rect, point)) {
				setLocation(-shadeWidthSide, frameTop); // 隐藏在左边，鼠标指到后显示窗体；
			} else if (frameLeft > -sideAndShadeWidth + hide
					&& frameLeft < sideAndShadeWidth + hide
					&& !(isPtInRect(rect, point))) {
				setLocation(-frameWidth + sideAndShadeWidth, frameTop); // 窗体移到左边边缘隐藏到左边；
			}
		} else if (position == Positions.TOP) {
			if (frameTop < -sideAndShadeWidth && isPtInRect(rect, point)) {
				setLocation(frameLeft, -shadeWidthSide); // 隐藏在左边，鼠标指到后显示窗体；
			} else if (frameTop > -sideAndShadeWidth + hide
					&& frameTop < sideAndShadeWidth + hide
					&& !(isPtInRect(rect, point))) {
				setLocation(frameLeft, -frameHeight + sideAndShadeWidth); // 窗体移到屏幕后隐藏
			}
		} else if (position == Positions.RIGHT) {
			if (frameRight < -sideAndShadeWidth && isPtInRect(rect, point)) {
				setLocation(screenXX - frameWidth + shadeWidthSide, frameTop);// 鼠标指到后显示；
			} else if (frameRight > -sideAndShadeWidth - hide
					&& frameRight < sideAndShadeWidth + hide
					&& !(isPtInRect(rect, point))) {
				setLocation(screenXX - sideAndShadeWidth, frameTop); // 窗体移到屏幕后隐藏
			}
		}

	}

	/**
	 * 检测是否在矩形框内
	 * 
	 * @param rect
	 * @param point
	 * @return
	 */
	public boolean isPtInRect(Rectangle rect, Point point) {
		if (rect != null && point != null) {
			int x0 = rect.x;
			int y0 = rect.y;
			int x1 = rect.width;
			int y1 = rect.height;
			int x = point.x;
			int y = point.y;

			return x >= x0 && x < x1 && y >= y0 && y < y1;
		}
		return false;
	}

	public void setPosition(IPosition position, int sideWidth, int side) {
		setPosition(position, sideWidth, side, 0);
	}

	public void setPosition(IPosition position, int sideWidth, int side, int gap) {
		this.position = position;
		this.sideWidth = sideWidth;
		this.side = side;
		this.sideAndShadeWidth = side + shadeWidthSide;
		this.gap = gap;
		setBounds(position.getPosition(this.sideWidth, this.sideAndShadeWidth,
				this.gap));
	}

	@Override
	public void destroy() throws IMException {
		super.destroy();
		IMEventHandlerProxy.unregister(this);
	}

	@Override
	public void setShadeWidth(int shadeWidth) {
		super.setShadeWidth(shadeWidth);
		this.shadeWidthSide = shadeWidth + 1;
	}

	private void initFrame() {
		setShadeWidth(0);
		setSize(100, 100);
		setUndecorated(true);
		setResizable(false);
		setShowTitleComponent(false);
		setShowWindowButtons(false);
		setPosition(Positions.RIGHT, 70, 0, 80); // 设置在屏幕显示地方
		setAlwaysOnTop(true);
		setVisible(true);

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				isDraging = true;
				xx = e.getX();
				yy = e.getY();
			}

			public void mouseReleased(MouseEvent e) {
				isDraging = false;
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if (isDraging) {
					int left = getLocation().x;
					int top = getLocation().y;
					setLocation(left + e.getX() - xx, top + e.getY() - yy);
					repaint();
				}
			}
		});
	}

	/**
	 * 创建显示内容
	 */
	private void createContent() {
		rootPanel = new WebPanel();
		headerPl = new WebPanel();
		footerPl = new WebPanel();
		msgModel = new DefaultListModel();
		msgList = new WebList(msgModel);
		msgList.setCellRenderer(new MsgBoxListCellRenderer());

		rootPanel.setOpaque(false);
		headerPl.setOpaque(false);
		msgList.setOpaque(false);
		footerPl.setOpaque(false);
		rootPanel.setBorder(null);

		headerPl.setMargin(5);
		headerPl.setPainter(new ColorBackgroundPainter(
				new Color(60, 60, 60, 80)));

		WebLabel titile = new WebLabel("Msg Box");
		titile.setDrawShade(true);
		titile.setShadeColor(Color.LIGHT_GRAY);
		titile.setForeground(new Color(220, 220, 200));
		titile.setHorizontalAlignment(SwingConstants.CENTER);
		headerPl.add(titile, BorderLayout.PAGE_START);

		WebScrollPane listSp = new WebScrollPane(msgList, false);
		listSp.setOpaque(false);
		listSp.setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		listSp.setVerticalScrollBarPolicy(WebScrollPane.VERTICAL_SCROLLBAR_NEVER);
		listSp.setBorder(null);
		listSp.setMargin(10, 0, 0, 0);
		listSp.setShadeWidth(0);
		listSp.setRound(0);

		rootPanel.add(headerPl, BorderLayout.PAGE_START);
		rootPanel.add(listSp, BorderLayout.CENTER);
		rootPanel.add(footerPl, BorderLayout.PAGE_END);
		setContentPanel(rootPanel);
		changeSkin(SkinUtils
				.getPainter(IMSkinService.Type.NPICON, "msgbox/bg2"));
		// changeSkin(new ColorBackgroundPainter(new Color(30, 30, 30)));

		msgList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (msgList.getSelectedIndex() == -1) {
						return;
					}
					IMEventService events = getContext().getSerivce(
							IMService.Type.EVENT);
					IMEvent imEvent = new IMEvent(IMEventType.SHOW_CHAT,
							msgList.getSelectedValue());

					events.broadcast(imEvent);
				}
			}
		});
	}

	@Override
	public void validate() {
		setAlwaysOnTop(true);
		super.validate();
	}

	@IMEventHandler(IMEventType.FLASH_USER_START)
	protected void processIMFlashUserStart(IMEvent event) {
		Object entity = event.getTarget();
		if (!msgModel.contains(entity)) {
			msgModel.addElement(entity);
			if (msgModel.size() == 1) {
				this.setVisible(true);
				IMTimerService timer = getContext().getSerivce(
						IMService.Type.TIMER);
				timer.setInterval(msgTimer, 300);
			}
		}
	}

	@IMEventHandler(IMEventType.FLASH_USER_STOP)
	protected void processIMFlashUserStop(IMEvent event) {
		Object entity = event.getTarget();
		if (!msgModel.isEmpty() && msgModel.contains(entity)) {
			msgModel.removeElement(entity);
		} else if (msgModel.isEmpty()) {
			IMTimerService timer = getContext()
					.getSerivce(IMService.Type.TIMER);
			timer.killTimer(msgTimer);
			this.setVisible(false);
		}
	}

	private class DesktopTimerTask implements Runnable {

		@Override
		public void run() {
			start();
		}

	}
}

package iqq.app.ui.module;

import iqq.app.core.IMContext;
import iqq.app.core.IMException;
import iqq.app.core.IMService;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventHandler;
import iqq.app.event.IMEventHandlerProxy;
import iqq.app.event.IMEventType;
import iqq.app.service.IMEventService;
import iqq.app.service.IMPropService;
import iqq.app.service.IMSkinService;
import iqq.app.service.IMTimerService;
import iqq.app.ui.IMWindowView;
import iqq.app.util.UIUtils;
import iqq.im.bean.QQAccount;
import iqq.im.bean.QQDiscuz;
import iqq.im.bean.QQGroup;
import iqq.im.bean.QQUser;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.Deque;
import java.util.LinkedList;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.alee.extended.painter.ColorPainter;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.button.WebButton;
import com.alee.laf.menu.WebMenuItem;
import com.alee.laf.menu.WebPopupMenu;
import com.alee.utils.ImageUtils;
import com.alee.utils.SystemUtils;

/**
 * 
 * 桌面悬浮通知
 * 
 * @author Zhihui_Chen
 *
 */
public class UINotificationModule extends IMWindowView {
	private static final long serialVersionUID = -7263706169144783333L;
	private boolean isLogin = false; // 是否已经登录
	private WebButton btnNotification; // 通知图标按钮
	private Runnable flashTimer;
	private Icon flashFace; // 当前闪动的头像
	private Icon defaultFace; // 默认头像
	private Icon blankFace; // 空白的头像
	private Object flashOwner; // 当前闪烁的用户
	private Deque<Object> flashQueue; // 带闪烁的对象列表
	private QQAccount self;

	@Override
	public void init(IMContext context) throws IMException {
		// TODO Auto-generated method stub
		super.init(context);
		IMEventHandlerProxy.register(this, context);

		IMPropService propService = context.getSerivce(IMService.Type.PROP);
		if(propService.getInt("desktopNotification") == 0) {
			return ;
		}
		
		initUI();
		initIcon();
	}

	@Override
	public void destroy() throws IMException {
		super.destroy();
		IMEventHandlerProxy.unregister(this);
		IMTimerService timer = getContext().getSerivce(IMService.Type.TIMER);
		timer.killTimer(flashTimer);
	}

	private void initIcon() {
		IMSkinService skins = getContext().getSerivce(IMService.Type.SKIN);
		ImageIcon img = skins.getImageIcon("defaultFace");
		defaultFace = new ImageIcon(ImageUtils.getBufferedImage(img));
		blankFace = new ImageIcon(new BufferedImage(32, 32,
				BufferedImage.TYPE_INT_ARGB));
		btnNotification.setIcon(defaultFace);
		flashQueue = new LinkedList<Object>();
		IMTimerService timer = getContext().getSerivce(IMService.Type.TIMER);
		flashTimer = new FlashTrayTimer();
		timer.setInterval(flashTimer, 500);
	}

	private void initUI() {
		int x = Toolkit.getDefaultToolkit().getScreenSize().width - 160;
		int y = 100;
		setSize(40, 40);
		setAlwaysOnTop(true);
		setLocation(x, y);
		//setOpacity(0.6f);

		btnNotification = new WebButton();
		btnNotification.setPainter(new ColorPainter(Color.lightGray));
		add(btnNotification);
		setVisible(true);

		final IMEventService events = getContext().getSerivce(
				IMService.Type.EVENT);
		final WebPopupMenu pop = new WebPopupMenu();
		WebMenuItem restore = new WebMenuItem("Restore");
		restore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				events.broadcast(new IMEvent(IMEventType.SHOW_MAIN_FRAME));
			}
		});
		pop.add(restore);

		// 退出
		WebMenuItem exit = new WebMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				events.broadcast(new IMEvent(IMEventType.APP_EXIT_READY));
			}
		});
		pop.add(exit);
		btnNotification.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				//setOpacity(0.6f);
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				//setOpacity(0.9f);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					// 如果有消息闪动，就弹出消息
					if (flashOwner != null) {
						events.broadcast(new IMEvent(IMEventType.SHOW_CHAT,
								flashOwner));
					} else {
						// 显示主窗口
						if (isLogin) {
							events.broadcast(new IMEvent(
									IMEventType.SHOW_MAIN_FRAME));
						}
					}
				}
				if (e.getButton() == MouseEvent.BUTTON3) {// 右键点击
					pop.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		btnNotification.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				setLocation(e.getXOnScreen() - btnNotification.getWidth() / 2,
						e.getYOnScreen() - btnNotification.getSize().height / 2);
			}
		});
	}

	@IMEventHandler(IMEventType.LOGIN_SUCCESS)
	protected void processIMLoginSucess(IMEvent event) {
		self = (QQAccount) event.getTarget();
		if (self.getFace() != null) {
			blankFace = new ImageIcon(self.getFace());
		}
		isLogin = true;
	}

	@IMEventHandler(IMEventType.SELF_INFO_UPDATE)
	protected void processIMSelfInfoUpdate(IMEvent event) {
		updateSelfFace();
	}

	@IMEventHandler(IMEventType.SELF_FACE_UPDATE)
	protected void processIMSelfFaceUpdate(IMEvent event) {
		updateSelfFace();
		if (self.getFace() != null) {
			blankFace = new ImageIcon(self.getFace());
		}
	}

	@IMEventHandler(IMEventType.FLASH_USER_START)
	protected void processIMFlashUserStart(IMEvent event) {
		if (flashQueue == null)
			return;
		if (flashQueue.contains(event.getTarget())) {
			flashQueue.remove(event.getTarget());
		}
		if (flashOwner != null && flashOwner != event.getTarget()) {
			flashQueue.addFirst(flashOwner);
		}
		flashOwner = event.getTarget();
		flashFace = getTrayFace(flashOwner);
		flashTimer.run();
	}

	@IMEventHandler(IMEventType.FLASH_USER_STOP)
	protected void processIMFlashUserStop(IMEvent event) {
		if (flashQueue == null)
			return;
		if (flashQueue.isEmpty()) {
			flashOwner = null;
			flashFace = null;
			btnNotification.setIcon(defaultFace);
		} else if (flashOwner != event.getTarget()) {
			flashQueue.remove(flashOwner);
		} else {
			flashOwner = flashQueue.poll();
			flashFace = getTrayFace(flashOwner);
			flashTimer.run();
		}
	}

	@IMEventHandler(IMEventType.USER_FACE_UPDATE)
	protected void processIMUserFaceUpdate(IMEvent event) {
		if (flashQueue == null)
			return;
		QQUser user = (QQUser) event.getTarget();
		if (flashOwner != null && flashOwner == user) {
			flashFace = getTrayFace(user);
		}
	}

	@IMEventHandler(IMEventType.CLIENT_OFFLINE)
	protected void processIMClientOffline(IMEvent event) {
		updateSelfFace();
	}

	@IMEventHandler(IMEventType.CLIENT_ONLINE)
	protected void processIMClientOnline(IMEvent event) {
		updateSelfFace();
	}

	@IMEventHandler(IMEventType.CHANGE_STATUS_SUCCESS)
	protected void processIMChangeSatusSucess(IMEvent event) {
		updateSelfFace();
	}

	private void updateSelfFace() {
		defaultFace = new ImageIcon(UIUtils.Bean.drawStatusFace(self,
				getContext()));
		if (flashOwner == null && btnNotification != null) {
			btnNotification.setIcon(defaultFace);
			btnNotification.setToolTipText(self.getUin() + "\n"
					+ self.getNickname() + "\n" + self.getStatus().getValue());
		}
	}

	private Icon getTrayFace(Object owner) {
		BufferedImage face = null;
		IMSkinService skins = getContext().getSerivce(IMService.Type.SKIN);
		if (owner instanceof QQUser) {
			face = ((QQUser) owner).getFace();
		} else if (owner instanceof QQGroup) {
			face = ((QQGroup) owner).getFace();
		} else if (owner instanceof QQDiscuz) {
			face = skins.getBufferedImage("discuzIcon");
		} else {
			throw new IllegalArgumentException("unknown tray face!!!!" + owner);
		}

		if (face == null) {
			face = UIUtils.Bean.getDefaultFace(owner);
		}
		return new ImageIcon(face.getScaledInstance(32, 32, 100));
	}

	private class FlashTrayTimer implements Runnable {
		@Override
		public void run() {
			if (flashOwner != null && btnNotification != null
					&& flashFace != null) {
				Icon curImg = btnNotification.getIcon();
				btnNotification.setIcon(curImg == flashFace ? blankFace
						: flashFace);
			}
		}
	}
}

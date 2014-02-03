/**
 * 
 */
package iqq.app.ui.module;

import iqq.app.bean.UINamedObject;
import iqq.app.bean.UIUser;
import iqq.app.core.IMContext;
import iqq.app.core.IMException;
import iqq.app.core.IMService;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventHandler;
import iqq.app.event.IMEventHandlerProxy;
import iqq.app.event.IMEventType;
import iqq.app.service.IMEventService;
import iqq.app.ui.BackgroundPanel;
import iqq.app.ui.IMWindowView;
import iqq.app.util.I18NUtil;
import iqq.app.util.UIUtils;
import iqq.im.bean.QQUser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

import javax.swing.ImageIcon;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alee.extended.image.WebDecoratedImage;
import com.alee.extended.painter.ColorPainter;
import com.alee.extended.panel.GroupPanel;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;

/**
 * 用户或群名片卡
 * 
 * @author ZhiHui_Chen<6208317@qq.com>
 * @create date 2013-4-21
 */
public class UIHoverInfoCardModule extends IMWindowView {
	private static final long serialVersionUID = -2757256296223203617L;
	private static final Logger LOG = Logger.getLogger(UIHoverInfoCardModule.class);

	private HoverInfoCardPanel contentPane;
	private boolean isMouseEntered;
	private UINamedObject namedObject;

	@Override
	public void init(IMContext context) throws IMException {
		super.init(context);

		// 注册感兴趣的事件
		IMEventHandlerProxy.register(this, context);

		initView();
		initContent();
	}

	@Override
	public void destroy() throws IMException {
		super.destroy();
		IMEventHandlerProxy.unregister(this);
	}

	private void initView() {
		setSize(230, 160);
		setAlwaysOnTop(true);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				isMouseEntered = true;
			}

			@Override
			public void mouseExited(MouseEvent e) {
				isMouseEntered = false;
				setVisible(false);
			}
		});
	}

	private void initContent() {
		contentPane = new HoverInfoCardPanel(this);
		setContentPanel(contentPane);
		changeSkin(new ColorPainter(new Color(200, 200, 200)));
	}

	@IMEventHandler(IMEventType.SHOW_HOVER_INFOCARD_WINDOW)
	protected void processIMInfoCardShow(IMEvent event) {
		namedObject = event.getData("namedObject");
		Window win = event.getData("view");
		Rectangle bounds = event.getData("nodeBounds");
		Component comp = event.getData("comp");
		
		if(!win.isVisible()) return;

		Point p = comp.getLocationOnScreen();
		int x = p.x - getWidth() - 5;
		int y = p.y + bounds.y + 30;

		setLocation(x, y);
		setVisible(true);

		contentPane.updateInfo(namedObject);

		if (namedObject instanceof UIUser) {
			QQUser user = (QQUser) namedObject.getEntity();
			if (user.getBirthday() == null) {
				IMEventService eventHub = getContext().getSerivce(
						IMService.Type.EVENT);
				eventHub.broadcast(new IMEvent(IMEventType.USER_INFO_REQUEST,
						user));
			}
			if(user.getLevel().getHours() <= 0) {
				IMEventService eventHub = getContext().getSerivce(
						IMService.Type.EVENT);
				eventHub.broadcast(new IMEvent(IMEventType.USER_LEVEL_REQUEST,
						user));
			}
		}
	}

	@IMEventHandler(IMEventType.HIDE_HOVER_INFOCARD_WINDOW)
	protected void processIMInfoCardHide(IMEvent event) {
		if (!isMouseEntered) {
			setVisible(false);
		}
	}

	@IMEventHandler(IMEventType.USER_INFO_UPDATE)
	protected void processIMUserInfoUpdate(IMEvent event) {
		if (namedObject != null && namedObject.getEntity() == event.getTarget()) {
			contentPane.updateInfo(namedObject);
		}
	}
	
	@IMEventHandler(IMEventType.USER_LEVEL_UPDATE)
	protected void processIMUserLevelUpdate(IMEvent event) {
		if (namedObject != null && namedObject.getEntity() == event.getTarget()) {
			contentPane.updateInfo(namedObject);
		}
	}
	
	@IMEventHandler(IMEventType.USER_STATUS_UPDATE)
	protected void processIMUserStatusUpdate(IMEvent event) {
		if (namedObject != null && namedObject.getEntity() == event.getTarget()) {
			contentPane.updateInfo(namedObject);
		}
	}
	
	@IMEventHandler(IMEventType.WINDOW_MAIN_LOST_FOCUS)
	protected void processIMMainWindowLostFocus(IMEvent event) {
		if(isVisible()) {
			setVisible(false);
		}
	}

	public class HoverInfoCardPanel extends BackgroundPanel {
		private static final long serialVersionUID = -1322939003125933902L;
		protected transient WebDecoratedImage iconImg;
		protected WebLabel firstNameLbl;
		protected WebLabel lastNameLbl;
		protected WebLabel textLbl;

		protected WebLabel sexLbl;
		protected WebLabel ageLbl;
		protected WebLabel countryLbl;
		protected WebLabel provinceLbl;
		protected WebLabel professionLbl;
		protected WebLabel emainLbl;

		protected WebLabel clientLbl;
		protected WebLabel levelLbl;

		private UINamedObject namedObject;

		/**
		 * @param view
		 */
		public HoverInfoCardPanel(Window view) {
			super(view);
			setRound(10);
			setOpaque(false);

			initComponent();
		}

		private void initComponent() {
			iconImg = new WebDecoratedImage(UIUtils.Bean.getDefaultFace());
			firstNameLbl = new WebLabel();
			lastNameLbl = new WebLabel();
			textLbl = new WebLabel();

			lastNameLbl.setForeground(Color.GRAY);
			iconImg.setShadeWidth(1);
			iconImg.setRound(4);

			Color textColor = new Color(90, 90, 90);
			sexLbl = new WebLabel();
			ageLbl = new WebLabel();
			countryLbl = new WebLabel();
			provinceLbl = new WebLabel();
			professionLbl = new WebLabel();
			emainLbl = new WebLabel();
			clientLbl = new WebLabel();
			levelLbl = new WebLabel();
			
			sexLbl.setForeground(textColor);
			ageLbl.setForeground(textColor);
			countryLbl.setForeground(textColor);
			provinceLbl.setForeground(textColor);
			professionLbl.setForeground(textColor);
			emainLbl.setForeground(textColor);
			clientLbl.setForeground(textColor);
			levelLbl.setForeground(textColor);
			levelLbl.setMargin(0, 0, 0, 10);

			GroupPanel namingPl = new GroupPanel(false, new GroupPanel(true,
					firstNameLbl, lastNameLbl), textLbl);
			namingPl.setMargin(5);

			WebPanel headerPl = new WebPanel();
			headerPl.setOpaque(false);
			headerPl.setMargin(5);
			headerPl.add(iconImg, BorderLayout.LINE_START);
			headerPl.add(namingPl, BorderLayout.CENTER);
			add(headerPl, BorderLayout.PAGE_START);

			GroupPanel leftGp = new GroupPanel(false, sexLbl, countryLbl,
					professionLbl, emainLbl);
			GroupPanel rightGp = new GroupPanel(false, ageLbl, provinceLbl);
			leftGp.setGap(2);
			rightGp.setGap(2);
			rightGp.setMargin(0, 0, 0, 10);
			WebPanel middlePl = new WebPanel();
			middlePl.setOpaque(false);
			middlePl.setMargin(0, 10, 0, 10);
			middlePl.add(leftGp, BorderLayout.CENTER);
			middlePl.add(rightGp, BorderLayout.LINE_END);
			add(middlePl, BorderLayout.CENTER);
			
			WebPanel footerPl = new WebPanel();
			footerPl.setOpaque(false);
			footerPl.setMargin(0, 10, 5, 10);
			footerPl.add(clientLbl, BorderLayout.CENTER);
			footerPl.add(levelLbl, BorderLayout.LINE_END);
			add(footerPl, BorderLayout.PAGE_END);
		}

		public void updateInfo(UINamedObject namedObject) {
			iconImg.setIcon(new ImageIcon(namedObject.getIcon()));
			firstNameLbl.setText(namedObject.getName());
			if (namedObject.getSubname() != null) {
				lastNameLbl.setText(" (" + namedObject.getSubname() + ")");
			} else {
				lastNameLbl.setText("");
			}
			textLbl.setText(namedObject.getExtra());

			if (namedObject instanceof UIUser) {
				QQUser user = (QQUser) namedObject.getEntity();
				iconImg.setIcon(new ImageIcon(user.getFace()));
				sexLbl.setText(I18NUtil.getMessage("user.gender") + ": " + I18NUtil.getMessage("user." + user.getGender()));
				Date birthday = user.getBirthday();
				int age = 0;
				try {
					age = birthday == null ? 0 : UIUtils.Bean.dateToAge(birthday);
				} catch (Exception e) {
					LOG.info("date to age error!", e);
				}
				ageLbl.setText(I18NUtil.getMessage("user.age") + ": "  + age);
				countryLbl.setText(I18NUtil.getMessage("user.country") + ": " + (StringUtils.isEmpty(user.getCountry()) ? " - " : user.getCountry()));
				provinceLbl.setText(I18NUtil.getMessage("user.province") + ": " + (StringUtils.isEmpty(user.getProvince()) ? " - " : user.getProvince()));
				professionLbl.setText(I18NUtil.getMessage("user.occupation") + ": " + (StringUtils.isEmpty(user.getOccupation()) ? " - " : user.getOccupation()));
				emainLbl.setText(I18NUtil.getMessage("user.email") + ": " + (StringUtils.isEmpty(user.getEmail()) ? " - " : user.getEmail()));
				
				clientLbl.setText(I18NUtil.getMessage("user.clientType") + ": " + user.getClientType());
				levelLbl.setText(I18NUtil.getMessage("user.level") + ": " + user.getLevel().getLevel());
			} else {
				lastNameLbl.setText("");
				sexLbl.setText("");
				ageLbl.setText("");
				countryLbl.setText("");
				provinceLbl.setText("");
				professionLbl.setText("");
				emainLbl.setText("");
				clientLbl.setText("");
				levelLbl.setText("");
			}
		}

		/*
		 * @Override protected void paintComponent(Graphics g){ Graphics2D g2 =
		 * (Graphics2D)g.create();
		 * g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		 * RenderingHints.VALUE_ANTIALIAS_ON); Composite old =
		 * g2.getComposite();
		 * g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
		 * 0.95f)); g2.setColor(new Color ( 90, 90, 90 )); g2.fillRect(0, 0,
		 * getWidth(), getHeight());
		 * g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
		 * 0.95f)); g2.setColor(new Color ( 200, 200, 200 )); Shape shape = new
		 * RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20);
		 * g2.fill(shape); g2.setComposite(old); g2.dispose(); }
		 */
	}
}

package iqq.app.ui.content.main;

import iqq.app.core.IMContext;
import iqq.app.core.IMService;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventType;
import iqq.app.service.IMEventService;
import iqq.app.service.IMSkinService.Type;
import iqq.app.ui.BackgroundPanel;
import iqq.app.ui.IMFrameView;
import iqq.app.ui.widget.StatusPopup;
import iqq.app.ui.widget.StatusPopup.StatusChangeListner;
import iqq.app.util.I18NUtil;
import iqq.app.util.IMImageUtil;
import iqq.app.util.SkinUtils;
import iqq.im.bean.QQStatus;
import iqq.im.bean.QQUser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.alee.extended.image.WebDecoratedImage;
import com.alee.extended.image.WebImage;
import com.alee.extended.panel.BorderPanel;
import com.alee.extended.panel.CenterPanel;
import com.alee.extended.panel.GroupPanel;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.text.WebTextField;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-18
 */
public class HeaderPanel extends BackgroundPanel {
	private static final long serialVersionUID = -2826795023287424570L;
	public static final String SELF_INFO = "SelfInfo";
	public static final String SELF_SIGN = "SelfSign";
	public static final String SELF_FACE = "SelfFace"; 
	public static final String SELF_STATUS = "SelfStatus";
	private IMFrameView view;
	private WebDecoratedImage faceImg;
	private WebLabel nicknameLbl;
	private WebTextField signFld;
	private WebTextField searchFld;
	private StatusPopup statusPopup;

	/**
	 * @param view
	 */
	public HeaderPanel(IMFrameView view) {
		super(view);
		this.view = view;
		this.setOpaque(false);
		addContent();
		addPropertyListener();
		initListener();
	}

	private void addContent() {
		// 上面是标题栏，下面为内容显示
		add(view.getIMTitleComponent(), BorderLayout.PAGE_START);
		add(createContent());
	}
	
	private void addPropertyListener() {
		this.view.addPropertyChangeListener(SELF_SIGN, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				QQUser user = (QQUser) arg0.getNewValue();
				signFld.setText(user.getSign());
			}
		});
		
		this.view.addPropertyChangeListener(SELF_FACE, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				QQUser user = (QQUser) arg0.getNewValue();
				faceImg.setImage(user.getFace());
			}
		});
		
		this.view.addPropertyChangeListener(SELF_INFO, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				QQUser user = (QQUser) arg0.getNewValue();
				nicknameLbl.setText(user.getNickname());
				statusPopup.setCurrentStatus(user.getStatus());
			}
		});
		
		this.view.addPropertyChangeListener(SELF_STATUS, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				QQStatus status = (QQStatus) arg0.getNewValue();
				statusPopup.setCurrentStatus(status);
			}
		});
	}

	/**
	 * @return
	 */
	private WebPanel createContent() {
		faceImg = new WebDecoratedImage(IMImageUtil.getScaledInstance(
				SkinUtils.getImageIcon("appLogo"), 40, 40));
		faceImg.setShadeWidth(2);
		faceImg.setRound(3);
		nicknameLbl = new WebLabel("IQQ");
		nicknameLbl.setMargin(0, 5, 0, 5);
		signFld = new WebTextField("IQQ For Mac");
		signFld.setMargin(0, 5, 0, 5);
		signFld.setOpaque(false);
		signFld.setPainter(SkinUtils.getPainter(Type.NPICON, "transparent"));
		statusPopup = new StatusPopup();
		statusPopup.setStatusListener(new StatusChangeListner() {
			public void statusChanged(QQStatus newStatus, QQStatus oldStatus) {
				IMContext context = view.getContext();
				IMEventService events = context.getSerivce(IMService.Type.EVENT);
				if(oldStatus == QQStatus.OFFLINE){
					IMEvent event = new IMEvent(IMEventType.RELOGIN_REQEUST, newStatus);
					events.broadcast(event);
				}else{
					IMEvent event = new IMEvent(IMEventType.CHANGE_STATUS_REQUEST, newStatus);
					events.broadcast(event);
				}
			}
		});

		WebPanel pl = new WebPanel();
		pl.add(faceImg, BorderLayout.LINE_START);
		pl.add(new CenterPanel(new GroupPanel(false, new GroupPanel(true,
				nicknameLbl, statusPopup), signFld), false, true),
				BorderLayout.CENTER);
		pl.setOpaque(false);
		pl.setMargin(5, 10, 8, 20);
		GroupPanel groupPanel = new GroupPanel(false, pl, createSearcher());
		return groupPanel;
	}

	private WebPanel createSearcher() {
		searchFld = new WebTextField(I18NUtil.getMessage("findContact"));
		searchFld
				.setPreferredSize(new Dimension(view.getWidth(), 25));
		WebImage searcheImg = new WebImage(IMImageUtil.getScaledInstance(
				SkinUtils.getImageIcon("searchNormal"), 20, 20));
		searcheImg.setMargin(0, 3, 0, 3);
		searchFld.setLeadingComponent(searcheImg);
		searchFld.setPainter(SkinUtils.getPainter(Type.NPICON, "searchBg"));
		searchFld.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				searchFld.setText(I18NUtil.getMessage("findContact"));
			}

			@Override
			public void focusGained(FocusEvent e) {
				searchFld.setText("");
			}
		});

		return new BorderPanel(searchFld);
	}
	
	private void initListener() {
		signFld.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				signFld.select(0, 0);
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				signFld.selectAll();
			}
		});
	}
}

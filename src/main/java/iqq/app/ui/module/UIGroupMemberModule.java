/**
 * 
 */
package iqq.app.ui.module;

import iqq.app.bean.UIDiscuz;
import iqq.app.bean.UIGroup;
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
import iqq.app.ui.IMDialogView;
import iqq.app.ui.bean.GroupMemberListModel;
import iqq.app.ui.bean.UserListElement;
import iqq.app.ui.renderer.GroupMemberListCellRenderer;
import iqq.app.ui.widget.IMTitleComponent;
import iqq.app.util.IMImageUtil;
import iqq.app.util.SkinUtils;
import iqq.im.bean.QQDiscuz;
import iqq.im.bean.QQGroup;
import iqq.im.bean.QQStatus;
import iqq.im.bean.QQStranger;
import iqq.im.bean.QQUser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ListModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alee.extended.progress.WebProgressOverlay;
import com.alee.laf.StyleConstants;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebList;
import com.alee.laf.menu.WebMenuItem;
import com.alee.laf.menu.WebPopupMenu;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebTextField;

/**
 * @author ZhiHui_Chen<6208317@qq.com>
 * @create date 2013-4-10
 */
public class UIGroupMemberModule extends IMDialogView {
	private static final long serialVersionUID = -5092777399603286098L;
	private static final Logger LOG = Logger
			.getLogger(UIGroupMemberModule.class);

	private UINamedObject namedObject;
	private WebPanel content;
	private WebList memberList;
	private WebLabel membersCount;

	private WebProgressOverlay progressOverlay;
	private Map<Object, ListModel> listModels = new HashMap<Object, ListModel>();
	private boolean isLoadFace = false;

	@Override
	public void init(IMContext context) throws IMException {
		super.init(context);

		// 初始化窗口
		initView();
		initMemberList();
		initSeacher();

		// 注册感兴趣的事件
		IMEventHandlerProxy.register(this, context);
	}

	@Override
	public void destroy() throws IMException {
		super.destroy();

		IMEventHandlerProxy.unregister(this);
	}

	/**
	 * 初始化窗口显示
	 */
	private void initView() {
		setSize(240, 500);
		// setAlwaysOnTop(true);

		IMTitleComponent title = getIMTitleComponent();
		title.setShowMinimizeButton(false);
		title.setShowMaximizeButton(false);

		memberList = new WebList();
		memberList.setCellRenderer(new GroupMemberListCellRenderer());
		WebScrollPane listScroll = new WebScrollPane(memberList) {

			{
				setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				setBorder(null);
				setMargin(0);
				setShadeWidth(0);
				setRound(0);
				setDrawBorder(false);
			}
		};

		content = new WebPanel();
		content.add(listScroll, BorderLayout.CENTER);
		WebPanel rootContent = new WebPanel();
		rootContent.add(title, BorderLayout.PAGE_START);
		rootContent.add(content, BorderLayout.CENTER);
		setContentPanel(rootContent);
	}

	/**
	 * 初始化List事件等等动作
	 */
	private void initMemberList() {
		final WebPopupMenu memberPopup = new WebPopupMenu();
		WebMenuItem openChatItem = new WebMenuItem("发送消息");
		WebMenuItem userDetailsItem = new WebMenuItem("查看资料");
		WebMenuItem refreshListItem = new WebMenuItem("刷新列表");
		WebMenuItem refreshFaceItem = new WebMenuItem("刷新头像");
		memberPopup.add(openChatItem);
		memberPopup.addSeparator();
		memberPopup.add(userDetailsItem);
		memberPopup.addSeparator();
		memberPopup.add(refreshListItem);
		memberPopup.addSeparator();
		memberPopup.add(refreshFaceItem);
		memberPopup.addSeparator();

		memberList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					checkSendSessionMsg();
				} else if (e.isMetaDown()) {
					memberPopup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		openChatItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				checkSendSessionMsg();
			}
		});
		userDetailsItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				WebOptionPane.showMessageDialog(UIGroupMemberModule.this,
						"No Implements!!!");
			}
		});
		refreshListItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (namedObject != null) {
					updateMembers(namedObject);
				}
			}
		});
		refreshFaceItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (namedObject != null) {
					updateFaces(namedObject);
				}
			}
		});
	}

	/**
	 * 成员搜索 content
	 */
	private void initSeacher() {
		final WebPanel headerPl = new WebPanel();
		membersCount = new WebLabel("Members (0/0)");
		membersCount.setMargin(0, 5, 0, 0);
		WebButton searcherBtn = WebButton.createIconWebButton(
				IMImageUtil.getScaledInstance(
						SkinUtils.getImageIcon("searchNormal"), 18, 18),
				StyleConstants.smallRound, true);
		final WebTextField seacherTxt = new WebTextField("Find a contact...");
		seacherTxt.setForeground(Color.LIGHT_GRAY);
		seacherTxt.setVisible(false);
		headerPl.add(membersCount, BorderLayout.CENTER);
		headerPl.add(searcherBtn, BorderLayout.LINE_END);
		headerPl.add(seacherTxt, BorderLayout.PAGE_END);

		progressOverlay = new WebProgressOverlay();
		progressOverlay.setComponent(headerPl);
		content.add(progressOverlay, BorderLayout.PAGE_START);
		searcherBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!seacherTxt.isVisible()) {
					seacherTxt.setVisible(true);
					headerPl.revalidate();
					headerPl.repaint();
				} else if (seacherTxt.isVisible()) {
					seacherTxt.setVisible(false);
					headerPl.revalidate();
					headerPl.repaint();
				}
			}
		});
		seacherTxt.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				seacherTxt.setText("Find a contact...");
			}

			@Override
			public void focusGained(FocusEvent e) {
				seacherTxt.setText("");
			}
		});
	}

	@IMEventHandler(IMEventType.SHOW_MEMBERS_WINDOW)
	protected void showMembersWindow(IMEvent event) {
		UINamedObject named = (UINamedObject) event.getTarget();
		Window win = event.getData("view");
		Rectangle rect = win.getBounds();
		setLocation(rect.x + rect.width - 30, rect.y + 25);

		setIconImage(named.getIcon());
		setTitle(named.getName());
		this.namedObject = named;

		progressOverlay.setShowLoad(true);
		setVisible(true);

		isLoadFace = true; // 设置允许下载头像

		checkReloadMembers(namedObject); // 显示成员
		
		win.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent e) {
				int x = e.getComponent().getX() + e.getComponent().getWidth()
						- 30;
				int y = e.getComponent().getY() + 25;
				if (isPtInRect(getBounds(), new Point(x, y))) {
					setLocation(x, y);
				}
			}
		});
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
			int x1 = rect.width + x0;
			int y1 = rect.height + y0;
			int x = point.x + 40;
			int y = point.y + 40;

			return x >= x0 && x < x1 && y >= y0 && y < y1;
		}
		return false;
	}

	/**
	 * @param named
	 */
	private void updateMembers(UINamedObject named) {
		IMEventService events = getContext().getSerivce(IMService.Type.EVENT);
		if (named instanceof UIGroup) {
			events.broadcast(new IMEvent(IMEventType.GROUP_INFO_REQUEST, named
					.getEntity()));
		} else if (named instanceof UIDiscuz) {
			events.broadcast(new IMEvent(IMEventType.DISCUZ_INFO_REQUEST, named
					.getEntity()));
		} else {
			LOG.info("Unkown UINamedObject type:" + named);
		}
	}

	private void updateFaces(UINamedObject namedObject) {
		List<? extends QQStranger> members = getMembers();

		for (QQStranger m : members) {
			IMEventService events = getContext().getSerivce(
					IMService.Type.EVENT);
			events.broadcast(new IMEvent(IMEventType.USER_FACE_REQUEST, m));
		}
	}

	/*
	 * 有点效率问题，下载头像时更新太频繁
	 */
	@IMEventHandler(IMEventType.USER_FACE_UPDATE)
	protected void processIMUpdateUserFace(IMEvent event) {
		ListModel m = memberList.getModel();
		QQUser user = (QQUser) event.getTarget();
		if (m instanceof GroupMemberListModel && user instanceof QQStranger) {
			GroupMemberListModel model = (GroupMemberListModel) m;
			model.update(user);
		}
	}

	@IMEventHandler(IMEventType.GROUP_INFO_UPDATE)
	protected void proccessGroupInfoUpdate(IMEvent event) {
		QQGroup group = (QQGroup) event.getTarget();
		String request = event.getData("reqeust");
		if (request != null && request.equals("true")) {
			isLoadFace = true;
			checkReloadMembers(new UIGroup(group));
		}
	}

	@IMEventHandler(IMEventType.DISCUZ_INFO_UPDATE)
	protected void proccessDiscuzInfoUpdate(IMEvent event) {
		QQDiscuz discuz = (QQDiscuz) event.getTarget();
		String request = event.getData("reqeust");
		if (request != null && request.equals("true")) {
			isLoadFace = true;
			checkReloadMembers(new UIDiscuz(discuz));
		}
	}

	public void checkReloadMembers(UINamedObject named) {
		// 如果窗口没显示，那用户更新的信息也不用加载
		if (!isVisible()) {
			return;
		}
		namedObject = named;
		reloadMembers();
	}

	private void reloadMembers() {
		GroupMemberListModel model = (GroupMemberListModel) listModels
				.get(namedObject.getEntity());

		List<? extends QQStranger> members = getMembers();

		if (model == null) {
			model = new GroupMemberListModel(new UserListComparator());
			for (QQStranger m : members) {
				model.addElement(new UserListElement(new UIUser(m)));

				// 下载头像, 只下载一次，后面是手动更新
				if (isLoadFace) {
					IMEventService events = getContext().getSerivce(
							IMService.Type.EVENT);
					events.broadcast(new IMEvent(IMEventType.USER_FACE_REQUEST,
							m));
				}
			}
			listModels.put(namedObject.getEntity(), model);
		}

		model.sort(); // 根据状态排序
		membersCount.setText("Members (" + getOnlineCount(members) + "/"
				+ members.size() + ")");
		if (model != memberList.getModel()) {
			memberList.setModel(model);
		}
		model.updateAll();
		// 停止进度条
		progressOverlay.setShowLoad(false);
	}

	protected List<? extends QQStranger> getMembers() {
		List<? extends QQStranger> members = null;
		if (namedObject instanceof UIGroup) {
			QQGroup group = (QQGroup) namedObject.getEntity();
			members = group.getMembers();
		} else if (namedObject instanceof UIDiscuz) {
			QQDiscuz discuz = (QQDiscuz) namedObject.getEntity();
			members = discuz.getMembers();
		} else {
			LOG.info("Unkown UINamedObject type:" + namedObject);
		}
		return members;
	}

	/**
	 * 获取在线人数
	 * 
	 * @param users
	 * @return
	 */
	protected int getOnlineCount(List<? extends QQUser> users) {
		int count = 0;
		for (QQUser user : users) {
			QQStatus stat = user.getStatus();
			if (QQStatus.isGeneralOnline(stat)) {
				count++;
			}
		}
		return count;
	}

	public void checkSendSessionMsg() {
		UserListElement userElt = (UserListElement) memberList
				.getSelectedValue();
		checkSendSessionMsg((QQStranger) userElt.getUserNamed().getUser());
	}

	public void checkSendSessionMsg(QQStranger member) {
		if (StringUtils.isEmpty(member.getGroupSig())) {
			IMEvent imEvent = new IMEvent(IMEventType.GET_SESSION_MSG_REQUEST,
					member);
			IMEventService events = getContext().getSerivce(
					IMService.Type.EVENT);
			events.broadcast(imEvent);
		} else {
			sendSessionMsg(member);
		}
	}

	public void sendSessionMsg(QQStranger member) {
		IMEvent imEvent = new IMEvent(IMEventType.SHOW_CHAT, member);
		IMEventService events = getContext().getSerivce(IMService.Type.EVENT);
		events.broadcast(imEvent);
	}

	@IMEventHandler(IMEventType.GET_SESSION_MSG_SUCCESS)
	public void sendSessionMsg(IMEvent event) {
		QQStranger member = (QQStranger) event.getTarget();
		sendSessionMsg(member);
	}

	private static class UserListComparator implements
			Comparator<UserListElement> {

		@Override
		public int compare(UserListElement o1, UserListElement o2) {
			if (o1.getUserNamed() instanceof UIUser
					&& o2.getUserNamed() instanceof UIUser) {
				QQUser user1 = (QQUser) o1.getUserNamed().getEntity();
				QQUser user2 = (QQUser) o2.getUserNamed().getEntity();
				if (user1.getStatus() == user2.getStatus()) {
					return user1.getNickname().compareTo(user2.getNickname());
				}

				List<QQStatus> orders = new ArrayList<QQStatus>();
				orders.add(QQStatus.ONLINE);
				orders.add(QQStatus.AWAY);
				orders.add(QQStatus.CALLME);
				orders.add(QQStatus.BUSY);
				orders.add(QQStatus.SLIENT);
				orders.add(QQStatus.HIDDEN);
				orders.add(QQStatus.OFFLINE);
				return orders.indexOf(user1.getStatus())
						- orders.indexOf(user2.getStatus());
			}
			return 0;
		}
	}

}

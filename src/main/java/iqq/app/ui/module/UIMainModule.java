package iqq.app.ui.module;

import iqq.app.bean.DefaultUINamedObject;
import iqq.app.bean.UICategory;
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
import iqq.app.ui.IMFrameView;
import iqq.app.ui.bean.IMNodeData;
import iqq.app.ui.bean.IMTreeNode;
import iqq.app.ui.content.main.HeaderPanel;
import iqq.app.ui.content.main.MainPanel;
import iqq.app.ui.content.main.MiddlePanel;
import iqq.app.ui.content.main.MiddlePanel.Action;
import iqq.app.ui.content.main.SearchBuddyPanel;
import iqq.app.ui.renderer.IMBuddySearchRenderer;
import iqq.app.util.Benchmark;
import iqq.app.util.I18NUtil;
import iqq.app.util.LocationUtil;
import iqq.app.util.SettingUtils;
import iqq.app.util.SkinUtils;
import iqq.app.util.UIUtils;
import iqq.im.bean.QQBuddy;
import iqq.im.bean.QQCategory;
import iqq.im.bean.QQDiscuz;
import iqq.im.bean.QQGroup;
import iqq.im.bean.QQStatus;
import iqq.im.bean.QQUser;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowStateListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.apache.log4j.Logger;

import sun.security.x509.FreshestCRLExtension;

import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebList;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.utils.ImageUtils;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-18
 */
public class UIMainModule extends IMFrameView {
	private static final long serialVersionUID = 2990417326989970378L;
	private static final Logger LOG = Logger.getLogger(UIMainModule.class);
	
	private static final BuddyListComparator BUDDY_LIST_COMPARATOR = new BuddyListComparator();
	private MainPanel mainPanel;
	private SearchBuddyPanel SBPanel;
	private WebPanel wp = new WebPanel();
	private DefaultTreeModel buddyModel;
	private DefaultTreeModel groupModel;
	private DefaultTreeModel recentModel;

	@Override
	public void init(IMContext context) throws IMException {
		super.init(context);

		initFrame();

		// 初始化显示内容，登录面板
		mainPanel = new MainPanel(this);
		// 初始化好友查询面板
		SBPanel = new SearchBuddyPanel(this);
		setContentPanel(mainPanel);
		setupDefaultModel();
		
		// 注册感兴趣的事件
		IMEventHandlerProxy.register(this, context);

	}

	private void initFrame() {
		// 设置标题
		setTitle(I18NUtil.getMessage("app.name"));
		setIconImage(SkinUtils.getImageIcon("appLogo").getImage());

		// 设置程序宽度 和高度
		setSize(SettingUtils.getInt("appWidth"),
				SettingUtils.getInt("appHeight"));
		
		// 设置在屏幕显示位置，右中
		setLocation(LocationUtil.getScreenRight(getWidth(), getHeight()));
		// setAlwaysOnTop(true);
		
		// 关闭后退出程序
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
		addWindowStateListener(new WindowStateListener() {
			
			@Override
			public void windowStateChanged(WindowEvent e) {
				if(e.getNewState() == Frame.ICONIFIED) {
					hide();
				}
			}
		});
		addWindowFocusListener(new WindowFocusListener() {
			
			@Override
			public void windowLostFocus(WindowEvent e) {
				IMEventService evtHubs = getContext().getSerivce(IMService.Type.EVENT);
				evtHubs.broadcast(new IMEvent(IMEventType.WINDOW_MAIN_LOST_FOCUS));
			}
			
			@Override
			public void windowGainedFocus(WindowEvent e) {
				IMEventService evtHubs = getContext().getSerivce(IMService.Type.EVENT);
				evtHubs.broadcast(new IMEvent(IMEventType.WINDOW_MAIN_GAINED_FOCUS));
			}
		});
	}
	
	private void setupDefaultModel(){
		//群TAB
		IMTreeNode root = new IMTreeNode("Groups");
		IMNodeData n = new IMNodeData(new DefaultUINamedObject(I18NUtil.getMessage("myGroup")));
		IMTreeNode d1 = new IMTreeNode(n);
		root.add(d1);
		
		IMNodeData d = new IMNodeData(new DefaultUINamedObject(I18NUtil.getMessage("myDiscuz")));
		IMTreeNode d2 = new IMTreeNode(d);
		root.add(d2);

		groupModel = new DefaultTreeModel(root);
		firePropertyChange(MiddlePanel.GROUP_TREE_MODEL, Action.SET, groupModel);
		
		//好友TAB
		
	}
	

	@Override
	public void destroy() throws IMException {
		super.destroy();
		IMEventHandlerProxy.unregister(this);
	}
	
	@IMEventHandler(IMEventType.LOGIN_SUCCESS)
	protected void processIMLoginSuccess(IMEvent event) {
		LOG.info("login success......");
		showView(event); // 显示窗口
	}
	
	@IMEventHandler(IMEventType.CLIENT_OFFLINE)
	protected void processIMClientOffline(IMEvent event){
		//更新自己头像为灰色
		firePropertyChange(HeaderPanel.SELF_STATUS, Action.UPDATE, QQStatus.OFFLINE);
		//离线后所有好友不在线，重画在线列表
		processIMUpdateOnlineList(event);
		
		//是否是强制下线，如果是提示消息
		if(event.getData("force")){
			 WebOptionPane.showMessageDialog(this, event.getString("msg"), 
					 "Error", WebOptionPane.INFORMATION_MESSAGE );
		}
	}

	@IMEventHandler(IMEventType.BUDDY_LIST_UPDATE)
	@SuppressWarnings("unchecked")
	protected void processIMUpdateBuddyList(IMEvent event) {
		Benchmark.start("processIMUpdateBuddyList");
		List<QQCategory> categoryList = (List<QQCategory>) event.getTarget();
		IMTreeNode root = new IMTreeNode("Friends");
		for (QQCategory category : categoryList) {
			IMNodeData n = new IMNodeData(new UICategory(category));
			IMTreeNode d1 = new IMTreeNode(n, BUDDY_LIST_COMPARATOR);

			Benchmark.start("processIMUpdateBuddyList category:" + category.getName());
			if (category.getBuddyList().size() > 0) {
				for (QQBuddy buddy : category.getBuddyList()) {
					buddy.setFace(ImageUtils.copy(UIUtils.Bean.getDefaultFace().getImage()));
					d1.add(new IMTreeNode(new IMNodeData(new UIUser(buddy))));
				}
			} else {
				// solosky: 这里有点BUG，如果一个分组的好友为空，显示出来就是一个普通的节点而已，无法展开。。
				// solosky: 请@承诺童鞋看看能不能解决这个BUG
				// 承诺: 空的节点，当列表为空时进行，否则该节点无法显示出来
				// 承诺: 我的解决方案是直接添加一个空的节点，当然在双击时也要取消处理
				// solosky: 已修改，渲染的时候当结点数据为空，返回一个空的GroupPanel即可
				d1.add(new IMTreeNode());
			}
			d1.sortChildren();
			root.add(d1);
			Benchmark.end("processIMUpdateBuddyList category:" + category.getName());
		}

		buddyModel = new DefaultTreeModel(root);
		firePropertyChange(MiddlePanel.BUDDY_TREE_MODEL, Action.SET, buddyModel);
		Benchmark.end("processIMUpdateBuddyList");
	}

	@IMEventHandler(IMEventType.GROUP_LIST_UPDATE)
	protected void processIMUpdateGroupList(IMEvent event) {
		List<QQGroup> groupList = (List<QQGroup>) event.getTarget();

		IMTreeNode root = (IMTreeNode) groupModel.getRoot();
		IMTreeNode d1 = (IMTreeNode) root.getChildAt(0);
		
		if(groupList.size() > 0) {
			for (QQGroup group : groupList) {
				group.setFace(ImageUtils.copy(UIUtils.Bean.getDefaultFace().getImage()));
				IMNodeData n1 = new IMNodeData(new UIGroup(group));
				d1.add(new IMTreeNode(n1));
			}
		} else {
			d1.add(new IMTreeNode());
		}
		
		firePropertyChange(MiddlePanel.GROUP_TREE_MODEL, Action.UPDATE, groupModel);
	}
	
	
	@IMEventHandler(IMEventType.DISCUZ_LIST_UPDATE)
	protected void processIMUpdateDiscuzList(IMEvent event) {
		List<QQDiscuz> discuzList = (List<QQDiscuz>) event.getTarget();

		IMTreeNode root = (IMTreeNode) groupModel.getRoot();
		IMTreeNode d1 = (IMTreeNode) root.getChildAt(1);
		
		if(discuzList.size() > 0) {
			for (QQDiscuz dicuz : discuzList) {
				IMNodeData n1 = new IMNodeData(new UIDiscuz(dicuz));
				d1.add(new IMTreeNode(n1));
			}
		} else {
			d1.add(new IMTreeNode());
		}
		
		firePropertyChange(MiddlePanel.GROUP_TREE_MODEL, Action.UPDATE, groupModel);
	}
	
	@IMEventHandler(IMEventType.RECENT_LIST_UPDATE)
	protected void processIMUpdateRecentList(IMEvent event) {
		List<Object> recentList = (List<Object>) event.getTarget();

		IMTreeNode root = new IMTreeNode("Friends");
		
		for (Object obj : recentList) {
			UINamedObject uiNamed = null;
			if(obj instanceof QQBuddy){
				uiNamed = new UIUser((QQBuddy) obj);
			}else if (obj instanceof QQGroup){
				uiNamed = new UIGroup((QQGroup) obj);
			}else if (obj instanceof QQDiscuz){
				uiNamed = new UIDiscuz((QQDiscuz) obj);
			}else{
				uiNamed = new DefaultUINamedObject(obj.toString());
				LOG.warn("unknown recent list object type: "+ obj);
			}
			IMNodeData n1 = new IMNodeData(uiNamed);
			root.add(new IMTreeNode(n1));
		}
		
		recentModel = new DefaultTreeModel(root);
		firePropertyChange(MiddlePanel.RECENT_TREE_MODEL, Action.SET, recentModel);
	}

	@IMEventHandler(IMEventType.USER_FACE_UPDATE)
	protected void processIMUpdateUserFace(IMEvent event) {
		QQUser user = (QQUser)event.getTarget();
		if(user instanceof QQBuddy) {
			reloadUserData(user, false);
		}
	}
	
	@IMEventHandler(IMEventType.USER_STATUS_UPDATE)
	protected void processIMUpdateUserStatus(IMEvent event) {
		firePropertyChange("cellUpdate", "USER_STATUS_UPDATE",
				event.getTarget());
		
		reloadUserData((QQUser)event.getTarget(), true);
	}
	
	@IMEventHandler(IMEventType.USER_CACHE_UPDATE)
	protected void processIMUpdateUserCache(IMEvent event) {
		reloadUserData((QQUser) event.getTarget(), false);
	}
	
	@IMEventHandler(IMEventType.USER_CACHE_BATCH_UPDATE)
	protected void processIMUpdateUserCacheBatch(IMEvent event) {
		if(buddyModel != null){
			buddyModel.reload();
		}
		if(recentModel != null){
			recentModel.reload();
		}
	}

	@IMEventHandler(IMEventType.USER_SIGN_UPDATE)
	protected void processIMUpdateBuddySign(IMEvent event) {
		reloadUserData((QQUser) event.getTarget(), false);
	}

	@IMEventHandler(IMEventType.GROUP_FACE_UPDATE)
	protected void processIMUpdateGroupFace(IMEvent event) {
		firePropertyChange(MiddlePanel.GROUP_TREE_MODEL, Action.UPDATE,
				groupModel);
	}
	
	@IMEventHandler(IMEventType.ONLINE_LIST_UPDATE)
	protected void processIMUpdateOnlineList(IMEvent event) {
		firePropertyChange("cellUpdate", "ONLINE_LIST_UPDATE",
				new ArrayList<QQBuddy>());
		
		IMTreeNode root = (IMTreeNode) buddyModel.getRoot();
		for(int i=0; i<root.getChildCount(); i++){
			IMTreeNode node = (IMTreeNode) root.getChildAt(i);
			node.sortChildren();
			buddyModel.reload(node);
		}
	}
	

	@IMEventHandler(IMEventType.SELF_SIGN_UPDATE)
	protected void processIMUpdateSelfSign(IMEvent event) {
		firePropertyChange(HeaderPanel.SELF_SIGN, Action.SET, event.getTarget());
	}

	@IMEventHandler(IMEventType.SELF_FACE_UPDATE)
	protected void processIMUpdateSelfFace(IMEvent event) {
		firePropertyChange(HeaderPanel.SELF_FACE, Action.SET, event.getTarget());
	}

	@IMEventHandler(IMEventType.SELF_INFO_UPDATE)
	protected void processIMUpdateSelfInfo(IMEvent event) {
		firePropertyChange(HeaderPanel.SELF_INFO, Action.SET, event.getTarget());
	}

	@IMEventHandler(IMEventType.SHOW_MAIN_FRAME)
	public void showView(IMEvent event) {
		setExtendedState(Frame.NORMAL);
		// setAlwaysOnTop(true);
		this.show();
	}
	
	private void reloadUserData(QQUser user, boolean forceSort){
		Benchmark.start("reloadUserData");
		if(buddyModel != null && buddyModel.getRoot() != null){
			reloadTreeNode(buddyModel, (IMTreeNode) buddyModel.getRoot(), user, forceSort);
		}
		if(recentModel != null && recentModel.getRoot() != null){
			reloadTreeNode(recentModel, (IMTreeNode) recentModel.getRoot(), user, forceSort);
		}
		Benchmark.end("reloadUserData");
	}
	
	private void reloadTreeNode(DefaultTreeModel model, IMTreeNode node, QQUser user, boolean forceSort){
		if(node.getUserObject() instanceof IMNodeData){
			IMNodeData data = (IMNodeData) node.getUserObject();
			if(data.getEntity() instanceof QQUser){
				QQUser current = (QQUser) data.getEntity();
				if(current.getUin() == user.getUin()){
					model.reload(node);
					
					if(forceSort){
						IMTreeNode parent = (IMTreeNode) node.getParent();
						parent.sortChildren();
						model.reload(parent);
					}
				}
			}
		}
		
		for(int i=0; i<node.getChildCount(); i++){
			reloadTreeNode(model, (IMTreeNode) node.getChildAt(i), user, forceSort);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@IMEventHandler(IMEventType.SHOW_SEARCHBUDDY_WINDOW)
	public void processSearchBuddy(IMEvent event){
		
		SBPanel.removeAll();
		SBPanel.setUndecorated(true);
		String keyword  = event.getData("searchKeyWord");
		if(null != keyword&&!"".equals(keyword)){
			Window win = event.getData("view");
			List<QQBuddy> buddys = (List<QQBuddy>)event.getResult();
			Rectangle bounds = event.getData("nodeBounds");
			Component cpt = event.getData("comp");
			if(!win.isVisible()) return;
			Point p = cpt.getLocationOnScreen();
			int x = p.x;
			int y = p.y + bounds.y+108;
			wp.removeAll();	//清空面板内容
			wp.add(SBPanel.addBuddyInfoPanel(buddys,getContext(),event.getData("searchKeyWord").toString()));
			wp.setSize(cpt.getWidth(),160);
			wp.setLocation(x, y);
			wp.setVisible(true);
			wp.updateUI();
			firePropertyChange(MainPanel.SEARCH_BUDDY_MODEL, Action.SET, wp);
		}else{
			firePropertyChange(MainPanel.CLOSE_SEARCH_BUDDY_MODEL, Action.SET, null);
		}
		
	}
	
	
	private static class BuddyListComparatorOld implements Comparator<TreeNode>{
		@Override
		public int compare(TreeNode node1, TreeNode node2) {
			Object obj1 = ((IMTreeNode)node1).getUserObject();
			Object obj2 = ((IMTreeNode)node2).getUserObject();
			if(obj1 instanceof IMNodeData && obj2 instanceof IMNodeData){
				IMNodeData data1 = (IMNodeData) obj1;
				IMNodeData data2 = (IMNodeData) obj2;
				if(data1.getEntity() instanceof QQUser && data2.getEntity() instanceof QQUser){
					QQUser user1 = (QQUser) data1.getEntity();
					QQUser user2 = (QQUser) data2.getEntity();
					if(user1.getStatus() == user2.getStatus()){
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
			}
			
			return 0;
		}
		
	}
	
	/**
	 * 改为switch来获取顺序，避免使用indexOf
	 * 
	 */
	private static class BuddyListComparator implements Comparator<TreeNode>{
		@Override
		public int compare(TreeNode node1, TreeNode node2) {
			Object obj1 = ((IMTreeNode)node1).getUserObject();
			Object obj2 = ((IMTreeNode)node2).getUserObject();
			if(obj1 instanceof IMNodeData && obj2 instanceof IMNodeData){
				IMNodeData data1 = (IMNodeData) obj1;
				IMNodeData data2 = (IMNodeData) obj2;
				if(data1.getEntity() instanceof QQUser && data2.getEntity() instanceof QQUser){
					QQUser user1 = (QQUser) data1.getEntity();
					QQUser user2 = (QQUser) data2.getEntity();
					if(user1.getStatus() == user2.getStatus()){
						return user1.getNickname().compareTo(user2.getNickname());
					}
					
					return statusToOrder(user1.getStatus()) - statusToOrder(user2.getStatus());
				}
			}
			return 0;
		}
		
		public int statusToOrder(QQStatus status) {
			int order = -1;
			switch (status) {
			case ONLINE:
				order = 1;
				break;
			case AWAY:
				order = 2;
				break;
			case CALLME:
				order = 3;
				break;
			case BUSY:
				order = 4;
				break;
			case HIDDEN:
				order = 5;
				break;
			case OFFLINE:
				order = 6;
				break;
			default:
				order = 0;
				break;
			}
			
			return order;
		}
	}
}

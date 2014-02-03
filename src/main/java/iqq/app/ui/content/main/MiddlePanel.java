package iqq.app.ui.content.main;

import iqq.app.IMApp;
import iqq.app.core.IMService;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventType;
import iqq.app.service.IMEventService;
import iqq.app.service.IMPropService;
import iqq.app.service.IMSkinService.Type;
import iqq.app.service.IMTimerService;
import iqq.app.ui.BackgroundPanel;
import iqq.app.ui.IMFrameView;
import iqq.app.ui.bean.IMNodeData;
import iqq.app.ui.renderer.IMMainTreeCellRenderer;
import iqq.app.ui.renderer.IMRecentTreeCellRenderer;
import iqq.app.util.I18NUtil;
import iqq.app.util.IMImageUtil;
import iqq.app.util.SkinUtils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JScrollBar;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.lang3.StringUtils;

import com.alee.extended.painter.ColorPainter;
import com.alee.extended.panel.BorderPanel;
import com.alee.laf.menu.WebMenu;
import com.alee.laf.menu.WebMenuItem;
import com.alee.laf.menu.WebPopupMenu;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.tabbedpane.TabStretchType;
import com.alee.laf.tabbedpane.TabbedPaneStyle;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.laf.tree.WebTree;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-18
 */
public class MiddlePanel extends BackgroundPanel {
	private static final long serialVersionUID = 4693638485616893982L;
	public static final String BUDDY_TREE_MODEL = "buddyTreeModel";
	public static final String GROUP_TREE_MODEL = "groupTreeModel";
	public static final String RECENT_TREE_MODEL = "recentTreeModel";
	public static final ImageIcon selectedIcon = SkinUtils
			.getImageIcon("selectedIcon");

	private IMFrameView view;
	private WebTabbedPane mainTab;
	private WebTree contactsTree;
	private WebTree groupsTree;
	private WebTree recentTree;

	private WebPopupMenu tabPopup;

	private TreeMouseListener treeMouse;
	private TreeMnouseMotion contactTreeMotion;
	private TreeMnouseMotion recentTreeMotion;

	public enum Action {
		SET, UPDATE
	}

	/**
	 * @param view
	 */
	public MiddlePanel(IMFrameView view) {
		super(view);
		this.view = view;
		this.setMargin(1, 0, 0, 0);
		setPainter(new ColorPainter(Color.WHITE));
		treeMouse = new TreeMouseListener();
		UIManager.put("Tree.paintLines", false); // 去掉Tree中的水平线
		UIManager.put("Tree.leftChildIndent", -5); // 去掉节点 缩进
		addContent();

		// 右键头像菜单
		addMenu();
		// 添加监听
		addPropertyListener();
	}

	/**
	 * tab右键头像设置菜单
	 */
	private void addMenu() {
		tabPopup = new WebPopupMenu();

		// 用户头像大小
		WebMenu userFaceMenu = new WebMenu(I18NUtil.getMessage("userView"));
		WebMenuItem bigUserFaceItem = new WebMenuItem(
				I18NUtil.getMessage("bigFace"));
		WebMenuItem normalUserFaceItem = new WebMenuItem(
				I18NUtil.getMessage("normalFace"));
		WebMenuItem smallUserFaceItem = new WebMenuItem(
				I18NUtil.getMessage("smallFace"));
		WebMenuItem cuscomUserFaceItem = new WebMenuItem(
				I18NUtil.getMessage("customFace"));
		// 设置菜单选中图标
		// bigUserFaceItem.setIcon(selectedIcon);
		userFaceMenu.add(bigUserFaceItem);
		userFaceMenu.add(normalUserFaceItem);
		userFaceMenu.add(smallUserFaceItem);
		userFaceMenu.addSeparator();
		userFaceMenu.add(cuscomUserFaceItem);

		WebMenu groupMenu = new WebMenu(I18NUtil.getMessage("groupView"));
		WebMenuItem bigGroupFaceItem = new WebMenuItem(
				I18NUtil.getMessage("bigFace"));
		WebMenuItem normalGroupFaceItem = new WebMenuItem(
				I18NUtil.getMessage("normalFace"));
		WebMenuItem smallGroupFaceItem = new WebMenuItem(
				I18NUtil.getMessage("smallFace"));
		WebMenuItem cuscomGroupFaceItem = new WebMenuItem(
				I18NUtil.getMessage("customFace"));
		groupMenu.add(bigGroupFaceItem);
		groupMenu.add(normalGroupFaceItem);
		groupMenu.add(smallGroupFaceItem);
		groupMenu.addSeparator();
		groupMenu.add(cuscomGroupFaceItem);

		WebMenu conversationMenu = new WebMenu(
				I18NUtil.getMessage("conversationView"));
		WebMenuItem bigConFaceItem = new WebMenuItem(
				I18NUtil.getMessage("bigFace"));
		WebMenuItem normalConFaceItem = new WebMenuItem(
				I18NUtil.getMessage("normalFace"));
		WebMenuItem smallConFaceItem = new WebMenuItem(
				I18NUtil.getMessage("smallFace"));
		WebMenuItem cuscomConFaceItem = new WebMenuItem(
				I18NUtil.getMessage("customFace"));
		conversationMenu.add(bigConFaceItem);
		conversationMenu.add(normalConFaceItem);
		conversationMenu.add(smallConFaceItem);
		conversationMenu.addSeparator();
		conversationMenu.add(cuscomConFaceItem);

		userFaceMenu.revalidate();
		userFaceMenu.repaint();
		groupMenu.revalidate();
		groupMenu.repaint();
		conversationMenu.revalidate();
		conversationMenu.repaint();

		tabPopup.add(userFaceMenu);
		tabPopup.add(groupMenu);
		tabPopup.add(conversationMenu);

		// 最近会话
		bigConFaceItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateRecentTree(45, 45);
			}
		});
		normalConFaceItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateRecentTree(32, 32);
			}
		});
		smallConFaceItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateRecentTree(20, 20);
			}
		});
		cuscomConFaceItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});

		// 群图标大小
		bigGroupFaceItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateGroupsTree(45, 45);
			}
		});
		normalGroupFaceItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateGroupsTree(32, 32);
			}
		});
		smallGroupFaceItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateGroupsTree(20, 20);
			}
		});
		cuscomGroupFaceItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});

		// 用户头像大小
		bigUserFaceItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateContactsTree(45, 45);
			}
		});
		normalUserFaceItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateContactsTree(32, 32);
			}
		});
		smallUserFaceItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateContactsTree(20, 20);
			}
		});
		cuscomUserFaceItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});

		mainTab.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.isMetaDown()) {
					if (!tabPopup.isShowing()) {
						tabPopup.show(e.getComponent(), e.getX(), e.getY());
						tabPopup.revalidate();
						tabPopup.repaint();
					}
				}
				if(recentTreeMotion != null) {
					recentTreeMotion.stopTimer();
				}
				if(contactTreeMotion != null) {
					contactTreeMotion.stopTimer();
				}
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				if(recentTreeMotion != null) {
					recentTreeMotion.stopTimer();
				}
				if(contactTreeMotion != null) {
					contactTreeMotion.stopTimer();
				}
			}
		});
	}

	public void updateRecentTree(int w, int h) {
		IMRecentTreeCellRenderer
		.iconSizeMap.put("recent", new Dimension(w, h));
		BasicTreeUI ui = (BasicTreeUI) recentTree.getUI();
		ui.setLeftChildIndent(0);
		saveFaceSize("recentFace", w, h);
	}

	public void updateGroupsTree(int w, int h) {
		IMMainTreeCellRenderer
		.iconSizeMap.put("group", new Dimension(w, h));
		BasicTreeUI ui = (BasicTreeUI) groupsTree.getUI();
		ui.setLeftChildIndent(0);
		saveFaceSize("groupFace", w, h);
	}

	public void updateContactsTree(int w, int h) {
		IMMainTreeCellRenderer
		.iconSizeMap.put("user", new Dimension(w, h));
		BasicTreeUI ui = (BasicTreeUI) contactsTree.getUI();
		ui.setLeftChildIndent(0);
		saveFaceSize("userFace", w, h);
	}
	
	public void saveFaceSize(String key, int w, int h) {
		IMPropService propService = IMApp.me().getSerivce(IMService.Type.PROP);
		propService.setString(key, w + "," + h);
	}
	
	/**
	 * 添加监听
	 */
	private void addPropertyListener() {
		// 好友列表
		view.addPropertyChangeListener(BUDDY_TREE_MODEL,
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if (evt.getOldValue() == Action.SET) {
							contactsTree.setModel((TreeModel) evt.getNewValue());
						} else if (evt.getOldValue() == Action.UPDATE) {
							TreeModel tree = (TreeModel) evt.getNewValue();
							contactsTree.revalidate();
						}
					}
				});

		view.addPropertyChangeListener(GROUP_TREE_MODEL,
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if (evt.getOldValue() == Action.SET) {
							groupsTree.setModel((TreeModel) evt.getNewValue());
						} else if (evt.getOldValue() == Action.UPDATE) {
							TreeModel tree = (TreeModel) evt.getNewValue();
							groupsTree.invalidate();
						}
					}
				});

		view.addPropertyChangeListener(RECENT_TREE_MODEL,
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if (evt.getOldValue() == Action.SET) {
							recentTree.setModel((TreeModel) evt.getNewValue());
						}
					}
				});
	}

	/**
	 * 初始化显示内容
	 */
	private void addContent() {
		mainTab = new WebTabbedPane();
		mainTab.addTab(null, IMImageUtil.getScaledInstance(
				SkinUtils.getImageIcon("group1"), 25, 25), createBuddyPl());
		mainTab.addTab(null, IMImageUtil.getScaledInstance(
				SkinUtils.getImageIcon("group2"), 25, 25), createGroupPl());
		mainTab.addTab(null, IMImageUtil.getScaledInstance(
				SkinUtils.getImageIcon("group3"), 25, 25), createRecentPl());

		mainTab.setTabbedPaneStyle(TabbedPaneStyle.attached);
		mainTab.setTabStretchType(TabStretchType.always);
		mainTab.setOpaque(false);
		mainTab.setPainter(SkinUtils.getPainter(Type.NPICON, "transparent"));
		add(new BorderPanel(mainTab));
		
		loadFaceSetting();
	}

	private void loadFaceSetting() {
		IMPropService propService = IMApp.me().getSerivce(IMService.Type.PROP);
		String userFace = propService.getString("userFace");
		String groupFace = propService.getString("groupFace");
		String recentFace = propService.getString("recentFace");
		if(StringUtils.isNotEmpty(userFace)) {
			String[] u = userFace.split(",");
			updateContactsTree(Integer.parseInt(u[0]), Integer.parseInt(u[1]));
		}
		if(StringUtils.isNotEmpty(groupFace)) {
			String[] u = groupFace.split(",");
			updateGroupsTree(Integer.parseInt(u[0]), Integer.parseInt(u[1]));
		}
		if(StringUtils.isNotEmpty(recentFace)) {
			String[] u = userFace.split(",");
			updateRecentTree(Integer.parseInt(u[0]), Integer.parseInt(u[1]));
		}
	}

	/**
	 * @return Component
	 */
	private Component createBuddyPl() {
		contactsTree = new WebTree();
		contactsTree.setPaintLines(false);
		contactsTree.setOpaque(true);
		contactsTree.setBackground(this.getBackground());
		contactsTree.setLayout(new FlowLayout());
		contactsTree.setRootVisible(false);
		contactsTree.setShowsRootHandles(false);
		contactsTree.setCellRenderer(new IMMainTreeCellRenderer(view));
		contactsTree.addMouseListener(treeMouse);
		WebScrollPane treeScroll = new WebScrollPane(contactsTree, false, false);
		treeScroll.setOpaque(false);
		treeScroll
				.setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		contactTreeMotion = new TreeMnouseMotion(contactsTree, treeScroll);
		contactsTree.addMouseMotionListener(contactTreeMotion);
		return treeScroll;
	}

	/**
	 * @return Component
	 */
	private Component createGroupPl() {
		groupsTree = new WebTree();
		groupsTree.setPaintLines(false);
		groupsTree.setOpaque(true);
		groupsTree.setBackground(this.getBackground());
		groupsTree.setRootVisible(false);
		groupsTree.setShowsRootHandles(false);
		groupsTree.setCellRenderer(new IMMainTreeCellRenderer(view));
		groupsTree.addMouseListener(treeMouse);

		WebScrollPane treeScroll = new WebScrollPane(groupsTree, false, false);
		treeScroll.setOpaque(false);
		treeScroll
				.setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		return treeScroll;
	}

	/**
	 * @return Component
	 */
	private Component createRecentPl() {
		recentTree = new WebTree();
		recentTree.setPaintLines(false);
		recentTree.setOpaque(true);
		recentTree.setBackground(this.getBackground());
		recentTree.setRootVisible(false);
		recentTree.setShowsRootHandles(false);
		recentTree.setCellRenderer(new IMRecentTreeCellRenderer(view));
		recentTree.addMouseListener(treeMouse);

		WebScrollPane treeScroll = new WebScrollPane(recentTree, false, false);
		treeScroll.setOpaque(false);
		treeScroll
				.setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		recentTreeMotion = new TreeMnouseMotion(recentTree, treeScroll);
		recentTree.addMouseMotionListener(recentTreeMotion);
		return treeScroll;
	}

	class TreeMnouseMotion extends MouseMotionAdapter implements Runnable {
		private IMTimerService timerService;
		private final JTree tree;
		private int lastRow;
		private Rectangle bounds;
		private boolean isShowing;
		private MouseEvent mEvnet;
		private int scrollValue;

		public TreeMnouseMotion(WebTree tree, WebScrollPane scrollPane) {
			this.tree = tree;
			timerService = view.getContext().getSerivce(IMService.Type.TIMER);
			if(scrollPane != null) {
				JScrollBar bar = scrollPane.getVerticalScrollBar();
				bar.addAdjustmentListener(new AdjustmentListener() {

					@Override
					public void adjustmentValueChanged(AdjustmentEvent e) {
						scrollValue = e.getValue();
						
						// 滚动过程不显示
						timerService.killTimer(TreeMnouseMotion.this);
					}
				});
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			int row = tree.getRowForLocation(e.getX() + 20, e.getY());
			Point point;
			if (row == -1) {
				bounds = null;
				point = null;
			} else {
				bounds = tree.getRowBounds(row);
				point = new Point(e.getX() - bounds.x, e.getY() - bounds.y);
			}
			if (lastRow != row) {
				if (lastRow != -1) {
					Rectangle lastBounds = tree.getRowBounds(lastRow);
					if (lastBounds != null) {
						Point lastPoint = new Point(e.getX() - lastBounds.x,
								e.getY() - lastBounds.y);

						dispatchEvent(
								new MouseEvent(tree, MouseEvent.MOUSE_EXITED,
										System.currentTimeMillis(), 0,
										lastPoint.x, lastPoint.y, 0, false, 0),
								lastRow);
					}
				}
				if (row != -1) {
					dispatchEvent(
							new MouseEvent(tree, MouseEvent.MOUSE_ENTERED,
									System.currentTimeMillis(), 0, point.x,
									point.y, 0, false, 0), row);
				}
			}
			lastRow = row;
			if (row == -1)
				return;
		}

		private void dispatchEvent(MouseEvent e, int row) {
			TreePath pathForLocation = tree.getPathForRow(row);
			if (pathForLocation == null)
				return;
			Object lastPathComponent = pathForLocation.getLastPathComponent();
			if (lastPathComponent instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) lastPathComponent;
				if (node.isLeaf()) {
					mEvnet = e;

					if (mEvnet.getID() == MouseEvent.MOUSE_ENTERED) {
						timerService.setTimeout(this, 800);
					} else {
						timerService.killTimer(this);
						if (isShowing) {
							hideEvent();
						}
					}
				}
			}
		}
		
		public void stopTimer() {
			timerService.killTimer(this);
			if (isShowing) {
				hideEvent();
			}
		}

		private void hideEvent() {
			IMEventService eventHub = view.getContext().getSerivce(
					IMService.Type.EVENT);
			IMEvent event = new IMEvent(IMEventType.HIDE_HOVER_INFOCARD_WINDOW);
			eventHub.broadcast(event);

			isShowing = false;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			TreePath pathForLocation = tree.getPathForRow(lastRow);
			if (pathForLocation == null)
				return;
			Object lastPathComponent = pathForLocation.getLastPathComponent();
			if (lastPathComponent instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) lastPathComponent;
				IMNodeData nodeData = (IMNodeData) node.getUserObject();
				IMEventService eventHub = view.getContext().getSerivce(
						IMService.Type.EVENT);
				if (mEvnet.getID() == MouseEvent.MOUSE_ENTERED) {
					if (nodeData.getEntity() != null) {
						bounds.setLocation(bounds.x, bounds.y - scrollValue);
						IMEvent event = new IMEvent(
								IMEventType.SHOW_HOVER_INFOCARD_WINDOW);
						event.setTarget(nodeData.getEntity());
						event.putData("namedObject", nodeData.getNamedObject());
						event.putData("view", view);
						event.putData("nodeBounds", bounds);
						event.putData("comp", MiddlePanel.this);
						eventHub.broadcast(event);

						isShowing = true;
					}
				} else {
					if (isShowing) {
						hideEvent();
					}
				}

			}

			timerService.killTimer(this);
		}

	}

	class TreeMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			// 获取选择的节点
			if (e.getSource() instanceof WebTree) {
				WebTree tree = (WebTree) e.getSource();
				Object obj = tree.getLastSelectedPathComponent();
				if (obj instanceof DefaultMutableTreeNode) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) obj;
					if (!node.isLeaf()) {
						if (!tree.isExpanded(tree.getSelectionPath())) {
							tree.expandPath(tree.getSelectionPath());
						} else {
							tree.collapsePath(tree.getSelectionPath());
						}
					} else if (e.getClickCount() == 2 && node.isLeaf()) {
						// 双击打开聊天窗口
						IMEventService eventHub = view.getContext().getSerivce(
								IMService.Type.EVENT);
						IMNodeData nodeData = (IMNodeData) node.getUserObject();
						if (nodeData.getEntity() != null) {
							IMEvent event = new IMEvent(IMEventType.SHOW_CHAT);
							event.setTarget(nodeData.getEntity());
							eventHub.broadcast(event);
						}
					}
				}
			}
		}
	}
}

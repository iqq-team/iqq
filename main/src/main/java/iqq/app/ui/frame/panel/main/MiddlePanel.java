package iqq.app.ui.frame.panel.main;

import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.tabbedpane.TabStretchType;
import com.alee.laf.tabbedpane.TabbedPaneStyle;
import com.alee.laf.tabbedpane.WebTabbedPane;
import iqq.api.bean.*;
import iqq.app.core.context.IMContext;
import iqq.app.core.service.SkinService;
import iqq.app.ui.IMPanel;
import iqq.app.ui.IMTree;
import iqq.app.ui.frame.MainFrame;
import iqq.app.ui.manager.ChatManager;
import iqq.app.ui.renderer.BoddyTreeCellRenderer;
import iqq.app.ui.renderer.RecentTreeCellRenderer;
import iqq.app.ui.renderer.RoomTreeCellRenderer;
import iqq.app.ui.renderer.node.BuddyNode;
import iqq.app.ui.renderer.node.CategoryNode;
import iqq.app.ui.renderer.node.EntityNode;
import iqq.app.ui.renderer.node.RoomNode;

import javax.imageio.ImageIO;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * 主界面，主要是包含了一个Tab控件
 * 显示：好友列表/群/最近列表
 * <p>
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-8
 * License  : Apache License 2.0
 */
public class MiddlePanel extends IMPanel {
    private MainFrame frame;
    private WebTabbedPane mainTab;
    /**
     * 三部分面板，用于添加到Tab中
     */
    private IMPanel buddyPanel = new IMPanel();
    private IMPanel groupPanel = new IMPanel();
    private IMPanel recentPanel = new IMPanel();

    /**
     * 三个树控件，可以使用model无状态更新数据
     */
    private IMTree contactsTree = new IMTree();
    private IMTree groupsTree = new IMTree();
    private IMTree recentTree = new IMTree();


    /**
     * 树组件的鼠标事件，点击展开，双击打开聊天窗口
     */
    private TreeMouseListener treeMouse;

    public MiddlePanel(MainFrame frame) {
        super();
        this.frame = frame;
        treeMouse = new TreeMouseListener(frame);

        initTab();
        initBuddy();
        initRoom();
        initRecent();
    }

    /**
     * 最近列表
     */
    private void initRecent() {
        recentTree.addMouseListener(treeMouse);
        // 使用自定义的渲染器
        recentTree.setCellRenderer(new RecentTreeCellRenderer());
        WebScrollPane treeScroll = new WebScrollPane(recentTree, false, false);
        // 背景色
        treeScroll.getViewport().setBackground(new Color(250, 250, 250));
        // 滚动速度
        treeScroll.getVerticalScrollBar().setUnitIncrement(30);
        treeScroll.setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        recentPanel.add(treeScroll);

        // 测试数据
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        IMBuddy[] buddys = new IMBuddy[10];
        int j = 0;
        for (IMBuddy buddy : buddys) {
            buddy = new IMBuddy();
            buddy.setNick("buddy-" + j);
            buddy.setSign("sing..." + j++);
            try {
                File file = frame.getResourceService().getFile("icons/login/avatar2.png");
                buddy.setAvatar(ImageIO.read(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            root.add(new BuddyNode(buddy));
        }
        IMRoom[] rooms = new IMRoom[10];
        int k = 0;
        for (IMRoom room : rooms) {
            room = new IMRoom();
            room.setNick("Room-" + k++);
            try {
                File file = frame.getResourceService().getFile("icons/login/group.png");
                room.setAvatar(ImageIO.read(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            root.add(new RoomNode(room));
        }

        DefaultTreeModel groupModel = new DefaultTreeModel(root);
        recentTree.setModel(groupModel);
    }

    /**
     * 聊天室列表
     */
    private void initRoom() {
        groupsTree.addMouseListener(treeMouse);
        // 使用自定义的渲染器
        groupsTree.setCellRenderer(new RoomTreeCellRenderer());
        WebScrollPane treeScroll = new WebScrollPane(groupsTree, false, false);
        // 背景色
        treeScroll.getViewport().setBackground(new Color(250, 250, 250));
        // 滚动速度
        treeScroll.getVerticalScrollBar().setUnitIncrement(30);
        treeScroll.setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        groupPanel.add(treeScroll);

        // 测试数据
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        IMRoomCategory[] cates = new IMRoomCategory[5];
        int i = 0;
        for (IMRoomCategory cate : cates) {
            cate = new IMRoomCategory();
            cate.setName("Room Category-" + i++);
            CategoryNode cateNode = new CategoryNode(cate);
            root.add(cateNode);

            IMRoom[] rooms = new IMRoom[10];
            int j = 0;
            for (IMRoom room : rooms) {
                room = new IMRoom();
                room.setNick("Room-" + j++);
                try {
                    File file = frame.getResourceService().getFile("icons/login/group.png");
                    room.setAvatar(ImageIO.read(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cateNode.add(new RoomNode(room));
            }
        }

        DefaultTreeModel groupModel = new DefaultTreeModel(root);
        groupsTree.setModel(groupModel);
    }

    /**
     * 好友列表
     */
    private void initBuddy() {
        contactsTree.addMouseListener(treeMouse);
        // 使用自定义的渲染器
        contactsTree.setCellRenderer(new BoddyTreeCellRenderer());
        WebScrollPane treeScroll = new WebScrollPane(contactsTree, false, false);
        // 背景色
        treeScroll.getViewport().setBackground(new Color(250, 250, 250));
        // 滚动速度
        treeScroll.getVerticalScrollBar().setUnitIncrement(30);
        treeScroll.setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        buddyPanel.add(treeScroll);

        // 测试数据
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        IMBuddyCategory[] cates = new IMBuddyCategory[10];
        int i = 0;
        for (IMBuddyCategory cate : cates) {
            cate = new IMBuddyCategory();
            cate.setName("Category-" + i++);
            CategoryNode cateNode = new CategoryNode(cate);

            IMBuddy[] buddys = new IMBuddy[30];
            int j = 0;
            for (IMBuddy buddy : buddys) {
                buddy = new IMBuddy();
                buddy.setNick("buddy-" + j);
                buddy.setSign("sing..." + j++);
                try {
                    File file = frame.getResourceService().getFile("icons/login/avatar2.png");
                    buddy.setAvatar(ImageIO.read(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cateNode.add(new BuddyNode(buddy));
            }
            root.add(cateNode);
        }

        DefaultTreeModel buddyModel = new DefaultTreeModel(root);
        contactsTree.setModel(buddyModel);
    }

    /**
     * 初始化Tab
     */
    private void initTab() {
        mainTab = new WebTabbedPane();
        mainTab.setTabbedPaneStyle(TabbedPaneStyle.attached);
        mainTab.setTabStretchType(TabStretchType.always);
        mainTab.setOpaque(false);
        mainTab.setTopBg(new Color(240, 240, 240, 60));
        mainTab.setBottomBg(new Color(255, 255, 255, 160));
        mainTab.setSelectedTopBg(new Color(240, 240, 255, 50));
        mainTab.setSelectedBottomBg(new Color(240, 240, 255, 50));
        mainTab.setBackground(new Color(255, 255, 255, 200));

        // 添加这几个的panel
        mainTab.addTab("", buddyPanel);
        mainTab.addTab("", groupPanel);
        mainTab.addTab("", recentPanel);


        add(mainTab);

    }

    /**
     * 安装皮肤
     *
     * @param skinService
     */
    @Override
    public void installSkin(SkinService skinService) {
        super.installSkin(skinService);

        mainTab.setIconAt(0, skinService.getIconByKey("main/tabBoddyIcon", 25, 25));
        mainTab.setIconAt(1, skinService.getIconByKey("main/tabGroupIcon", 25, 25));
        mainTab.setIconAt(2, skinService.getIconByKey("main/tabRecentIcon", 25, 25));

        //buddyPanel.setPainter(skinService.getPainterByKey("skin/background"));
        //groupPanel.setPainter(skinService.getPainterByKey("skin/background"));
        //recentPanel.setPainter(skinService.getPainterByKey("skin/background"));

    }

    public void updateBuddyList(List<IMBuddyCategory> imCategories) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        BufferedImage defaultAvatar = getDefaultAvatar();
        for (IMBuddyCategory cate : imCategories) {
            CategoryNode cateNode = new CategoryNode(cate);
            for (IMBuddy buddy : cate.getBuddyList()) {
                if (buddy.getAvatar() == null) {
                    buddy.setAvatar(defaultAvatar);
                }
                cateNode.add(new BuddyNode(buddy));
            }
            root.add(cateNode);
        }

        DefaultTreeModel buddyModel = new DefaultTreeModel(root);
        contactsTree.setModel(buddyModel);
    }

    public void updateGroupList(List<IMRoomCategory> roomCategories) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        BufferedImage defaultAvatar = getDefaultAvatar();
        for (IMRoomCategory cate : roomCategories) {
            CategoryNode cateNode = new CategoryNode(cate);
            for (IMRoom room : cate.getRoomList()) {
                if (room.getAvatar() == null) {
                    room.setAvatar(defaultAvatar);
                }
                cateNode.add(new RoomNode(room));
            }
            root.add(cateNode);
        }

        DefaultTreeModel groupModel = new DefaultTreeModel(root);
        groupsTree.setModel(groupModel);
    }

    public void updateRecentList(List<IMBuddy> buddies) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        BufferedImage defaultAvatar = getDefaultRoomAvatar();
        for (IMBuddy buddy : buddies) {
            if (buddy.getAvatar() == null) {
                buddy.setAvatar(defaultAvatar);
            }
            root.add(new BuddyNode(buddy));
        }

        DefaultTreeModel groupModel = new DefaultTreeModel(root);
        recentTree.setModel(groupModel);
    }

    public void updateUserFace(IMUser imUser) {
        DefaultTreeModel model = (DefaultTreeModel) contactsTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        for (int i = 0; i < root.getChildCount(); i++) {
            CategoryNode categoryNode = (CategoryNode) root.getChildAt(i);
            for (int j = 0; j < categoryNode.getChildCount(); j++) {
                BuddyNode buddyNode = (BuddyNode) categoryNode.getChildAt(j);
                if (buddyNode.getBuddy().getId() == imUser.getId()) {
                    buddyNode.getBuddy().setAvatar(imUser.getAvatar());
                    model.reload(buddyNode);
                }
            }
        }
    }

    private BufferedImage getDefaultAvatar() {
        try {
            File file = frame.getResourceService().getFile("icons/login/avatar2.png");
            return ImageIO.read(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private BufferedImage getDefaultRoomAvatar() {
        try {
            File file = frame.getResourceService().getFile("icons/login/group.png");
            return ImageIO.read(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 树组件的鼠标事件，点击展开，双击打开聊天窗口
     */
    class TreeMouseListener extends MouseAdapter {
        MainFrame frame;

        public TreeMouseListener(MainFrame frame) {
            this.frame = frame;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            // 获取选择的节点
            if (e.getSource() instanceof IMTree) {
                IMTree tree = (IMTree) e.getSource();
                Object obj = tree.getLastSelectedPathComponent();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) obj;
                if (obj instanceof CategoryNode) {
                    // 判断是否展开
                    if (!tree.isExpanded(tree.getSelectionPath())) {
                        // 展开
                        tree.expandPath(tree.getSelectionPath());
                    } else {
                        // 合并
                        tree.collapsePath(tree.getSelectionPath());
                    }
                } else if (e.getClickCount() == 2 && obj instanceof EntityNode) {
                    // 双击打开聊天窗口
                    EntityNode entityNode = (EntityNode) obj;
                    IMContext.getBean(ChatManager.class).addChat((IMEntity) entityNode.getUserObject());
                }
            }
        }
    }
}

package iqq.app.ui.frame.panel.main;

import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.tabbedpane.TabStretchType;
import com.alee.laf.tabbedpane.TabbedPaneStyle;
import com.alee.laf.tabbedpane.WebTabbedPane;
import iqq.api.bean.IMBuddy;
import iqq.api.bean.IMBuddyCategory;
import iqq.api.bean.IMRoom;
import iqq.api.bean.IMRoomCategory;
import iqq.app.core.service.SkinService;
import iqq.app.ui.IMPanel;
import iqq.app.ui.IMTree;
import iqq.app.ui.frame.MainFrame;
import iqq.app.ui.renderer.BoddyTreeCellRenderer;
import iqq.app.ui.renderer.RecentTreeCellRenderer;
import iqq.app.ui.renderer.RoomTreeCellRenderer;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

/**
 * 主界面，主要是包含了一个Tab控件
 * 显示：好友列表/群/最近列表
 *
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
    private TreeMouseListener treeMouse = new TreeMouseListener();

    public MiddlePanel(MainFrame frame) {
        super();
        this.frame = frame;

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
        for(IMBuddy buddy : buddys) {
            buddy = new IMBuddy();
            buddy.setNick("buddy-" + j);
            buddy.setSign("sing..." + j++);
            try {
                File file = frame.getResourceService().getFile("icons/login/avatar2.png");
                byte[] b = Files.readAllBytes(file.toPath());
                buddy.setAvatar(b);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            root.add(new DefaultMutableTreeNode(buddy));
        }
        IMRoom[] rooms = new IMRoom[10];
        int k = 0;
        for(IMRoom room : rooms) {
            room = new IMRoom();
            room.setNick("Room-" + k++);
            try {
                File file = frame.getResourceService().getFile("icons/login/group.png");
                byte[] b = Files.readAllBytes(file.toPath());
                room.setAvatar(b);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            root.add(new DefaultMutableTreeNode(room));
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
        for(IMRoomCategory cate : cates) {
            cate = new IMRoomCategory();
            cate.setName("Room Category-" + i++);
            DefaultMutableTreeNode cateNode = new DefaultMutableTreeNode(cate);
            root.add(cateNode);

            IMRoom[] rooms = new IMRoom[10];
            int j = 0;
            for(IMRoom room : rooms) {
                room = new IMRoom();
                room.setNick("Room-" + j++);
                try {
                    File file = frame.getResourceService().getFile("icons/login/group.png");
                    byte[] b = Files.readAllBytes(file.toPath());
                    room.setAvatar(b);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cateNode.add(new DefaultMutableTreeNode(room));
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
        IMBuddyCategory[] cates = new IMBuddyCategory[20];
        int i = 0;
        for(IMBuddyCategory cate : cates) {
            cate = new IMBuddyCategory();
            cate.setName("Category-" + i++);
            DefaultMutableTreeNode cateNode = new DefaultMutableTreeNode(cate);

            IMBuddy[] buddys = new IMBuddy[100];
            int j = 0;
            for(IMBuddy buddy : buddys) {
                buddy = new IMBuddy();
                buddy.setNick("buddy-" + j);
                buddy.setSign("sing..." + j++);
                try {
                    File file = frame.getResourceService().getFile("icons/login/avatar2.png");
                    byte[] b = Files.readAllBytes(file.toPath());
                    buddy.setAvatar(b);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cateNode.add(new DefaultMutableTreeNode(buddy));
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

    /**
     * 树组件的鼠标事件，点击展开，双击打开聊天窗口
     */
    class TreeMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            // 获取选择的节点
            if (e.getSource() instanceof IMTree) {
                IMTree tree = (IMTree) e.getSource();
                Object obj = tree.getLastSelectedPathComponent();
                if (obj instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) obj;
                    // 非叶子节点
                    if (!node.isLeaf()) {
                        // 判断是否展开
                        if (!tree.isExpanded(tree.getSelectionPath())) {
                            // 展开
                            tree.expandPath(tree.getSelectionPath());
                        } else {
                            // 合并
                            tree.collapsePath(tree.getSelectionPath());
                        }
                    } else if (e.getClickCount() == 2 && node.isLeaf()) {
                        // 叶子节点
                        // 双击打开聊天窗口

                    }
                }
            }
        }
    }
}

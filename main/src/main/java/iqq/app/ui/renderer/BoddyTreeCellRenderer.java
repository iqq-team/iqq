package iqq.app.ui.renderer;

import com.alee.extended.image.WebDecoratedImage;
import com.alee.extended.panel.CenterPanel;
import com.alee.extended.panel.GroupPanel;
import com.alee.laf.label.WebLabel;
import iqq.api.bean.IMBuddy;
import iqq.api.bean.IMBuddyCategory;
import iqq.app.ui.IMPanel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

/**
 * 好友树中单元显示
 *
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-8
 * License  : Apache License 2.0
 */
public class BoddyTreeCellRenderer extends IMPanel implements TreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if(node.getUserObject() instanceof IMBuddyCategory) {
            IMBuddyCategory cate = (IMBuddyCategory) node.getUserObject();
            WebLabel cateLbl = new WebLabel();
            cateLbl.setMargin(5, 8, 5, 5);
            if(expanded) {
                cateLbl.setIcon(UIManager.getIcon("Tree.arrowDown"));
            } else {
                cateLbl.setIcon(UIManager.getIcon("Tree.arrowLeft"));
            }
            cateLbl.setText(cate.getName());
            return cateLbl;
        } else if(node.getUserObject() instanceof IMBuddy) {
            IMBuddy buddy = (IMBuddy) node.getUserObject();
            ImageIcon icon = new ImageIcon(new ImageIcon(buddy.getAvatar()).getImage().getScaledInstance(40, 40, 100));
            IMPanel cell = new IMPanel();
            // 头像
            WebDecoratedImage avatar = new WebDecoratedImage(icon);
            avatar.setShadeWidth(1);
            avatar.setRound(4);
            avatar.setDrawGlassLayer(false);
            cell.add(avatar, BorderLayout.WEST);
            // 昵称
            WebLabel nickLbl = new WebLabel(buddy.getNick());
            WebLabel signLbl = new WebLabel(buddy.getSign());
            nickLbl.setFontSize(14);
            signLbl.setFontSize(13);
            signLbl.setForeground(Color.GRAY);
            GroupPanel textGroup = new GroupPanel(0, false, nickLbl, signLbl);
            textGroup.setMargin(0, 5, 0, 0);
            cell.add(new CenterPanel(textGroup, false , true));
            cell.setMargin(5);
            return cell;
        }
        return this;
    }

}

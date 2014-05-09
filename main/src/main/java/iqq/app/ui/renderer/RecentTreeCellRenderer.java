package iqq.app.ui.renderer;

import com.alee.extended.image.WebDecoratedImage;
import com.alee.extended.panel.CenterPanel;
import com.alee.extended.panel.GroupPanel;
import com.alee.laf.label.WebLabel;
import iqq.api.bean.IMBuddy;
import iqq.api.bean.IMRoom;
import iqq.app.ui.IMPanel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

/**
 * 最近列表树中单元显示
 *
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-9
 * License  : Apache License 2.0
 */
public class RecentTreeCellRenderer extends IMPanel implements TreeCellRenderer {
    public static int ICON_SIZE = 40;
    public static Insets LINE_MARGIN = new Insets(4, 15, 4, 4);

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if(node.getUserObject() instanceof IMBuddy) {
            IMBuddy buddy = (IMBuddy) node.getUserObject();
            ImageIcon icon = new ImageIcon(new ImageIcon(buddy.getAvatar()).getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, 100));
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
            cell.setMargin(LINE_MARGIN);
            return cell;
        } else if(node.getUserObject() instanceof IMRoom) {
            IMRoom room = (IMRoom) node.getUserObject();
            WebLabel roomLbl = new WebLabel();
            roomLbl.setMargin(LINE_MARGIN);
            roomLbl.setText(room.getNick());
            ImageIcon icon = new ImageIcon(new ImageIcon(room.getAvatar()).getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, 100));
            roomLbl.setIcon(icon);
            return roomLbl;
        }
        return this;
    }
}

package iqq.app.ui.renderer;

import com.alee.laf.label.WebLabel;
import iqq.api.bean.IMRoom;
import iqq.api.bean.IMRoomCategory;
import iqq.app.ui.IMPanel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

/**
 * 聊天室树中单元显示
 *
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-9
 * License  : Apache License 2.0
 */
public class RoomTreeCellRenderer extends IMPanel implements TreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if(node.getUserObject() instanceof IMRoomCategory) {
            IMRoomCategory cate = (IMRoomCategory) node.getUserObject();
            WebLabel cateLbl = new WebLabel();
            cateLbl.setMargin(5, 8, 5, 5);

            if(expanded) {
                cateLbl.setIcon(UIManager.getIcon("Tree.arrowDown"));
            } else {
                cateLbl.setIcon(UIManager.getIcon("Tree.arrowLeft"));
            }
            cateLbl.setText(cate.getName());
            return cateLbl;
        } else if(node.getUserObject() instanceof IMRoom) {
            IMRoom room = (IMRoom) node.getUserObject();
            WebLabel roomLbl = new WebLabel();
            roomLbl.setMargin(2, 5, 2, 2);
            roomLbl.setText(room.getNick());
            ImageIcon icon = new ImageIcon(new ImageIcon(room.getAvatar()).getImage().getScaledInstance(30, 30, 100));
            roomLbl.setIcon(icon);
            return roomLbl;
        }
        return this;
    }
}

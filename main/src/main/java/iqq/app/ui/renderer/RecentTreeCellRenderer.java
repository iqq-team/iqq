package iqq.app.ui.renderer;

import com.alee.laf.label.WebLabel;
import iqq.app.ui.renderer.node.BuddyNode;
import iqq.app.ui.renderer.node.RoomNode;

import javax.swing.*;
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
public class RecentTreeCellRenderer implements TreeCellRenderer {
    public static int ICON_SIZE = 40;
    public static Insets LINE_MARGIN = new Insets(4, 15, 4, 4);

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if(value instanceof BuddyNode) {
            return ((BuddyNode) value).getView();
        } else if(value instanceof RoomNode) {
            return ((RoomNode) value).getView(40, 40);
        }
        return new WebLabel(value.toString());
    }
}

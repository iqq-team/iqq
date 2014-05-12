package iqq.app.ui.renderer;

import com.alee.laf.label.WebLabel;
import iqq.app.ui.renderer.node.BuddyNode;
import iqq.app.ui.renderer.node.CategoryNode;

import javax.swing.*;
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
public class BoddyTreeCellRenderer implements TreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if(value instanceof CategoryNode) {
            CategoryNode cate = (CategoryNode) value;
            WebLabel cateLbl = cate.getView();
            if(expanded) {
                cateLbl.setIcon(UIManager.getIcon("Tree.arrowDown"));
            } else {
                cateLbl.setIcon(UIManager.getIcon("Tree.arrowLeft"));
            }
            return cateLbl;
        } else if(value instanceof BuddyNode) {
            return ((BuddyNode) value).getView();
        }
        return new WebLabel(value.toString());
    }

}

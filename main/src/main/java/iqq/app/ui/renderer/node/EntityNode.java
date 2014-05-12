package iqq.app.ui.renderer.node;

import iqq.api.bean.IMEntity;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-11
 * License  : Apache License 2.0
 */
public class EntityNode extends DefaultMutableTreeNode {
    public EntityNode(IMEntity entity) {
        super(entity);
    }
}

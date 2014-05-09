package iqq.app.ui;

import com.alee.extended.tree.WebAsyncTree;
import iqq.app.core.service.SkinService;
import iqq.app.ui.skin.Skin;
import javax.swing.tree.TreeSelectionModel;

/**
 * 树组件，继承于weblaf的异步加载树组件
 *
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-8
 * License  : Apache License 2.0
 */
public class IMTree extends WebAsyncTree implements Skin {

    public IMTree() {
        this.setPaintLines(false);
        this.setOpaque(false);
        this.setRootVisible(false);
        this.setShowsRootHandles(false);
        this.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    @Override
    public void installSkin(SkinService skinService) {

    }
}

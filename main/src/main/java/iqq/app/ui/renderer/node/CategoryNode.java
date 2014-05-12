package iqq.app.ui.renderer.node;

import com.alee.laf.label.WebLabel;
import iqq.api.bean.IMCategory;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * 好友分类、聊天室分类显示节点
 *
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-10
 * License  : Apache License 2.0
 */
public class CategoryNode extends DefaultMutableTreeNode {
    private IMCategory category;
    private WebLabel view = new WebLabel();

    public CategoryNode(IMCategory category) {
        super();
        this.category = category;

        view.setMargin(5, 8, 5, 5);
    }

    public IMCategory getCategory() {
        return category;
    }

    public void setCategory(IMCategory category) {
        this.category = category;
    }

    /**
     * 这个方法特别频繁，一定要处理好
     *
     * @return
     */
    public WebLabel getView() {
        if(!view.getText().equals(category.getName())) {
            view.setText(category.getName());
        }
        return view;
    }
}

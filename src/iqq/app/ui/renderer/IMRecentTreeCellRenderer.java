package iqq.app.ui.renderer;

import iqq.app.bean.UINamedObject;
import iqq.app.ui.IMFrameView;
import iqq.app.ui.bean.IMNodeData;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-19
 */
public class IMRecentTreeCellRenderer extends IMMainTreeCellRenderer {
	private static final long serialVersionUID = 2017259200319405400L;

	/**
	 * @param view
	 */
	public IMRecentTreeCellRenderer(IMFrameView view) {
		super(view);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		if (node.getUserObject() instanceof IMNodeData) {
			IMNodeData data = (IMNodeData) node.getUserObject();
			UINamedObject named = data.getNamedObject();

			if (named != null) {
				return updateIconCell(data, tree);
			}
		}

		return this;
	}
	
	@Override
	public IconCell updateIconCell(IMNodeData data, JTree tree) {
		UINamedObject named = data.getNamedObject();

		IconCell iconCell = iconCells.get(named.getEntity());
		if (iconCell == null) {
			iconCell = new IconCell(named);
			iconCells.put(named.getEntity(), iconCell);
		}

		// 判断是否为更新的对象
		if (iconCell.isUpdate()) {
			iconCell.updateInfo(named);
			iconCell.setUpdate(false);
		}

		JViewport sp = (JViewport) tree.getParent(); // 滚动条显示VIEW
		// 宽度为滚动条可显示的宽度，高度是头像的高度 + 边框
		Dimension uDim = new Dimension(sp.getViewSize().width - 20,
				iconSizeMap.get("recent").height + 10);
		iconCell.setPreferredSize(uDim);
		iconCell.setSize(uDim);
		iconCell.updateIcon(named.getIcon(), iconSizeMap.get("recent"));
		iconCell.setMargin(0, 5, 0, 0);
		
		return iconCell;
	}

}

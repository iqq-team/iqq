package iqq.app.ui.renderer;

import iqq.app.bean.DefaultUINamedObject;
import iqq.app.bean.UICategory;
import iqq.app.bean.UINamedObject;
import iqq.app.bean.UIUser;
import iqq.app.ui.IMFrameView;
import iqq.app.ui.bean.IMNodeData;
import iqq.im.bean.QQBuddy;
import iqq.im.bean.QQUser;

import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import com.alee.laf.panel.WebPanel;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-19
 */
public class IMMainTreeCellRenderer extends WebPanel implements
		TreeCellRenderer {
	private static final long serialVersionUID = 2017259200319405400L;
	public static Map<String, Dimension> iconSizeMap = new HashMap<String, Dimension>();
	protected Map<Object, CategroyCell> categorys = new HashMap<Object, CategroyCell>();
	protected Map<Object, IconCell> iconCells = new HashMap<Object, IconCell>();

	public IMMainTreeCellRenderer(IMFrameView view) {
		// 初始化默认头像
		iconSizeMap.put("user", new Dimension(45, 45));
		iconSizeMap.put("group", new Dimension(45, 45));
		iconSizeMap.put("recent", new Dimension(45, 45));

		view.addPropertyChangeListener("cellUpdate",
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						// QQBuddy/QQGroup/QQDiscuz
						if (evt.getOldValue().equals("ONLINE_LIST_UPDATE")) {
							List<QQBuddy> buddyList = (List<QQBuddy>) evt
									.getNewValue();
							for (Object obj : categorys.keySet()) {
								CategroyCell c = categorys.get(obj);
								c.setUpdate(true);
							}
							for (QQUser u : buddyList) {
								IconCell ic = iconCells.get(u);
								if (ic != null) {
									ic.setUpdate(true);
								}
							}
						} else if (evt.getOldValue().equals(
								"USER_STATUS_UPDATE")) {
							QQUser u = (QQUser) evt.getNewValue();
							IconCell ic = iconCells.get(u);
							if (ic != null) {
								ic.setUpdate(true);
							}
						}
					}
				});
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		if (node.getUserObject() instanceof IMNodeData) {
			IMNodeData data = (IMNodeData) node.getUserObject();
			UINamedObject named = data.getNamedObject();

			if (data.getNamedObject() instanceof UICategory
					|| named instanceof DefaultUINamedObject) {
				return updateCategroyCell(data).getView(expanded);
			} else if (named != null) {
				return updateIconCell(data, tree);
			}
		}

		return this;
	}

	public CategroyCell updateCategroyCell(IMNodeData data) {
		UINamedObject named = data.getNamedObject();
		CategroyCell categoryCell = categorys.get(named);
		if (categoryCell == null) {
			categoryCell = new CategroyCell(
					data.getImageIcon(IMNodeData.KEY_OPEN_ICON),
					data.getImageIcon(IMNodeData.KEY_CLOSED_ICON),
					named.getName());
			categorys.put(named, categoryCell);
		}
		if (categoryCell.isUpdate()) {
			categoryCell.setName(named.getName());
		}

		return categoryCell;
	}

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
		if (named instanceof UIUser) {
			Dimension uDim = new Dimension(sp.getViewSize().width - 20,
					iconSizeMap.get("user").height + 10);
			iconCell.setPreferredSize(uDim);
			iconCell.setSize(uDim);
			iconCell.updateIcon(named.getIcon(), iconSizeMap.get("user"));
		} else {
			Dimension uDim = new Dimension(sp.getViewSize().width - 20,
					iconSizeMap.get("group").height + 10);
			iconCell.setPreferredSize(uDim);
			iconCell.setSize(uDim);
			iconCell.updateIcon(named.getIcon(), iconSizeMap.get("group"));
		}

		return iconCell;
	}

}

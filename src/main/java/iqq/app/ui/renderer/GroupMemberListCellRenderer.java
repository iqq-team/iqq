package iqq.app.ui.renderer;

import java.awt.Component;

import javax.swing.JList;

import com.alee.laf.list.WebListCellRenderer;
import com.alee.laf.list.WebListElement;

/**
 * 聊天窗口列表单元
 * 
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-22
 */
public class GroupMemberListCellRenderer extends WebListCellRenderer {
	private static final long serialVersionUID = -4765528260568429188L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		WebListElement elt = (WebListElement) super
				.getListCellRendererComponent(list, value, index, isSelected,
						cellHasFocus);

		if (value instanceof WebListElement) {
			WebListElement e = (WebListElement) value;
			elt.setText(e.getText());
			elt.setIcon(e.getIcon());
			elt.setMargin(e.getMargin());
		}
		return elt;
	}
}

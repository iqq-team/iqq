/**
 * 
 */
package iqq.app.ui.renderer;

import iqq.app.ui.bean.UserListElement;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;

import com.alee.laf.combobox.WebComboBoxCellRenderer;

/**
 * @author ZhiHui_Chen<6208317@qq.com>
 * @create date 2013-4-12
 */
public class UserComboBoxCellRenderer extends WebComboBoxCellRenderer {
	private static final long serialVersionUID = 3454631359718390061L;

	/**
	 * @param comboBox
	 */
	public UserComboBoxCellRenderer(JComboBox comboBox) {
		super(comboBox);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		JLabel renderer = (JLabel) super.getListCellRendererComponent(list,
				value, index, isSelected, cellHasFocus);
		UserListElement userElt = (UserListElement) value;
		renderer.setText(userElt.getText());
		//renderer.setIcon(IMImageUtil.getScaledInstance((ImageIcon)userElt.getIcon(), 40, 40));
		renderer.setIcon(userElt.getIcon());
		return renderer;
	}

}

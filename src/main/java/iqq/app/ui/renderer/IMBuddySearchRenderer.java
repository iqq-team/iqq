package iqq.app.ui.renderer;


import java.awt.Component;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

import com.alee.laf.list.WebListCellRenderer;

public class IMBuddySearchRenderer extends WebListCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		JLabel renderer = (JLabel) super.getListCellRendererComponent(list,
				value, index, isSelected, cellHasFocus);
		
		Object[] objs = (Object[]) value;
		setIcon(new ImageIcon((BufferedImage)objs[0]));
		setText(objs[1].toString());
		return this;
	}

}

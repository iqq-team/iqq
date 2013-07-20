package iqq.app.ui.renderer;

import iqq.app.bean.UIDiscuz;
import iqq.app.bean.UIGroup;
import iqq.app.bean.UINamedObject;
import iqq.app.bean.UIUser;
import iqq.app.ui.bean.painter.MsgBoxListElementPainter;
import iqq.im.bean.QQDiscuz;
import iqq.im.bean.QQGroup;
import iqq.im.bean.QQUser;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import com.alee.laf.list.WebListCellRenderer;
import com.alee.laf.list.WebListElement;

/**
 * 聊天窗口列表单元
 * 
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-22
 */
public class MsgBoxListCellRenderer extends WebListCellRenderer {
	private static final long serialVersionUID = -4765528260568429188L;
	private static final Logger LOG = Logger
			.getLogger(MsgBoxListCellRenderer.class);
	private static final MsgBoxListElementPainter msgboxPainter = new MsgBoxListElementPainter();

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		WebListElement elt = (WebListElement) super
				.getListCellRendererComponent(list, value, index, isSelected,
						cellHasFocus);
		UINamedObject namedObject = null;
		if (value instanceof QQUser) {
			namedObject = new UIUser((QQUser) value);
		} else if (value instanceof QQGroup) {
			namedObject = new UIGroup((QQGroup) value);
		} else if (value instanceof QQDiscuz) {
			namedObject = new UIDiscuz((QQDiscuz) value);
		} else {
			LOG.info("Unkown type...");
		}
		if (namedObject != null) {
			// elt.setText("0");
			elt.setIconTextGap(5);
			elt.setHorizontalAlignment(SwingConstants.CENTER);
			elt.setPainter(msgboxPainter);
			elt.setDrawShade(true);
			elt.setShadeColor(Color.GRAY);
			elt.setForeground(new Color(220, 220, 200));
			elt.setIcon(new ImageIcon(namedObject.getIcon()));
			elt.setToolTipText(namedObject.getName());
		}
		return elt;
	}
}

class MsgCount {
	private UINamedObject namedObject;
	private int count;

	public MsgCount(Object entity) {

	}

	public UINamedObject getNamedObject() {
		return namedObject;
	}

	public void setNamedObject(UINamedObject namedObject) {
		this.namedObject = namedObject;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
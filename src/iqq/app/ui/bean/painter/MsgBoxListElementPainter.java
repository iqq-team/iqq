/**
 * 
 */
package iqq.app.ui.bean.painter;

import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

import com.alee.extended.painter.DefaultPainter;
import com.alee.laf.StyleConstants;
import com.alee.laf.list.WebListElement;
import com.alee.utils.LafUtils;

/**
 * @author ZhiHui_Chen<6208317@qq.com>
 * @create date 2013-4-4
 */
public class MsgBoxListElementPainter extends DefaultPainter<WebListElement> {
	public MsgBoxListElementPainter() {
		super();
	}

	public Insets getMargin(WebListElement element) {
		return new Insets(element.getShadeWidth() + 3,
				element.getShadeWidth() + 3, element.getShadeWidth() + 3,
				element.getShadeWidth() + 3);
	}

	public void paint(Graphics2D g2d, Rectangle bounds, WebListElement element) {
		if (element.isSelected()) {
			paintSelectedBackground(g2d, element);
		} else if (element.isRollover()) {
			paintRolloverBackground(g2d, element);
		} else {
			paintNormalBackground(g2d, element);
		}
	}

	protected void paintSelectedBackground(Graphics2D g2d,
			WebListElement element) {
		LafUtils.drawWebStyle(g2d, element, StyleConstants.shadeColor,
				element.getShadeWidth(), element.getRound(), true, true, 0.5f);
	}

	protected void paintRolloverBackground(Graphics2D g2d,
			WebListElement element) {
		LafUtils.drawWebStyle(g2d, element, StyleConstants.shadeColor,
				element.getShadeWidth(), element.getRound(), true, true, 0.35f);
	}

	protected void paintNormalBackground(Graphics2D g2d, WebListElement element) {
		//
	}
}

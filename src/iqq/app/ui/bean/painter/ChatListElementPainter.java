package iqq.app.ui.bean.painter;

import iqq.app.ui.bean.IMChatListElement;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

import com.alee.extended.painter.DefaultPainter;
import com.alee.laf.StyleConstants;
import com.alee.utils.LafUtils;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-22
 */
public class ChatListElementPainter extends DefaultPainter<IMChatListElement> {
	public ChatListElementPainter() {
		super();
	}

	public Insets getMargin(IMChatListElement element) {
		return new Insets(element.getShadeWidth() + 3,
				element.getShadeWidth() + 3, element.getShadeWidth() + 3,
				element.getShadeWidth() + 8);
	}

	public void paint(Graphics2D g2d, Rectangle bounds,
			IMChatListElement element) {
		if (element.isSelected()) {
			paintSelectedBackground(g2d, element);
		} else if (element.isRollover()) {
			paintRolloverBackground(g2d, element);
		} else {
			paintNormalBackground(g2d, element);
		}
	}

	protected void paintSelectedBackground(Graphics2D g2d,
			IMChatListElement element) {
		LafUtils.drawWebStyle(g2d, element, StyleConstants.shadeColor,
				element.getShadeWidth(), element.getRound(), true, true, 0.6f);
	}

	protected void paintRolloverBackground(Graphics2D g2d,
			IMChatListElement element) {
		LafUtils.drawWebStyle(g2d, element, StyleConstants.shadeColor,
				element.getShadeWidth(), element.getRound(), true, true, 0.35f);
	}

	protected void paintNormalBackground(Graphics2D g2d,
			IMChatListElement element) {
		//
	}
}

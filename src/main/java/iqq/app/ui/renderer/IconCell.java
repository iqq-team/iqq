/**
 * 
 */
package iqq.app.ui.renderer;

import iqq.app.bean.UINamedObject;
import iqq.app.util.UIUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import org.apache.commons.lang3.StringUtils;

import com.alee.extended.image.WebDecoratedImage;
import com.alee.extended.layout.GroupLayout;
import com.alee.extended.panel.GroupPanel;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;

/**
 * @author ZhiHui_Chen<6208317@qq.com>
 * @create date 2013-4-8
 */
public class IconCell extends WebPanel {
	private static final long serialVersionUID = -8031925473208720653L;
	public static final Color NAME_COLOR = Color.BLACK;
	public static final Color TEXT_COLOR = Color.GRAY;
	public Dimension iconSize = new Dimension(45, 45);
	protected WebLabel firstNameLbl;
	protected WebLabel lastNameLbl;
	protected WebLabel textLbl;
	protected transient WebDecoratedImage iconImg;
	protected GroupPanel textMultiLinePanel;
	
	private UINamedObject namedObject;

	protected boolean isUpdate = false;

	public IconCell(String firstName, String lastName, String text,
			BufferedImage icon) {
		firstNameLbl = new WebLabel();
		lastNameLbl = new WebLabel();
		textLbl = new WebLabel();
		iconImg = new WebDecoratedImage(UIUtils.Bean.getDefaultFace());

		// component
		firstNameLbl.setForeground(NAME_COLOR);
		iconImg.setShadeWidth(1);
		iconImg.setRound(4);
		lastNameLbl.setForeground(TEXT_COLOR);
		textLbl.setForeground(TEXT_COLOR);
		textLbl.setMargin(0, 0, 0, 5);
		textLbl.setFont(WebLookAndFeel.globalTitleFont.deriveFont(12f));

		// set
		updateInfo(firstName, lastName, text, icon);

		// Panel
		textMultiLinePanel = new GroupPanel(0, false, new GroupPanel(true,
				firstNameLbl, lastNameLbl), textLbl);
		textMultiLinePanel.setMargin(10, 5, 5, 10);
		add(iconImg, BorderLayout.LINE_START);
		add(textMultiLinePanel, BorderLayout.CENTER);
		setOpaque(false);
	}

	/**
	 * @param named
	 */
	public IconCell(UINamedObject named) {
		this(named.getName(), named.getSubname(), named.getExtra(), named
				.getIcon());
		
		namedObject = named;
	}

	public IconCell updateIcon(BufferedImage icon, Dimension size) {
		this.iconSize = size;
		if (icon != null && iconImg.getSize().width != size.width
				&& iconImg.getSize().height != size.height) {
			iconImg.setImage(icon.getScaledInstance(iconSize.width,
					iconSize.height, 100));
			iconImg.revalidate();
			iconImg.repaint();
		}
		if (size.height >= 28) {
			if (textMultiLinePanel.getGap() != 0) {
				textMultiLinePanel.setLayout(new GroupLayout(
						GroupLayout.VERTICAL, 0));
				textMultiLinePanel.setGap(0);
			}
			if (size.height > 40) {
				textMultiLinePanel.setMargin(10, 5, 5, 10);
			} else {
				textMultiLinePanel.setMargin(2);
			}
		} else {
			if (textMultiLinePanel.getGap() != 1) {
				textMultiLinePanel.setLayout(new GroupLayout(
						GroupLayout.HORIZONTAL, 1));
				textMultiLinePanel.setGap(1);
				textMultiLinePanel.setMargin(5);
			}
		}
		revalidate();
		repaint();

		return this;
	}

	public void updateInfo(String firstName, String lastName, String text,
			BufferedImage icon) {
		// 备注（昵称）
		if (StringUtils.isNotEmpty(firstName)) {
			if (StringUtils.isNotEmpty(lastName)) {
				lastName = "(" + lastName + ")";
			} else {
				lastName = "";
			}
		}

		firstNameLbl.setText(firstName != null ? firstName : "");
		lastNameLbl.setText(lastName != null ? lastName : "");
		textLbl.setText(text);

		if (icon != null) {
			iconImg.setImage(icon.getScaledInstance(iconSize.width,
					iconSize.height, 100));
		}

	}

	public boolean isUpdate() {
		return isUpdate;
	}

	public void setUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	/**
	 * @param named
	 */
	public void updateInfo(UINamedObject named) {
		updateInfo(named.getName(), named.getSubname(), named.getExtra(),
				named.getIcon());
	}

	public UINamedObject getNamedObject() {
		return namedObject;
	}

	public void setNamedObject(UINamedObject namedObject) {
		this.namedObject = namedObject;
	}
}

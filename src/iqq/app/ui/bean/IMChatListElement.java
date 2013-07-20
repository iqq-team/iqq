package iqq.app.ui.bean;

import iqq.app.ui.bean.painter.ChatListElementPainter;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.Icon;

import com.alee.extended.painter.Painter;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebListElementStyle;
import com.alee.laf.panel.WebPanel;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-22
 */
public class IMChatListElement extends WebPanel {
	private static final long serialVersionUID = -5042407479685379384L;

	private Icon icon;
	private String title;
	private String id;

	private Painter<?> painter;
	private int round = 0;
	private int shadeWidth = 0;

	private boolean selected = false;
	private boolean isRollover = false;
	private boolean cellHasFocus = false;

	private WebButton removeBtn;

	public IMChatListElement(Icon icon, String title, WebButton removeBtn) {
		this.removeBtn = removeBtn;

		// 图片和标题
		WebLabel titleLabel = new WebLabel(title, icon);
		titleLabel.setMargin(0, 0, 0, 4);
		titleLabel.setAlignmentX(WebLabel.LEFT_ALIGNMENT);
		titleLabel.setOpaque(false);
		titleLabel.setForeground(Color.WHITE);
		this.add(titleLabel, BorderLayout.CENTER);
		this.add(removeBtn, BorderLayout.LINE_END);
		
		setPainter(new ChatListElementPainter());
	}

	/**
	 * @return the icon
	 */
	public Icon getIcon() {
		return icon;
	}

	/**
	 * @param icon
	 *            the icon to set
	 */
	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	public void updatePainter() {
		super.setPainter(painter);
	}

	public Painter getPainter() {
		return painter;
	}

	public void setPainter(Painter painter) {
		this.painter = painter;
		updatePainter();
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public int getShadeWidth() {
		return shadeWidth;
	}

	public void setShadeWidth(int shadeWidth) {
		this.shadeWidth = shadeWidth;
		updatePainter();
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isRollover() {
		return isRollover;
	}

	public void setRollover(boolean rollover) {
		isRollover = rollover;
	}

	public boolean isCellHasFocus() {
		return cellHasFocus;
	}

	public void setCellHasFocus(boolean cellHasFocus) {
		this.cellHasFocus = cellHasFocus;
	}

	/**
	 * @return the removeBtn
	 */
	public WebButton getRemoveBtn() {
		return removeBtn;
	}

	/**
	 * @param removeBtn
	 *            the removeBtn to set
	 */
	public void setRemoveBtn(WebButton removeBtn) {
		this.removeBtn = removeBtn;
	}

}

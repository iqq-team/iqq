package iqq.app.ui.bean;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * 表情
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-25
 */
public class FaceIcon{
	public final static String REGEX_PREFIX = "@{id}$";
	public final static String REGEX_ID = "{id}";
	
	private Icon icon;
	private String description;
	private String regex;
	private int id;

	/**
	 * 表情图标方法，传给表情图标的参数都不允许为空
	 * 
	 * @param imageIcon
	 * @param regex
	 */
	public FaceIcon(int id, Icon icon, String regex, String description) {
		setId(id);
		setIcon(icon);
		setRegex(regex);
		setDescription(description);
	}
	
	public int getIconHeight() {
		return icon.getIconHeight();
	}

	public int getIconWidth() {
		return icon.getIconWidth();
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		icon.paintIcon(c, g, x, y);
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getRegex() {
		return regex;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * 设置图标的描述串，建议用图标的相对路径描述
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the icon
	 */
	public Icon getIcon() {
		return icon;
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
}

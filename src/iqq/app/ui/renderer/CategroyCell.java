/**
 * 
 */
package iqq.app.ui.renderer;

import javax.swing.Icon;

import com.alee.laf.label.WebLabel;

/**
 * @author ZhiHui_Chen<6208317@qq.com>
 * @create date 2013-4-8
 */
public class CategroyCell extends WebLabel {
	private static final long serialVersionUID = -4935198658849269497L;
	protected Icon openIcon;
	protected Icon closedIcon;
	protected String name;
	protected boolean expanded;
	protected boolean isUpdate;
	
	public CategroyCell(Icon openIcon, Icon closedIcon, String name) {
		setOpenIcon(openIcon);
		setClosedIcon(closedIcon);
		setName(name);
		setMargin(5, 4, 4, 6);
	}

	/**
	 * @param expanded
	 * @return
	 */
	public CategroyCell getView(boolean expanded) {
		this.expanded = expanded;

		if (expanded) {
			setIcon(openIcon);
		} else {
			setIcon(closedIcon);
		}
		return this;
	}

	public Icon getClosedIcon() {
		return closedIcon;
	}

	public void setClosedIcon(Icon closedIcon) {
		this.closedIcon = closedIcon;
	}

	public Icon getOpenIcon() {
		return openIcon;
	}

	public void setOpenIcon(Icon openIcon) {
		this.openIcon = openIcon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		setText(name);
	}

	public boolean isUpdate() {
		return isUpdate;
	}

	public void setUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

}
/**
 * 
 */
package iqq.app.ui.bean;

import iqq.app.bean.UIUser;
import iqq.app.util.SkinUtils;
import iqq.app.util.UIUtils;
import iqq.im.bean.QQAccount;

import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.alee.laf.list.WebListElement;

/**
 * 群小小头像成员
 * @author ZhiHui_Chen<6208317@qq.com>
 * @create date 2013-4-10
 */
public class UserListElement extends WebListElement {
	private static final long serialVersionUID = -1101170613045864361L;

	private UIUser userNamed;

	public UserListElement(UIUser uiUser) {
		userNamed = uiUser;
	}
	
	public UIUser getUserNamed() {
		return userNamed;
	}

	public void setUserNamed(UIUser userNamed) {
		this.userNamed = userNamed;
	}

	@Override
	public String getText() {
		if(userNamed != null) {
			if(userNamed.getEntity() instanceof QQAccount) {
				QQAccount account = (QQAccount) userNamed.getEntity();
				return account.getUsername();
			}
			return userNamed.getName();
		}
		return super.getText();
	}
	
	@Override
	public Icon getIcon() {
		if(userNamed != null) {
			if(userNamed.getEntity() instanceof QQAccount) {
				QQAccount account = (QQAccount) userNamed.getEntity();
				BufferedImage face = account.getFace();
				if(face == null) {
					return UIUtils.Bean.getDefaultFace();
				} else {
					return new ImageIcon(face);
				}
			}
			
			return new ImageIcon(userNamed.getIcon().getScaledInstance(25, 25, 100));
		}
		return super.getIcon();
	}
	
	@Override
	public int hashCode() {
		if(userNamed != null) {
			return userNamed.getEntity().hashCode();
		}
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(userNamed != null) {
			return userNamed.getEntity().equals(obj);
		}
		return super.equals(obj);
	}
	
	@Override
	public String toString() {
		if(userNamed.getEntity() instanceof QQAccount) {
			QQAccount account = (QQAccount) userNamed.getEntity();
			return account.getUsername();
		}
		return userNamed.getName();
	}
}

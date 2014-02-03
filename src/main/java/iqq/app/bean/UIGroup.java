package iqq.app.bean;

import iqq.im.bean.QQGroup;

import java.awt.image.BufferedImage;


public class UIGroup implements UINamedObject {

	private QQGroup group;
	
	public UIGroup(QQGroup group) {
		this.group = group;
	}
	
	@Override
	public String getName() {
		return group.getName();
	}

	@Override
	public BufferedImage getIcon() {
		return group.getFace();
	}
	
	@Override
	public String getSubname() {
		return "";
	}
	
	@Override
	public Object getEntity() {
		return group;
	}

	public QQGroup getGroup() {
		return group;
	}

	public void setGroup(QQGroup group) {
		this.group = group;
	}

	@Override
	public String getExtra() {
		return group.getFingermemo();
	}
	
}

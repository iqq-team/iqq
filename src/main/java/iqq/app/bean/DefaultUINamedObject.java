package iqq.app.bean;

import java.awt.image.BufferedImage;

public class DefaultUINamedObject implements UINamedObject {
	private String name;
	private String subname;
	private BufferedImage icon;
	private Object entity;

	public DefaultUINamedObject(String name) {
		this.name = name;
	}

	public DefaultUINamedObject(String name, BufferedImage icon) {
		this.name = name;
		this.icon = icon;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getSubname() {
		return subname;
	}

	public void setIcon(BufferedImage icon) {
		this.icon = icon;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public BufferedImage getIcon() {
		return icon;
	}

	public Object getEntity() {
		return entity;
	}

	public void setEntity(Object entity) {
		this.entity = entity;
	}

	public void setSubname(String subname) {
		this.subname = subname;
	}

	@Override
	public String getExtra() {
		return "";
	}

}

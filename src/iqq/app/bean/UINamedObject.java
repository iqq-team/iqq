package iqq.app.bean;

import java.awt.image.BufferedImage;

public interface UINamedObject {
	public String getName();
	public String getSubname();
	public String getExtra();
	public BufferedImage getIcon();
	public Object getEntity();
}

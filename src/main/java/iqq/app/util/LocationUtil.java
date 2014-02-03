package iqq.app.util;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;

public class LocationUtil {
	public static Rectangle getScreenSize() {
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
		return defaultScreen.getDefaultConfiguration().getBounds();
	}

	public static Point getScreenRight(int width, int height) {
		Rectangle rect = getScreenSize();
		int x = (int) rect.getMaxX() - width - width / 2;
		int y = (int) rect.getMaxY() - height - height / 6;
		return new Point(x, y);
	}
	
	public static Point getScreenLeft(int width, int height) {
		Rectangle rect = getScreenSize();
		int x = (int) rect.getMaxX()/2 - width/2 - 100;
		int y = (int) rect.getMaxY() - height - height / 4;
		return new Point(x, y);
	}
}

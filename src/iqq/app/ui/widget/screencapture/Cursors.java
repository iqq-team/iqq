package iqq.app.ui.widget.screencapture;


import iqq.app.util.SkinUtils;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

public class Cursors {
	private static ResourceReader resourceReader = new ResourceReader();

	private Cursors() {
	}

	public static Cursor getCursor(CanvasAction action) {
		switch (action) {
			case CUT : return createCursor(SkinUtils.getImageIcon("chat/screenshot", "cut").getImage(), new Point(11, 11), "cut");
			case MOVE : return createCursor(SkinUtils.getImageIcon("chat/screenshot", "move").getImage(), new Point(12, 12), "move");
			case ZOOMIN : return createCursor(SkinUtils.getImageIcon("chat/screenshot", "zoomin").getImage(), new Point(13, 13), "zoomin");
			case ZOOMOUT : return createCursor(SkinUtils.getImageIcon("chat/screenshot", "zoomout").getImage(), new Point(13, 13), "zoomout");
			case PENCIL : return createCursor(SkinUtils.getImageIcon("chat/screenshot", "pencil").getImage(), new Point(8, 22), "pencil");
			case DRAW_RECTANGLE : return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
			case NOTHING : return Cursor.getDefaultCursor();
			case NW_RESIZE : return Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
			case N_RESIZE : return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
			case NE_RESIZE : return Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
			case E_RESIZE : return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
			case SE_RESIZE : return Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
			case S_RESIZE : return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
			case SW_RESIZE : return Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
			case W_RESIZE : return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
			default : return Cursor.getDefaultCursor();
		}
	}

	/**
	 * 根据指定图片URL路径创建自定义光标
	 * 
	 * @param imageUrl
	 *            图片URL路径
	 * @param hotSpot
	 *            光标热点
	 * @param name
	 *            光标名称
	 * @return 自定义Cursor
	 */
	public static Cursor createCursor(Image image, Point hotSpot, String name) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		return toolkit.createCustomCursor(image, hotSpot, name);
	}

	/**
	 * 根据指定二进制数组创建自定义光标
	 * 
	 * @param imagePixel
	 *            图片像素
	 * @param hotSpot
	 *            光标热点
	 * @param name
	 *            光标名称
	 * @return 自定义Cursor
	 */
	public static Cursor createCursor(byte[] imagePixel, Point hotSpot,
			String name) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		return toolkit.createCustomCursor(
			toolkit.createImage(imagePixel),
			hotSpot, name
		);
	}
}

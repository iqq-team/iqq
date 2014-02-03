package iqq.app.util;

import iqq.app.IMApp;
import iqq.app.core.IMService;
import iqq.app.service.IMSkinService;
import iqq.app.service.IMSkinService.Type;

import javax.swing.ImageIcon;

import com.alee.extended.painter.Painter;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-14
 */
public class SkinUtils {

	public static Painter<?> getPainter(Type type, String key) {
		return getService().getPainter(type, key);
	}

	public static ImageIcon getImageIcon(String key) {
		return getService().getImageIcon(key);
	}
	public static ImageIcon getImageIcon(String key, Object... params) {
		return getService().getImageIcon(key, params);
	}

	public static void setString(Type type, String key, String value) {
		getService().setString(type, key, value);
	}

	public static IMSkinService getService() {
		return (IMSkinService) IMApp.me().getSerivce(IMService.Type.SKIN);
	}
}

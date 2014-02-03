package iqq.app.util;

import iqq.app.IMApp;
import iqq.app.core.IMService.Type;
import iqq.app.service.IMPropService;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-14
 */
public class SettingUtils {
	public static String getString(String key) {
		return getService().getString(key);
	}

	public static void setString(String key, String value) {
		getService().setString(key, value);
	}

	public static int getInt(String key) {
		return getService().getInt(key);
	}

	public static void setInt(String key, int value) {
		getService().setInt(key, value);
	}

	public static IMPropService getService() {
		return (IMPropService) IMApp.me().getSerivce(Type.PROP);
	}
}

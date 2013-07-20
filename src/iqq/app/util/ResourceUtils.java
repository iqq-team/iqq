package iqq.app.util;

import iqq.app.IMApp;
import iqq.app.core.IMService.Type;
import iqq.app.service.IMResourceService;

import java.net.URL;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-14
 */
public class ResourceUtils {
	public static URL getFileURL(String url_name) {
		return getService().getFileURL(url_name);
	}

	public static String getFilePath(String filePath) {
		return getService().getFilePath(filePath);
	}
	
	public static String getFileDir(String filePath) {
		return getService().getFileDir(filePath);
	}

	public static URL getClassLoaderResouce(String url_name) {
		return getService().getClassLoaderResouce(url_name);
	}

	public static URL getResource(String url_name) {
		return getService().getResource(url_name);
	}
	
	public static String getResourceDir() {
		return getService().getResourceDir();
	}

	public static IMResourceService getService() {
		return (IMResourceService) IMApp.me().getSerivce(Type.RESOURCE);
	}
}

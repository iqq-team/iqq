package iqq.app.util;

import java.net.URI;
import java.net.URL;

import org.apache.log4j.Logger;

import com.alee.laf.optionpane.WebOptionPane;

/**
 * 封装一些操作系统原生的支持
 * 
 * @author ZhiHui_Chen
 * 
 */
public class OSUtils {
	private static final Logger LOG = Logger.getLogger(OSUtils.class);
	/**
	 * from:
	 * http://www.syntacticsugr.com/22-java/sugr_cubes/97-open-a-browser-from
	 * -java-windows-osx-linux
	 * 
	 * @author gedden
	 */
	public static class Browser {
		/**
		 * Attempts to open a browser. Currently this method ONLY supports http.
		 * 
		 * @param url
		 */
		public static final void open(URL url) {
			open(url.toString());
		}

		/**
		 * Attempts to open a browser. Currently this method ONLY supports http.
		 * 
		 * @param url
		 */
		public static final void open(String url) {
			// Seemingly awkward way of getting the current OS
			// Really just a masked call to System.getProperty("os.name");

			// Make sure the URL starts with http
			if (!url.startsWith("http")) {
				url = "http://" + url;
			}
			try {
				if (SystemUtils.isWindows()) {
					openBroswerWindows(url);
				} else if (SystemUtils.isLinux()) {
					openBroswerLINUX(url);
				} else if (SystemUtils.isMac()) {
					openBroswerOSX(url);
				} else {
					java.awt.Desktop desktop = null;
					if(java.awt.Desktop.isDesktopSupported()) {
						desktop = java.awt.Desktop.getDesktop();
						if(desktop.isSupported( java.awt.Desktop.Action.BROWSE )) {
							desktop.browse(new URI(url));
						}
			        } else {
			        	WebOptionPane.showMessageDialog(null, "系统不支持打开URL：" +  url);
			        }
				}
			} catch (Exception e) {
				LOG.error("打开URL出错：" +  url, e);
				WebOptionPane.showMessageDialog(null, "打开URL出错：" +  url);
			}
		}

		/**
		 * Opens a browser on a windows computer
		 * 
		 * @param url
		 */
		private static void openBroswerWindows(String url) throws Exception {
			Runtime.getRuntime().exec(
					"rundll32 url.dll,FileProtocolHandler " + url);
		}

		/**
		 * Opens a browser on a mac
		 * 
		 * @param url
		 */
		private static void openBroswerOSX(String url) throws Exception {
			Runtime.getRuntime().exec("open " + url);
		}

		/**
		 * Opens a browser on linux
		 * 
		 * @param url
		 */
		private static void openBroswerLINUX(String url) throws Exception {
			Process p = Runtime.getRuntime().exec("which mozilla");
			if (p.waitFor() == 0) {
				Runtime.getRuntime().exec("mozilla " + url);
				return;
			}

			p = Runtime.getRuntime().exec("which netscape");
			if (p.waitFor() == 0) {
				Runtime.getRuntime().exec("netscape " + url);
				return;
			}

			p = Runtime.getRuntime().exec("which opera");
			if (p.waitFor() == 0) {
				Runtime.getRuntime().exec("opera " + url);
				return;
			}
		}
	}
}

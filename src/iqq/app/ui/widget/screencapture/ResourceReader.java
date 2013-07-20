package iqq.app.ui.widget.screencapture;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 通过资源名称或URL及从文件系统查找并读取资源
 * 
 * @author Johnson Lee
 *
 */
public class ResourceReader {
	private URL url;
	
	/**
	 * 默认构造器
	 */
	public ResourceReader() {}
	
	/**
	 * 以资源名称构造Reader
	 * @param name 资源名称，通常以/开头
	 */
	public ResourceReader(String name) {
		this.applyResource(name);
	}
	
	/**
	 * 以资源的URL构造Reader
	 * @param url 资源的URL
	 */
	public ResourceReader(URL url) {
		this.applyResource(url);
	}
	
	/**
	 * 以File构造Reader
	 * @param file 文件
	 */
	public ResourceReader(File file) {
		this.applyResource(file);
	}
	
	/**
	 * 获取资源的字节，读取字符使用了<code>BufferedInputStream</code>，默认的缓冲区大小为10240
	 * @return 返回此Reader定位的资源的字节数组
	 * @throws ResourceNotFoundException 如果未找到相关资源，或者读取资源失误，会抛出此异常
	 */
	public byte[] getBytes() {
		ByteArrayOutputStream baos = null;
		BufferedInputStream bis = null;
		if (url == null) {
			return null;
		}
		try {
			bis = new BufferedInputStream(url.openStream());
			baos = new ByteArrayOutputStream();
			byte[] buff = new byte[10240];
			int len = -1;
			while ((len = bis.read(buff)) != -1) {
				baos.write(buff, 0, len);
			}
			baos.flush();
			return baos.toByteArray();
		} catch (IOException e) {
			return null;
		} finally {
			try {
				if (bis != null) bis.close();
				if (baos != null) baos.close();
			} catch (IOException e) { }
		}
	}
	
	/**
	 * 为此Reader重新应用URL
	 * @param url 资源的URL
	 */
	public void applyResource(URL url) {
		this.url = url;
	}
	
	/**
	 * 为此Reader重新应用name
	 * @param name 资源的name
	 */
	public void applyResource(String name) {
		this.url = this.getClass().getResource(name);
	}
	
	/**
	 * 为此Reader重新应用File
	 * @param file 文件
	 */
	public void applyResource(File file) {
		try {
			this.url = new URL("file://" + file.getAbsolutePath());
		} catch (MalformedURLException e) {
		}
	}
	
	/**
	 * 获取Reader定位的URL
	 * @return 返回此Reader定位的资源的URL
	 */
	public URL getURL() {
		return this.url;
	}
	
	public URL getURL(String name) {
		this.applyResource(name);
		return this.getURL();
	}
	
	public URL getURL(File file) {
		this.applyResource(file);
		return this.getURL();
	}
}

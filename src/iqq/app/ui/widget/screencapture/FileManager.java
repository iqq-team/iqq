package iqq.app.ui.widget.screencapture;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 文件操作工具类
 * 
 * @author Johnson Lee
 *
 */
public class FileManager {
	private static int bufferSize = 1024;
	
	/**
	 * 设置缓冲区大小，必须大于0
	 * 
	 * @param size 缓冲区大小	 * @throws IllegalArgumentException 当size小于0时，抛出异常
	 */
	public void setBufferSize(int size) {
		if (size < 1) {
			throw new IllegalArgumentException("bufferSize must greater than 0");
		}
		bufferSize = size;
	}

	/**
	 * 将指定字符串追加到文件末尾	 * 
	 * @param file 目标文件
	 * @param data 追加的字符串
	 * @param newline 是否在字符串末尾后追加\r\n
	 * @throws IOException
	 */
	public static void append(File file, String data, boolean newline) throws IOException {
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(file, true));
			bos.write(data.getBytes());
			if (newline) {
				bos.write('\r');
				bos.write('\n');
			}
		} finally {
			if (bos != null) { bos.flush(); bos.close();}
		}
	}
	
	public static void append(File file, byte[] data) throws IOException {
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(file, true));
			bos.write(data);
		} finally {
			if (bos != null) { bos.flush(); bos.close();}
		}
	}
	
	/**
	 * 将源文件与目标文件合并，合并后将删除源文件	 * 
	 * @param dest 目标文件
	 * @param src 源文件	 * @throws IOException
	 */
	public static void join(File dest, File src) throws IOException {
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		boolean complete = false;
		try {
			bis = new BufferedInputStream(new FileInputStream(src));
			bos = new BufferedOutputStream(new FileOutputStream(dest, true));
			byte[] buff = new byte[bufferSize];
			int len = -1;
			while ((len = bis.read(buff)) != -1) {
				bos.write(buff, 0, len);
			}
			complete = true;
		} finally {
			if (bos != null) { bos.flush(); bos.close();}
			if (bis != null) { bis.close();}
			if (complete) { src.delete(); }
		}
	}
	
	/**
	 * 分割文件，分割后的子文件的命名如下所示：
	 * <ul>
	 * 	<li><code>FileUtils.split(<i>file</i>, 1024, null)</code> &gt; document.txt &gt; document1.txt, document2.txt, ...</li>
	 *  <li><code>FileUtils.split(<i>file</i>, 1024, "mydoc")</code> &gt; document.txt &gt; mydoc1.txt, mydoc2.txt, ...</li>
	 * </ul>
	 * 
	 * @param file 待分割文件	 * @param size 分割大小
	 * @param newName 分割后的文件的名称	 * @throws IOException
	 */
	public static void split(File file, long size, String newName) throws IOException {
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		String extName = "";
		int dot = newName.lastIndexOf('.');
		if (dot != -1) {
			extName = newName.substring(dot);
			newName = newName.substring(0, dot);
		}
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
			long length = file.length();
			for (int i = 0; i < (int) Math.ceil(length * 1.0 / size); i++) {
				try {
					bos = new BufferedOutputStream(new FileOutputStream(file.getParent() + File.separator + newName + (i+1) + extName));
					byte[] buff = new byte[bufferSize];
					int len = -1;
					long buffCount = size / buff.length;
					for (int j = 0; j < buffCount; j += len) {
						len = bis.read(buff);
						bos.write(buff);
					}
					len = bis.read(buff, 0, (int) size % buff.length);
					bos.write(buff, 0, len);
				} finally {
					if (bos != null) { bos.flush(); bos.close();}
				}
			}
		} finally {
			if (bis != null) { bis.close();}
		}
	}
	
	/**
	 * 移动文件或目录	 * 
	 * @param file 待移动文件或目录
	 * @param path 目标路径
	 * @throws IOException
	 */
	public static void move(File file, String path) throws IOException {
		copy(file, path, true);
	}
	
	/**
	 * 复制文件或目录	 * 
	 * @param file 待复制的文件或目录	 * @param path 目标路径
	 * @throws IOException
	 */
	public static void copy(File file, String path) throws IOException {
		copy(file, path, false);
	}

	/**
	 * 移动文件到指定目录	 * @param files 文件
	 * @param path 目标目录
	 * @throws IOException
	 */
	public static void move(File[] files, String path) throws IOException {
		File dir = new File(path);
		if (!dir.exists()) {
			if (dir.mkdirs()) {
//System.out.println("create directory " + dir.getAbsolutePath());
			}
		}
		for (int i = 0; i < files.length; i++) {
			move(files[i], path + File.separator + files[i].getName());
		}
	}
	
	private static void copy(File file, String path, boolean deleteSrc) throws IOException {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		File target = new File(path);
		try {
			if (file.isDirectory()) {
				if (!target.exists()) {
					if (target.mkdirs()) {
//System.out.println("create directory " + target.getAbsolutePath());
					}
				}
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					move(files[i], target.getAbsolutePath() + File.separator + files[i].getName());
				}
			} else {
				bis = new BufferedInputStream(new FileInputStream(file));
				bos = new BufferedOutputStream(new FileOutputStream(target));
				byte[] buff = new byte[bufferSize];
				int len = -1;
				while ((len = bis.read(buff)) != -1) {
					bos.write(buff, 0, len);
				}
				bos.flush();
//System.out.println("copy file " + file.getAbsolutePath() + " > " + target.getAbsolutePath());
			}
		} finally {
			try {
				if (bis != null) bis.close();
				if (bos != null) bos.close();
				if (deleteSrc && target.exists() && file.delete()) {
					System.out.println("delete file " + file.getAbsolutePath());
				}
			} catch (IOException e) { throw e; }
		}
	}
}
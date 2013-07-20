package iqq.app.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.alee.extended.filechooser.SelectionMode;
import com.alee.extended.filechooser.WebFileChooser;
import com.alee.laf.GlobalConstants;

/**
 * Image Tools
 * 
 * @author ChenZhiHui
 * @create-time 2013-3-8
 */
public class IMImageUtil {
	public static final float DEFAULT_QUALITY = 0.2125f;
	public static WebFileChooser imageChooser = null;

	public static Image getScaledInstance(Image img, int width, int height,
			int hints) {
		if (img != null) {
			img = img.getScaledInstance(width, height, hints);
		}
		return img;
	}

	public static Image getScaledInstance(Image img, int width, int height) {
		return getScaledInstance(img, width, height, 100);
	}

	public static ImageIcon getScaledInstance(ImageIcon ImgIcon, int width,
			int height, int hints) {
		if (ImgIcon != null) {
			ImgIcon.setImage(getScaledInstance(ImgIcon.getImage(), width,
					height, hints));
		}
		return ImgIcon;
	}

	public static ImageIcon getScaledInstance(ImageIcon ImgIcon, int width,
			int height) {
		if (ImgIcon != null) {
			return getScaledInstance(ImgIcon, width, height, 100);
		}
		return ImgIcon;
	}

	/**
	 * 
	 * 压缩图片操作,返回BufferedImage对象 From:
	 * http://www.cnblogs.com/dennisit/archive/2012/12/28/2837452.html
	 * 
	 * @param imgPath
	 *            待处理图片
	 * @param quality
	 *            图片质量(0-1之間的float值)
	 * @param width
	 *            输出图片的宽度 输入负数参数表示用原来图片宽
	 * @param height
	 *            输出图片的高度 输入负数参数表示用原来图片高
	 * @param autoSize
	 *            是否等比缩放 true表示进行等比缩放 false表示不进行等比缩放
	 * @return 处理后的图片对象
	 * @throws IOException
	 * @throws Exception
	 */
	public static BufferedImage compressImage(Image img, float quality,
			int width, int height, boolean autoSize) {
		BufferedImage targetImage = null;
		if (quality < 0F || quality > 1F) {
			quality = DEFAULT_QUALITY;
		}
		// 如果用户输入的图片参数合法则按用户定义的复制,负值参数表示执行默认值
		int newwidth = (width > 0) ? width : img.getWidth(null);
		// 如果用户输入的图片参数合法则按用户定义的复制,负值参数表示执行默认值
		int newheight = (height > 0) ? height : img.getHeight(null);
		// 如果是自适应大小则进行比例缩放
		if (autoSize) {
			// 为等比缩放计算输出的图片宽度及高度
			double Widthrate = ((double) img.getWidth(null)) / (double) width
					+ 0.1;
			double heightrate = ((double) img.getHeight(null))
					/ (double) height + 0.1;
			double rate = Widthrate > heightrate ? Widthrate : heightrate;
			newwidth = (int) (((double) img.getWidth(null)) / rate);
			newheight = (int) (((double) img.getHeight(null)) / rate);
		}
		// 创建目标图像文件
		targetImage = new BufferedImage(newwidth, newheight,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = targetImage.createGraphics();
		g.drawImage(img, 0, 0, newwidth, newheight, null);
		// 如果添加水印或者文字则继续下面操作,不添加的话直接返回目标文件
		g.dispose();

		return targetImage;
	}

	/**
	 * 
	 * 压缩图片操作(文件物理存盘,使用png格式)
	 * 
	 * @param imgPath
	 *            待处理图片
	 * @param quality
	 *            图片质量(0-1之間的float值)
	 * @param width
	 *            输出图片的宽度 输入负数参数表示用原来图片宽
	 * @param height
	 *            输出图片的高度 输入负数参数表示用原来图片高
	 * @param autoSize
	 *            是否等比缩放 true表示进行等比缩放 false表示不进行等比缩放
	 * @param format
	 *            压缩后存储的格式
	 * @param destPath
	 *            文件存放路径
	 * @throws IOException
	 * 
	 * @throws Exception
	 */
	public static void compressImage(Image img, float quality, int width,
			int height, boolean autoSize, File destFile, String format)
			throws IOException {
		BufferedImage bufferedImage = compressImage(img, quality, width,
				height, autoSize);
		ImageIO.write(bufferedImage, format, destFile);

	}
	
	public static WebFileChooser getWebImgageChooser(Window view) {
		if (imageChooser == null) {
			imageChooser = new WebFileChooser(view, "Choose any image file");
			imageChooser.setSelectionMode(SelectionMode.SINGLE_SELECTION);
			imageChooser
					.setAvailableFilter(GlobalConstants.IMAGES_AND_FOLDERS_FILTER);
			imageChooser.setChooseFilter(GlobalConstants.IMAGES_FILTER);
		}
		return imageChooser;
	}

}

package iqq.app.ui.widget.screencapture;

import java.awt.image.BufferedImage;
/**
 * 
 */


/**
 * @author ZhiHui_Chen<6208317@qq.com>
 * @create date 2013-4-6
 */
public class ScreenEvent {
	public BufferedImage target;

	public ScreenEvent(BufferedImage target) {
		this.target = target;
	}

	public BufferedImage getTarget() {
		return target;
	}

	public void setTarget(BufferedImage target) {
		this.target = target;
	}
}

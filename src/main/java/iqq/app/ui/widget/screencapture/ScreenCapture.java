package iqq.app.ui.widget.screencapture;

/**
 * 
 */

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alee.laf.rootpane.WebWindow;

/**
 * Swing 截图实现
 * 
 * From: http://scrapture.googlecode.com/svn 做了一 些修改调整
 * 
 * @author ZhiHui_Chen<6208317@qq.com>
 * @create date 2013-4-6
 */
public class ScreenCapture extends WebWindow {
	private static final long serialVersionUID = 4330980461564059448L;
	private static final Logger LOG = LoggerFactory.getLogger(ScreenCapture.class);
	private List<ScreenCaptureListener> screenListeners = new ArrayList<ScreenCaptureListener>();
	private Canvas canvas;
	private BufferedImage screenImage;
	
	private static ScreenCapture instance = null;

	public static ScreenCapture getInstance(Frame onwer) {
		if(instance == null) {
			instance = new ScreenCapture(onwer);
			
		}
		return instance;
	}

	private ScreenCapture(Frame onwer) {
		super(onwer);
		Dimension dimScreen = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(dimScreen.width, dimScreen.height);
		try {
			screenImage = new Robot().createScreenCapture(new Rectangle(getSize()));
		} catch (AWTException e) {
			LOG.error("Screen Capture Error !!!", e);
			dispose();
		}
		canvas = new Canvas(this, screenImage);
		canvas.setAction(CanvasAction.CUT);
	}
	
	public void showScreenCapture() {
		try {
			screenImage = new Robot().createScreenCapture(new Rectangle(getSize()));
		} catch (AWTException e) {
			LOG.error("Screen Capture Error !!!", e);
			dispose();
		}
		canvas.setScreenBackground(screenImage);
		canvas.resize(new Point(), new Point(5, 5));
		canvas.setAction(CanvasAction.CUT);
		canvas.validate();
		canvas.repaint();
		getRootPane().setContentPane(canvas);
		setAlwaysOnTop(true);
		setVisible(true);
	}

	public void fireClipEvent(ScreenEvent evt) {
		for (ScreenCaptureListener s : screenListeners) {
			s.clip(evt);
		}
		setVisible(false);
	}

	public void fireCancelEvent(ScreenEvent evt) {
		for (ScreenCaptureListener s : screenListeners) {
			s.cancel(evt);
		}
		setVisible(false);
	}

	public List<ScreenCaptureListener> getScreenListeners() {
		return screenListeners;
	}

	public void setScreenListeners(List<ScreenCaptureListener> screenListeners) {
		this.screenListeners = screenListeners;
	}

	public void addScreenListener(ScreenCaptureListener s) {
		screenListeners.add(s);
	}
}

package iqq.app.ui.widget.screencapture;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Stack;

import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

/**
 * 画布
 * 
 * @author Johnson Lee
 * 
 */
public class Canvas extends JScrollPane {
	private static final long serialVersionUID = -5914144711867806611L;
	/**
	 * 裁剪框颜色
	 */
	public static final Color MARQUEE_COLOR = new Color(51, 153, 254);

	/**
	 * 画布提示信息的背景色
	 */
	public static final Color TOOL_TIP_COLOR = Color.getHSBColor(60, 12, 100);
	/**
	 * 画布提示信息的圆角大小
	 */
	public static final int TOOL_TIP_ROUND = 10;
	/**
	 * 裁剪框的拖拽角的大小
	 */
	public static final int EAR = 5;

	/**
	 * 画布的前景色
	 */
	public Color foreColor = Color.BLACK;

	/**
	 * 画布的背景色
	 */
	public Color backColor = foreColor;

	/**
	 * 当前画笔笔触轮廓，默认为1px粗
	 */
	public Stroke stroke = new BasicStroke(1.0f);

	/**
	 * 画布中的画的形状
	 */
	private Stack<CustomizableShape> shapes = new Stack<CustomizableShape>();
	/**
	 * 画面的当前动作
	 */
	public CanvasAction action = CanvasAction.NOTHING;
	/**
	 * 画布的当前未完成的形状
	 */
	private CustomizableShape currentShape;

	private KeyBuffer keyBuffer;

	private Zoomer zoomer;

	private JColorChooser colorChooser;

	/**
	 * 所属窗口
	 */
	private ScreenCapture owner;

	/**
	 * 画布的背景
	 */
	private BufferedImage screenBackground;

	private PreferencesDialog dlgSetting;

	public Canvas(ScreenCapture owner, BufferedImage image) {
		this.owner = owner;
		this.screenBackground = image;
		MouseInputAdapter mia = new CanvasMouseAdapter(this);
		this.setLayout(null);
		this.setFocusable(true);
		this.addMouseListener(mia);
		this.addMouseMotionListener(mia);
		
		CanvasKeyAdapter cka = new CanvasKeyAdapter(this);
		this.owner.addKeyListener(cka); 
		this.addKeyListener(cka);
		
		colorChooser = new JColorChooser(this.foreColor);
		zoomer = new Zoomer();
		keyBuffer = new KeyBuffer(4);
	}

	public void paint(Graphics g) {
		Image offScreen = doubleBufferedDraw();
		g.drawImage(offScreen, 0, 0, this);
		g.dispose();
	}

	public void update(Graphics g) {
		this.paint(g);
	}

	/**
	 * 使用双缓冲重绘画布
	 * 
	 * @return
	 */
	public Image doubleBufferedDraw() {
		Image offScreen = this.createImage(this.getWidth(), this.getHeight());
		Graphics2D g2d = (Graphics2D) offScreen.getGraphics();
		g2d.drawImage(screenBackground, 0, 0, this);
		for (Iterator<CustomizableShape> it = shapes.iterator(); it.hasNext();) {
			CustomizableShape shape = it.next();
			shape.paint(g2d);
		}
		if (this.currentShape != null) {
			this.currentShape.paint(g2d);
		}
		g2d.dispose();
		return offScreen;
	}

	public BufferedImage getScreenBackground() {
		return screenBackground;
	}

	public void setScreenBackground(BufferedImage screenBackground) {
		this.screenBackground = screenBackground;
	}

	public void setAction(CanvasAction action) {
		this.action = action;
		this.setCursor(Cursors.getCursor(action));
	}

	/**
	 * 裁剪画布
	 * 
	 * @param bounds
	 *            - 要截剪的矩形区域
	 * @return 返回截剪区域的图片
	 */
	public BufferedImage clip(Rectangle bounds) {
		Image offscreen = this.createImage(this.getWidth(), this.getHeight());
		Graphics2D g2d = (Graphics2D) offscreen.getGraphics();
		g2d.drawImage(screenBackground, 0, 0, this);
		for (Iterator<CustomizableShape> it = shapes.iterator(); it.hasNext();) {
			CustomizableShape shape = it.next();
			shape.paint(g2d);
		}
		g2d.dispose();
		BufferedImage buffer = new BufferedImage(offscreen.getWidth(this),
				offscreen.getHeight(this), BufferedImage.TYPE_INT_RGB);
		g2d = buffer.createGraphics();
		g2d.drawImage(offscreen, 0, 0, this);
		g2d.dispose();
		return buffer.getSubimage(bounds.x, bounds.y, bounds.width,
				bounds.height);
	}

	public void draw(CustomizableShape shape) {
		this.currentShape = shape;
		this.repaint();
	}

	/**
	 * 显示提示消息
	 * 
	 * @param g
	 *            Graphics
	 * @param p
	 *            显示的坐标
	 * @param tip
	 *            消息内容
	 */
	public void showTip(Graphics g, Point p, String tip) {
		Graphics2D g2d = (Graphics2D) g;
		Color c = g2d.getColor();
		Composite composite = g2d.getComposite();
		Object renderingHint = g2d
				.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		Composite alpha_2F = AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 0.5F);
		// 画长宽提示框
		TextLayout layout = new TextLayout(tip, g2d.getFont(),
				g2d.getFontRenderContext());
		Rectangle2D rect2D = layout.getBounds();
		rect2D.setRect(rect2D.getX() + p.x, rect2D.getY() + p.y,
				rect2D.getWidth() + TOOL_TIP_ROUND, rect2D.getHeight()
						+ TOOL_TIP_ROUND);

		// 颜色及半透明
		g2d.setColor(TOOL_TIP_COLOR);
		g2d.setComposite(alpha_2F);
		g2d.fillRoundRect((int) rect2D.getX(), (int) rect2D.getY(),
				(int) rect2D.getWidth(), (int) rect2D.getHeight(),
				TOOL_TIP_ROUND, TOOL_TIP_ROUND);
		g2d.setColor(c);
		g2d.setComposite(composite);
		// 布局文本
		layout.draw(g2d, p.x + TOOL_TIP_ROUND / 2, p.y + TOOL_TIP_ROUND / 2);
		// 抗锯齿
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.drawRoundRect((int) rect2D.getX(), (int) rect2D.getY(),
				(int) rect2D.getWidth(), (int) rect2D.getHeight(),
				TOOL_TIP_ROUND, TOOL_TIP_ROUND);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, renderingHint);
		this.repaint();
	}

	/**
	 * 移动裁剪框
	 * 
	 * @param start
	 * @param end
	 */
	public void move(Point start, Point end) {
		if (this.currentShape != null) {
			Rectangle rect = (Rectangle) this.currentShape.getShape();
			rect.x += end.x - start.x;
			rect.y += end.y - start.y;
			this.repaint();
		}
	}

	public void zoom(Point start, int size) {

	}

	/**
	 * 裁剪屏幕截图
	 * 
	 * @param start
	 * @param end
	 */
	public void cut(Point start, Point end) {
		Rectangle rect = null;
		boolean shift = this.keyBuffer.isPressed(KeyEvent.VK_SHIFT);
		if (!shift) {
			rect = new Rectangle(Math.min(start.x, end.x), Math.min(start.y,
					end.y), Math.abs(start.x - end.x),
					Math.abs(start.y - end.y));
		} else {
			int l = Math.min(Math.abs(start.x - end.x),
					Math.abs(start.y - end.y));
			rect = new Rectangle(Math.min(start.x, end.x), Math.min(start.y,
					end.y), l, l);
		}
		this.currentShape = new CustomizableShape(rect, this) {
			public Rectangle getBounds() {
				return (Rectangle) this.getShape();
			}

			public void paint(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				Color c = g2d.getColor();
				Composite composite = g2d.getComposite();
				Composite alpha_2F = AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, 0.3F);
				Rectangle r = (Rectangle) this.getShape();
				g2d.setColor(Canvas.MARQUEE_COLOR);
				g2d.setComposite(alpha_2F);
				g2d.fill(this.getBounds());
				g2d.setComposite(composite);
				g2d.draw(r);
				g2d.draw(new Rectangle(r.x - EAR / 2, r.y - EAR / 2, EAR, EAR));
				g2d.draw(new Rectangle(r.x + r.width / 2 - EAR / 2, r.y - EAR
						/ 2, EAR, EAR));
				g2d.draw(new Rectangle(r.x + r.width - EAR / 2, r.y - EAR / 2,
						EAR, EAR));
				g2d.draw(new Rectangle(r.x + r.width - EAR / 2, r.y + r.height
						/ 2 - EAR / 2, EAR, EAR));
				g2d.draw(new Rectangle(r.x + r.width - EAR / 2, r.y + r.height
						- EAR / 2, EAR, EAR));
				g2d.draw(new Rectangle(r.x + r.width / 2 - EAR / 2, r.y
						+ r.height - EAR / 2, EAR, EAR));
				g2d.draw(new Rectangle(r.x - EAR / 2, r.y + r.height - EAR / 2,
						EAR, EAR));
				g2d.draw(new Rectangle(r.x - EAR / 2, r.y + r.height / 2 - EAR
						/ 2, EAR, EAR));
				g2d.setColor(c);
				g2d.setComposite(composite);
				// 坐标 长宽 提示框
				this.getCanvas().showTip(
						g,
						new Point(r.x, r.y - 15),
						"(" + r.x + ", " + r.y + ") " + r.width + " × "
								+ r.height);
			}
		};
		this.repaint();
	}

	/**
	 * 复制截图
	 */
	public void copy() {
		if(!owner.isVisible()) return ;
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable transferable = new Transferable() {
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[] { DataFlavor.imageFlavor };
			}

			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return DataFlavor.imageFlavor.equals(flavor);
			}

			public Object getTransferData(DataFlavor flavor) {
				Image image = null;
				if (isDataFlavorSupported(flavor)) {
					switch (action) {
					case CUT:
					case MOVE:
					case E_RESIZE:
					case N_RESIZE:
					case NE_RESIZE:
					case NW_RESIZE:
					case S_RESIZE:
					case SE_RESIZE:
					case SW_RESIZE:
					case W_RESIZE:
						Rectangle r = new Rectangle(owner.getSize());
						if(r == null ||r.width <= 0 || currentShape == null || currentShape.getBounds().width <= 0) return "";
						image = clip(currentShape.getBounds().intersection(r));
						break;
					default:
						image = doubleBufferedDraw();
						break;
					}
					return image;
				}
				return null;
			}
		};
		clipboard.setContents(transferable, null);
	}

	/**
	 * 调整截剪框的大小
	 * 
	 * @param start
	 * @param end
	 */
	public void resize(Point start, Point end) {
		if (currentShape != null) {
			Rectangle rect = new Rectangle(Math.min(start.x, end.x), Math.min(
					start.y, end.y), Math.abs(start.x - end.x),
					Math.abs(start.y - end.y));
			currentShape.setShape(rect);
			this.repaint();
		}
	}
	
	public void exit() {
		owner.fireCancelEvent(new ScreenEvent(screenBackground));
		this.owner.setVisible(false);
	}

	/**
	 * 保存截图
	 * 
	 * @param image
	 */
	public void save(Image image) {
		
	}

	public void setColor() {
		final ColorSelectionModel csm = colorChooser.getSelectionModel();
		csm.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				foreColor = csm.getSelectedColor();
			}
		});
		JDialog dlgColor = JColorChooser.createDialog(this, "选择颜色", false,
				colorChooser, null, null);
		dlgColor.setVisible(true);
	}

	public void setStroke() {
		String width = "1";
		do {
			width = JOptionPane.showInputDialog(this, "粗细(px):", "设置笔触大小",
					JOptionPane.PLAIN_MESSAGE);
			try {
				if (width != null) {
					stroke = new BasicStroke(Float.parseFloat(width));
				}
				return;
			} catch (NumberFormatException e) {
				continue;
			}
		} while (true);
	}

	class CanvasMouseAdapter extends MouseInputAdapter {
		private Canvas canvas;
		private Point start;
		private Point end;

		private CanvasMouseAdapter(Canvas canvas) {
			this.canvas = canvas;
		}

		public void mousePressed(MouseEvent e) {
			end = start = e.getPoint();
			Rectangle r = null;
			if (currentShape != null) {
				r = currentShape.getBounds();
			}
			switch (action) {
			case N_RESIZE:
			case NW_RESIZE:
				start = new Point(r.x + r.width, r.y + r.height);
				break;
			case E_RESIZE:
			case NE_RESIZE:
				start = new Point(r.x, r.y + r.height);
				break;
			case S_RESIZE:
			case SE_RESIZE:
				start = new Point(r.x, r.y);
				break;
			case W_RESIZE:
			case SW_RESIZE:
				start = new Point(r.x + r.width, r.y);
				break;
			case ZOOMIN:
				canvas.zoom(start, zoomer.zoomIn());
				break;
			case ZOOMOUT:
				canvas.zoom(start, zoomer.zoomOut());
				break;
			case DRAW_LINE:
				canvas.draw(new JLine(start, end, canvas));
				break;
			case DRAW_RECTANGLE:
				canvas.draw(new JRectangle(new Rectangle2D.Double(start.x,
						start.y, 0, 0), canvas));
				break;
			case PENCIL:
				canvas.draw(new JPath(new GeneralPath(new Line2D.Double(start,
						start)), canvas));
				break;
			}
		}

		public void mouseDragged(MouseEvent e) {
			int deltaX = e.getPoint().x - end.x;
			int deltaY = e.getPoint().y - end.y;
			Rectangle r = null;
			end = e.getPoint();
			if (currentShape != null) {
				r = currentShape.getBounds();
			}
			switch (action) {
			case CUT:
				canvas.cut(start, end);
				break;
			case N_RESIZE:
				resize(start, new Point(start.x - r.width, end.y));
				break;
			case E_RESIZE:
				resize(start, new Point(end.x, start.y - r.height));
				break;
			case S_RESIZE:
				resize(start, new Point(start.x + r.width, end.y));
				break;
			case W_RESIZE:
				resize(start, new Point(end.x, start.y + r.height));
				break;
			case NW_RESIZE:
			case NE_RESIZE:
			case SE_RESIZE:
			case SW_RESIZE:
				resize(start, new Point(end.x, end.y));
				break;
			case MOVE:
				canvas.move(new Point(end.x - deltaX, end.y - deltaY), end);
				break;
			}
		}

		public void mouseReleased(MouseEvent e) {
			start = end = null;
			switch (action) {
			case PENCIL:
			case DRAW_TEXT:
			case DRAW_LINE:
			case DRAW_CIRCLE:
			case DRAW_ELLIPSE:
			case DRAW_POLYGON:
			case DRAW_RECTANGLE:
			case DRAW_ROUNDRECTANGLE:
				if (currentShape != null) {
					shapes.push(currentShape.clone());
					currentShape = null;
				}
				break;
			}
		}

		public void mouseClicked(MouseEvent e) {
			switch (action) {
			case CUT:
			case MOVE:
			case E_RESIZE:
			case N_RESIZE:
			case NE_RESIZE:
			case NW_RESIZE:
			case S_RESIZE:
			case SE_RESIZE:
			case SW_RESIZE:
			case W_RESIZE:
				if (currentShape != null) {
					if (!e.isMetaDown() && e.getClickCount() == 2) {
						copy(); // 复制到剪贴板
						Rectangle r = new Rectangle(owner.getSize());
						owner.fireClipEvent(new ScreenEvent(clip(currentShape
								.getBounds().intersection(r))));
						exit();
					} else if(e.getClickCount() == 1) {
						//resize(new Point(), new Point());
					} else if(e.getClickCount() == 2) {
						exit();
					}
				}
				break;
			}
		}

		public void mouseMoved(MouseEvent e) {
			switch (action) {
			case CUT:
			case MOVE:
			case E_RESIZE:
			case N_RESIZE:
			case NE_RESIZE:
			case NW_RESIZE:
			case S_RESIZE:
			case SE_RESIZE:
			case SW_RESIZE:
			case W_RESIZE:
				if (currentShape != null) {
					Point p = e.getPoint();
					Rectangle r = currentShape.getBounds();
					if (new Rectangle(r.x - EAR / 2, r.y - EAR / 2, EAR, EAR)
							.contains(p)) {
						canvas.setAction(CanvasAction.NW_RESIZE);
					} else if (new Rectangle(r.x + r.width / 2 - EAR / 2, r.y
							- EAR / 2, EAR, EAR).contains(p)) {
						canvas.setAction(CanvasAction.N_RESIZE);
					} else if (new Rectangle(r.x + r.width - EAR / 2, r.y - EAR
							/ 2, EAR, EAR).contains(p)) {
						canvas.setAction(CanvasAction.NE_RESIZE);
					} else if (new Rectangle(r.x + r.width - EAR / 2, r.y
							+ r.height / 2 - EAR / 2, EAR, EAR).contains(p)) {
						canvas.setAction(CanvasAction.E_RESIZE);
					} else if (new Rectangle(r.x + r.width - EAR / 2, r.y
							+ r.height - EAR / 2, EAR, EAR).contains(p)) {
						canvas.setAction(CanvasAction.SE_RESIZE);
					} else if (new Rectangle(r.x + r.width / 2 - EAR / 2, r.y
							+ r.height - EAR / 2, EAR, EAR).contains(p)) {
						canvas.setAction(CanvasAction.S_RESIZE);
					} else if (new Rectangle(r.x - EAR / 2, r.y + r.height
							- EAR / 2, EAR, EAR).contains(p)) {
						canvas.setAction(CanvasAction.SW_RESIZE);
					} else if (new Rectangle(r.x - EAR / 2, r.y + r.height / 2
							- EAR / 2, EAR, EAR).contains(p)) {
						canvas.setAction(CanvasAction.W_RESIZE);
					} else if (r.contains(p)) {
						canvas.setAction(CanvasAction.MOVE);
					} else {
						canvas.setAction(CanvasAction.CUT);
					}
				}
				break;
			case ZOOMIN:
				canvas.setAction(CanvasAction.ZOOMIN);
				break;
			case ZOOMOUT:
				canvas.setAction(CanvasAction.ZOOMOUT);
				break;
			}
		}
	}

	class CanvasKeyAdapter extends KeyAdapter {
		private Canvas canvas;

		public CanvasKeyAdapter(Canvas canvas) {
			this.canvas = canvas;
		}

		public void keyPressed(KeyEvent e) {
			keyBuffer.pressed(e);
			switch (e.getKeyCode()) {
			case KeyEvent.VK_C:
				if (keyBuffer.isPressed(KeyEvent.VK_CONTROL)) {
					canvas.copy();
				} else if (keyBuffer.isPressed(KeyEvent.VK_ALT)) {
					canvas.setColor();
				} else {
					canvas.setAction(CanvasAction.CUT);
				}
				break;
			case KeyEvent.VK_F12:
				if (dlgSetting == null) {
					dlgSetting = new PreferencesDialog("Preferences");
				}
				dlgSetting.setVisible(true);
				break;
			case KeyEvent.VK_P:
				canvas.setAction(CanvasAction.PENCIL);
				break;
			case KeyEvent.VK_L:
				canvas.setAction(CanvasAction.DRAW_LINE);
				break;
			case KeyEvent.VK_R:// 画矩形
				if (keyBuffer.isPressed(KeyEvent.VK_SHIFT)) {
					canvas.setAction(CanvasAction.DRAW_ROUNDRECTANGLE);
				} else {
					canvas.setAction(CanvasAction.DRAW_RECTANGLE);
				}
				break;
			case KeyEvent.VK_E:// 画椭圆
				if (keyBuffer.isPressed(KeyEvent.VK_SHIFT)) {
					canvas.setAction(CanvasAction.DRAW_CIRCLE);
				} else {
					canvas.setAction(CanvasAction.DRAW_ELLIPSE);
				}
			case KeyEvent.VK_S:
				if (keyBuffer.isPressed(KeyEvent.VK_CONTROL)) {
					canvas.save(canvas.doubleBufferedDraw());// 保存
				} else if (keyBuffer.isPressed(KeyEvent.VK_ALT)) {
					canvas.setStroke();// 设置笔触粗细
				}
				break;
			case KeyEvent.VK_Z:
				if (keyBuffer.isPressed(KeyEvent.VK_ALT)) {
					canvas.setAction(CanvasAction.ZOOMIN);// 缩小
				} else if (keyBuffer.isPressed(KeyEvent.VK_CONTROL)) {// 撤消
					if (!shapes.empty()) {
						shapes.pop();
						canvas.repaint();
					}
				} else {
					canvas.setAction(CanvasAction.ZOOMOUT);// 放大
				}
				break;
			case KeyEvent.VK_ESCAPE: // 取消,退出
				exit();
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_KP_DOWN:
			case KeyEvent.VK_UP:
			case KeyEvent.VK_KP_UP:
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_KP_LEFT:
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_KP_RIGHT:
				switch (action) {
				case CUT:
				case MOVE:
				case E_RESIZE:
				case N_RESIZE:
				case NE_RESIZE:
				case NW_RESIZE:
				case S_RESIZE:
				case SE_RESIZE:
				case SW_RESIZE:
				case W_RESIZE:
					if (currentShape != null) {
						Rectangle rect = (Rectangle) currentShape.getShape();
						if (keyBuffer.isPressed(KeyEvent.VK_DOWN)
								|| keyBuffer.isPressed(KeyEvent.VK_KP_DOWN)) {
							rect.y += 1;
						}
						if (keyBuffer.isPressed(KeyEvent.VK_UP)
								|| keyBuffer.isPressed(KeyEvent.VK_KP_UP)) {
							rect.y -= 1;
						}
						if (keyBuffer.isPressed(KeyEvent.VK_LEFT)
								|| keyBuffer.isPressed(KeyEvent.VK_KP_LEFT)) {
							rect.x -= 1;
						}
						if (keyBuffer.isPressed(KeyEvent.VK_RIGHT)
								|| keyBuffer.isPressed(KeyEvent.VK_KP_RIGHT)) {
							rect.x += 1;
						}
						currentShape.setShape(rect);
						canvas.repaint();
					}
					break;
				}
				break;
			}
			super.keyPressed(e);
		}

		public void keyReleased(KeyEvent e) {
			keyBuffer.released(e);
		}
	}

	static class KeyBuffer {
		private static int buffer[];

		public KeyBuffer(int size) {
			buffer = new int[size];
		}

		public synchronized boolean isPressed(int keyCode) {
			for (int i = 0; i < buffer.length; i++) {
				if (buffer[i] == keyCode)
					return true;
			}
			return false;
		}

		public synchronized void pressed(KeyEvent e) {
			int index = -1;
			for (int i = 0; i < buffer.length; i++) {
				if (buffer[i] == e.getKeyCode()) {
					index = i;
					break;
				} else if (index < 0 && buffer[i] == KeyEvent.VK_UNDEFINED) {
					index = i;
				}
			}
			if (index >= 0) {
				buffer[index] = e.getKeyCode();
				this.sort();
			}
		}

		public synchronized void released(KeyEvent e) {
			for (int i = 0; i < buffer.length; i++) {
				if (buffer[i] == e.getKeyCode()) {
					buffer[i] = KeyEvent.VK_UNDEFINED;
					this.sort();
					break;
				}
			}
		}

		public synchronized void sort() {
			for (int i = 0; i < buffer.length; i++) {
				for (int j = 0; j < i; j++) {
					if (buffer[i] > buffer[j]) {
						buffer[i] = buffer[i] ^ buffer[j];
						buffer[j] = buffer[i] ^ buffer[j];
						;
						buffer[i] = buffer[i] ^ buffer[j];
					}
				}
			}
		}
	}

	static class Zoomer {
		private static final int[] size = { 1, 2, 3, 4, 5, 6, 7, 8, 12, 16, 32 };
		private static int index = 0;

		public synchronized int zoomOut() {
			if (index < size.length - 1) {
				index += 1;
			}
			return size[index];
		}

		public synchronized int zoomIn() {
			if (index > 0) {
				index -= 1;
			}
			return size[index];
		}

		public synchronized void reset() {
			index = 0;
		}
	}
}
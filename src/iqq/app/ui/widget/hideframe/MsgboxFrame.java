package iqq.app.ui.widget.hideframe;

/**
 * @author ZhiHui_Chen<6208317@qq.com>
 * @create date 2013-4-3
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;

public class MsgboxFrame extends WebFrame implements ActionListener {
	private static final long serialVersionUID = -496898429932291825L;

	private int sideWidth = 100; // 边宽度
	private int side = 5; // 左显示多少隐藏后可见，最少为1
	private int gap = 0;

	private Rectangle rect;
	private int frameLeft;// 窗体离屏幕左边的距离
	private int frameRight;// 窗体离屏幕右边的距离；
	private int frameTop;// 窗体离屏幕顶部的距离
	private int frameWidth; // 窗体的宽
	private int frameHeight; // 窗体的高

	private int screenXX;// 屏幕的宽度；
	private Point point; // 鼠标在窗体的位置

	private Timer timer = new Timer(10, this);
	private IPosition position;

	public MsgboxFrame() {
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		frameLeft = (int) getBounds().getX();
		frameTop = (int) getBounds().getY();
		frameWidth = getWidth();
		frameHeight = getHeight();
		screenXX = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
		frameRight = screenXX - frameLeft - frameWidth;

		// 获取窗体的轮廓
		rect = new Rectangle(0, 0, frameWidth, frameHeight);
		// 获取鼠标在窗体的位置
		point = getMousePosition();

		if (position == Positions.LEFT) {
			if (frameLeft < 0 && isPtInRect(rect, point)) {
				setLocation(0, frameTop); // 隐藏在左边，鼠标指到后显示窗体；
			} else if (frameLeft > -side + 1 && frameLeft < side + 1
					&& !(isPtInRect(rect, point))) {
				setLocation(frameLeft - frameWidth + side, frameTop); // 窗体移到左边边缘隐藏到左边；
			}
		} else if (position == Positions.TOP) {
			if (frameTop < 0 && isPtInRect(rect, point)) {
				setLocation(frameLeft, frameTop + side); // 隐藏在左边，鼠标指到后显示窗体；
			} else if (frameTop > -side + 1 && frameTop < side + 1
					&& !(isPtInRect(rect, point))) {
				setLocation(frameLeft, frameTop - frameHeight + side); // 窗体移到屏幕后隐藏
			}
		} else if (position == Positions.RIGHT) {
			if (frameRight < 0 && isPtInRect(rect, point)) {
				setLocation(screenXX - frameWidth, frameTop);// 鼠标指到后显示；
			} else if (frameRight > -side - 1 && frameRight < side + 1
					&& !(isPtInRect(rect, point))) {
				setLocation(screenXX - side, frameTop); // 窗体移到屏幕后隐藏
			}
		}

	}

	/**
	 * 检测是否在矩形框内
	 * 
	 * @param rect
	 * @param point
	 * @return
	 */
	public boolean isPtInRect(Rectangle rect, Point point) {
		if (rect != null && point != null) {
			int x0 = rect.x;
			int y0 = rect.y;
			int x1 = rect.width;
			int y1 = rect.height;
			int x = point.x;
			int y = point.y;

			return x >= x0 && x < x1 && y >= y0 && y < y1;
		}
		return false;
	}

	public void setPosition(IPosition position, int sideWidth, int side) {
		setPosition(position, sideWidth, side, 0);
	}

	public void setPosition(IPosition position, int sideWidth, int side, int gap) {
		this.position = position;
		this.side = side;
		this.gap = gap;
		setBounds(position.getPosition(sideWidth, side, gap));
	}

	public static void main(String[] args) {
		MsgboxFrame frame = new MsgboxFrame();
		frame.setPosition(Positions.RIGHT, 100, 2, 100); // 设置在屏幕显示地方
		frame.getContentPane().setBackground(new Color(20, 20, 20));
		frame.setVisible(true);
		
		JLabel title = new JLabel("Message Box");
		title.setForeground(Color.WHITE);
		WebPanel p = new WebPanel();
		p.setBackground(new Color(20, 20, 20));
		p.add(title, BorderLayout.CENTER);
		frame.add(p, BorderLayout.PAGE_START);
	}
}
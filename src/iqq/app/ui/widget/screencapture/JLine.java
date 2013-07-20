package iqq.app.ui.widget.screencapture;


import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import javax.swing.event.MouseInputAdapter;

public class JLine extends CustomizableShape {

	public JLine(Point p1, Point p2, Canvas canvas) {
		this(new Line2D.Double(p1, p2), canvas);
	}
	
	public JLine(Line2D line, Canvas canvas) {
		super(line, canvas);
		this.addMouseInputListener(new JLineMouseAdapter(this));
	}
	
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Color c = g2d.getColor();
		Composite comp = g2d.getComposite();
		Stroke s = g2d.getStroke();
		if (foreColor != null) g2d.setColor(foreColor);
		if (composite != null) g2d.setComposite(composite);
		if (stroke != null) g2d.setStroke(stroke);
		g2d.draw((Shape) this.getShape());
		g2d.setComposite(comp);
		g2d.setColor(c);
		g2d.setStroke(s);
	}
	
	class JLineMouseAdapter extends MouseInputAdapter {
		private JLine line;
		JLineMouseAdapter(JLine line) {
			this.line = line;
		}
		
		public void mouseDragged(MouseEvent e) {
			Point end = e.getPoint();
			Line2D l2d = (Line2D) line.getShape();
			l2d.setLine(l2d.getP1(), end);
			line.getCanvas().repaint();
		}

		public void mouseReleased(MouseEvent e) {
			line.removeMouseInputListener(this);
		}
	}
}

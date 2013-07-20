package iqq.app.ui.widget.screencapture;


import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import javax.swing.event.MouseInputAdapter;

public class JRectangle extends CustomizableShape {

	public JRectangle(Rectangle2D shape, Canvas canvas) {
		super(shape, canvas);
		this.addMouseInputListener(new JRectangleMouseAdapter(this));
	}
	
	public JRectangle(Point p, Dimension d, Canvas canvas) {
		this(new Rectangle2D.Double(p.x, p.y, d.width, d.height), canvas);
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
	
	class JRectangleMouseAdapter extends MouseInputAdapter {
		private JRectangle rect;
		private Point start;
		
		JRectangleMouseAdapter(JRectangle rect) {
			this.rect = rect;
			this.start = rect.getBounds().getLocation();
		}
		
		public void mouseDragged(MouseEvent e) {
			Point end = e.getPoint();
			Rectangle2D r2d = (Rectangle2D) rect.getShape();
			r2d.setRect(
				Math.min(start.x, end.x),
				Math.min(start.y, end.y),
				Math.abs(end.x - start.x),
				Math.abs(end.y - start.y)
			);
			rect.getCanvas().repaint();
		}

		public void mouseReleased(MouseEvent e) {
			rect.removeMouseInputListener(this);
		}
	}
}

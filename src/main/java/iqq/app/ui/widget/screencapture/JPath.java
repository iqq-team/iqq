package iqq.app.ui.widget.screencapture;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import javax.swing.event.MouseInputAdapter;

public class JPath extends CustomizableShape {

	public JPath(GeneralPath path, Canvas canvas) {
		super(path, canvas);
		this.addMouseInputListener(new JFreeLineMouseAdapter(this));
	}
	
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Color c = g2d.getColor();
		Composite comp = g2d.getComposite();
		Stroke s = g2d.getStroke();
		if (foreColor != null) g2d.setColor(foreColor);
		if (composite != null) g2d.setComposite(composite);
		if (stroke != null) {
			BasicStroke bs = (BasicStroke) stroke;
			g2d.setStroke(new BasicStroke(
				bs.getLineWidth(), 
				BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND
			));
		}
		g2d.draw((Shape) this.getShape());
		g2d.setComposite(comp);
		g2d.setColor(c);
		g2d.setStroke(s);
	}
	
	class JFreeLineMouseAdapter extends MouseInputAdapter {
		private JPath path;
		
		JFreeLineMouseAdapter(JPath path) {
			this.path = path;
		}
		
		public void mouseDragged(MouseEvent e) {
			Point end = e.getPoint();
			GeneralPath gp = (GeneralPath) path.getShape();
			gp.append(new Line2D.Double(end, end), true);
			path.getCanvas().repaint();
		}

		public void mouseReleased(MouseEvent e) {
			path.removeMouseInputListener(this);
		}
	}

}

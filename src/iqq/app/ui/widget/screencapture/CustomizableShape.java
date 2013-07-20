package iqq.app.ui.widget.screencapture;


import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.KeyListener;
import javax.swing.event.MouseInputListener;

public abstract class CustomizableShape implements Cloneable {
	protected Canvas canvas;
	protected Object shape;
	protected Composite composite;
	protected Color foreColor;
	protected Stroke stroke;
	
	public CustomizableShape(Object shape, Canvas canvas) {
		super();
		this.shape = shape;
		this.canvas = canvas;
		this.setForeColor(canvas.foreColor);
		this.setStroke(canvas.stroke);
	}
	
	public abstract void paint(Graphics g);
	
	public void addMouseInputListener(MouseInputListener mil) {
		canvas.addMouseListener(mil);
		canvas.addMouseMotionListener(mil);
	}
	
	public void addKeyListener(KeyListener listener) {
		canvas.addKeyListener(listener);
	}
	
	public void removeMouseInputListener(MouseInputListener mil) {
		canvas.removeMouseListener(mil);
		canvas.removeMouseMotionListener(mil);
	}
	
	public void removeKeyListener(KeyListener listener) {
		canvas.removeKeyListener(listener);
	}
	
	public Rectangle getBounds() {
		if (shape instanceof Shape) {
			return ((Shape) shape).getBounds();
		}
		return new Rectangle(0, 0, 0, 0);
	}
	
	public CustomizableShape clone()  {
		try {
			return (CustomizableShape) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object getShape() {
		return shape;
	}

	public void setShape(Object shape) {
		this.shape = shape;
	}

	public Canvas getCanvas() {
		return canvas;
	}
	
	public void setComposite(Composite composite) {
		this.composite = composite;
	}

	public void setForeColor(Color foreColor) {
		this.foreColor = foreColor;
	}

	public void setStroke(Stroke stroke) {
		this.stroke = stroke;
	}

	public String toString() {
		return this.shape.toString();
	}
}
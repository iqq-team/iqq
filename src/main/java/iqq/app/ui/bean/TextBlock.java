package iqq.app.ui.bean;

/**
 * 文本块记录对象
 * 
 * @author liao.lh
 * 
 */
public class TextBlock {
	public enum Type {
		TEXT, ICON
	}

	private int start;// 文本块起始位置
	private int end;// 文本块结束位置

	private String text;// 文本块对应文本

	private FaceIcon icon;// 如果存在的话，该变量标识文本块对应替代图标
	
	private Type type;

	public TextBlock(int start, int end) {
		this.start = start;
		this.end = end;
	}

	public TextBlock(int start, int end, String text) {
		this.start = start;
		this.end = end;
		this.text = text;
		this.type = Type.TEXT;
	}

	public TextBlock(int start, int end, FaceIcon icon) {
		this.start = start;
		this.end = end;
		this.icon = icon;
		this.type = Type.ICON;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getStart() {
		return start;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getEnd() {
		return end;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setIcon(FaceIcon icon) {
		this.icon = icon;
	}

	public FaceIcon getIcon() {
		return icon;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}
}

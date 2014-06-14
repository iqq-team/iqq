package iqq.app.ui.frame.panel.chat.rich;

import com.alee.laf.text.WebTextPane;
import iqq.app.util.UIUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.AbstractDocument.BranchElement;
import javax.swing.text.AbstractDocument.LeafElement;
import javax.swing.text.html.HTML;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-24
 */
public class RichTextPane extends WebTextPane {
	private static final long serialVersionUID = -4363522393708478690L;
	private static final Logger LOG = Logger.getLogger(RichTextPane.class);
	/**
	 * 编辑面板的默认样式
	 */
	private static final MutableAttributeSet defaultStyle = new SimpleAttributeSet();

	public RichTextPane() {
		super();
		init();
	}

	public RichTextPane(StyledDocument doc) {
		super(doc);
		setRequestFocusEnabled(true);
		requestFocus();
		init();
	}

	private void init() {
		setEditorKit(new WrapEditorKit());
		setBorder(null);
		StyleConstants.setForeground(defaultStyle, Color.black);
		// StyleConstants.setBackground(defaultStyle, Color.white);
		Font font = getFont();
		StyleConstants.setFontFamily(defaultStyle, font.getFamily());
		StyleConstants.setFontSize(defaultStyle, font.getSize());
		StyleConstants.setUnderline(defaultStyle, false);
		StyleConstants.setStrikeThrough(defaultStyle, false);

		int style = font.getStyle();
		if (style == 0) {
			StyleConstants.setBold(defaultStyle, false);
			StyleConstants.setItalic(defaultStyle, false);
		} else if (style == 1) {
			StyleConstants.setBold(defaultStyle, true);
			StyleConstants.setItalic(defaultStyle, false);
		} else if (style == 2) {
			StyleConstants.setBold(defaultStyle, false);
			StyleConstants.setItalic(defaultStyle, true);
		} else if (style == 3) {
			StyleConstants.setBold(defaultStyle, true);
			StyleConstants.setItalic(defaultStyle, true);
		}
		setCharacterAttributes(defaultStyle, true);

		LinkController link = new LinkController();
		addMouseListener(link);
		addMouseMotionListener(link);
	}
	
	public void setDefaultCharacterAttributes() {
		setCharacterAttributes(defaultStyle, true);
	}
	
	@Override
	public void insertComponent(Component c) {
		super.insertComponent(c);
		setCharacterAttributes(defaultStyle, true);
	}
	
	@Override
	public void insertIcon(Icon g) {
		super.insertIcon(g);
		setCharacterAttributes(defaultStyle, true);
	}

	@Override
	public AttributeSet getCharacterAttributes() {
		return defaultStyle;
	}

	/**
	 * @return the defaultStyle
	 */
	public MutableAttributeSet getDefaultStyle() {
		return defaultStyle;
	}

	public void setRichItems(List<UIRichItem> richItem){
		try {
			StyledDocument styledDocument = getStyledDocument();
			styledDocument.remove(0, styledDocument.getLength());
			for (UIRichItem b : richItem) {
				b.insertTo(this);
			}
		} catch (Exception e) {
			LOG.error("set text block error!", e);
		}
	}

	public List<UIRichItem> getRichItems() {
		List<UIRichItem> richItems = new ArrayList<UIRichItem>();
		StyledDocument styledDocument = getStyledDocument();
		// 获取文档根节点
		Element rootElement = styledDocument.getDefaultRootElement();
		// 遍历获取文档中段落节点
		for (int i = 0; i < rootElement.getElementCount(); i++) {
			Element pElement = rootElement.getElement(i);
			if (pElement instanceof BranchElement) {
				// 遍历获取段落中文本节点
				for (int j = 0; j < pElement.getElementCount(); j++) {
					Element tElement = pElement.getElement(j);
					if (tElement instanceof LeafElement) {
						try {
							int tStart = tElement.getStartOffset();
							int tEnd = tElement.getEndOffset();
							AttributeSet tAttr = tElement.getAttributes();
							Object component = tAttr
									.getAttribute(StyleConstants.ComponentAttribute);
							if (component != null
									&& component instanceof UIRichComponent) {
								UIRichComponent comp = (UIRichComponent) component;
								richItems.add(comp.getRichItem());
							} else {
								String text = styledDocument.getText(tStart,
										tEnd - tStart);
								if (text.length() > 0 && !text.isEmpty()) {
									richItems.addAll(UIUtils.Bean.parseLink(text));
								}
							}
						} catch (BadLocationException e) {
							LOG.error("getRichItems error!", e);
						}
					}
				}
			}
		}
		
		// 去除最后那个回车符，我也不知道怎么来的，求解释....

		int index = richItems.size() - 1;
        if(index >= 0) {
            UIRichItem item = richItems.get(index);
            if (item instanceof UITextItem) {
                if (((UITextItem) item).getText().equals("\n")) {
                    richItems.remove(index);
                }
            }
        }
		return richItems;
	}



	class WrapEditorKit extends StyledEditorKit {
		private static final long serialVersionUID = 1L;

		ViewFactory defaultFactory = new WrapColumnFactory();

		public ViewFactory getViewFactory() {
			return defaultFactory;
		}

	}

	class WrapColumnFactory implements ViewFactory {
		public View create(Element elem) {
			String kind = elem.getName();
			if (kind != null) {
				if (kind.equals(AbstractDocument.ContentElementName)) {
					return new WrapLabelView(elem);
				} else if (kind.equals(AbstractDocument.ParagraphElementName)) {
					return new ParagraphView(elem);
				} else if (kind.equals(AbstractDocument.SectionElementName)) {
					return new BoxView(elem, View.Y_AXIS);
				} else if (kind.equals(StyleConstants.ComponentElementName)) {
					return new ComponentView(elem);
				} else if (kind.equals(StyleConstants.IconElementName)) {
					return new IconView(elem);
				}
			}

			// default to text display
			return new LabelView(elem);
		}
	}

	class WrapLabelView extends LabelView {
		public WrapLabelView(Element elem) {
			super(elem);
		}

		public float getMinimumSpan(int axis) {
			switch (axis) {
			case View.X_AXIS:
				return 0;
			case View.Y_AXIS:
				return super.getMinimumSpan(axis);
			default:
				throw new IllegalArgumentException("Invalid axis: " + axis);
			}
		}
	}
	
	/**
	 * 处理超链接点击事件
	 *
	 * 部分代码来自 http://www.daniweb.com/software-development/java/threads/331500/how-can-i-add-a-clickable-url-in-a-jtextpane
	 *
	 */
	public class LinkController extends MouseAdapter implements MouseMotionListener {

		public void mouseClicked(MouseEvent e) {
			JTextPane editor = (JTextPane) e.getSource();
			Document doc = editor.getDocument();
			Point pt = new Point(e.getX(), e.getY());
			int pos = editor.viewToModel(pt);
			if (pos >= 0) {
				if (doc instanceof DefaultStyledDocument) {
					DefaultStyledDocument hdoc = (DefaultStyledDocument) doc;
					Element el = hdoc.getCharacterElement(pos);
					AttributeSet a = el.getAttributes();
					String href = (String) a.getAttribute(HTML.Attribute.HREF);
					if (href != null) {
						Action action = (Action) a.getAttribute("action");
						if(action != null){
							action.actionPerformed(new ActionEvent(a.getAttribute("self"), 0, ""));
						}else{
							//OSUtils.Browser.open(href);
						}
						
					}
				}// if (doc instanceof DefaultStyledDocument)
			}// if (pos >= 0)
		}// public void mouseClicked(MouseEvent e)

		public void mouseMoved(MouseEvent ev) {
			Cursor handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
			Cursor defaultCursor = Cursor
					.getPredefinedCursor(Cursor.TEXT_CURSOR);
			JTextPane editor = (JTextPane) ev.getSource();
			Point pt = new Point(ev.getX(), ev.getY());
			int pos = editor.viewToModel(pt);
			if (pos >= 0) {
				Document doc = editor.getDocument();
				if (doc instanceof DefaultStyledDocument) {
					DefaultStyledDocument hdoc = (DefaultStyledDocument) doc;
					Element e = hdoc.getCharacterElement(pos);
					AttributeSet a = e.getAttributes();
					String href = (String) a.getAttribute(HTML.Attribute.HREF);
					if (href != null) {
						if (getCursor() != handCursor) {
							editor.setCursor(handCursor);
						}
					} else {
						editor.setCursor(defaultCursor);
					}
				}// (doc instanceof DefaultStyledDocument)
			}// pos >=0
			else {
				setToolTipText(null);
			}
		}// mouseMoved
	}// LinkController
}

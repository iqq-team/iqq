package iqq.app.ui.content.chat.conversation;

import iqq.app.ui.IMWindowView;
import iqq.app.ui.bean.FaceIcon;
import iqq.app.ui.content.chat.rich.UIFaceItem;
import iqq.app.util.SkinUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.BorderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-25
 */
public class FaceWindow extends IMWindowView {
	private static final Logger LOG = LoggerFactory.getLogger(FaceWindow.class);
	private static final long serialVersionUID = 651992619157145784L;
	private static final int[] FACE_ID_TABLE = {
		14, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 0, 50, 
		51, 96, 53, 54, 73, 74, 75, 76, 77, 78, 55, 56, 57, 58, 79, 80, 
		81, 82, 83, 84, 85, 86, 87, 88, 97, 98, 99, 100, 101, 102, 103, 104, 
		105, 106, 107, 108, 109, 110, 111, 112, 32, 113, 114, 115, 63, 64, 59, 33, 
		34, 116, 36, 37, 38, 91, 92, 93, 29, 117, 72, 45, 42, 39, 62, 46,
		47, 71, 95, 118, 119, 120, 121, 122, 123, 124, 27, 21, 23, 25, 26, 125, 
		126, 127, 128, 129, 130, 131, 132, 133, 134, 52, 24, 22, 20, 60, 61, 89, 
		90, 31, 94, 65, 35, 66, 67, 68, 69, 70, 15, 16, 17, 18, 19, 28, 30, 40, 41, 43, 44, 48, 49};
	
	private RichTextPane editor;
	private WebButton faceBtn;

	private WebPanel facePl;
	private GridLayout gridLayout;
	private static FaceIcon[] faceIcons; // 表情图标
	private static boolean isLoadFace = false;
	private WebLabel[] faceLbl; // 表情

	public FaceWindow(WebButton faceBtn, RichTextPane editor) {
		super();
		this.faceBtn = faceBtn;
		this.editor = editor;

		// 初始化表情...
		initFaceIcon();
		initFace();

		// 显示
		Dimension d = new Dimension(28 * 15, 28 * 7);
		super.setSize(d);
		super.setPreferredSize(d);
		super.setAlwaysOnTop(true);
		this.setVisible(true);

		this.requestFocus();
		this.addWindowFocusListener(new WindowFocusListener() {
			
			@Override
			public void windowLostFocus(WindowEvent e) {
				setVisible(false);
			}
			
			@Override
			public void windowGainedFocus(WindowEvent e) {
				
			}
		});
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				setVisible(false);
			}
		});
	}

	/**
	 * 初始始化表情图片，静态，重复使用
	 */
	private static void initFaceIcon() {
		if (!isLoadFace) {
			faceIcons = new FaceIcon[105];
			for (int i = 0; i < faceIcons.length; i++) {
				FaceIcon face = new FaceIcon(FACE_ID_TABLE[i], SkinUtils.getImageIcon("chat/face", i), "", "");
				face.setRegex(FaceIcon.REGEX_PREFIX.replace(
						FaceIcon.REGEX_ID, i+ ""));
				face.setDescription(":) " + i);
				faceIcons[i] = face;
			}
			isLoadFace = true;
		}
	}

	/**
	 * 初始化表情图片
	 */
	private void initFace() {
		gridLayout = new GridLayout(7, 15);
		facePl = new WebPanel(gridLayout);
		faceLbl = new WebLabel[105]; // 表情
		for (int i = 0; i < faceLbl.length; i++) {
			final FaceIcon faceIcon = faceIcons[i];
			faceLbl[i] = new WebLabel(faceIcon.getIcon());
			faceLbl[i].setMargin(2);
			faceLbl[i].setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225), 1));
			faceLbl[i].setToolTipText(":) " + i);
			faceLbl[i].setIconTextGap(i);
			faceLbl[i].addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == 1) {
						WebLabel faceLbl = (WebLabel) (e.getSource());
						faceLbl.setBorder(BorderFactory.createLineBorder(
								new Color(225, 225, 225), 1));
						try {
							new UIFaceItem(faceIcon.getId()).insertTo(editor);
							editor.requestFocus();
							setVisible(false);
						} catch (Exception ex) {
							LOG.warn("insert face icon failed!!!", ex);
						}
						
					}
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					((WebLabel) e.getSource()).setBorder(BorderFactory
							.createLineBorder(Color.BLACK));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					((WebLabel) e.getSource()).setBorder(BorderFactory
							.createLineBorder(new Color(225, 225, 225), 1));
				}

			});
			facePl.add(faceLbl[i]);
		}

		setContentPane(facePl);
	}
	
	@Override
	public void setVisible(boolean b) {
		determineAndSetLocation(); // 设置到表情按钮的位置显示
		super.setVisible(b);
	}
	
	private void determineAndSetLocation() {
		Point location = faceBtn.getLocationOnScreen();/* 控件相对于屏幕的位置 */
		setBounds(location.x - getPreferredSize().width / 4, location.y
				- getPreferredSize().height, getPreferredSize().width,
				getPreferredSize().height);
	}
	
	public static FaceIcon getFaceIconById(int id){
		initFaceIcon();
		for(int i=0; i<faceIcons.length; i++){
			if(faceIcons[i].getId() == id){
				return faceIcons[i];
			}
		}
		return null;
	}
}

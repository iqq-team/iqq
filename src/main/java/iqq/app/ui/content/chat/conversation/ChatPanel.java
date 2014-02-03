package iqq.app.ui.content.chat.conversation;

import iqq.app.bean.UIDiscuz;
import iqq.app.bean.UIGroup;
import iqq.app.bean.UIMsg;
import iqq.app.bean.UINamedObject;
import iqq.app.bean.UIUser;
import iqq.app.core.IMContext;
import iqq.app.core.IMException;
import iqq.app.core.IMService;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventType;
import iqq.app.service.IMEventService;
import iqq.app.service.IMPropService;
import iqq.app.service.IMSkinService.Type;
import iqq.app.service.IMTaskService;
import iqq.app.service.IMTimerService;
import iqq.app.ui.BackgroundPanel;
import iqq.app.ui.IMFrameView;
import iqq.app.ui.action.IMActionHandler;
import iqq.app.ui.content.chat.IChat;
import iqq.app.ui.content.chat.picloader.PicLoader;
import iqq.app.ui.content.chat.picloader.PicLoaderFactory;
import iqq.app.ui.content.chat.rich.UILoaderPicItem;
import iqq.app.ui.content.chat.rich.UITextItem;
import iqq.app.ui.widget.screencapture.ScreenCapture;
import iqq.app.ui.widget.screencapture.ScreenCaptureListener;
import iqq.app.ui.widget.screencapture.ScreenEvent;
import iqq.app.util.I18NUtil;
import iqq.app.util.IMImageUtil;
import iqq.app.util.SkinUtils;
import iqq.app.util.UIUtils;
import iqq.im.bean.QQAccount;
import iqq.im.bean.QQDiscuz;
import iqq.im.bean.QQGroup;
import iqq.im.bean.QQUser;
import iqq.im.bean.content.FontItem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JScrollBar;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import com.alee.extended.image.WebDecoratedImage;
import com.alee.extended.panel.GroupPanel;
import com.alee.laf.StyleConstants;
import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.combobox.WebComboBoxCellRenderer;
import com.alee.laf.combobox.WebComboBoxUI;
import com.alee.laf.filechooser.WebFileChooser;
import com.alee.laf.label.WebLabel;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.toolbar.ToolbarStyle;
import com.alee.laf.toolbar.WebToolBar;
import com.alee.utils.FileUtils;
import com.alee.utils.ImageUtils;

/**
 * 个人对话聊天面板
 * 
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-21
 */
public class ChatPanel extends BackgroundPanel implements IChat {
	private static final Logger LOG = Logger.getLogger(ChatPanel.class);
	private static final long serialVersionUID = -1361072898693239839L;
	private static final ImageIcon nullIcon = SkinUtils.getImageIcon("transparent");
	private static final Set<Object> appendFrom = new HashSet<Object>();
	private static Map<Object, ChatPanel> chatPanels = new HashMap<Object, ChatPanel>();
	private boolean selected;

	private IMFrameView view;
	private UINamedObject namedObject;
	private QQAccount self;

	private WebPanel contentPl;
	private WebPanel inputPl;
	private WebDecoratedImage buddyFace;
	private WebLabel inputLabel;
	private long lastSendInput;
	// 消息显示面板
	private MsgListPanel msgPanel;
	private WebScrollPane msgScroll;
	private boolean isAdd;

	// 提示信息
	private WebPanel alertPl;
	private WebLabel alertLbl;
	private WebButton alertBtn;

	// 输入面板
	private RichTextPane contentInput;
	private FaceWindow fw;
	private WebFileChooser imageChooser = null;
	private ScreenCapture screenCapture;

	// 头像部功能工具条
	private WebToolBar headerToolbar;
	private WebFileChooser fileChooser = null;

	private WebComboBox receiveMsgCbx;

	private ClearInputtingTask clearInputTask;
	private ClearAlertTask clearAlertTask;
	private CompressPicTask compressPicTask;

	/**
	 * @param view
	 */
	public ChatPanel(final IMFrameView view, final UINamedObject namedObject, final QQAccount self) {
		super(view);
		this.view = view;
		this.namedObject = namedObject;
		this.self = self;
		this.clearInputTask = new ClearInputtingTask();
		this.clearAlertTask = new ClearAlertTask();
		this.compressPicTask = new CompressPicTask();
		this.selected = true;
		
		setBorder(null);
		setShadeWidth(0);
		setRound(0);
		addCentent();
		
		chatPanels.put(namedObject.getEntity(), this);
		screenCapture = ScreenCapture.getInstance(view);
		screenCapture.addScreenListener(
				new ScreenCaptureListener() {
	
					@Override
					public void clip(ScreenEvent evt) {
						if(isSelected()) {
							try {
								BufferedImage img = ImageUtils
										.getBufferedImage((Image) evt
												.getTarget());
								File tmp = File.createTempFile("iqqclip",
										".jpg");
								ImageIO.write(img, "jpg", tmp);
								PicLoader picLoader = PicLoaderFactory.createLoader(
														tmp, namedObject.getEntity(), self);
								UILoaderPicItem picItem = new UILoaderPicItem(picLoader);
								picItem.setContext(view.getContext());
								picItem.insertTo(contentInput);
								tmp.deleteOnExit();
							} catch (Exception e) {
								LOG.warn("clip error!", e);
							}
						}
					}
	
					@Override
					public void cancel(ScreenEvent evt) {
						getInputText().requestFocus();
					}
				});
		
		view.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				if (fw != null) {
					fw.dispose();
				}
			}

			@Override
			public void windowLostFocus(WindowEvent e) {
				super.windowLostFocus(e);
				if (fw != null) {
					fw.setVisible(false);
				}
			}
		});
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				if (fw != null) {
					fw.setVisible(false);
				}
			}
		});
		contentInput.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				super.focusGained(e);
				if (fw != null && fw.isVisible()) {
					fw.setVisible(false);
				}
			}
		});

		contentInput.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				Object entity = ChatPanel.this.namedObject.getEntity();
				if (System.currentTimeMillis() - lastSendInput > 7000
						&& entity instanceof QQUser && queryIsOnline()) {
					IMContext context = ChatPanel.this.view.getContext();
					IMEventService events = context
							.getSerivce(IMService.Type.EVENT);
					IMEvent event = new IMEvent(IMEventType.SEND_INPUT_REQUEST,
							entity);
					events.broadcast(event);
					lastSendInput = System.currentTimeMillis();
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_V && e.getModifiers() == KeyEvent.CTRL_MASK) {
					past(); // 粘贴
				}
			}
		});
	}
	
	protected void past() {
		System.out.println("past : - " + namedObject);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		if(clipboard.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
			try {
				Image image = (Image) clipboard.getData(DataFlavor.imageFlavor);
				File file = File.createTempFile("past", ".jpg");
				ImageIO.write(ImageUtils.copy(image), "jpg", file);
				PicLoader picLoader = PicLoaderFactory.createLoader(
										file, namedObject.getEntity(), self);
				UILoaderPicItem picItem = new UILoaderPicItem(picLoader);
				picItem.setContext(view.getContext());
				picItem.insertTo(contentInput);
			} catch (UnsupportedFlavorException e) {
				LOG.warn("Clipboard past image error!", e);
			} catch (Exception e) {
				LOG.warn("Clipboard past image error!", e);
			}
		}
	}

	/**
	 * 添加到主面板中显示
	 */
	private void addCentent() {
		add(createContentPl(), BorderLayout.CENTER);
		add(createInputPl(), BorderLayout.PAGE_END);
	}

	private Component createHeaderToolBar() {
		headerToolbar = new WebToolBar();
		headerToolbar.setMargin(8, 5, 5, 8);
		headerToolbar.setFloatable(false);
		headerToolbar.setToolbarStyle(ToolbarStyle.attached);
		headerToolbar
				.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		headerToolbar.setPainter(SkinUtils.getPainter(Type.NPICON,
				"transparent"));

		WebButton fileTransfere = WebButton.createIconWebButton(nullIcon,
				StyleConstants.smallRound, true);
		WebButton setting = WebButton.createIconWebButton(nullIcon,
				StyleConstants.smallRound, true);
		WebButton showMembers = WebButton.createIconWebButton(nullIcon,
				StyleConstants.smallRound, true);

		// Actions
		fileTransfere.setAction(view.getActionService().getActionMap(this)
				.get("openFileChooser"));
		setting.setAction(view.getActionService().getActionMap(this)
				.get("openSettigWindow"));
		showMembers.setAction(view.getActionService().getActionMap(this)
				.get("openMembersWindow"));

		// button property
		fileTransfere.setIcon(SkinUtils
				.getImageIcon("chat/headerbar/fileTransfere"));
		setting.setIcon(SkinUtils.getImageIcon("chat/headerbar/setting"));
		showMembers.setIcon(SkinUtils
				.getImageIcon("chat/headerbar/showMembers"));

		// tip
		fileTransfere.setToolTipText(I18NUtil.getMessage("chat.fileTransfer"));
		setting.setToolTipText(I18NUtil.getMessage("chat.setting"));
		showMembers.setToolTipText(I18NUtil.getMessage("chat.showMembers"));

		//暂时隐藏掉文件传输和设置按钮
		//if (namedObject instanceof UIUser) {
		//	headerToolbar.add(fileTransfere);
		//}
		//headerToolbar.add(setting);
		// 群和讨论组才有成员
		if (!(namedObject instanceof UIUser)) {
			headerToolbar.add(showMembers);
		}

		return headerToolbar;
	}

	/**
	 * @return 创建聊天内容面板
	 */
	private Component createContentPl() {
		contentPl = new WebPanel();

		// 头部：头像、名称等
		WebPanel topPl = new WebPanel();
		topPl.setPainter(SkinUtils.getPainter(Type.NPICON, "chatNavBg"));
		topPl.setMargin(2, 10, 0, 20);

		buddyFace = new WebDecoratedImage(
				namedObject.getIcon());
		WebLabel titleLbl = new WebLabel(namedObject.getName());
		WebLabel subtitle = new WebLabel(namedObject.getExtra());
		subtitle.setToolTipText(namedObject.getSubname());
		subtitle.setPreferredSize(new Dimension(view.getWidth() / 2, 20));

		inputLabel = new WebLabel("");

		buddyFace.setRound(3);
		buddyFace.setShadeWidth(1);

		Insets margin = new Insets(5, 8, 2, 5);
		titleLbl.setMargin(margin);
		subtitle.setMargin(margin);

		topPl.add(buddyFace, BorderLayout.LINE_START);
		topPl.add(new GroupPanel(false, new GroupPanel(titleLbl, inputLabel),
				subtitle), BorderLayout.CENTER);
		topPl.add(createHeaderToolBar(), BorderLayout.LINE_END);

		// 消息面板处理
		msgPanel = new MsgListPanel(view);
		msgPanel.setShadeWidth(0);
		msgPanel.setRound(0);
		msgPanel.setBorder(null);
		msgScroll = new WebScrollPane(msgPanel) {
			private static final long serialVersionUID = 1L;

			{
				setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				setBorder(null);
				setMargin(0);
				setShadeWidth(0);
				setRound(0);
				setDrawBorder(false);
			}
		};

		// 获取JScrollPane中的纵向JScrollBar
		JScrollBar bar = msgScroll.getVerticalScrollBar();
		bar.addAdjustmentListener(new AdjustmentListener() {

			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if (isAdd) {
					e.getAdjustable().setValue(e.getAdjustable().getMaximum());
					isAdd = false;
				}
			}
		});

		// 提示信息，center: icon-text right: close button
		alertPl = new WebPanel();
		alertLbl = new WebLabel(IMImageUtil.getScaledInstance(
				SkinUtils.getImageIcon("alert"), 16, 16));
		alertLbl.setForeground(Color.WHITE);
		alertLbl.setIconTextGap(10);
		alertLbl.setMargin(2, 10, 2, 5);
		alertBtn = new WebButton(SkinUtils.getImageIcon("removeFocused"));
		alertBtn.setPainter(SkinUtils.getPainter(Type.NPICON, "transparent"));
		alertBtn.setMargin(5, 5, 5, 10);
		alertBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				alertPl.setVisible(false);
			}
		});
		alertBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				alertBtn.setIcon(SkinUtils.getImageIcon("removeActive"));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				alertBtn.setIcon(SkinUtils.getImageIcon("removeFocused"));
			}
		});

		alertPl.setPainter(SkinUtils.getPainter(Type.NPICON, "chat/alertBg"));
		alertPl.add(alertLbl, BorderLayout.CENTER);
		alertPl.add(alertBtn, BorderLayout.LINE_END);
		alertPl.setVisible(false);

		// 添加到聊天内容面板中
		contentPl.add(topPl, BorderLayout.PAGE_START);
		contentPl.add(msgScroll, BorderLayout.CENTER);
		contentPl.add(alertPl, BorderLayout.PAGE_END);
		contentPl.setOpaque(true);
		contentPl.setBackground(Color.WHITE);
		return contentPl;
	}

	/**
	 * @return 创建聊天输入面板
	 */
	private Component createInputPl() {
		inputPl = new WebPanel();
		contentInput = new RichTextPane();
		contentInput.setOpaque(true);
		contentInput.setBackground(this.getBackground());

		// 输入小工具栏
		WebToolBar toolbar = new WebToolBar(WebToolBar.HORIZONTAL);
		toolbar.setFloatable(false);
		toolbar.setToolbarStyle(ToolbarStyle.attached);
		toolbar.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		toolbar.setPainter(SkinUtils.getPainter(Type.NPICON, "chatNavBg"));

		receiveMsgCbx = new WebComboBox();
		WebComboBoxUI cbxUi = new WebComboBoxUI() {
			@Override
			public void paintCurrentValueBackground(Graphics g,
					Rectangle bounds, boolean hasFocus) {
				// super.paintCurrentValueBackground(g, bounds, hasFocus);
			}
		};
		receiveMsgCbx.setUI(cbxUi);
		receiveMsgCbx.setDrawBorder(false);
		receiveMsgCbx.setOpaque(false);
		receiveMsgCbx.setRenderer(new WebComboBoxCellRenderer(receiveMsgCbx));
		receiveMsgCbx.addItem(SkinUtils.getImageIcon("chat/toolbar/noMessage"));
		receiveMsgCbx
				.addItem(SkinUtils.getImageIcon("chat/toolbar/offMessage"));

		WebButton text = WebButton.createIconWebButton(nullIcon,
				StyleConstants.smallRound, true);
		WebButton emoticon = WebButton.createIconWebButton(nullIcon,
				StyleConstants.smallRound, true);
		WebButton screenCapture = WebButton.createIconWebButton(nullIcon,
				StyleConstants.smallRound, true);
		WebButton shake = WebButton.createIconWebButton(nullIcon,
				StyleConstants.smallRound, true);
		WebButton pictures = WebButton.createIconWebButton(nullIcon,
				StyleConstants.smallRound, true);
		WebButton history = WebButton.createIconWebButton(nullIcon,
				StyleConstants.smallRound, true);
		WebButton send = WebButton.createIconWebButton(nullIcon,
				StyleConstants.smallRound, true);

		// Action
		text.setAction(view.getActionService().getActionMap(this)
				.get("openFontChooser"));
		emoticon.setAction(view.getActionService().getActionMap(this)
				.get("openEmoticonChooser"));
		screenCapture.setAction(view.getActionService().getActionMap(this)
				.get("openScreenCapture"));
		shake.setAction(view.getActionService().getActionMap(this)
				.get("shakeWindow"));
		pictures.setAction(view.getActionService().getActionMap(this)
				.get("openPicturesChooser"));
		history.setAction(view.getActionService().getActionMap(this)
				.get("openHistory"));
		send.setAction(view.getActionService().getActionMap(this)
				.get("doSendMsg")); // doSendMsg
		receiveMsgCbx.setAction(view.getActionService().getActionMap(this)
				.get("receiveMsg"));

		// button property
		text.setIcon(SkinUtils.getImageIcon("chat/toolbar/text"));
		emoticon.setIcon(SkinUtils.getImageIcon("chat/toolbar/emoticon"));
		screenCapture.setIcon(SkinUtils
				.getImageIcon("chat/toolbar/screenCapture"));
		shake.setIcon(SkinUtils.getImageIcon("chat/toolbar/shake"));
		pictures.setIcon(SkinUtils.getImageIcon("chat/toolbar/pictures"));
		history.setIcon(SkinUtils.getImageIcon("chat/toolbar/history"));
		send.setIcon(SkinUtils.getImageIcon("chat/toolbar/send"));

		IMContext context = view.getContext();
		IMPropService propService = context.getSerivce(IMService.Type.PROP);
		if(propService.getInt("sentMsgKey") == 0) {
			KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
			contentInput.getInputMap().put(enter, "none");
			contentInput.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER
							&& e.getModifiers() != KeyEvent.CTRL_MASK) {
						doSendMsg();
					} else if(e.getKeyCode() == KeyEvent.VK_ENTER
							&& e.getModifiers() == KeyEvent.CTRL_MASK) {
						contentInput.setText(contentInput.getText() + "\n");
					}
				}
			});
		} else {
			contentInput.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER
							&& e.getModifiers() == KeyEvent.CTRL_MASK) {
						doSendMsg();
					}
				}
			});
		}

		// set ToolTip
		text.setToolTipText(I18NUtil.getMessage("chat.text"));
		emoticon.setToolTipText(I18NUtil.getMessage("chat.emoticon"));
		screenCapture.setToolTipText(I18NUtil.getMessage("chat.screenCapture"));
		shake.setToolTipText(I18NUtil.getMessage("chat.shake"));
		pictures.setToolTipText(I18NUtil.getMessage("chat.pictures"));
		history.setToolTipText(I18NUtil.getMessage("chat.history"));
		send.setToolTipText(I18NUtil.getMessage("chat.send"));

		// add to Toolbar
		toolbar.addSpacing(10);
		// toolbar.add(text);
		toolbar.add(emoticon);
		toolbar.add(screenCapture);
		if (namedObject instanceof UIUser) {
			toolbar.add(shake);
		}
		toolbar.add(pictures);
		toolbar.add(history);
		if (namedObject instanceof UIGroup) {
			QQGroup group = (QQGroup) namedObject.getEntity();
			if (group.getMask() == 1 || group.getMask() == 2) {
				receiveMsgCbx.setSelectedIndex(1);
			}
			toolbar.add(receiveMsgCbx);
		}
		toolbar.addToEnd(send);
		toolbar.addSpacingToEnd(30);

		WebScrollPane textPaneScroll = new WebScrollPane(contentInput) {
			private static final long serialVersionUID = 1L;

			{
				setPreferredSize(new Dimension(ChatPanel.this.getWidth(), 120));
				setOpaque(false);
				setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				setBorder(null);
				setMargin(8, 8, 8, 0);
				setDrawBorder(false);
			}
		};

		inputPl.add(toolbar, BorderLayout.PAGE_START);
		inputPl.add(textPaneScroll, BorderLayout.CENTER);
		return inputPl;
	}

	public void clearInput() {
		getInputText().setText("");
		getInputText().setDefaultCharacterAttributes();
		getInputText().requestFocus();
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(namedObject.getIcon().getScaledInstance(32, 32,
				100));
	}

	@Override
	public String getTitle() {
		return namedObject.getName();
	}

	@Override
	public Component getViewContent() {
		return this;
	}

	@Override
	public Object getEntity() {
		return namedObject.getEntity();
	}

	@Override
	public RichTextPane getInputText() {
		return contentInput;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public void showMsg(UIMsg msg) {
		isAdd = true;
		msgPanel.add(new UIMsgPane(msg, self));
		msgPanel.revalidate();
	}

	private boolean isNotEmptyMsg() {
		if (contentInput.getRichItems().size() > 0) {
			alertPl.setVisible(false);
			return true;
		}
		showAlertMsg(I18NUtil.getMessage("sendMsgEmpty"));
		clearInput();
		return false;
	}

	private UIMsg packUIMsg() {
		UIMsg msg = new UIMsg();
		msg.setMsgId(UIMsg.genMsgId());
		msg.setDate(new Date());
		msg.setContents(contentInput.getRichItems());
		msg.setDirection(UIMsg.Direction.SEND);
		msg.setSender(self);
		msg.setOwner(namedObject.getEntity());
		msg.setState(UIMsg.State.PENDING);
		msg.setCategory(UIMsg.Category.CHAT);
		msg.setDialogType(UIMsgUtils.getDialogType(namedObject.getEntity()));

		FontItem font = new FontItem();
		font.setName("宋体");
		font.setSize(10);
		font.setColor(0);
		font.setBold(false);
		font.setItalic(false);
		font.setUnderline(false);
		msg.setFont(font);
		return msg;
	}

	private void sendMsg() throws Exception {
		if (queryIsOnline()) {
			//先显示再发送
			UIMsg uiMsg = packUIMsg();
			if(!appendFrom.contains(uiMsg.getOwner())) {
				uiMsg.getContents().add(new UITextItem(UIUtils.Bean.getSentForm()));
				appendFrom.add(uiMsg.getOwner());
			}
			showMsg(uiMsg);
			clearInput(); // 发送消息后，清空输入框
			
			//请求发送
			IMEventService events = view.getContext().getSerivce(
					IMService.Type.EVENT);
			IMEvent event = new IMEvent(IMEventType.SEND_CHAT_MSG, uiMsg);
			events.broadcast(event);
		} else {
			showAlertMsg(I18NUtil.getMessage("chat.selfOffline"));
		}
	}

	@IMActionHandler
	public void doSendMsg() {
		try {
			if (isNotEmptyMsg()) {
				sendMsg();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@IMActionHandler
	public void openFontChooser(ActionEvent e) {
		System.out.println("openFontChooser");
	}

	@IMActionHandler
	public void openEmoticonChooser(ActionEvent e) {
		if (fw != null) {
			if (!fw.isVisible()) {
				fw.setVisible(true);
			} else {
				fw.setVisible(false);
			}
		} else {
			fw = new FaceWindow((WebButton) e.getSource(), contentInput);
		}
	}

	@IMActionHandler
	public void openPicturesChooser(ActionEvent e) {
		IMTaskService tasks = view.getContext().getSerivce(
				IMService.Type.TASK);
		tasks.submit(compressPicTask);
	}

	@IMActionHandler
	public void openScreenCapture(ActionEvent e) {
		screenCapture.showScreenCapture();
	}

	@IMActionHandler
	public void shakeWindow(ActionEvent e) {
		if (namedObject instanceof UIUser) {
			if (queryIsOnline()) {
				QQUser user = (QQUser) namedObject.getEntity();
				IMEventService events = view.getContext().getSerivce(
						IMService.Type.EVENT);
				IMEvent shakeEvent = new IMEvent(
						IMEventType.SEND_SHAKE_REQUEST, user);
				events.broadcast(shakeEvent);
			} else {
				showAlertMsg(I18NUtil.getMessage("chat.selfOffline"));
			}
		}
	}

	@IMActionHandler
	public void openSettigWindow(ActionEvent e) {
		System.out.println("openSettigWindow");
	}

	@IMActionHandler
	public void openFileChooser(ActionEvent e) {
		if (fileChooser == null) {
			fileChooser = new WebFileChooser();
			fileChooser.setMultiSelectionEnabled (false);
		}
		fileChooser.setVisible(true);
		/*if (fileChooser.getResult() == StyleConstants.OK_OPTION) {
			File file = fileChooser.getSelectedFile();
			WebOptionPane.showMessageDialog(view,
					FileUtils.getDisplayFileName(file));
		}*/
		if (fileChooser.showOpenDialog(view) == WebFileChooser.APPROVE_OPTION )
        {
			File file = fileChooser.getSelectedFile();
			WebOptionPane.showMessageDialog(view,
					FileUtils.getDisplayFileName(file));
        }
	}

	@IMActionHandler
	public void openHistory(ActionEvent e) {
		IMEventService events = view.getContext().getSerivce(
				IMService.Type.EVENT);
		IMEvent event = new IMEvent(IMEventType.SHOW_MSG_HISTORY_WINDOW,
				namedObject);
		event.putData("view", view);
		events.broadcast(event);
	}

	@IMActionHandler
	public void receiveMsg(ActionEvent e) {
		if (namedObject instanceof UIGroup || namedObject instanceof UIDiscuz) {
			boolean isUpdate = false;
			int index = receiveMsgCbx.getSelectedIndex();
			if (index == 0) {
				if (namedObject instanceof UIGroup) {
					QQGroup group = (QQGroup) namedObject.getEntity();
					if(group.getMask() == 0) {
						return ;
					} else {
						group.setMask(0);
						isUpdate = true;
					}
				}
			} else {
				if (namedObject instanceof UIGroup) {
					QQGroup group = (QQGroup) namedObject.getEntity();
					if(group.getMask() == 2) {
						return ;
					} else {
						group.setMask(2);
						isUpdate = true;
					}
				}
			}
			if(isUpdate) {
				IMEventService events = view.getContext().getSerivce(
						IMService.Type.EVENT);
				IMEvent event = new IMEvent(IMEventType.GROUP_MSG_FILTER_REQUEST,
						namedObject);
				events.broadcast(event);
			}
		}
	}

	@IMActionHandler
	public void openMembersWindow(ActionEvent e) {
		IMEventService events = view.getContext().getSerivce(
				IMService.Type.EVENT);
		IMEvent event = new IMEvent(IMEventType.SHOW_MEMBERS_WINDOW,
				namedObject);
		event.putData("view", view);
		events.broadcast(event);
	}

	@Override
	public void updateUser(QQUser user) {
		msgPanel.updateUser(user);
		if(user == getEntity()){
			buddyFace.setImage(namedObject.getIcon());
		}
	}

	@Override
	public void updateMsg(UIMsg msg) {
		msgPanel.updateMsg(msg);
	}

	@Override
	public List<UIMsg> getMsgList() {
		return msgPanel.getMsgList();
	}

	@Override
	public void showInput() {
		IMTimerService timers = view.getContext().getSerivce(
				IMService.Type.TIMER);
		timers.killTimer(clearInputTask);
		inputLabel.setText(I18NUtil.getMessage("chat.userInputting"));
		timers.setTimeout(clearInputTask, 8000);
	}

	private void showAlertMsg(String msg) {
		alertLbl.setText(msg);
		alertPl.setVisible(true);
		IMTimerService timers = view.getContext().getSerivce(
				IMService.Type.TIMER);
		timers.killTimer(clearAlertTask);
		timers.setTimeout(clearAlertTask, 3000);
	}

	private boolean queryIsOnline() {
		try {
			IMEventService events = view.getContext().getSerivce(
					IMService.Type.EVENT);
			IMEvent event = new IMEvent(IMEventType.QUERY_SELF_IS_ONLINE);
			events.query(event);
			return (Boolean) event.getResult();
		} catch (IMException e) {
			LOG.warn("query is Online!!", e);
			return false;
		}
	}

	private class ClearInputtingTask implements Runnable {
		@Override
		public void run() {
			inputLabel.setText("");
		}
	}

	private class ClearAlertTask implements Runnable {
		@Override
		public void run() {
			alertPl.setVisible(false);
		}
	}
	
	private class CompressPicTask implements Runnable {

		@Override
		public void run() {
			Object obj = namedObject.getEntity();
			int maxLength = 1000000;
			if (obj instanceof QQGroup || obj instanceof QQDiscuz) {
				maxLength = 500000;
			}

			if (imageChooser == null) {
				imageChooser = IMImageUtil.getWebImgageChooser(view);
			}
			imageChooser.setVisible(true);

			if (imageChooser.showOpenDialog (view) == WebFileChooser.APPROVE_OPTION) {
				File targetFile = imageChooser.getSelectedFile();
				File destFile = null;
				// 判断是否为图片文件... TODO ..
				// 判断图片大小 length return a bytes. 1024 * 1024 = 1M
				float quality = 1.0f;
				if (targetFile.length() > maxLength) {
					boolean isCompress = true;
					int compressCount = 0;
					Image img = null;
					try {
						img = ImageIO.read(targetFile);
					} catch (IOException e2) {
						e2.printStackTrace();
					}
					int width = img.getWidth(null);
					int height = img.getHeight(null);
					try {
						destFile = File.createTempFile("compress", ".jpeg");
					} catch (IOException e2) {
						LOG.error("create temp file error!" + e2);
					}
					while (isCompress) {
						width = (int)(width * 0.8);
						height = (int)(height * 0.8);
						quality = quality - 0.2f;
						compressCount++;
						try {
							IMImageUtil.compressImage(img, quality, width, height,
									false, destFile, "jpeg");
						} catch (IOException e1) {
							LOG.error(
									"Compress image error! "
											+ destFile.getAbsolutePath(), e1);
						}
						if (destFile.length() < maxLength) {
							isCompress = false;
						} else if (compressCount > 5) {
							isCompress = false;
							showAlertMsg(I18NUtil
									.getMessage("chat.picLengthNotSupported"));
						}
						System.out.println("Compress Image, Length : "
								+ destFile.length());
					}
				} else {
					destFile = targetFile;
				}
				try {
					PicLoader picLoader = PicLoaderFactory.createLoader(
											destFile, namedObject.getEntity(), self);
					UILoaderPicItem picItem = new UILoaderPicItem(picLoader);
					picItem.setContext(view.getContext());
					picItem.insertTo(contentInput);
				} catch (Exception ex) {
					LOG.warn("insert pic error!", ex);
				}
			}
		}
	}
}

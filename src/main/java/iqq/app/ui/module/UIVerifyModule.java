package iqq.app.ui.module;

import iqq.app.core.IMContext;
import iqq.app.core.IMException;
import iqq.app.core.IMService;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventHandler;
import iqq.app.event.IMEventHandlerProxy;
import iqq.app.event.IMEventType;
import iqq.app.service.IMEventService;
import iqq.app.ui.BackgroundPanel;
import iqq.app.ui.IMDialogView;
import iqq.app.ui.action.IMActionHandler;
import iqq.app.ui.widget.IMTitleComponent;
import iqq.app.util.I18NUtil;
import iqq.app.util.SkinUtils;
import iqq.im.event.QQNotifyEvent;
import iqq.im.event.QQNotifyEventArgs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alee.extended.image.WebImage;
import com.alee.extended.label.WebLinkLabel;
import com.alee.extended.panel.CenterPanel;
import com.alee.extended.panel.EmptyPanel;
import com.alee.extended.panel.GroupPanel;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.text.WebTextField;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-18
 */
public class UIVerifyModule extends IMDialogView {
	private static final Logger LOG = LoggerFactory.getLogger(UIVerifyModule.class);

	private IMEventService eventHub;
	private QQNotifyEventArgs.ImageVerify verify;
	private VerifyPanel verifyView;
	private IMEvent verifyEvent;

	@Override
	public void init(IMContext context) throws IMException {
		super.init(context);

		// 初始化窗口
		initFrame();

		// 初始化显示内容，登录面板
		verifyView = new VerifyPanel(this);
		setContentPanel(verifyView);

		// 注册感兴趣的事件
		IMEventHandlerProxy.register(this, context);
		// 广播服务
		eventHub = getContext().getSerivce(IMService.Type.EVENT);
	}

	@Override
	public void destroy() throws IMException {
		super.destroy();
		IMEventHandlerProxy.unregister(this);
	}

	@IMEventHandler(IMEventType.IMAGE_VERIFY_NEED)
	protected void processIMLoginVerify(IMEvent event) {
		LOG.info("Request need verification code......");
		QQNotifyEvent ne = (QQNotifyEvent) event.getTarget();
		verify = (QQNotifyEventArgs.ImageVerify) ne.getTarget();
		verifyView.changeVerifyImage();
		//需要把这个事件保存起来，提交验证的时候要把这个发起事件作为relatedEvent传递回去
		verifyEvent = event;	
		show();
	}
	
	@IMEventHandler(IMEventType.FRESH_VERIFY_SUCCESS)
	protected void processIMFreshVerifySuccess(IMEvent event){
		verify.image = (BufferedImage) event.getTarget();
		verifyView.changeVerifyImage();
	}
	
	@IMEventHandler(IMEventType.FRESH_VERIFY_FAILED)
	protected void processIMFreshVerifyFailed(IMEvent event){
		int ret = WebOptionPane.showConfirmDialog ( verifyView, I18NUtil.getMessage("verify.freshVerifyFailed"), 
				 "Confirm", WebOptionPane.YES_NO_OPTION, WebOptionPane.QUESTION_MESSAGE );
		if(ret == WebOptionPane.YES_OPTION){
			verifyView.processFresh();
		}else{
			verifyView.processCancel();
		}
	}

	/**
	 * 初始化窗口
	 */
	private void initFrame() {
		// 设置标题
		setTitle(I18NUtil.getMessage("verify"));
		setIconImage(SkinUtils.getImageIcon("appLogo").getImage());

		// 设置程序宽度 和高度
		setSize(350, 250);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setAlwaysOnTop(true);
	}

	public class VerifyPanel extends BackgroundPanel {
		private static final long serialVersionUID = -4454221705551179784L;

		private Window view;
		private WebImage verifyImage;
		private WebLinkLabel veriryReset;
		private WebLabel veriryLbl;
		private WebTextField verifyFld;

		private WebButton confirmBtn;
		private WebButton cancelBtn;

		public VerifyPanel(Window view) {
			super(view);
			this.view = view;
			
			view.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					super.windowClosing(e);
					processCancel();
				}
			});

			addContent();
		}

		private void addContent() {
			// 上面是标题栏，下面为内容显示
			IMTitleComponent titleComp = new IMTitleComponent(view);
			titleComp.setShowMinimizeButton(false);
			titleComp.setShowMaximizeButton(false);
			add(titleComp, BorderLayout.PAGE_START);
			add(new CenterPanel(createContent()), BorderLayout.CENTER);
		}

		/**
		 * @return
		 */
		private WebPanel createContent() {
			verifyImage = new WebImage();
			veriryReset = new WebLinkLabel(I18NUtil.getMessage("veriryReset"));
			veriryLbl = new WebLabel(I18NUtil.getMessage("verify"));
			veriryLbl.setMargin(0, 0, 0, 10);
			verifyFld = new WebTextField();
			confirmBtn = new WebButton();
			cancelBtn = new WebButton();
			
			verifyFld.setAction(getActionService().getActionMap(VerifyPanel.class, VerifyPanel.this).get("processVerify"));
			confirmBtn.setAction(getActionService().getActionMap(VerifyPanel.class, VerifyPanel.this).get("processVerify"));
			confirmBtn.setText(I18NUtil.getMessage("confirm"));
			cancelBtn.setAction(getActionService().getActionMap(VerifyPanel.class, VerifyPanel.this).get("processCancel"));
			cancelBtn.setText(I18NUtil.getMessage("cancel"));
			
			confirmBtn.setPreferredSize(new Dimension(100, 30));
			cancelBtn.setPreferredSize(new Dimension(100, 30));

			GroupPanel imgPanel = new GroupPanel(true, verifyImage,
					new EmptyPanel(20, 10), veriryReset);
			GroupPanel btnPanel = new GroupPanel(true, confirmBtn,
					new EmptyPanel(20, 10), cancelBtn);

			WebPanel txtPanel = new WebPanel(new BorderLayout());
			txtPanel.add(veriryLbl, BorderLayout.LINE_START);
			txtPanel.add(verifyFld, BorderLayout.CENTER);
			txtPanel.setOpaque(false);

			Insets insets = new Insets(5, 10, 5, 10);
			imgPanel.setMargin(insets);
			txtPanel.setMargin(insets);
			btnPanel.setMargin(insets);

			// 看不清楚，直接提交，重新显示验证码
			veriryReset.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					processFresh();
				}
			});
			
			return new GroupPanel(0, false, imgPanel, txtPanel,
					new CenterPanel(btnPanel));
		}

		public void changeVerifyImage() {
			verifyImage.setImage(verify.image);
		}
		
		@IMActionHandler
		public void processFresh(){
			IMEvent event = new IMEvent(IMEventType.FRESH_VERIFY_REQUEST);
			event.setRelatedEvent(verifyEvent);
			eventHub.broadcast(event);
			verifyFld.clear();
		}
		
		@IMActionHandler
		public void processVerify() {
			LOG.info("process erify");
			IMEvent event = new IMEvent(IMEventType.SUBMIT_VERIFY_REQUEST);
			event.setRelatedEvent(verifyEvent);
			event.putData("code", verifyFld.getText().trim().toUpperCase());
			eventHub.broadcast(event);
			verifyFld.clear();
			dispose();
		}
		
		@IMActionHandler
		public void processCancel() {
			LOG.info("process Cancel");
			IMEvent event = new IMEvent(IMEventType.VERIFY_CANCEL);
			event.setRelatedEvent(verifyEvent);
			eventHub.broadcast(event);
			dispose();
		}
	}
}

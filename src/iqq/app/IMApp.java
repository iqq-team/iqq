package iqq.app;

import iqq.app.core.IMContext;
import iqq.app.core.IMException;
import iqq.app.core.IMModule;
import iqq.app.core.IMService;
import iqq.app.core.IMService.Type;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventHandler;
import iqq.app.event.IMEventHandlerProxy;
import iqq.app.event.IMEventType;
import iqq.app.module.QQAccountModule;
import iqq.app.module.QQCacheModule;
import iqq.app.module.QQLogicModule;
import iqq.app.module.QQMsgHistoryModule;
import iqq.app.module.QQMsgManagerModule;
import iqq.app.service.IMEventService;
import iqq.app.service.IMModuleService;
import iqq.app.service.impl.IMActionServiceImpl;
import iqq.app.service.impl.IMEventServiceImpl;
import iqq.app.service.impl.IMI18nServiceImpl;
import iqq.app.service.impl.IMModuleServiceImpl;
import iqq.app.service.impl.IMPropServiceImpl;
import iqq.app.service.impl.IMResourceServiceImpl;
import iqq.app.service.impl.IMSkinServiceImpl;
import iqq.app.service.impl.IMTaskServiceImpl;
import iqq.app.service.impl.IMTimerServiceImpl;
import iqq.app.ui.module.UIChatModule;
import iqq.app.ui.module.UIGroupMemberModule;
import iqq.app.ui.module.UIHoverInfoCardModule;
import iqq.app.ui.module.UILoginModule;
import iqq.app.ui.module.UIMainModule;
import iqq.app.ui.module.UIMsgHistoryModule;
import iqq.app.ui.module.UIPicPreviewModule;
import iqq.app.ui.module.UIPopupModule;
import iqq.app.ui.module.UIProxyModule;
import iqq.app.ui.module.UISettingModule;
import iqq.app.ui.module.UISoundModule;
import iqq.app.ui.module.UITrayModule;
import iqq.app.ui.module.UIVerifyModule;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.alee.laf.window.WebWindow;

/**
 * IM客户端的实现
 * 
 * @author ChenZhiHui
 * @create-time 2013-3-12
 */
public class IMApp implements IMContext {
	private static final Logger LOG = Logger.getLogger(IMApp.class);
	private Map<IMService.Type, IMServiceEntry> services;
	private boolean appExiting;
	private WebWindow startWin;
	/** 单例 */
	private static final IMApp instance = new IMApp();

	public static IMApp me() {
		return instance;
	}

	private IMApp() {
		this.services = new HashMap<IMService.Type, IMServiceEntry>();
		this.services.put(Type.TASK, new IMServiceEntry(new IMTaskServiceImpl(), 2));
		this.services.put(Type.RESOURCE, new IMServiceEntry(new IMResourceServiceImpl(), 3));
		this.services.put(Type.PROP, new IMServiceEntry(new IMPropServiceImpl(), 4));
		this.services.put(Type.I18N, new IMServiceEntry(new IMI18nServiceImpl(), 5));
		this.services.put(Type.SKIN, new IMServiceEntry(new IMSkinServiceImpl(), 6));
		this.services.put(Type.EVENT, new IMServiceEntry( new IMEventServiceImpl(), 7));
		this.services.put(Type.MODULE, new IMServiceEntry(new IMModuleServiceImpl(), 8));
		this.services.put(Type.ACTION, new IMServiceEntry(new IMActionServiceImpl(), 9));
		this.services.put(Type.TIMER, new IMServiceEntry(new IMTimerServiceImpl(), 10));
		this.appExiting = false;
	}

	public void startup() {
		LOG.info("starting IMApp...");
		LOG.info("init services...");
		List<IMServiceEntry> serviceList = new ArrayList<IMServiceEntry>();
		serviceList.addAll(services.values());
		Collections.sort(serviceList);
		//按优先级从高到低排序，优先级高的服务优先初始化，数字越小优先级越高
		for (IMServiceEntry entry :serviceList) {
			try {
				long start = System.currentTimeMillis();
				entry.service.init(this);
				long end   = System.currentTimeMillis(); 
				LOG.info("initialized service " + entry.service.getClass().getName() + ", used " + ( end - start) +" ms!!");
			} catch (Exception e) {
				LOG.error("init service Error!!", e);
			}
		}
		LOG.info("init IMApp [OK]!");

		LOG.info("starting bootstrap modules...");
		IMModuleService ms = (IMModuleService) getSerivce(IMService.Type.MODULE);
		try {
			LOG.info("starting QQLogic...");
			ms.installModule(new QQLogicModule());
			ms.installModule(new QQMsgHistoryModule());
			ms.installModule(new QQCacheModule());
			ms.installModule(new QQMsgManagerModule());
			ms.installModule(new QQAccountModule());

			LOG.info("starting UILoginModule...");
			ms.installModule(new UIVerifyModule());
			ms.installModule(new UIProxyModule());
			ms.installModule(new UILoginModule());
			ms.installModule(new UIMainModule());
			ms.installModule(new UIChatModule());
			ms.installModule(new UIPopupModule());
			ms.installModule(new UITrayModule());
			ms.installModule(new UISoundModule());
			//ms.installModule(new UIDesktopMsgBoxModule());
			ms.installModule(new UIPicPreviewModule());
			ms.installModule(new UIGroupMemberModule());
			ms.installModule(new UIMsgHistoryModule());
			ms.installModule(new UIHoverInfoCardModule());
			ms.installModule(new UISettingModule());
			
		} catch (IMException e) {
			LOG.error("start error!!!", e);
		}

		LOG.info("register APP_EXIT_READY event...");
		IMEventHandlerProxy.register(this, this);
		LOG.info("broadcast LOGIN_READY event...");
		IMEventService eventHub = (IMEventService) getSerivce(IMService.Type.EVENT);
		eventHub.broadcast(new IMEvent(IMEventType.LOGIN_READY));
	}
	
	private void shutdown(){
		try {
			LOG.info("brodcast APP_EXIT event..");
			IMEventService eventHub = (IMEventService) getSerivce(IMService.Type.EVENT);
			eventHub.broadcast(new IMEvent(IMEventType.APP_EXIT));
			
			LOG.info("destory all modules...");
			IMModuleService ms = (IMModuleService) getSerivce(IMService.Type.MODULE);
			List<IMModule> modules = new ArrayList<IMModule>(ms.getModuleList());
			for (IMModule module : modules) {
				try {
					long start = System.currentTimeMillis();
					ms.removeModule(module);
					long end   = System.currentTimeMillis(); 
					LOG.info("destoryed module " + module.getClass().getName() + ", used " + ( end - start) +" ms!!");
				} catch (Exception e) {
					LOG.info("destory module Error!!", e);
				}
			}

			LOG.info("destory all services...");
			
			List<IMServiceEntry> serviceList = new ArrayList<IMServiceEntry>();
			serviceList.addAll(services.values());
			Collections.sort(serviceList);
			//注意这里优先级低的先destroy，高的最后destroy
			for (int i = serviceList.size() - 1; i >= 0; i--) {
				try {
					serviceList.get(i).service.destroy();
				} catch (Exception e) {
					LOG.info("destory service Error!!", e);
				}
			}
		}catch (Exception e) {
			LOG.fatal("IMApp exit error!!!", e);
		} finally {
			LOG.info("IMApp exit!!!");
			System.exit(0);
		}
	}
	
	public WebWindow startWin() {
		if(startWin == null) {
			startWin = new WebWindow() {
				private static final long serialVersionUID = 1L;
				@Override
				public void paint(Graphics g) {
					g.drawImage(doubleBufferedDraw(), 0, 0, null);
					g.dispose();
				}
				public Image doubleBufferedDraw() {
					Image image = this.createImage(this.getWidth(), this.getHeight());
					Graphics2D g2d = (Graphics2D) image.getGraphics();
					g2d.drawImage(new ImageIcon("res/images/startup_logo.png").getImage(), 0, 0, this);
					g2d.dispose();
					return image;
				}
			};
		}
		startWin.setSize(360, 270);
		startWin.setLocationRelativeTo(null);
		startWin.setAlwaysOnTop(true);
		startWin.setVisible(true);
		startWin.repaint();
		return startWin;
	}
	
	public void endWin() {
		if(startWin != null) {
			startWin.dispose();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends IMService> T getSerivce(Type type) {
		return (T) services.get(type).service;
	}
	
	
	@IMEventHandler(IMEventType.LOGOUT_SUCCESS)
	protected void processIMLogoutSuccess(IMEvent event){
		if( appExiting ){
			shutdown();
		}
	}
	
	@IMEventHandler(IMEventType.APP_EXIT_READY)
	protected void processIMAppExitReady(IMEvent event){
		appExiting = true;
		IMEventService eventHub = (IMEventService) getSerivce(IMService.Type.EVENT);
		eventHub.broadcast(new IMEvent(IMEventType.LOGOUT_REQUEST));
	}
	
	private static class IMServiceEntry implements Comparable<IMServiceEntry>{
		public IMService service;
		public int priority;
		public IMServiceEntry(IMService service, int priority) {
			this.service = service;
			this.priority = priority;
		}
		@Override
		public int compareTo(IMServiceEntry other) {
			return this.priority - other.priority;
		}
	}
	
	public static void main(String[] args) {
		LOG.info(System.getProperty("java.vm.name") + System.getProperty("java.version"));
		
		IMApp.me().startWin();	// 显示程序LOGO
		SwingUtilities.invokeLater(new Runnable(){
		    public void run() {
		    	IMApp.me().startup();
		    }
		});
		IMApp.me().endWin();	// 处理掉启动LOGO
	}

}

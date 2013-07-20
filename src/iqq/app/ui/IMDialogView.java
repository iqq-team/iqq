/**
 * 
 */
package iqq.app.ui;

import iqq.app.core.IMContext;
import iqq.app.core.IMException;
import iqq.app.core.IMModule;
import iqq.app.core.IMService;
import iqq.app.service.IMActionService;
import iqq.app.service.IMSkinService;
import iqq.app.ui.widget.IMTitleComponent;
import iqq.app.util.SkinUtils;

import java.awt.Image;

import com.alee.extended.painter.Painter;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;

/**
 * 
 * @author ZhiHui_Chen<6208317@qq.com>
 * @create date 2013-4-3
 */
public class IMDialogView extends WebDialog implements IMModule,View {
	private static final long serialVersionUID = 1853538560617253177L;
	private IMContext context;
	private IMTitleComponent titleComponent;

	@Override
	public void init(IMContext context) throws IMException {
		this.context = context;
	}

	@Override
	public void destroy() throws IMException {
		dispose();
	}

	public IMTitleComponent getIMTitleComponent() {
		if(titleComponent == null) {
			titleComponent = new IMTitleComponent(this);
		}
		return titleComponent;
	}

	public void setIMTitleComponent(IMTitleComponent titleComponent) {
		this.titleComponent = titleComponent;
	}

	public IMActionService getActionService() {
		return getContext().getSerivce(IMService.Type.ACTION);
	}
	
	/**
	 * @return the context
	 */
	public IMContext getContext() {
		return context;
	}
	
	/**
	 * 显示内容
	 */
	public void setContentPanel(WebPanel container) {
		setContentPane(container);
		// 默认
		changeSkin(SkinUtils.getPainter(IMSkinService.Type.NPICON, "appBg"));
	}

	public void changeSkin(Painter<?> painter) {
		WebPanel mainPl = (WebPanel) getContentPane();
		mainPl.setPainter(painter);
		mainPl.revalidate();
		mainPl.repaint();
	}
	
	@Override
	public void setIconImage(Image image) {
		super.setIconImage(image);
		firePropertyChange("appLogo", null, image);
	}
}

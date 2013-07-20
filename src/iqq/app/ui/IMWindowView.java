/**
 * 
 */
package iqq.app.ui;

import java.awt.Image;

import iqq.app.core.IMContext;
import iqq.app.core.IMException;
import iqq.app.core.IMModule;
import iqq.app.service.IMSkinService;
import iqq.app.util.SkinUtils;

import com.alee.extended.painter.Painter;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.window.WebWindow;

/**
 * 
 * @author ZhiHui_Chen<6208317@qq.com>
 * @create date 2013-4-3
 */
public class IMWindowView extends WebWindow implements IMModule,View {
	private static final long serialVersionUID = 1853538560617253177L;
	private IMContext context;

	@Override
	public void init(IMContext context) throws IMException {
		this.context = context;
	}

	@Override
	public void destroy() throws IMException {
		dispose();
	}

	/**
	 * @return the context
	 */
	public IMContext getContext() {
		return context;
	}
	
	@Override
	public void setIconImage(Image image) {
		super.setIconImage(image);
		firePropertyChange("appLogo", null, image);
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
	}
}

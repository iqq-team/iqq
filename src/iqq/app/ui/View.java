/**
 * 
 */
package iqq.app.ui;

import iqq.app.core.IMContext;

import com.alee.extended.painter.Painter;
import com.alee.laf.panel.WebPanel;

/**
 * @author ZhiHui_Chen<6208317@qq.com>
 * @create date 2013-4-10
 */
public interface View {
	public void changeSkin(Painter<?> painter);
	public void setContentPanel(WebPanel container);
	public IMContext getContext();
}

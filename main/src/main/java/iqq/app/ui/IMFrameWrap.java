package iqq.app.ui;

import com.alee.laf.panel.WebPanel;
import iqq.app.core.context.IMContext;
import iqq.app.core.service.SkinService;
import iqq.app.core.service.impl.SkinServiceImpl;
import iqq.app.ui.skin.Skin;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-5
 * License  : Apache License 2.0
 */
public class IMFrameWrap extends WebPanel implements Skin {

    public IMFrameWrap(IMContext context) {
    }

    @Override
    public void installSkin(SkinService skinService) {
        // 背景只带了阴影，就只是一个底而已
        setPainter(skinService.getPainterByKey("window/background"));
    }
}

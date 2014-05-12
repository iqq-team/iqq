package iqq.app.ui;

import com.alee.extended.painter.*;
import com.alee.laf.StyleConstants;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.panel.AdaptivePanelPainter;
import com.alee.laf.panel.PanelPainter;
import com.alee.laf.panel.WebPanel;
import com.alee.utils.LafUtils;
import com.alee.utils.NinePatchUtils;
import iqq.app.core.context.IMContext;
import iqq.app.core.service.SkinService;
import iqq.app.ui.skin.Skin;

import java.awt.*;

/**
 * 窗口阴影背景
 *
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
        //Painter painter = skinService.getPainterByKey("window/background");
        Painter painter = new NinePatchIconPainter(NinePatchUtils.getShadeIcon(20, 1, 0.75f));
        setPainter(painter);
    }

}

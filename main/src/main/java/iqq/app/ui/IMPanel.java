package iqq.app.ui;

import com.alee.laf.panel.WebPanel;
import iqq.app.core.service.SkinService;
import iqq.app.ui.skin.Skin;

/**
 * 面板封装类
 *
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-7
 * License  : Apache License 2.0
 */
public class IMPanel extends WebPanel implements Skin {
    public IMPanel() {
        this.setOpaque(false);
    }

    @Override
    public void installSkin(SkinService skinService) {

    }
}

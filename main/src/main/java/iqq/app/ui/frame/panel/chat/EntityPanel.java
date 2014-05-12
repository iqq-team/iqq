package iqq.app.ui.frame.panel.chat;

import iqq.api.bean.IMEntity;
import iqq.app.ui.IMPanel;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-11
 * License  : Apache License 2.0
 */
public class EntityPanel extends IMPanel {
    private IMEntity entity;

    public EntityPanel(IMEntity entity) {
        this.entity = entity;
    }

    public IMEntity getEntity() {
        return entity;
    }

    public void setEntity(IMEntity entity) {
        this.entity = entity;
    }
}

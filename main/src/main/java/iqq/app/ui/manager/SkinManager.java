package iqq.app.ui.manager;

import iqq.app.core.service.SkinService;
import iqq.app.ui.skin.Skin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * 皮肤管理器，可以更新新皮肤
 * <p/>
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-5
 * License  : Apache License 2.0
 */
@Component
public class SkinManager {
    private final Logger LOG = LoggerFactory.getLogger(SkinManager.class);
    private List<Skin> skinList = new LinkedList<Skin>();

    /**
     * 注册皮肤管理
     *
     * @param skin
     */
    public void register(Skin skin) {
        skinList.add(skin);
        LOG.debug("register " + skin.getClass().getName());
    }

    /**
     * 取消注册皮肤管理，放弃持有引用，用来释放对象
     *
     * @param skin
     */
    public void unregister(Skin skin) {
        skinList.remove(skin);
        LOG.debug("unregister " + skin.getClass().getName());
    }

    /**
     * 安装所有皮肤，更新覆盖原来的
     *
     * @param skinService
     */
    public void installAll(SkinService skinService) {
        for (Skin skin : skinList) {
            skin.installSkin(skinService);
            LOG.debug("install " + skin.getClass().getName());
        }
    }
}

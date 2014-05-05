package iqq.app.ui;
import com.alee.laf.rootpane.WebFrame;
import iqq.app.core.context.IMContext;
import iqq.app.core.service.I18nService;
import iqq.app.core.service.ResourceService;
import iqq.app.core.service.SkinService;
import iqq.app.core.service.impl.I18nServiceImpl;
import iqq.app.core.service.impl.ResourceServiceImpl;
import iqq.app.core.service.impl.SkinServiceImpl;
import iqq.app.ui.skin.Skin;
import iqq.app.ui.skin.SkinManager;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-4-15
 * License  : Apache License 2.0
 */
public abstract class IMFrame extends WebFrame implements Skin {

    protected IMContext context;
    protected IMFrameWrap contentWrap;

    protected I18nService i18nService;
    protected SkinService skinService;
    protected ResourceService resourceService;

    public IMFrame(IMContext context) {
        this.context = context;

        i18nService = context.getIoc().get(I18nServiceImpl.class);
        skinService = context.getIoc().get(SkinServiceImpl.class);
        resourceService = context.getIoc().get(ResourceServiceImpl.class);

        // 创建wrap，并设置为默认面板(该面板为窗口阴影面板)
        contentWrap = new IMFrameWrap(context);
        contentWrap.installSkin(skinService);
        super.setContentPane(contentWrap);
        // 注册皮肤管理
        SkinManager.register(this);
    }

    /**
     * 获取Context
     * @return
     */
    public IMContext getContext() {
        return context;
    }

    /**
     * 设置窗口内容面板
     * @param contentPane
     */
    @Override
    public void setContentPane(Container contentPane) {
        contentWrap.add(contentPane);
    }

    /**
     * 安装窗口需要的皮肤
     *
     * @param skinService
     */
    @Override
    public void installSkin(SkinService skinService) {

    }

    /**
     * 释放窗口
     */
    @Override
    public void dispose() {
        super.dispose();
        // 取消注册皮肤管理
        SkinManager.unregister(this);
    }

    /**
     * 获取皮肤服务
     *
     * @return
     */
    public SkinService getSkinService() {
        return skinService;
    }

    /**
     * 获取资源文件服务
     *
     * @return
     */
    public ResourceService getResourceService() {
        return resourceService;
    }

    /**
     * 获取I18N服务
     *
     * @return
     */
    public I18nService getI18nService() {
        return i18nService;
    }
}

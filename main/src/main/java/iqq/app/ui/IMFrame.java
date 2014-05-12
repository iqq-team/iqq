package iqq.app.ui;

import com.alee.extended.painter.NinePatchIconPainter;
import com.alee.extended.painter.Painter;
import com.alee.laf.rootpane.WebFrame;
import com.sun.awt.AWTUtilities;
import iqq.app.core.context.IMContext;
import iqq.app.core.service.I18nService;
import iqq.app.core.service.ResourceService;
import iqq.app.core.service.SkinService;
import iqq.app.core.service.impl.I18nServiceImpl;
import iqq.app.core.service.impl.ResourceServiceImpl;
import iqq.app.core.service.impl.SkinServiceImpl;
import iqq.app.ui.skin.Skin;
import iqq.app.ui.skin.SkinManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * IM窗口抽象类，带阴影背景
 * 实现了皮肤接口
 *
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
        super.setContentPane(contentWrap);

        // 去了默认边框
        setUndecorated(true);
        // 把窗口设置为透明
        AWTUtilities.setWindowOpaque(this, false);

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
    public void setIMContentPane(IMContentPane contentPane) {
        // 设置一个边框
        contentPane.setMargin(0, 2, 2, 2);
        contentWrap.add(contentPane);
    }

    /**
     * 安装窗口需要的皮肤
     *
     * @param skinService
     */
    @Override
    public void installSkin(SkinService skinService) {
        contentWrap.installSkin(getSkinService());
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

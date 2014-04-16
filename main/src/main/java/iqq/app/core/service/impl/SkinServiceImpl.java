package iqq.app.core.service.impl;

import com.alee.extended.painter.Painter;
import iqq.app.core.service.SkinService;
import org.nutz.ioc.loader.annotation.IocBean;

import java.awt.*;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-4-15
 * License  : Apache License 2.0
 */
@IocBean
public class SkinServiceImpl implements SkinService {
    public static final String SKIN_CONFIG_FILE = "skin.xml";
    public static final String DEFAULT_SKIN_DIR = System.getProperty("app.dir",
            System.getProperty("user.dir")) + "/skin-default/";

    /**
     * 获取颜色
     *
     * @param key
     * @return
     */
    @Override
    public Color getColorByKey(String key) {
        return null;
    }

    /**
     * 获取点9图的Painter
     *
     * @param key
     * @return
     */
    @Override
    public Painter getPainterByKey(String key) {
        return null;
    }

    /**
     * 获取自定义皮肤目录
     *
     * @return
     */
    @Override
    public String getDirectory() {
        return DEFAULT_SKIN_DIR;
    }

    /**
     * 设置自定义皮肤目录
     *
     * @param path
     */
    @Override
    public void setDirectory(String path) {

    }

    /**
     * 是否启用了自定义皮肤
     *
     * @return
     */
    @Override
    public boolean isEnabledCustom() {
        return false;
    }

    /**
     * 设置启用自定义皮肤
     *
     * @param enable
     */
    @Override
    public void setEnableCustom(boolean enable) {

    }
}

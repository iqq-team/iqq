package iqq.app.core.service;

import com.alee.extended.painter.Painter;

import java.awt.*;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-4-15
 * License  : Apache License 2.0
 */
public interface SkinService {

    /**
     * 获取颜色
     *
     * @return
     */
    public Color getColorByKey(String key);

    /**
     * 获取点9图的Painter
     *
     * @param key
     * @return
     */
    public Painter getPainterByKey(String key);

    /**
     * 获取自定义皮肤目录
     *
     * @return
     */
    public String getDirectory();

    /**
     * 设置自定义皮肤目录
     *
     * @param path
     */
    public void setDirectory(String path);

    /**
     * 是否启用了自定义皮肤
     * @return
     */
    public boolean isEnabledCustom();

    /**
     * 设置启用自定义皮肤
     *
     * @param enable
     */
    public void setEnableCustom(boolean enable);
}

package iqq.app.core.service.impl;

import com.alee.extended.painter.NinePatchIconPainter;
import com.alee.extended.painter.Painter;
import iqq.app.core.service.SkinService;
import iqq.app.util.XmlUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.nutz.ioc.loader.annotation.IocBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * 皮肤读取服务，提供皮肤目录获取、自定义皮肤目录获取
 * 配置是否启用自定义皮肤目录
 * 如果自定义目录下寻找不到资源，就到默认皮肤目录下寻找
 *
 * 还没加入缓存优化
 *
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-4-15
 * License  : Apache License 2.0
 */
@IocBean
public class SkinServiceImpl implements SkinService {
    private static final Logger LOG = LoggerFactory.getLogger(SkinServiceImpl.class);
    public static final String SKIN_CONFIG_FILE = "skin.xml";
    public static final String DEFAULT_SKIN_DIR = System.getProperty("app.dir",
                               System.getProperty("user.dir")) + File.separator + "skin-default" + File.separator;

    /**
     * 获取颜色
     *
     * @param key
     * @param def
     * @return
     */
    public Color getColorByKey(String key, Color def) {
        Color color = getColorByKey(key);
        if(color == null) {
            color = def;
        }
        return color;
    }

    /**
     * 获取颜色
     *
     * @param key
     * @return
     */
    @Override
    public Color getColorByKey(String key) {
        if(StringUtils.isNotEmpty(key)) {
            try {
                // 先获取皮肤目录，会自动判断是否启用自定义皮肤
                String color = XmlUtils.getNodeText(getSkinConfig(), key);
                return Color.decode(color);
            } catch (NullPointerException e) {
                // 获取不到，有可能是自定义皮肤中没有，转到默认皮肤里面拿
                String color = null;
                try {
                    // 改用getDefaultConfig()，直接获取默认配置文件
                    color = XmlUtils.getNodeText(getDefaultConfig(), key);
                } catch (NullPointerException e1) {
                    LOG.error("获取皮肤中的颜色错误, key" + key, e1);
                } catch (DocumentException e2) {
                    LOG.error("获取皮肤中的颜色错误, key" + key, e2);
                }
                return Color.decode(color);
            } catch (DocumentException e) {
                LOG.error("获取皮肤中的颜色错误, key" + key, e);
            }
        }
        return null;
    }

    /**
     * 获取图标
     *
     * @param key
     * @return
     */
    @Override
    public ImageIcon getIconByKey(String key) {
        if(StringUtils.isNotEmpty(key)) {
            try {
                // 先获取皮肤目录，会自动判断是否启用自定义皮肤
                String iconPath = getDirectory();
                iconPath += XmlUtils.getNodeText(getSkinConfig(), key);
                return new ImageIcon(iconPath);
            } catch (NullPointerException ex) {
                // 获取不到，到默认皮肤目录下获取
                String iconPath = DEFAULT_SKIN_DIR;
                try {
                    // 改用getDefaultConfig()，直接获取默认配置文件
                    iconPath += XmlUtils.getNodeText(getDefaultConfig(), key);
                } catch (NullPointerException e1) {
                    LOG.error("获取皮肤中的图标错误, key" + key, e1);
                } catch (DocumentException e) {
                    LOG.error("获取皮肤中的图标错误, key" + key, e);
                }
                return new ImageIcon(iconPath);
            } catch (DocumentException e) {
                LOG.error("获取皮肤中的图标错误, key" + key, e);
            }
        }
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
        if(StringUtils.isNotEmpty(key)) {
            try {
                // 先获取皮肤目录，会自动判断是否启用自定义皮肤
                String iconPath = getDirectory();
                iconPath += XmlUtils.getNodeText(getSkinConfig(), key);
                return new NinePatchIconPainter(iconPath);
            } catch (NullPointerException ex) {
                // 获取不到，到默认皮肤目录下获取
                String iconPath = DEFAULT_SKIN_DIR;
                try {
                    // 改用getDefaultConfig()，直接获取默认配置文件
                    iconPath += XmlUtils.getNodeText(getDefaultConfig(), key);
                } catch (NullPointerException e1) {
                    LOG.error("获取皮肤中的图标错误, key" + key, e1);
                } catch (DocumentException e) {
                    LOG.error("获取皮肤中的图标错误, key" + key, e);
                }
                LOG.debug(iconPath);
                return new NinePatchIconPainter(iconPath);
            } catch (DocumentException e) {
                LOG.error("获取皮肤中的图标错误, key" + key, e);
            }
        }
        return null;
    }

    /**
     * 获取默认的皮肤配置文件
     *
     * @return
     */
    @Override
    public String getDefaultConfig() {
        return DEFAULT_SKIN_DIR + SKIN_CONFIG_FILE;
    }

    /**
     * 获取皮肤配置文件，如果用户没有配置自定义，则获取默认配置文件
     *
     * @return
     */
    public String getSkinConfig() {
        return getDirectory() + SKIN_CONFIG_FILE;
    }

    /**
     * 获取自定义皮肤目录
     *
     * @return
     */
    @Override
    public String getDirectory() {
        if(isEnabledCustom()) {
            try {
                // 直接读取默认配置文件
                return XmlUtils.getNodeText(getDefaultConfig(), "config/customDir");
            } catch (DocumentException e) {
                LOG.error("获取皮肤配置文件错误", e);
                return DEFAULT_SKIN_DIR;
            }
        }
        return DEFAULT_SKIN_DIR;
    }

    /**
     * 设置自定义皮肤目录
     *
     * @param path
     */
    @Override
    public void setDirectory(String path) {
        if(StringUtils.isNotEmpty(path)) {
            try {
                // 直接读取默认配置文件
                XmlUtils.setNodeText(getDefaultConfig(), "config/customDir", path);
            } catch (DocumentException e) {
                LOG.error("写入自定义目录到皮肤配置Document错误 path" + path, e);
            } catch (IOException e) {
                LOG.error("写入自定义目录到皮肤配置IO错误 path" + path, e);
            }
        }
    }

    /**
     * 是否启用了自定义皮肤
     *
     * @return
     */
    @Override
    public boolean isEnabledCustom() {
        String isEnable = "";
        try {
            // 直接读取默认配置文件
            isEnable = XmlUtils.getNodeText(getDefaultConfig(), "config/enable");
            LOG.debug("启用自定义皮肤：" + isEnable);
        } catch (DocumentException e) {
            LOG.error("获取是否启用自定义皮肤选项错误", e);
            return false;
        }
        return isEnable.equalsIgnoreCase("true");
    }

    /**
     * 设置启用自定义皮肤
     *
     * @param enable
     */
    @Override
    public void setEnableCustom(boolean enable) {
        try {
            // 直接读取默认配置文件
            XmlUtils.setNodeText(getDefaultConfig(), "config/enable", enable ? "true" : "false");
        } catch (DocumentException e) {
            LOG.error("写入自定义目录到皮肤配置Document错误", e);
        } catch (IOException e) {
            LOG.error("写入自定义目录到皮肤配置IO错误", e);
        }
    }
}

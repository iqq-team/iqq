package iqq.app.core.service.impl;

import iqq.app.core.service.ResourceService;
import org.nutz.ioc.loader.annotation.IocBean;

import javax.swing.*;
import java.io.File;

/**
 * 资源文件获取服务
 *
 * #还没有加入缓存优化
 *
 * Project  : iqq
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-4-17
 * License  : Apache License 2.0
 */
@IocBean
public class ResourceServiceImpl implements ResourceService {
    /**
     * 资源文件目录
     */
    public static final String RESOURCES_DIR = System.getProperty("app.dir",
            System.getProperty("user.dir")) + File.separator + "resources" + File.separator;

    /**
     * 获取绝对资源目录
     *
     * @return
     */
    @Override
    public String getResourcePath() {
        return RESOURCES_DIR;
    }

    /**
     * 获取资源文件
     *
     * @param filename
     * @return
     */
    @Override
    public File getFile(String filename) {
        return new File(RESOURCES_DIR + filename);
    }

    /**
     * 获取资源文件图片
     *
     * @param filename
     * @return
     */
    @Override
    public ImageIcon getIcon(String filename) {
        return new ImageIcon(getFile(filename).getAbsolutePath());
    }

    /**
     * 获取资源文件图片，调整为固定大小
     *
     * @param filename
     * @param width
     * @param height
     * @return
     */
    @Override
    public ImageIcon getIcon(String filename, int width, int height) {
        return new ImageIcon(getIcon(filename).getImage().getScaledInstance(width, height, 100));
    }
}

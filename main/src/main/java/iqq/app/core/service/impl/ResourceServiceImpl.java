package iqq.app.core.service.impl;

import iqq.app.core.service.ResourceService;
import org.springframework.stereotype.Service;

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
@Service
public class ResourceServiceImpl implements ResourceService {
    /**
     * 资源文件目录
     */
    public static final String RESOURCES_DIR = System.getProperty("app.dir",  System.getProperty("user.dir")) + File.separator + "resources" + File.separator;

    public static final String USER_DIR = "user" + File.separator;

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

    /**
     * 获取用户目录
     *
     * @return
     */
    @Override
    public String getUserPath() {
        return getResourcePath() + USER_DIR;
    }

    /**
     * 获取用户目录下面的文件
     *
     * @param filename
     * @return
     */
    @Override
    public File getUserFile(String filename) {
        return new File(RESOURCES_DIR + filename);
    }

    /**
     * 获取资源文件图片
     *
     * @param filename
     * @return
     */
    @Override
    public ImageIcon getUserIcon(String filename) {
        return new ImageIcon(getUserFile(filename).getAbsolutePath());
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
    public ImageIcon getUserIcon(String filename, int width, int height) {
        return new ImageIcon(getUserIcon(filename).getImage().getScaledInstance(width, height, 100));
    }
}

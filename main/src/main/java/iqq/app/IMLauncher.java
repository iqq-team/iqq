package iqq.app;

import iqq.app.core.context.IMContext;
import iqq.app.ui.manager.FrameManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.swing.*;
import java.io.File;

/**
 * 引导启动类
 * <p>
 * Project  : iqq
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-4-17
 * License  : Apache License 2.0
 */
@Configuration
@ComponentScan("iqq.app")
public class IMLauncher {
    private static final Logger LOG = LoggerFactory.getLogger(IMLauncher.class);

    /**
     * 程序入口
     *
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                init();
                startup();
            }
        });
    }

    /**
     * 启动前配置
     */
    private static void init() {
        // APP路径
        String path = System.getProperty("user.dir");
        if (new File(path + File.separator + "resources").exists()) {
            System.setProperty("app.dir", new File(path).getAbsolutePath());
        } else {
            // 去掉了最后一个main目录
            path = path.substring(0, path.lastIndexOf(File.separator));
            System.setProperty("app.dir", new File(path).getAbsolutePath());
        }
        LOG.info("app.dir = " + System.getProperty("app.dir"));

        // 关闭勾子
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                shutdown();
            }
        }));
        LOG.info("init...");
    }

    /**
     * 运行程序环境
     */
    private static void startup() {
        IMContext.init(new AnnotationConfigApplicationContext("iqq.app"));

        FrameManager frameManager = IMContext.getBean(FrameManager.class);
        frameManager.showLogin();
    }

    /**
     * 程序关闭
     */
    private static void shutdown() {
        LOG.info("shutdown...");
    }
}

package iqq.app;

import com.alee.laf.WebLookAndFeel;
import com.alee.utils.SwingUtils;
import iqq.app.core.context.IMContext;
import iqq.app.ui.frame.ChatFrame;
import iqq.app.ui.frame.LoginFrame;
import iqq.app.ui.frame.MainFrame;
import iqq.app.ui.manager.MainManager;
import iqq.app.util.Benchmark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Locale;

/**
 * 引导启动类
 *
 * Project  : iqq
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-4-17
 * License  : Apache License 2.0
 */
public final class Bootstrap {
    private static final Logger LOG = LoggerFactory.getLogger(Bootstrap.class);
    private static IMApp app;
    /**
     * 程序入口
     *
     * @param args
     */
    public static void main(String[] args) {
        SwingUtils.invokeLater(new Runnable() {
            @Override
            public void run() {
                configBefore();
                starup();
                configAfter();
            }
        });
    }

    /**
     * 启动前配置
     */
    private static void configBefore() {
        Benchmark.start("appStart");
        // APP路径
        String path = System.getProperty("user.dir");
        if(new File(path + File.separator + "resources").exists()) {
            System.setProperty("app.dir", new File(path).getAbsolutePath());
        } else  {
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
        LOG.info("bootstrap configBefore...");
    }

    /**
     * 运行程序环境
     */
    private static void starup() {
        // 运行IMApp环境
        app = new IMApp();
        app.launch();
        LOG.info("bootstrap starup...");
    }

    /**
     * 启动后配置
     */
    private static void configAfter() {
        // 显示入口窗口
        //new LoginFrame(IMContext.me()).setVisible(true);
        //new MainFrame(IMContext.me()).setVisible(true);
        //new ChatFrame(IMContext.me()).setVisible(true);
        MainManager.show();
        MainManager.enableTray();

        LOG.info("bootstrap configAfter...");
        Benchmark.end("appStart");
    }

    /**
     * 程序关闭
     */
    private static void shutdown() {
        LOG.info("bootstrap shutdown...");
    }
}

package iqq.app;

import com.alee.utils.SwingUtils;
import iqq.app.core.context.IMContext;
import iqq.app.core.query.BuddyQuery;
import iqq.app.ui.frame.LoginFrame;
import iqq.app.util.Benchmark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 引导启动类
 *
 * Project  : iqq
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-4-17
 * License  : Apache License 2.0
 */
public final class IMLauncher {
    private static final Logger LOG = LoggerFactory.getLogger(IMLauncher.class);
    /**
     * 程序入口
     *
     * @param args
     */
    public static void main(String[] args) {
        SwingUtils.invokeLater(new Runnable() {
            @Override
            public void run() {
                init();
                starup();
            }
        });
    }

    /**
     * 启动前配置
     */
    private static void init() {
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
        LOG.info("bootstrap init...");
    }

    /**
     * 运行程序环境
     */
    private static void starup() {
       // FrameManager frameManager = IMContext.getBean(FrameManager.class);
      new LoginFrame().setVisible(true);
        BuddyQuery buddyQuery = IMContext.getBean(BuddyQuery.class);
        System.out.println(buddyQuery);
    }


    /**
     * 程序关闭
     */
    private static void shutdown() {
        LOG.info("bootstrap shutdown...");
    }
}

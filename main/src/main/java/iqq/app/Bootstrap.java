package iqq.app;

import com.alee.laf.WebLookAndFeel;
import com.alee.utils.SwingUtils;
import iqq.app.ui.frame.LoginFrame;

import javax.swing.*;

/**
 * 引导启动类
 *
 * Project  : iqq
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-4-17
 * License  : Apache License 2.0
 */
public final class Bootstrap {

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
        // 配置weblaf
        WebLookAndFeel.setDecorateAllWindows(false);
        WebLookAndFeel.install();
    }

    /**
     * 运行程序环境
     */
    private static void starup() {
        // 运行IMApp环境
        new IMApp().launch();
    }

    /**
     * 启动后配置
     */
    private static void configAfter() {
        // 显示入口窗口
        new LoginFrame().setVisible(true);
    }
}

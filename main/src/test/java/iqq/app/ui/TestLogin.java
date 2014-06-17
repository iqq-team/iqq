package iqq.app.ui;

import com.alee.laf.WebLookAndFeel;
import iqq.app.ui.frame.LoginFrame;
import iqq.app.ui.frame.VerifyFrame;
import org.junit.Before;
import org.junit.Test;

import java.util.Scanner;

/**
 * Project  : iqq
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-4-16
 * License  : Apache License 2.0
 */
public class TestLogin {

    @Before
    public void before() {
        WebLookAndFeel.install();
    }

    @Test
    public void testLoginBg() {
        LoginFrame login = new LoginFrame();
        new Scanner(System.in).next();
    }

    @Test
    public void testVerification() {
        VerifyFrame ver = new VerifyFrame();
        ver.setVisible(true);
        new Scanner(System.in).next();
    }
}

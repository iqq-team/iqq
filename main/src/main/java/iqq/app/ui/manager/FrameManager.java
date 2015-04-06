package iqq.app.ui.manager;

import iqq.app.ui.frame.LoginFrame;
import iqq.app.ui.frame.VerifyFrame;
import org.springframework.stereotype.Component;

/**
 * Created by Tony on 4/6/15.
 */
@Component
public class FrameManager {

    private LoginFrame loginFrame = null;
    private VerifyFrame verifyFrame = null;

    public void showLogin() {
        if (loginFrame == null) {
            loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        } else if (!loginFrame.isVisible()) {
            loginFrame.setVisible(true);
        }
    }

    public void hideLogin() {
        if (loginFrame != null) {
            loginFrame.setVisible(false);
        }
    }

    public void disposeLogin() {
        if (loginFrame != null) {
            loginFrame.dispose();
            loginFrame = null;
        }
    }

    public void showVerify() {
        if (verifyFrame == null) {
            verifyFrame = new VerifyFrame();
            verifyFrame.setVisible(true);
        } else if (!verifyFrame.isVisible()) {
            verifyFrame.setVisible(true);
        }
    }

    public void hideVerify() {
        if (verifyFrame != null) {
            verifyFrame.setVisible(false);
        }
    }

}

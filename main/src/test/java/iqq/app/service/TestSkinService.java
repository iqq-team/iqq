package iqq.app.service;

import com.alee.extended.painter.Painter;
import iqq.app.core.service.SkinService;
import iqq.app.core.service.impl.SkinServiceImpl;
import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;
import java.io.File;

/**
 * Project  : iqq
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-4-16
 * License  : Apache License 2.0
 */
public class TestSkinService {
    @Test
    public void testPath() {
        //System.setProperty("app.dir", "");
        SkinService skinService = new SkinServiceImpl();
        String path = skinService.getDirectory();
        System.out.println(path);
        Assert.assertNotNull(path);
        Assert.assertTrue(new File(path).exists());
    }

    @Test
    public void testCustomPath() {
        SkinService skinService = new SkinServiceImpl();
        String path = skinService.getDirectory();
        System.out.println(path);
        skinService.setDirectory("skins/test");
        skinService.setEnableCustom(false);
    }

    @Test
    public void testColor() {
        SkinService skinService = new SkinServiceImpl();

        System.out.println(skinService.getColorByKey("window/titleColor1"));
    }

    @Test
    public void tesIcon() {
        SkinService skinService = new SkinServiceImpl();
        Assert.assertTrue(skinService.getIconByKey("window/titleIcon") instanceof ImageIcon);
        Assert.assertTrue(skinService.getPainterByKey("login/background") instanceof Painter);
    }
}

package iqq.app.service;

import iqq.app.core.service.SkinService;
import iqq.app.core.service.impl.SkinServiceImpl;
import org.junit.Assert;
import org.junit.Test;

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
}

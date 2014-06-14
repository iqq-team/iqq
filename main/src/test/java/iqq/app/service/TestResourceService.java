package iqq.app.service;

import iqq.app.core.context.IMContext;
import iqq.app.core.service.ResourceService;
import iqq.app.core.service.impl.ResourceServiceImpl;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * Project  : iqq
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-4-17
 * License  : Apache License 2.0
 */
public class TestResourceService {

    ResourceService resourceService = null;

    @Before
    public void before() {
        resourceService = IMContext.getBean(ResourceServiceImpl.class);
    }

    @Test
    public void testResourcesPath() {
        String path = resourceService.getResourcePath() + "login/avatar.jpg";
        System.out.println(path);
        Assert.assertNotNull(path);
        Assert.assertTrue(new File(path).exists());
    }
}

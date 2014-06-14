package iqq.app;

import iqq.app.core.context.IMContext;
import iqq.app.core.service.SkinService;
import iqq.app.core.service.impl.SkinServiceImpl;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Project  : iqq
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-4-16
 * License  : Apache License 2.0
 */
public class TestIoc {
    @Before
    public void before() {

    }

    @Test
    public void testBean() {
        SkinService skinService = IMContext.getBean(SkinServiceImpl.class);
        Assert.assertNotNull(skinService);
    }
}

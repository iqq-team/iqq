package iqq.app.service;

import iqq.app.core.context.IMContext;
import iqq.app.core.service.I18nService;
import iqq.app.core.service.impl.I18nServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Administrator on 2014/4/17.
 */
public class TestI18nService {
    I18nService i18nService;
    @Before
    public void before() {
        i18nService = IMContext.getBean(I18nServiceImpl.class);
    }

    @Test
    public void testDefault() {
        //Locale.setDefault(Locale.CANADA);
        Assert.assertNotNull(i18nService);
        Assert.assertNotNull(i18nService.getMessage("login"));
        System.out.println(i18nService.getMessage("conversationTitle", " TEST"));
        System.out.println(i18nService.getMessage("login"));
    }
}

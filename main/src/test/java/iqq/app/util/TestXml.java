package iqq.app.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Project  : iqq
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-4-16
 * License  : Apache License 2.0
 */
public class TestXml {

    @Test
    public void testWrite() {
        String xmlFile = System.getProperty("user.dir") + "/config.xml";
        Document doc = DocumentHelper.createDocument();

        Element root = doc.addElement("roots");
        root.addElement("test").setText("test123");

        Element element = root.addElement("iqq");
        element.addElement("version").setText("iqqv3");

        // 测试XmlUtils
        try {
            XmlUtils.writeXml(xmlFile, doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRead() {
        String xmlFile = System.getProperty("user.dir") + "/config.xml";
        try {
            String text = XmlUtils.getNodeText(xmlFile, "iqq/version");
            System.out.println(text);
            Assert.assertNotNull(text);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}

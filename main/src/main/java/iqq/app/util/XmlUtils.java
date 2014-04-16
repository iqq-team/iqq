package iqq.app.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * XML操作类 读取/写入/获取值
 *
 * Project  : iqq
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-4-16
 * License  : Apache License 2.0
 */
public class XmlUtils {
    private static Map<String, Document> xmlCache = new HashMap<String, Document>();

    /**
     * 获取XML节点的值
     *
     * @param filename
     * @param key
     * @return
     * @throws DocumentException
     */
    public static String getNodeText(String filename, String key) throws DocumentException {
        return getNode(filename, key).getText();
    }

    /**
     * 获取XML节点
     *
     * @param filename
     * @param key
     * @return
     * @throws DocumentException
     */
    public static Node getNode(String filename, String key) throws DocumentException {
        return getElement(filename).selectSingleNode(key);
    }

    /**
     * 获取XML元素
     *
     * @param filename
     * @return
     * @throws DocumentException
     */
    public static Element getElement(String filename) throws DocumentException {
        return readXml(filename).getRootElement();
    }

    /**
     * 读入XML文件
     *
     * @param filename
     * @return
     * @throws DocumentException
     */
    public static Document readXml(String filename) throws DocumentException {
        Document document = null;
        if(xmlCache.get(filename) == null) {
            // 创建SAXReader读取器
            SAXReader saxReader = new SAXReader();
            // 读取xml
            document = saxReader.read(new File(filename));
            // 放到缓存中
            xmlCache.put(filename, document);
        }
        return document;
    }

    /**
     * 写入XML文件
     *
     * @param document
     * @param xmlFile
     * @throws IOException
     */
    public static void writeXml(Document document, String filename) throws IOException {
        OutputFormat outputFormat = OutputFormat.createPrettyPrint();// 设置XML文档输出格式
        outputFormat.setEncoding("UTF-8");// 设置XML文档的编码类型
        outputFormat.setIndent(true);// 设置是否缩进
        outputFormat.setIndent("	");// 以TAB方式实现缩进
        outputFormat.setNewlines(true);// 设置是否换行
        XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(filename), outputFormat);
        xmlWriter.write(document);
        xmlWriter.close();
    }

}

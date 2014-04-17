package iqq.app.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * XML操作类 读取/写入/获取值/写入值
 *
 * Project  : iqq
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-4-16
 * License  : Apache License 2.0
 */
public class XmlUtils {
    private static final Logger LOG = LoggerFactory.getLogger(XmlUtils.class);
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
     * 写入数据到XML
     *
     * @param filename
     * @param key
     * @param Value
     */
    public static void setNodeText(String filename, String key, String value) throws DocumentException, IOException {
        Document document = readXml(filename);
        Element rootElement = document.getRootElement();
        Node node = rootElement.selectSingleNode(key);
        if (node == null) {
            node = rootElement.addElement(key);
        }
        node.setText(value);
        writeXml(filename, document);
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
        return getRootElement(filename).selectSingleNode(key);
    }

    /**
     * 获取XML元素
     *
     * @param filename
     * @return
     * @throws DocumentException
     */
    public static Element getRootElement(String filename) throws DocumentException {
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
        if(xmlCache.get(filename) == null) {
            LOG.debug("读入XML: " + filename);
            // 创建SAXReader读取器
            SAXReader saxReader = new SAXReader();
            // 读取xml
            Document document = saxReader.read(new File(filename));
            // 放到缓存中
            xmlCache.put(filename, document);
            return document;
        }
        return xmlCache.get(filename);
    }

    /**
     * 写入XML文件
     *
     * @param document
     * @param xmlFile
     * @throws IOException
     */
    public static void writeXml(String filename, Document document) throws IOException {
        LOG.debug("写入XML: " + filename);
        OutputFormat outputFormat = OutputFormat.createPrettyPrint();// 设置XML文档输出格式
        outputFormat.setEncoding("UTF-8");// 设置XML文档的编码类型
        outputFormat.setIndent(true);// 设置是否缩进
        outputFormat.setIndent("	");// 以TAB方式实现缩进
        outputFormat.setNewlines(true);// 设置是否换行
        synchronized (document) {
            XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(filename), outputFormat);
            xmlWriter.write(document);
            xmlWriter.close();
        }
    }
}

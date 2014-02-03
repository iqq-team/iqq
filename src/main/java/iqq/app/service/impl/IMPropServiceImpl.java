package iqq.app.service.impl;

import iqq.app.core.IMContext;
import iqq.app.core.IMException;
import iqq.app.service.IMResourceService;
import iqq.app.util.ResourceUtils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.alee.laf.WebLookAndFeel;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-14
 */
public class IMPropServiceImpl extends AbstractServiceImpl implements
		iqq.app.service.IMPropService {
	private static final Logger LOG = Logger.getLogger(IMPropServiceImpl.class);
	private static final String CONFIG_XML_FILE_NAME = "config.xml";// config.xml配置文件名称
	private static final String SETTING_CACHE_KEY = "CONFIG_SETTING";// 配置文件缓存Key
	private static Map<String, Object> CACHE = new HashMap<String, Object>();

	@Override
	public void init(IMContext context) throws IMException {
		super.init(context);

		initConfig();
	}

	/**
	 * 
	 */
	private void initConfig() {
		// 获取默认语言环境
		Locale defautLocale = Locale.getDefault();

		try {
			// 读取配置文件
			read();
		} catch (URISyntaxException e) {
			LOG.error("getResource to URI error !!!", e);
		}
		
		// 挂载WebLookAndFeel样式， 为皮肤做基础
		// 会把语言环境设置为英文
		WebLookAndFeel.install();
		WebLookAndFeel.setDecorateFrames(true);
		WebLookAndFeel.setDecorateDialogs(true);

		// 设置默认语言环境
		Locale.setDefault(defautLocale);
	}

	@Override
	public void destroy() throws IMException {
		super.destroy();
		// 写入配置文件
		try {
			write();
		} catch (URISyntaxException e) {
			LOG.error("getResource to URI error !!!", e);
		}
	}

	@Override
	public String getString(String key) {
		String v = "";
		try {
			v = getNodeText(key);
		} catch (Exception e) {
			LOG.error("获取配置文件出错", e);
		}
		return v;
	}

	@Override
	public int getInt(String key) {
		int v = -1;
		try {
			v = Integer.parseInt(getNodeText(key));
		} catch (Exception e) {
			LOG.error("获取配置文件出错", e);
		}
		return v;
	}

	@Override
	public void setString(String key, String value) {
		Element rootElement = getRootElement();
		Node node = rootElement.selectSingleNode(key);
		if (node == null) {
			node = rootElement.addElement(key);
		}
		node.setText(value);
	}

	@Override
	public void setInt(String key, int value) {
		Element rootElement = getRootElement();
		Node node = rootElement.selectSingleNode(key);
		if (node == null) {
			node = rootElement.addElement(key);
		}
		node.setText(String.valueOf(value));
	}

	protected String getNodeText(String key) {
		return getRootElement().selectSingleNode(key).getText();
	}

	protected Element getRootElement() {
		Document doc = (Document) CACHE.get(SETTING_CACHE_KEY);
		return doc.getRootElement();
	}

	protected Document getDocument() {
		return (Document) CACHE.get(SETTING_CACHE_KEY);
	}

	public void read() throws URISyntaxException {
		IMResourceService fileSvr =  getContext().getSerivce(Type.RESOURCE);

		URI uri = ResourceUtils.getResource(CONFIG_XML_FILE_NAME).toURI();
		File configXmlFile = new File(uri);
		CACHE.put(SETTING_CACHE_KEY, fileSvr.readXml(configXmlFile));
	}

	public void write() throws URISyntaxException {
		IMResourceService fileSvr =  getContext().getSerivce(Type.RESOURCE);
		URI uri = ResourceUtils.getResource(CONFIG_XML_FILE_NAME).toURI();
		File configXmlFile = new File(uri);
		fileSvr.writeXml(getDocument(), configXmlFile);
	}

}

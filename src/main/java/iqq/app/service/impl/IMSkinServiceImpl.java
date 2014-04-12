package iqq.app.service.impl;

import iqq.app.core.IMContext;
import iqq.app.core.IMException;
import iqq.app.core.IMService;
import iqq.app.service.IMResourceService;
import iqq.app.service.IMSkinService;
import iqq.app.util.ResourceUtils;
import iqq.app.util.SettingUtils;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dom4j.Document;
import org.dom4j.Element;

import com.alee.extended.painter.NinePatchIconPainter;
import com.alee.extended.painter.Painter;
import com.alee.laf.WebLookAndFeel;
import com.alee.utils.XmlUtils;
import com.alee.utils.ninepatch.NinePatchIcon;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-14
 */
public class IMSkinServiceImpl extends AbstractServiceImpl implements
		IMSkinService {
	private static final Logger LOG = LoggerFactory.getLogger(IMSkinServiceImpl.class);
	private static final String SKIN_CACHE_KEY = "SKIN_CACHE";// 配置文件缓存Key
	private static final String SKIN_DIR_MATCHING = "{$skinDir}";// 配置文件缓存Key
	private static Map<String, Object> CACHE = new HashMap<String, Object>();
	private URL skinFile = null;// 皮肤文件
	private String skinDir = null;// 皮肤目录
	private Font globalFont = null;

	@Override
	public Painter<?> getPainter(Type type, String key) {
		String fileName = getNodeText(type, key);
		if (skinDir == null) {
			loadSkinPath();
		}
		if (type == Type.NPICON) {
			// 直接导入NP图片
			return new NinePatchIconPainter(new NinePatchIcon(new ImageIcon(
					ResourceUtils.getResource(skinDir + fileName))));
		} else if (type == Type.RESOURCE) {
			// 为XML资源
			File file = null;
			FileInputStream fileInput = null;
			try {
				file = new File(ResourceUtils.getResource(skinDir + fileName)
						.toURI());
				fileInput = new FileInputStream(file);
				int x = fileInput.available();
				byte b[] = new byte[x];
				fileInput.read(b);
				String xmlStr = new String(b);
				xmlStr = xmlStr.replace(SKIN_DIR_MATCHING,
						ResourceUtils.getResourceDir() + File.separator
								+ skinDir);
				// System.out.println(xmlStr);
				return XmlUtils
						.loadNinePatchStatePainter(new ByteArrayInputStream(
								xmlStr.getBytes()));
			} catch (URISyntaxException e) {
				LOG.error("Skin URISyntaxException: ", e);
			} catch (FileNotFoundException e) {
				LOG.error("Skin FileNotFoundException: ", e);
			} catch (IOException e) {
				LOG.error("Skin IOException: ", e);
			} finally {
				if (fileInput != null) {
					try {
						fileInput.close();
					} catch (IOException e) {
						LOG.error("Skin IOException: ", e);
					}
				}
			}
		}
		try {
			throw new FileNotFoundException("not found file: "
					+ ResourceUtils.getResource(skinDir + fileName));
		} catch (FileNotFoundException e) {
			LOG.error("Skin IOException: ", e);
		}
		return null;
	}

	@Override
	public ImageIcon getImageIcon(String key) {
		String fileName = getNodeText(Type.ICON, key);
		if (skinDir == null) {
			loadSkinPath();
		}
		// 普通图标
		return new ImageIcon(ResourceUtils.getResource(skinDir + fileName));
	}
	

	@Override
	public BufferedImage getBufferedImage(String key){
		String fileName = getNodeText(Type.ICON, key);
		if (skinDir == null) {
			loadSkinPath();
		}
		
		try {
			URL url = ResourceUtils.getResource(skinDir + fileName);
			return ImageIO.read(url);
		} catch (Exception e) {
			LOG.warn("getBufferedImage error", e);
		}
		return null;
	}

	@Override
	public ImageIcon getImageIcon(String key, Object... params) {
		String fileName = getNodeText(Type.ICON, key);
		if (fileName != null && params != null) {
			fileName = MessageFormat.format(fileName, params);
		}
		if (skinDir == null) {
			loadSkinPath();
		}
		// 普通图标
		return new ImageIcon(ResourceUtils.getResource(skinDir + fileName));
	}
	

	@Override
	public Font getFont() {
		return globalFont;
	}

	@Override
	public void setString(Type type, String key, String value) {

	}

	protected void loadSkinPath() {
		String skin = SettingUtils.getString("skin");
		skinFile = ResourceUtils.getResource(skin);
		skinDir = ResourceUtils.getFileDir(skin);
	}

	protected String getNodeText(Type type, String key) {
		// System.out.println(type.getName() + " " + key);
		return getTypeElement(type).selectSingleNode(key).getText();
	}

	protected Element getTypeElement(Type type) {
		// icon,npicon or resource node
		return getRootElement().element(type.getName());
	}

	protected Element getRootElement() {
		Document doc = (Document) CACHE.get(SKIN_CACHE_KEY);
		return doc.getRootElement();
	}

	protected Document getDocument() {
		return (Document) CACHE.get(SKIN_CACHE_KEY);
	}

	@Override
	public void init(IMContext context) throws IMException {
		super.init(context);

		try {
			read();
			loadDefaultFont();
		} catch (URISyntaxException e) {
			LOG.error("getResource to URI error !!!", e);
		}
	}

	@Override
	public void destroy() throws IMException {
		super.destroy();
		try {
			write();
		} catch (URISyntaxException e) {
			LOG.error("getResource to URI error !!!", e);
		}
	}

	public void read() throws URISyntaxException {
		IMResourceService fileSvr =  getContext().getSerivce(IMService.Type.RESOURCE);
		if (skinFile == null) {
			loadSkinPath();
		}
		URI uri = skinFile.toURI();
		File configXmlFile = new File(uri);
		CACHE.put(SKIN_CACHE_KEY, fileSvr.readXml(configXmlFile));
	}

	public void write() throws URISyntaxException {
		IMResourceService fileSvr =  getContext().getSerivce(IMService.Type.RESOURCE);
		URI uri = skinFile.toURI();
		File configXmlFile = new File(uri);

		fileSvr.writeXml(getDocument(), configXmlFile);
	}

	private void loadDefaultFont() throws URISyntaxException {

		Element el = getRootElement().element("font");
		String fontName = el.elementText("name");
		String fontSource = el.elementText("source");
		String fontFile = el.elementText("file");
		int fontSize = Integer.parseInt(el.elementText("size"));
		if (fontSource.equals("file")) {
			URL url = ResourceUtils.getResource(skinDir + fontFile);
			Font font = loadFontFromFile(new File(url.toURI()));
			if (font != null) {
				globalFont = font.deriveFont((float) fontSize);
				setDefaultFont(globalFont);
			}
		} else if (fontSource.equals("system")) {
			globalFont = new Font(fontName, Font.PLAIN, fontSize);
			setDefaultFont(globalFont);
		} else {
			LOG.warn("unknown font source :" + fontSource);
		}
	}

	private Font loadFontFromFile(File file) {
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			Font font = Font.createFont(Font.TRUETYPE_FONT, in);
			return font;
		} catch (FileNotFoundException e) {
			LOG.warn("font " + file + "not found!", e);
		} catch (FontFormatException e) {
			LOG.warn("invalid font file!", e);
		} catch (IOException e) {
			LOG.warn("read font error!", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					LOG.warn("close font file error!", e);
				}
			}
		}
		return null;
	}

	private void setDefaultFont(Font vFont) {
		UIManager.put("ToolTip.font", vFont);
		UIManager.put("Table.font", vFont);
		UIManager.put("TableHeader.font", vFont);
		UIManager.put("TextField.font", vFont);
		UIManager.put("ComboBox.font", vFont);
		UIManager.put("TextField.font", vFont);
		UIManager.put("PasswordField.font", vFont);
		UIManager.put("TextArea.font", vFont);
		UIManager.put("TextPane.font", vFont);
		UIManager.put("EditorPane.font", vFont);
		UIManager.put("FormattedTextField.font", vFont);
		UIManager.put("Button.font", vFont);
		UIManager.put("CheckBox.font", vFont);
		UIManager.put("RadioButton.font", vFont);
		UIManager.put("ToggleButton.font", vFont);
		UIManager.put("ProgressBar.font", vFont);
		UIManager.put("DesktopIcon.font", vFont);
		UIManager.put("TitledBorder.font", vFont);
		UIManager.put("Label.font", vFont);
		UIManager.put("List.font", vFont);
		UIManager.put("TabbedPane.font", vFont);
		UIManager.put("MenuBar.font", vFont);
		UIManager.put("Menu.font", vFont);
		UIManager.put("MenuItem.font", vFont);
		UIManager.put("PopupMenu.font", vFont);
		UIManager.put("CheckBoxMenuItem.font", vFont);
		UIManager.put("RadioButtonMenuItem.font", vFont);
		UIManager.put("Spinner.font", vFont);
		UIManager.put("Tree.font", vFont);
		UIManager.put("ToolBar.font", vFont);
		UIManager.put("OptionPane.messageFont", vFont);
		UIManager.put("OptionPane.buttonFont", vFont);
		
		WebLookAndFeel.globalTextFont = vFont;
		WebLookAndFeel.globalTitleFont = vFont;
		WebLookAndFeel.globalTooltipFont = vFont;
		WebLookAndFeel.globalAcceleratorFont = vFont;
		WebLookAndFeel.menuItemAcceleratorFont = vFont;
		WebLookAndFeel.globalAlertFont = vFont;
		WebLookAndFeel.globalMenuFont = vFont;
	}

}

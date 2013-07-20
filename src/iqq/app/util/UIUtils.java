package iqq.app.util;

import iqq.app.IMApp;
import iqq.app.core.IMContext;
import iqq.app.core.IMService;
import iqq.app.service.IMSkinService;
import iqq.im.bean.QQClientType;
import iqq.im.bean.QQDiscuz;
import iqq.im.bean.QQGroup;
import iqq.im.bean.QQStatus;
import iqq.im.bean.QQUser;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.ImageIcon;

import org.apache.commons.lang3.StringUtils;

import com.alee.utils.ImageUtils;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-24
 */
public class UIUtils {
	
	//一些转换工具
	public static class Convert{
		// 16进制字符数组
	    private static char[] hex = new char[] { 
	            '0', '1', '2', '3', '4', '5', '6', '7',
	            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
	    };

		/**
	     * 把字节数组转换成16进制字符串
	     * 
	     * @param b
	     * 			字节数组
	     * @return
	     * 			16进制字符串，每个字节之间空格分隔，头尾无空格
	     */
	    public static String byte2HexString(byte[] b) {
	    	if(b == null)
	    		return "null";
	    	else
	    		return byte2HexString(b, 0, b.length);
	    }

		/**
	     * 把字节数组转换成16进制字符串
	     * 
	     * @param b
	     * 			字节数组
	     * @param offset
	     * 			从哪里开始转换
	     * @param len
	     * 			转换的长度
	     * @return 16进制字符串，每个字节之间空格分隔，头尾无空格
	     */
	    public static String byte2HexString(byte[] b, int offset, int len) {
	    	if(b == null)
	    		return "null";
	    	
	        // 检查索引范围
	        int end = offset + len;
	        if(end > b.length)
	            end = b.length;
	        
	        StringBuffer sb = new StringBuffer();
	        
	        for(int i = offset; i < end; i++) {
	            sb.append(hex[(b[i] & 0xF0) >>> 4])
	            	.append(hex[b[i] & 0xF])
	            	.append(' ');
	        }
	        if(sb.length() > 0)
	            sb.deleteCharAt(sb.length() - 1);
	        return sb.toString();
	    }

		/**
	     * 把字节数组转换成16进制字符串
	     * 
	     * @param b
	     * 			字节数组
	     * @return
	     * 			16进制字符串，每个字节没有空格分隔
	     */
	    public static String byte2HexStringWithoutSpace(byte[] b) {
	    	if(b == null)
	    		return "null";
	    	
	        return byte2HexStringWithoutSpace(b, 0, b.length);
	    }

		/**
	     * 把字节数组转换成16进制字符串
	     * 
	     * @param b
	     * 			字节数组
	     * @param offset
	     * 			从哪里开始转换
	     * @param len
	     * 			转换的长度
	     * @return 16进制字符串，每个字节没有空格分隔
	     */
	    public static String byte2HexStringWithoutSpace(byte[] b, int offset, int len) {
	    	if(b == null)
	    		return "null";
	    	
	        // 检查索引范围
	        int end = offset + len;
	        if(end > b.length)
	            end = b.length;
	        
	        StringBuffer sb = new StringBuffer();
	        
	        for(int i = offset; i < end; i++) {
	            sb.append(hex[(b[i] & 0xF0) >>> 4])
	            	.append(hex[b[i] & 0xF]);
	        }
	        return sb.toString();
	    }

		/**
	     * 转换16进制字符串为字节数组
	     * 
	     * @param s
	     * 			16进制字符串，每个字节由空格分隔
	     * @return 字节数组，如果出错，返回null，如果是空字符串，也返回null
	     */
	    public static byte[] hexString2Byte(String s) {
	        try {
	            s = s.trim();
	            StringTokenizer st = new StringTokenizer(s, " ");
	            byte[] ret = new byte[st.countTokens()];
	            for(int i = 0; st.hasMoreTokens(); i++) {
	                String byteString = st.nextToken();
	                
	                // 一个字节是2个16进制数，如果不对，返回null
	                if(byteString.length() > 2)
	                    return null;
	                
	                ret[i] = (byte)(Integer.parseInt(byteString, 16) & 0xFF);     
	            }
	            return ret;
	        } catch (Exception e) {
	            return null;
	        }
	    }

		/**
	     * 把一个16进制字符串转换为字节数组，字符串没有空格，所以每两个字符
	     * 一个字节
	     * 
	     * @param s
	     * @return
	     */
	    public static byte[] hexString2ByteNoSpace(String s) {
	        int len = s.length();
	        byte[] ret = new byte[len >>> 1];
	        for(int i = 0; i <= len - 2; i += 2) {
	            ret[i >>> 1] = (byte)(Integer.parseInt(s.substring(i, i + 2).trim(), 16) & 0xFF);
	        }
	        return ret;
	    }
	    
	    /**
	     * 把输入流转化一个字符串
	     * @param in 输入流
	     * @return	字符串
	     * @throws IOException
	     */
	    public static String inputStream2String(InputStream in) throws IOException
	    {
	    	ByteArrayOutputStream out = new ByteArrayOutputStream();
	    	byte[] b = new byte[1024];
	    	int len = 0;
	    	while((len=in.read(b))!=-1)
	    		out.write(b, 0, len);
	    	return new String(out.toByteArray());
	    }
	    
	    /**
	     * 把字符串转换为utf8字节数据
	     * @param src		utf8编码原字符串
	     * @return			字节数组
	     */
	    public static byte[] string2Byte(String src)
	    {
	    	byte[] ret = null;
	    	try {
		        ret = src.getBytes("UTF8");
	        } catch (UnsupportedEncodingException e) {
	        	throw new RuntimeException(e);
	        }
	        return ret;
	    }
	    
	    /**
	     * 把utf8字节数据转换为字符串
	     * @param src		utf8编码原字节数组
	     * @return			编码后的字符串
	     */
	    public static String byte2String(byte[] src)
	    {
	    	String ret = null;
	    	try {
		        ret = new String(src, "UTF8");
	        } catch (UnsupportedEncodingException e) {
	        	throw new RuntimeException(e);
	        }
	        return ret;
	    }
	    
	    /**
	     * 把整形数转换为字节数组
	     * @param i
	     * @return
	     */
	    public static byte[] int2Byte(int i)
	    {
	          byte [] b = new byte[4];
	          for(int m=0; m<4; m++, i>>=8) {
	        	  b[m] = (byte) (i & 0x000000FF);	//奇怪, 在C# 整型数是低字节在前  byte[] bytes = BitConverter.GetBytes(i);
	        	  									//而在JAVA里，是高字节在前
	          }
	          return b;
	    }
	}
	
	//Hash 工具
	public static class Hash{
		/**
		 * MD5计算
		 * @param value
		 * @return		HEX编码的32个字符MD5结果
		 */
		public static String md5(String value){
			return Convert.byte2HexStringWithoutSpace(digest("md5", value.getBytes()));
		}
		
		/**
		 * 计算HASH值
		 * @param type		Hash类型
		 * @param bytes		需计算的字节
		 * @return			HASH结果
		 */
		public static byte[] digest(String type, byte[] bytes)
		{
	        try {
		   		MessageDigest dist = MessageDigest.getInstance(type);
			    return dist.digest(bytes);
	        } catch (NoSuchAlgorithmException e) {
		      throw new IllegalArgumentException("Cannot find digest:"+type, e);
	        }
		}
	}
	
	
	/***
	 *  
	 * 加解密工具
	 *
	 * @author solosky <solosky772@qq.com>
	 *
	 */
	public static class Crypt{
		/**
		 * 使用AES算法解密数据，
		 * @param encrypted
		 * @param key			密钥为128位
		 * @param iv			加密矢量，增加加密强度
		 * @return
		 */
		public static byte[] AESDecrypt(byte[] encrypted, byte[] key, byte[] iv) {
			try {
		        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		        IvParameterSpec ivSpec = new IvParameterSpec(iv);
		        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);
		        return cipher.doFinal(encrypted);
	        } catch (Exception e) {
	        	throw new IllegalArgumentException("AESDecrypt failed.", e);
	        }
		}
		
		/**
		 * 使用AES算法解密数据，
		 * @param plain			明文
		 * @param key			密钥为128位
		 * @param iv			加密矢量，增加加密强度
		 * @return
		 */
		public static byte[] AESEncrypt(byte[] plain, byte[] key, byte[] iv) {
			try {
		        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		        IvParameterSpec ivSpec = new IvParameterSpec(iv);
		        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
		        return cipher.doFinal(plain);
	        } catch (Exception e) {
	        	throw new IllegalArgumentException("AESDecrypt failed.", e);
	        }
		}
	}

	// bean 实用小工具
	public static class Bean {
		public static final Map<String, BufferedImage> statusCache = new HashMap<String, BufferedImage>();
		
		public static String getDisplayName(QQUser user){
			if(StringUtils.isNotEmpty(user.getNickname())){
				return user.getNickname();
			}
			return user.getUin() + "";
		}
		
		public static BufferedImage getBIDefaultFace() {
			BufferedImage face = statusCache.get("defaultFace");
			if(face == null){
				IMSkinService skins = IMApp.me().getSerivce(IMService.Type.SKIN);
				face = skins.getBufferedImage("defaultFace");
				statusCache.put("defaultFace", face);
			}
			return face;
		}
		
		public static ImageIcon getDefaultFace() {
			BufferedImage face = statusCache.get("defaultFace");
			if(face == null){
				IMSkinService skins = IMApp.me().getSerivce(IMService.Type.SKIN);
				face = skins.getBufferedImage("defaultFace");
				statusCache.put("defaultFace", face);
			}
			return new ImageIcon(face);
		}
		
		public static BufferedImage getDefaultFace(Object entity) {
			BufferedImage face = null;
			String key = null;
			if(entity instanceof QQUser) {
				key = "defaultFace";
				face = statusCache.get(key);
			} else if(entity instanceof QQGroup){
				key = "defaultGroupFace";
				face = statusCache.get("defaultGroupFace");
			} else if(entity instanceof QQDiscuz) {
				key = "discuzIcon";
				face = statusCache.get("discuzIcon");
			}
			if(face == null){
				IMSkinService skins = IMApp.me().getSerivce(IMService.Type.SKIN);
				face = skins.getBufferedImage(key);
				statusCache.put(key, face);
			}
			return face;
		}
		
		/**
		 * 效率有问题，待优化
		 * 
		 * 性能优化：
		 * 	因为加载用户时，第一时间用户是没有下载头像的
		 *  而每个用户都会调用到了默认的头像和默认头像的状态
		 *  我把默认头像的状态都放入到mapCache中，加快加载和画的时间
		 *  最后，打开群的成员下载也卡，用起来也爽YY了
		 *  如果有朋友有什么优化建议的一定要说啊
		 */
		public static BufferedImage drawStatusFace(QQUser user, IMContext context){
			BufferedImage face = user.getFace();
			IMSkinService skins = context.getSerivce(IMService.Type.SKIN);
			BufferedImage def_face = statusCache.get("defaultFace");
			if(face == null){
				face = def_face;
				if(face == null) {
					face = skins.getBufferedImage("def_face");
					statusCache.put("def_face", face);
				}
			}
			
			QQStatus status = user.getStatus();
			if(status == null){
				status = QQStatus.OFFLINE;
			}
			
			BufferedImage canvas = null;
			if(status == QQStatus.OFFLINE){
				// 看看是不是系统默认头像
				if(face == def_face) {
					canvas = statusCache.get("defaultFace_gray");
					if(canvas ==  null) {
						canvas = createGrayscaleCopy(face);
						statusCache.put("defaultFace_gray", canvas);
					}
				} else {
					canvas = createGrayscaleCopy(face);
				}
				return canvas;
			} else if(user.getClientType() == QQClientType.MOBILE) {
				if(user.getStatus() == QQStatus.ONLINE) {
					// 状态图标加入缓存中，加快频繁更新状态的响应
					BufferedImage statusIcon = cacheStatusIcon("online_phone", "online_phone", skins);
					// 看看是不是系统默认头像
					if(face == def_face) {
						canvas = cacheCanvas(face, statusIcon, "def_online_phone");
					} else {
						canvas = drawStatusFace(face, statusIcon);
					}
					return canvas;
				}
			} else if(user.getClientType() == QQClientType.WEBQQ) {
				if(user.getStatus() == QQStatus.ONLINE) {
					// 状态图标加入缓存中，加快频繁更新状态的响应
					BufferedImage statusIcon = cacheStatusIcon("online_webqq", "online_webqq", skins);
					// 看看是不是系统默认头像
					if(face == def_face) {
						canvas = cacheCanvas(face, statusIcon, "def_online_webqq");
					} else {
						canvas = drawStatusFace(face, statusIcon);
					}
					return canvas;
				}
			}  else if(user.getClientType() == QQClientType.PAD) {
				if(user.getStatus() == QQStatus.ONLINE) {
					// 状态图标加入缓存中，加快频繁更新状态的响应
					BufferedImage statusIcon = cacheStatusIcon("online_pad", "online_pad", skins);
					// 看看是不是系统默认头像
					if(face == def_face) {
						canvas = cacheCanvas(face, statusIcon, "def_online_pad");
					} else {
						canvas = drawStatusFace(face, statusIcon);
					}
					return canvas;
				}
			} 
			
			// 默认以PC在线来画
			// PC 在线不画
			if(user.getStatus() == QQStatus.ONLINE) {
				return face;
			}
			
			// 状态图标加入缓存中，加快频繁更新状态的响应
			BufferedImage statusIcon = cacheStatusIcon(status.getValue(), "status_" + status.getValue(), skins);
			
			// 看看是不是系统默认头像
			if(face == def_face) {
				canvas = cacheCanvas(face, statusIcon, "def_" + status.getValue());
			} else {
				canvas = drawStatusFace(face, statusIcon);
			}
			
			return canvas;
		}
		
		public static BufferedImage cacheCanvas(BufferedImage face, BufferedImage statusIcon, String canvasKey) {
			BufferedImage canvas = statusCache.get(canvasKey);
			if(canvas ==  null) {
				canvas = drawStatusFace(face, statusIcon);
				statusCache.put(canvasKey, canvas);
			}
			return canvas;
		}
		
		public static BufferedImage cacheStatusIcon(String key, String statusRes, IMSkinService skins) {
			BufferedImage statusIcon = statusCache.get(key);
			if(statusIcon == null) {
				statusIcon = skins.getBufferedImage(statusRes);
				statusIcon = ImageUtils.getBufferedImage(statusIcon.getScaledInstance(14, 14, 100));
				statusCache.put(key, statusIcon);
			}
			return statusIcon;
		}
		
		public static BufferedImage drawStatusFace(BufferedImage face, BufferedImage stat) {
			BufferedImage canvas = ImageUtils.createCompatibleImage(face);
			Graphics2D g2d = canvas.createGraphics();
			g2d.drawImage(face, 0, 0, null);
			g2d.drawImage(stat, canvas.getWidth() - stat.getWidth(), canvas.getHeight() - stat.getHeight(), null);
			g2d.dispose();
			return canvas;
		}
		
		public static BufferedImage grayFace(BufferedImage raw){
			BufferedImage gray = new BufferedImage( raw.getWidth(), raw.getHeight(), BufferedImage.TYPE_BYTE_GRAY );  
			ColorConvertOp op = new ColorConvertOp( raw.getColorModel().getColorSpace(), gray.getColorModel().getColorSpace(), null );
			op.filter( raw, gray );
			return gray;
		}

		
		public static BufferedImage createGrayscaleCopy(BufferedImage sourceImg) {
			// return ImageUtils.createGrayscaleCopy(face);
			BufferedImage image = new BufferedImage( sourceImg.getWidth(), sourceImg.getHeight(), BufferedImage.TYPE_BYTE_GRAY );  
			Graphics g = image.getGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, sourceImg.getWidth(), sourceImg.getHeight());
			g.drawImage( sourceImg, 0, 0, null );
			g.dispose();
			return image;
		}
		
		public static int dateToAge(Date birthDay) throws Exception {
	        Calendar cal = Calendar.getInstance();

	        if (cal.before(birthDay)) {
	            throw new IllegalArgumentException(
	                "The birthDay is before Now.It's unbelievable!");
	        }

	        int yearNow = cal.get(Calendar.YEAR);
	        int monthNow = cal.get(Calendar.MONTH);
	        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
	        cal.setTime(birthDay);

	        int yearBirth = cal.get(Calendar.YEAR);
	        int monthBirth = cal.get(Calendar.MONTH);
	        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

	        int age = yearNow - yearBirth;

	        if (monthNow <= monthBirth) {
	            if (monthNow == monthBirth) {
	                //monthNow==monthBirth
	                if (dayOfMonthNow < dayOfMonthBirth) {
	                    age--;
	                } else {
	                    //do nothing
	                }
	            } else {
	                //monthNow>monthBirth
	                age--;
	            }
	        } else {
	            //monthNow<monthBirth
	            //donothing
	        }

	        return age;
	    }
	}
}

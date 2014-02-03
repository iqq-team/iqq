package iqq.app.util;

import iqq.app.IMApp;
import iqq.app.core.IMService.Type;
import iqq.app.service.IMI18nService;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-16
 */
public class I18NUtil {

	/**
	 * Set the locale for the current thread.
	 * 
	 * @param locale
	 *            the locale
	 */
	public static void setLocale(Locale locale) {
		getService().setLocale(locale);
	}

	/**
	 * Get the general local for the current thread, will revert to the default
	 * locale if none specified for this thread.
	 * 
	 * @return the general locale
	 */
	public static Locale getLocale() {
		return getService().getLocale();
	}

	/**
	 * Set the <b>content locale</b> for the current thread.
	 * 
	 * @param locale
	 *            the content locale
	 */
	public static void setContentLocale(Locale locale) {
		getService().setContentLocale(locale);
	}

	/**
	 * Get the content local for the current thread.<br/>
	 * This will revert to {@link #getLocale()} if no value has been defined.
	 * 
	 * @return Returns the content locale
	 */
	public static Locale getContentLocale() {
		return getService().getContentLocale();
	}

	/**
	 * Get the content local for the current thread.<br/>
	 * This will revert <tt>null</tt> if no value has been defined.
	 * 
	 * @return Returns the content locale
	 */
	public static Locale getContentLocaleOrNull() {
		return getService().getContentLocaleOrNull();
	}

	/**
	 * Searches for the nearest locale from the available options. To match any
	 * locale, pass in <tt>null</tt>.
	 * 
	 * @param templateLocale
	 *            the template to search for or <tt>null</tt> to match any
	 *            locale
	 * @param options
	 *            the available locales to search from
	 * @return Returns the best match from the available options, or the
	 *         <tt>null</tt> if all matches fail
	 */
	public static Locale getNearestLocale(Locale templateLocale,
			Set<Locale> options) {
		return getService().getNearestLocale(templateLocale, options);
	}

	/**
	 * Factory method to create a Locale from a <tt>lang_country_variant</tt>
	 * string.
	 * 
	 * @param localeStr
	 *            e.g. fr_FR
	 * @return Returns the locale instance, or the {@link Locale#getDefault()
	 *         default} if the string is invalid
	 */
	public static Locale parseLocale(String localeStr) {
		return getService().parseLocale(localeStr);
	}

	/**
	 * Register a resource bundle.
	 * <p>
	 * This should be the bundle base name eg, spring-surf.messages.errors
	 * <p>
	 * Once registered the messges will be available via getMessage
	 * 
	 * @param bundleBaseName
	 *            the bundle base name
	 */
	public static void registerResourceBundle(Object bundleBaseName) {
		getService().registerResourceBundle(bundleBaseName);
	}

	/**
	 * Get message from registered resource bundle.
	 * 
	 * @param messageKey
	 *            message key
	 * @return localised message string, null if not found
	 */
	public static String getMessage(String messageKey) {
		return getService().getMessage(messageKey);
	}

	/**
	 * Get a localised message string
	 * 
	 * @param messageKey
	 *            the message key
	 * @param locale
	 *            override the current locale
	 * @return the localised message string, null if not found
	 */
	public static String getMessage(String messageKey, Locale locale) {
		return getService().getMessage(messageKey, locale);
	}

	/**
	 * Get a localised message string, parameterized using standard
	 * MessageFormatter.
	 * 
	 * @param messageKey
	 *            message key
	 * @param params
	 *            format parameters
	 * @return the localised string, null if not found
	 */
	public static String getMessage(String messageKey, Object... params) {
		return getService().getMessage(messageKey, params);
	}

	/**
	 * Get a localised message string, parameterized using standard
	 * MessageFormatter.
	 * 
	 * @param messageKey
	 *            the message key
	 * @param locale
	 *            override current locale
	 * @param params
	 *            the localised message string
	 * @return the localaised string, null if not found
	 */
	public static String getMessage(String messageKey, Locale locale,
			Object... params) {
		return getService().getMessage(messageKey, locale, params);
	}

	/**
	 * @return the map of all available messages for the current locale
	 */
	public static Map<String, String> getAllMessages() {
		return getService().getAllMessages();
	}

	/**
	 * @return the map of all available messages for the specified locale
	 */
	public static Map<String, String> getAllMessages(Locale locale) {
		return getService().getAllMessages(locale);
	}

	public static IMI18nService getService() {
		return (IMI18nService) IMApp.me().getSerivce(Type.I18N);
	}
}
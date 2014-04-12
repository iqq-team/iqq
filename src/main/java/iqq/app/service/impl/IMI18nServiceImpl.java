/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Project  : IQQ_V2.1
 * Package  : iqq.app.service.impl
 * File     : IMI18nServiceImpl.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-3-13
 * License  : Apache License 2.0 
 */
package iqq.app.service.impl;

import iqq.app.core.IMContext;
import iqq.app.core.IMException;
import iqq.app.service.IMI18nService;
import iqq.app.util.ResourceUtils;
import iqq.app.util.SettingUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author solosky <solosky772@qq.com>
 * 
 */
public class IMI18nServiceImpl extends AbstractServiceImpl implements
		IMI18nService {

	private static final Logger LOG = LoggerFactory.getLogger(IMI18nServiceImpl.class);

	/**
	 * Thread-local containing the general Locale for the current thread
	 */
	private static ThreadLocal<Locale> threadLocale = new ThreadLocal<Locale>();

	/**
	 * Thread-local containing the content Locale for for the current thread.
	 * This can be used for content and property filtering.
	 */
	private static ThreadLocal<Locale> threadContentLocale = new ThreadLocal<Locale>();
	/**
	 * List of registered bundles
	 */
	private static Set<String> resouceBundleBaseNames = new HashSet<String>();

	/**
	 * Map of loaded bundles by Locale
	 */
	private static Map<Locale, Set<String>> loadedResourceBundles = new HashMap<Locale, Set<String>>();

	/**
	 * Map of cached messaged by Locale
	 */
	private static Map<Locale, Map<String, String>> cachedMessages = new HashMap<Locale, Map<String, String>>();

	/**
	 * Lock objects
	 */
	private ReadWriteLock lock = new ReentrantReadWriteLock();
	private Lock readLock = lock.readLock();
	private Lock writeLock = lock.writeLock();

	private final static ClassLoader WD_CLASS_LOADER;
	private final static String PATH_DOT_FIRST = ".";
	private final static String PATH_DOT_LAST = "~FUCKDOT~";
	static {
		WD_CLASS_LOADER = AccessController
				.doPrivileged(new PrivilegedAction<ClassLoader>() {
					@Override
					public ClassLoader run() {
						return new ClassLoader() {
							@Override
							public URL getResource(String name) {
								// 替换目录点问题
								name = name.replace(PATH_DOT_LAST,
										PATH_DOT_FIRST);
								try {
									URL result = super.getResource(name);
									if (result != null) {
										return result;
									}
									return (new File(name)).toURI().toURL();
								} catch (MalformedURLException ex) {
									return null;
								}
							}
						};
					}
				});
	}

	@Override
	public void init(IMContext context) throws IMException {
		super.init(context);

		this.registerResourceBundle(ResourceUtils.getResource(SettingUtils
				.getString("i18nBundle")));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see iqq.app.service.impl.I18NUtil#setLocale(java.util.Locale)
	 */
	@Override
	public void setLocale(Locale locale) {
		threadLocale.set(locale);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see iqq.app.service.impl.I18NUtil#getLocale()
	 */
	@Override
	public Locale getLocale() {
		Locale locale = threadLocale.get();
		if (locale == null) {
			// Get the default locale
			locale = Locale.getDefault();
		}
		return locale;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see iqq.app.service.impl.I18NUtil#setContentLocale(java.util.Locale)
	 */
	@Override
	public void setContentLocale(Locale locale) {
		threadContentLocale.set(locale);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see iqq.app.service.impl.I18NUtil#getContentLocale()
	 */
	@Override
	public Locale getContentLocale() {
		Locale locale = threadContentLocale.get();
		if (locale == null) {
			// Revert to the normal locale
			locale = getLocale();
		}
		return locale;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see iqq.app.service.impl.I18NUtil#getContentLocaleOrNull()
	 */
	@Override
	public Locale getContentLocaleOrNull() {
		return threadContentLocale.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see iqq.app.service.impl.I18NUtil#getNearestLocale(java.util.Locale,
	 * java.util.Set)
	 */
	@Override
	public Locale getNearestLocale(Locale templateLocale, Set<Locale> options) {
		if (options.isEmpty()) // No point if there are no options
		{
			return null;
		} else if (templateLocale == null) {
			for (Locale locale : options) {
				return locale;
			}
		} else if (options.contains(templateLocale)) // First see if there is an
														// exact match
		{
			return templateLocale;
		}
		// make a copy of the set
		Set<Locale> remaining = new HashSet<Locale>(options);

		// eliminate those without matching languages
		Locale lastMatchingOption = null;
		String templateLanguage = templateLocale.getLanguage();
		if (templateLanguage != null && !templateLanguage.equals("")) {
			Iterator<Locale> iterator = remaining.iterator();
			while (iterator.hasNext()) {
				Locale option = iterator.next();
				if (option != null
						&& !templateLanguage.equals(option.getLanguage())) {
					iterator.remove(); // It doesn't match, so remove
				} else {
					lastMatchingOption = option; // Keep a record of the last
													// match
				}
			}
		}
		if (remaining.isEmpty()) {
			return null;
		} else if (remaining.size() == 1 && lastMatchingOption != null) {
			return lastMatchingOption;
		}

		// eliminate those without matching country codes
		lastMatchingOption = null;
		String templateCountry = templateLocale.getCountry();
		if (templateCountry != null && !templateCountry.equals("")) {
			Iterator<Locale> iterator = remaining.iterator();
			while (iterator.hasNext()) {
				Locale option = iterator.next();
				if (option != null
						&& !templateCountry.equals(option.getCountry())) {
					// It doesn't match language - remove
					// Don't remove the iterator. If it matchs a langage but not
					// the country, returns any matched language
					// iterator.remove();
				} else {
					lastMatchingOption = option; // Keep a record of the last
													// match
				}
			}
		}
		/*
		 * if (remaining.isEmpty()) { return null; } else
		 */
		if (remaining.size() == 1 && lastMatchingOption != null) {
			return lastMatchingOption;
		} else {
			// We have done an earlier equality check, so there isn't a matching
			// variant
			// Also, we know that there are multiple options at this point,
			// either of which will do.

			// This gets any country match (there will be worse matches so we
			// take the last the country match)
			if (lastMatchingOption != null) {
				return lastMatchingOption;
			} else {
				for (Locale locale : remaining) {
					return locale;
				}
			}
		}
		// The logic guarantees that this code can't be called
		throw new RuntimeException("Logic should not allow code to get here.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see iqq.app.service.impl.I18NUtil#parseLocale(java.lang.String)
	 */
	@Override
	public Locale parseLocale(String localeStr) {
		if (localeStr == null) {
			return null;
		}
		Locale locale = Locale.getDefault();

		StringTokenizer t = new StringTokenizer(localeStr, "_");
		int tokens = t.countTokens();
		if (tokens == 1) {
			locale = new Locale(t.nextToken());
		} else if (tokens == 2) {
			locale = new Locale(t.nextToken(), t.nextToken());
		} else if (tokens == 3) {
			locale = new Locale(t.nextToken(), t.nextToken(), t.nextToken());
		}

		return locale;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * iqq.app.service.impl.I18NUtil#registerResourceBundle(java.lang.String)
	 */
	@Override
	public void registerResourceBundle(Object bundleBaseName) {
		try {
			writeLock.lock();
			if (bundleBaseName instanceof URL) {
				URL url = (URL) bundleBaseName;
				String bundleEx = ".properties";
				String bundle = null;
				try {
					bundle = new File(url.toURI()).getAbsolutePath();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				// 去掉扩展名
				bundle = bundle.replace(bundleEx, "");
				// 替换目录点问题
				bundle = bundle.replace(PATH_DOT_FIRST, PATH_DOT_LAST);

				resouceBundleBaseNames.add(bundle);
			} else {
				resouceBundleBaseNames.add(bundleBaseName.toString());
			}

		} finally {
			writeLock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see iqq.app.service.impl.I18NUtil#getMessage(java.lang.String)
	 */
	@Override
	public String getMessage(String messageKey) {
		return getMessage(messageKey, getLocale());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see iqq.app.service.impl.I18NUtil#getMessage(java.lang.String,
	 * java.util.Locale)
	 */
	@Override
	public String getMessage(String messageKey, Locale locale) {
		String message = null;
		Map<String, String> props = getLocaleProperties(locale);
		if (props != null) {
			message = props.get(messageKey);
		}
		return message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see iqq.app.service.impl.I18NUtil#getMessage(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public String getMessage(String messageKey, Object... params) {
		return getMessage(messageKey, getLocale(), params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see iqq.app.service.impl.I18NUtil#getMessage(java.lang.String,
	 * java.util.Locale, java.lang.Object)
	 */
	@Override
	public String getMessage(String messageKey, Locale locale, Object... params) {
		String message = getMessage(messageKey, locale);
		if (message != null && params != null) {
			message = MessageFormat.format(message, params);
		}
		return message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see iqq.app.service.impl.I18NUtil#getAllMessages()
	 */
	@Override
	public Map<String, String> getAllMessages() {
		return getLocaleProperties(getLocale());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see iqq.app.service.impl.I18NUtil#getAllMessages(java.util.Locale)
	 */
	@Override
	public Map<String, String> getAllMessages(Locale locale) {
		return getLocaleProperties(locale);
	}

	/**
	 * Get the messages for a locale.
	 * <p>
	 * Will use cache where available otherwise will load into cache from
	 * bundles.
	 * 
	 * @param locale
	 *            the locale
	 * @return message map
	 */
	private Map<String, String> getLocaleProperties(Locale locale) {
		Set<String> loadedBundles = null;
		Map<String, String> props = null;
		int loadedBundleCount = 0;
		try {
			readLock.lock();
			loadedBundles = loadedResourceBundles.get(locale);
			props = cachedMessages.get(locale);
			loadedBundleCount = resouceBundleBaseNames.size();
		} finally {
			readLock.unlock();
		}

		if (loadedBundles == null) {
			try {
				writeLock.lock();
				loadedBundles = new HashSet<String>();
				loadedResourceBundles.put(locale, loadedBundles);
			} finally {
				writeLock.unlock();
			}
		}

		if (props == null) {
			try {
				writeLock.lock();
				props = new HashMap<String, String>();
				cachedMessages.put(locale, props);
			} finally {
				writeLock.unlock();
			}
		}

		if (loadedBundles.size() != loadedBundleCount) {
			try {
				writeLock.lock();
				for (String resourceBundleBaseName : resouceBundleBaseNames) {
					if (loadedBundles.contains(resourceBundleBaseName) == false) {
						ResourceBundle resourcebundle = ResourceBundle
								.getBundle(resourceBundleBaseName, getLocale(),
										WD_CLASS_LOADER);
						Enumeration<String> enumKeys = resourcebundle.getKeys();
						while (enumKeys.hasMoreElements() == true) {
							String key = enumKeys.nextElement();
							props.put(key, resourcebundle.getString(key));
						}
						loadedBundles.add(resourceBundleBaseName);
					}
				}
			} finally {
				writeLock.unlock();
			}
		}

		return props;
	}
}

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
 * Package  : iqq.app.service
 * File     : I18nService.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-3-13
 * License  : Apache License 2.0 
 */
package iqq.app.service;

import iqq.app.core.IMService;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 
 * 提供国际化支持
 * 
 * @author solosky <solosky772@qq.com>
 * 
 */
public interface IMI18nService extends IMService {

	/**
	 * Set the locale for the current thread.
	 * 
	 * @param locale
	 *            the locale
	 */
	public abstract void setLocale(Locale locale);

	/**
	 * Get the general local for the current thread, will revert to the default
	 * locale if none specified for this thread.
	 * 
	 * @return the general locale
	 */
	public abstract Locale getLocale();

	/**
	 * Set the <b>content locale</b> for the current thread.
	 * 
	 * @param locale
	 *            the content locale
	 */
	public abstract void setContentLocale(Locale locale);

	/**
	 * Get the content local for the current thread.<br/>
	 * This will revert to {@link #getLocale()} if no value has been defined.
	 * 
	 * @return Returns the content locale
	 */
	public abstract Locale getContentLocale();

	/**
	 * Get the content local for the current thread.<br/>
	 * This will revert <tt>null</tt> if no value has been defined.
	 * 
	 * @return Returns the content locale
	 */
	public abstract Locale getContentLocaleOrNull();

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
	public abstract Locale getNearestLocale(Locale templateLocale,
			Set<Locale> options);

	/**
	 * Factory method to create a Locale from a <tt>lang_country_variant</tt>
	 * string.
	 * 
	 * @param localeStr
	 *            e.g. fr_FR
	 * @return Returns the locale instance, or the {@link Locale#getDefault()
	 *         default} if the string is invalid
	 */
	public abstract Locale parseLocale(String localeStr);

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
	public abstract void registerResourceBundle(Object bundleBaseName);

	/**
	 * Get message from registered resource bundle.
	 * 
	 * @param messageKey
	 *            message key
	 * @return localised message string, null if not found
	 */
	public abstract String getMessage(String messageKey);

	/**
	 * Get a localised message string
	 * 
	 * @param messageKey
	 *            the message key
	 * @param locale
	 *            override the current locale
	 * @return the localised message string, null if not found
	 */
	public abstract String getMessage(String messageKey, Locale locale);

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
	public abstract String getMessage(String messageKey, Object... params);

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
	public abstract String getMessage(String messageKey, Locale locale,
			Object... params);

	/**
	 * @return the map of all available messages for the current locale
	 */
	public abstract Map<String, String> getAllMessages();

	/**
	 * @return the map of all available messages for the specified locale
	 */
	public abstract Map<String, String> getAllMessages(Locale locale);
}

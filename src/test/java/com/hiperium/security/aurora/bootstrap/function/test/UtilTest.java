/**
 * Product       : Hiperium Project
 * Lead Architect: Andres Solorzano.
 * Created date  : 08-05-2009 23:30:00
 * 
 * The contents of this file are copyrighted by Andres Solorzano 
 * and it is protected by the license: "GPL V3." You can find a copy of this 
 * license at: http://www.hiperium.com/about/licence.html
 * 
 * Copyright 2014. All rights reserved.
 * 
 */
package com.hiperium.security.aurora.bootstrap.function.test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Properties;

/**
 * 
 * @author Andres Solorzano
 */
public final class UtilTest {

	private static Properties properties;

	static {
		properties = new Properties();
		try {
			properties.load(UtilTest.class.getClassLoader().getResourceAsStream("test.properties"));
		} catch (IOException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}

	private UtilTest() {
		// Nothing to implement.
	}

	public static String getPropertyValueByKey(String propertyKey) {
		return properties.getProperty(propertyKey);
	}
	
	@SuppressWarnings("unchecked")
	public static void injectEnvironmentVariable(String key, String value) throws Exception {
		Class<?> processEnvironment = Class.forName("java.lang.ProcessEnvironment");
		Field unmodifiableMapField = getAccessibleField(processEnvironment, "theUnmodifiableEnvironment");
		Object unmodifiableMap = unmodifiableMapField.get(null);
		injectIntoUnmodifiableMap(key, value, unmodifiableMap);

		Field mapField = getAccessibleField(processEnvironment, "theEnvironment");
		Map<String, String> map = (Map<String, String>) mapField.get(null);
		map.put(key, value);
	}

	private static Field getAccessibleField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
		Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		return field;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void injectIntoUnmodifiableMap(String key, String value, Object map)
			throws ReflectiveOperationException {
		Class unmodifiableMap = Class.forName("java.util.Collections$UnmodifiableMap");
		Field field = getAccessibleField(unmodifiableMap, "m");
		Object obj = field.get(map);
		((Map<String, String>) obj).put(key, value);
	}
}

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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;

import com.hiperium.security.aurora.bootstrap.function.handler.Handler;

/**
 * 
 * @author Andres Solorzano
 */
public class LoadSqlScriptTest {

	/**
	 * Add environment variables (in memory) to simulate the execution of the Lambda
	 * Function in the AWS cloud.
	 * 
	 * @throws Exception
	 */
	@Before
	public void init() throws Exception {
		assertNull(System.getenv("DBHost"));
		assertNull(System.getenv("DBPort"));
		assertNull(System.getenv("DBName"));
		assertNull(System.getenv("DBUser"));
		assertNull(System.getenv("DBPass"));

		UtilTest.injectEnvironmentVariable("DBHost", "localhost");
		UtilTest.injectEnvironmentVariable("DBPort", "5432");
		UtilTest.injectEnvironmentVariable("DBName", "hsecurity");
		UtilTest.injectEnvironmentVariable("DBUser", "hsecurity");
		UtilTest.injectEnvironmentVariable("DBPass", "hsecurity");
	}

	@Test
	public void dbConnectionTest() {
		assertNotNull(System.getenv("DBHost"));
		assertNotNull(System.getenv("DBPort"));
		assertNotNull(System.getenv("DBName"));
		assertNotNull(System.getenv("DBUser"));
		assertNotNull(System.getenv("DBPass"));

		InputStream inputStream = LoadSqlScriptTest.class.getClassLoader().getResourceAsStream("request-event.json");
		assertNotNull(inputStream);
		
		Handler handler = new Handler();
		OutputStream outputStream = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				// Nothing to implement
			}
		};
		handler.handleRequest(inputStream, outputStream, null);
	}

}

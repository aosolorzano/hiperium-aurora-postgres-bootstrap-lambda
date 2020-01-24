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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Objects;

import org.junit.Before;
import org.junit.Test;

import com.hiperium.security.aurora.bootstrap.function.exception.HiperiumFunctionException;
import com.hiperium.security.aurora.bootstrap.function.util.Util;

/**
 * 
 * @author Andres Solorzano
 */
public class GetSecretAccessTest {

	/**
	 * Add environment variables (in memory) to simulate the execution of the Lambda
	 * Function in the AWS cloud.
	 * 
	 * @throws Exception
	 */
	@Before
	public void init() throws Exception {
		assertNull(System.getenv("Secret_ARN"));
		assertNull(System.getenv("Region_Name"));
		UtilTest.injectEnvironmentVariable("Secret_ARN", UtilTest.getPropertyValueByKey("hiperium.secret.arn"));
		UtilTest.injectEnvironmentVariable("Region_Name", UtilTest.getPropertyValueByKey("hiperium.aws.region"));
	}

	@Test
	public void dbConnectionTest() {
		assertNotNull(System.getenv("Secret_ARN"));
		assertNotNull(System.getenv("Region_Name"));
		try {
			String secret = Util.getSecret(System.getenv("Secret_ARN"), System.getenv("Region_Name"));
			assertTrue("Secret value is null", Objects.nonNull(secret));
		} catch (HiperiumFunctionException e) {
			fail(e.getMessage());
		}
	}

}

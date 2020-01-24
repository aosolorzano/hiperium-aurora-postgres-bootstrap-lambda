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
package com.hiperium.security.aurora.bootstrap.function.util;

/**
 * 
 * @author Andres Solorzano
 */
public final class Constants {

	private Constants() {
		// Nothing to implement
	}

	// JSON REQUEST AND RESPONSE PROPERTIES
	public static final String STATUS_CODE_PROPERTY = "statusCode";
	public static final String BODY_PROPERTY = "body";
	public static final String HTTP_200_RESPONSE_MESSAGE = "Message received successfully";
	
	// STRING TEMPLATES
	public static final String CONNECTION_URL_TEMPLATE = "jdbc:postgresql://{0}:{1}/{2}";
}

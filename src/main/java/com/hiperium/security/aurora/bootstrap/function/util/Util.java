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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Objects;

import org.json.JSONObject;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.DecryptionFailureException;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.secretsmanager.model.InternalServiceErrorException;
import com.amazonaws.services.secretsmanager.model.InvalidParameterException;
import com.amazonaws.services.secretsmanager.model.InvalidRequestException;
import com.amazonaws.services.secretsmanager.model.ResourceNotFoundException;
import com.hiperium.security.aurora.bootstrap.function.exception.HiperiumFunctionException;

/**
 * 
 * @author Andres Solorzano
 */
public final class Util {

	private Util() {
		// Nothing to implement.
	}

	public static String getRequestEventAsString(InputStream inputStream) throws IOException {
		int letter;
		String eventObject = "";
		while ((letter = inputStream.read()) > -1) {
			char inputChar = (char) letter;
			eventObject += inputChar;
		}
		return eventObject;
	}

	public static void setNotAcceptableInResponse(OutputStream outputStream, String messageDetail) throws IOException {
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put(Constants.STATUS_CODE_PROPERTY, 406);
		jsonResponse.put(Constants.BODY_PROPERTY, messageDetail);
		outputStream.write(jsonResponse.toString().getBytes(StandardCharsets.UTF_8));
	}

	public static void setOkInResponse(OutputStream outputStream) throws IOException {
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put(Constants.STATUS_CODE_PROPERTY, 200);
		jsonResponse.put(Constants.BODY_PROPERTY, Constants.HTTP_200_RESPONSE_MESSAGE);
		outputStream.write(jsonResponse.toString().getBytes(StandardCharsets.UTF_8));
	}

	public static void createBatchFromStatements(String[] statements, Statement statement) throws SQLException {
		for (int i = 0; i < statements.length; i++) {
			// we ensure that there is no spaces before or after the statement string
			// in order to not execute empty SQL statements
			if (!statements[i].trim().equals("")) {
				statement.addBatch(statements[i]);
			}
		}
	}

	public static StringBuffer getDBScriptBuffered() throws IOException {
		InputStream sqlStream = Util.class.getClassLoader().getResourceAsStream("insert_data.sql");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(sqlStream));
		String line = null;
		StringBuffer stringBuffer = new StringBuffer();
		while (Objects.nonNull(line = bufferedReader.readLine())) {
			stringBuffer.append(line);
		}
		bufferedReader.close();
		return stringBuffer;
	}

	public static String getSecret(String secretName, String region) throws HiperiumFunctionException {
		String secret = null;
		AWSSecretsManager client = AWSSecretsManagerClientBuilder.standard().withRegion(region).build();
		GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId(secretName);
		GetSecretValueResult getSecretValueResult = null;

		try {
			getSecretValueResult = client.getSecretValue(getSecretValueRequest);
		} catch (DecryptionFailureException e) {
			// Secrets Manager can't decrypt the protected secret text using the provided
			// KMS key.
			throw new HiperiumFunctionException(e.getErrorMessage(), e);
		} catch (InternalServiceErrorException e) {
			// An error occurred on the server side.
			throw new HiperiumFunctionException(e.getErrorMessage(), e);
		} catch (InvalidParameterException e) {
			// You provided an invalid value for a parameter.
			throw new HiperiumFunctionException(e.getErrorMessage(), e);
		} catch (InvalidRequestException e) {
			// You provided a parameter value that is not valid for the current state of the
			// resource.
			throw new HiperiumFunctionException(e.getErrorMessage(), e);
		} catch (ResourceNotFoundException e) {
			// We can't find the resource that you asked for.
			throw new HiperiumFunctionException(e.getErrorMessage(), e);
		}

		// Decrypts secret using the associated KMS CMK.
		// Depending on whether the secret is a string or binary, one of these fields
		// will be populated.
		if (getSecretValueResult.getSecretString() != null) {
			secret = getSecretValueResult.getSecretString();
		} else {
			secret = new String(Base64.getDecoder().decode(getSecretValueResult.getSecretBinary()).array());
		}

		return secret;
	}
}

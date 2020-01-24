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
package com.hiperium.security.aurora.bootstrap.function.handler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.hiperium.security.aurora.bootstrap.function.util.Constants;
import com.hiperium.security.aurora.bootstrap.function.util.Util;

/**
 * 
 * @author Andres Solorzano
 */
public class Handler implements RequestStreamHandler {

	private static final Logger LOGGER = LogManager.getLogger(Handler.class);

	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) {
		try {
			JSONObject request = new JSONObject(Util.getRequestEventAsString(inputStream));
			LOGGER.debug("REQUEST EVENT: \n" + request);

			Connection connection = null;
			try {
				// We use ";" as a delimiter for each statement
				// and then we are sure to have well formed SQL statements
				StringBuffer stringBuffer = Util.getDBScriptBuffered();
				String[] statements = stringBuffer.toString().split(";");

				// Connect to DB and add batches for every statement existing in the SQL script
				String connectionURL = MessageFormat.format(Constants.CONNECTION_URL_TEMPLATE, System.getenv("DBHost"),
						System.getenv("DBPort"), System.getenv("DBName"));
				connection = DriverManager.getConnection(connectionURL, System.getenv("DBUser"),
						System.getenv("DBPass"));
				Statement statement = connection.createStatement();
				Util.createBatchFromStatements(statements, statement);

				// Execute all batches added at once
				connection.setAutoCommit(false);
				int counts[] = statement.executeBatch();
				connection.commit();

				LOGGER.info(counts.length + " inserts excecuted successfuly.");
				connection.close();

			} catch (FileNotFoundException e) {
				LOGGER.error(e.getMessage());
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			} catch (SQLException e) {
				LOGGER.error(e.getMessage());
				if (Objects.nonNull(connection)) {
					try {
						connection.rollback();
					} catch (SQLException e1) {
						LOGGER.error(e1.getMessage());
					}
				}
			}

			// SEND A RESPONSE RESPONSE
			Util.setOkInResponse(outputStream);

		} catch (JSONException | IOException e) {
			LOGGER.error("ERROR: " + e.getMessage());
			try {
				Util.setNotAcceptableInResponse(outputStream, e.getMessage());
			} catch (IOException e1) {
				LOGGER.error("ERROR: " + e1.getMessage());
			}
		}
	}

}

/*
 * #%L
 * P6Spy
 * %%
 * Copyright (C) 2002 - 2014 P6Spy
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.p6spy.engine.common;


import org.nustaq.serialization.FSTConfiguration;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: kataev
 * Date: 10.06.14
 */
public class PersistentStatementInformation extends StatementInformation {

	private Map<Integer, Serializable> parameterValues = new HashMap<Integer, Serializable>(10);

	private ArrayList<Map<Integer, Serializable>> batchParametersValues;

	private boolean batch;

	private Long id;

	static FSTConfiguration fstConfiguration = FSTConfiguration.createDefaultConfiguration();

	static {
		fstConfiguration.registerClass(ArrayList.class, HashMap.class, Integer.class, Timestamp.class, Date.class, Long.class);
	}

	public PersistentStatementInformation(ConnectionInformation connectionInformation) {
		super(connectionInformation);
	}

	public PersistentStatementInformation(PersistentStatementInformation original) {
		super(original.connectionInformation);
		setStatementQuery(original.getStatementQuery());
		parameterValues.putAll(original.getParameterValues());
		if (original.isBatch()) {
			batch = true;
			batchParametersValues = new ArrayList<Map<Integer, Serializable>>(original.getBatchParametersValues());
		}
	}

	public byte[] getSerializedValues() {
		return fstConfiguration.asByteArray(parameterValues);
	}

	public byte[] getSerializedBatchValues() {
		if (batchParametersValues == null) {
			throw new RuntimeException("Batch parameters not found");
		}

		return fstConfiguration.asByteArray(batchParametersValues);
	}

	/**
	 * Records the value of a parameter.
	 *
	 * @param position the position of the parameter (starts with 1 not 0)
	 * @param value    the value of the parameter
	 */
	public void setParameterValue(final int position, final Object value) {
		Serializable parameterValue = null;

		if (value != null) {
			if (!(value instanceof Serializable)) {
				parameterValue = trySerializable(value);
			} else {
				parameterValue = (Serializable) value;
			}
		}

		parameterValues.put(position, parameterValue);
	}

	private Serializable trySerializable(final Object value) {
		if (value instanceof Blob) {
			try {
				Blob blob = (Blob) value;
				byte[] bbuf = blob.getBytes(1, (int) blob.length());
				return new SerialBlob(bbuf);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new IllegalArgumentException("Illegal Blob parameter " + e.getMessage());
			}
		} else if (value instanceof Clob) {
			try {
				Clob clob = (Clob) value;
				int len = (int) clob.length();
				char[] cbuf = new char[len];
				int read;
				int offset = 0;

				try (Reader charStream = clob.getCharacterStream()) {
					if (charStream == null) {
						throw new SQLException("Invalid Clob object. The call to getCharacterStream " +
								"returned null which cannot be serialized.");
					}

					// Note: get an ASCII stream in order to null-check it,
					// even though we don't do anything with it.
					try (InputStream asciiStream = clob.getAsciiStream()) {
						if (asciiStream == null) {
							throw new SQLException("Invalid Clob object. The call to getAsciiStream " +
									"returned null which cannot be serialized.");
						}
					}

					try (Reader reader = new BufferedReader(charStream)) {
						do {
							read = reader.read(cbuf, offset, (len - offset));
							offset += read;
						} while (read > 0);
					}
				} catch (java.io.IOException ex) {
					throw new SerialException("SerialClob: " + ex.getMessage());
				}

				return new SerialClob(cbuf);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new IllegalArgumentException("Illegal Clob parameter " + e.getMessage());
			}
		} else {
			// TODO look at more cases
			throw new IllegalArgumentException("Non serializable parameter value " + value);
		}
	}

	public void addBatch() {
		batch = true;
		if (batchParametersValues == null) {
			batchParametersValues = new ArrayList<Map<Integer, Serializable>>();
		}

		if (!parameterValues.isEmpty()) {
			batchParametersValues.add(new HashMap<Integer, Serializable>(parameterValues));
			parameterValues.clear();
		}
	}

	public Map<Integer, Serializable> getParameterValues() {
		return parameterValues;
	}

	public ArrayList<Map<Integer, Serializable>> getBatchParametersValues() {
		return batchParametersValues;
	}

	public boolean isBatch() {
		return batch;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void clearParameters() {
		parameterValues.clear();
		if (batchParametersValues != null) {
			batchParametersValues.clear();
		}
	}

	@SuppressWarnings("unchecked")
	public static void fillParameters(PersistentStatementInformation emptyStatement, byte[] params, boolean batch) throws Exception {
		if (batch) {
			emptyStatement.batch = true;
			emptyStatement.batchParametersValues = (ArrayList<Map<Integer, Serializable>>) fstConfiguration.asObject(params);
		} else {
			emptyStatement.parameterValues = (Map<Integer, Serializable>) fstConfiguration.asObject(params);
		}
	}

	@Override
	public String toString() {
		return "PersistentStatementInformation{" +
				"id=" + id +
				", connectionId=" + getConnectionId() +
				", sql=" + getSql() +
				", batch=" + batch +
				'}';
	}
}

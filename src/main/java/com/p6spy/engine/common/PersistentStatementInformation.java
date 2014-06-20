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

import de.ruedigermoeller.serialization.FSTConfiguration;
import de.ruedigermoeller.serialization.FSTObjectInput;
import de.ruedigermoeller.serialization.FSTObjectOutput;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * User: kataev
 * Date: 10.06.14
 */
public class PersistentStatementInformation extends StatementInformation {

    private Map<Integer, Serializable> parameterValues = new HashMap<Integer, Serializable>();

    private ArrayList<Map<Integer, Serializable>> batchParametersValues;

    private boolean batch;

    private Long id;

    static FSTConfiguration fstConfiguration = FSTConfiguration.createDefaultConfiguration();

    public PersistentStatementInformation(ConnectionInformation connectionInformation) {
        super(connectionInformation);
    }

    public byte[] getSerializedValues() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        FSTObjectOutput out = fstConfiguration.getObjectOutput(stream);
        try {
            out.writeObject(parameterValues, HashMap.class, Integer.class, Serializable.class);
            out.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot serialize statement parameters");
        }

        return stream.toByteArray();
    }

    public byte[] getSerializedBatchValues() {
        if (batchParametersValues == null) {
            throw new RuntimeException("Batch parameters not found");
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        FSTObjectOutput out = fstConfiguration.getObjectOutput(stream);
        try {
            out.writeObject(batchParametersValues, ArrayList.class, HashMap.class, Integer.class);
            out.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot serialize batch statement parameters");
        }

        return stream.toByteArray();
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
                return new SerialBlob((Blob) value);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("Illegal Blob parameter " + e.getMessage());
            }
        } else if (value instanceof Clob) {
            try {
                return new SerialClob((Clob) value);
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

        batchParametersValues.add(new HashMap<Integer, Serializable>(parameterValues));
        parameterValues.clear();
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
        FSTObjectInput input = fstConfiguration.getObjectInput(params);

        if (batch) {
            emptyStatement.batch = true;
            emptyStatement.batchParametersValues = (ArrayList<Map<Integer, Serializable>>) input.readObject(ArrayList.class, HashMap.class, Integer.class, Serializable.class);
        } else {
            emptyStatement.parameterValues = (Map<Integer, Serializable>) input.readObject(HashMap.class, Integer.class, Serializable.class);
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

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
package com.p6spy.engine.ha;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.common.PersistentStatementInformation;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: kataev
 * Date: 20.06.14
 */
public class PersistentStatementInformationTest {

    private PersistentStatementInformation statementInformation;
    static ConnectionInformation connectionInformation = new ConnectionInformation();

    @Before
    public void setup() {
        statementInformation = new PersistentStatementInformation(connectionInformation);
    }

    @Test
    public void noDuplicateIndexValues() {
        List<Object> values = new ArrayList<Object>();
        values.add("Homer");
        values.add(43);

        List<Object> values2 = new ArrayList<Object>();
        values2.add("Lisa");
        values2.add(10);

        for (int i = 0; i < values.size(); i++) {
            statementInformation.setParameterValue(i + 1, values.get(i));
        }

        for (int i = 0; i < values2.size(); i++) {
            statementInformation.setParameterValue(i + 1, values2.get(i));
        }

        assertEquals(2, statementInformation.getParameterValues().size());
    }


    @Test
    public void parametersSerialization() {
        List<Object> values = new ArrayList<Object>();
        values.add("Homer");
        values.add(43);

        for (int i = 0; i < values.size(); i++) {
            statementInformation.setParameterValue(i + 1, values.get(i));
        }

        byte[] params = statementInformation.getSerializedValues();

        assertTrue(params.length > 0);
    }

    @Test
    public void addBatchParameters() {
        List<Object> values = new ArrayList<Object>();
        values.add("Homer");
        values.add(43);

        List<Object> values2 = new ArrayList<Object>();
        values2.add("Lisa");
        values2.add(10);

        for (int i = 0; i < values.size(); i++) {
            statementInformation.setParameterValue(i + 1, values.get(i));
        }

        statementInformation.addBatch();

        for (int i = 0; i < values2.size(); i++) {
            statementInformation.setParameterValue(i + 1, values2.get(i));
        }

        statementInformation.addBatch();

        assertEquals(2, statementInformation.getBatchParametersValues().size());
        assertEquals(2, statementInformation.getBatchParametersValues().get(0).size());
        assertEquals(2, statementInformation.getBatchParametersValues().get(1).size());
    }

    @Test
    public void batchParametersSerialization() {
        List<Object> values = new ArrayList<Object>();
        values.add("Homer");
        values.add(43);

        List<Object> values2 = new ArrayList<Object>();
        values2.add("Lisa");
        values2.add(10);

        for (int i = 0; i < values.size(); i++) {
            statementInformation.setParameterValue(i + 1, values.get(i));
        }

        statementInformation.addBatch();

        for (int i = 0; i < values2.size(); i++) {
            statementInformation.setParameterValue(i + 1, values2.get(i));
        }

        statementInformation.addBatch();

        byte[] batchParams = statementInformation.getSerializedBatchValues();

        assertTrue(batchParams.length > 0);
    }


    @Test
    public void parametersDeserialization() throws Exception {
        List<Object> values = new ArrayList<Object>();
        values.add("Homer");
        values.add(43);

        for (int i = 0; i < values.size(); i++) {
            statementInformation.setParameterValue(i + 1, values.get(i));
        }

        byte[] params = statementInformation.getSerializedValues();

        PersistentStatementInformation statementInformation1 = new PersistentStatementInformation(connectionInformation);
        PersistentStatementInformation.fillParameters(statementInformation1, params, false);

        assertEquals("Homer", statementInformation1.getParameterValues().get(1));
        assertEquals(43, statementInformation1.getParameterValues().get(2));
    }

    @Test
    public void batchParametersDeserialization() throws Exception {
        List<Object> values = new ArrayList<Object>();
        values.add("Homer");
        values.add(43);

        List<Object> values2 = new ArrayList<Object>();
        values2.add("Lisa");
        values2.add(10);

        for (int i = 0; i < values.size(); i++) {
            statementInformation.setParameterValue(i + 1, values.get(i));
        }

        statementInformation.addBatch();

        for (int i = 0; i < values2.size(); i++) {
            statementInformation.setParameterValue(i + 1, values2.get(i));
        }

        statementInformation.addBatch();

        byte[] batchParams = statementInformation.getSerializedBatchValues();

        PersistentStatementInformation statementInformation1 = new PersistentStatementInformation(connectionInformation);
        PersistentStatementInformation.fillParameters(statementInformation1, batchParams, true);

        assertEquals("Homer", statementInformation1.getBatchParametersValues().get(0).get(1));
        assertEquals(43, statementInformation1.getBatchParametersValues().get(0).get(2));
        assertEquals("Lisa", statementInformation1.getBatchParametersValues().get(1).get(1));
        assertEquals(10, statementInformation1.getBatchParametersValues().get(1).get(2));
    }
}

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
import com.p6spy.engine.proxy.GenericInvocationHandler;
import com.p6spy.engine.proxy.MethodNameMatcher;

import java.sql.PreparedStatement;

/**
 * User: kataev
 * Date: 10.06.14
 */
public class P6HaPreparedStatementInvocationHandler extends GenericInvocationHandler<PreparedStatement> {
    /**
     * Creates a new invocation handler for the given object.
     *
     * @param underlying The object being proxied
     */
    public P6HaPreparedStatementInvocationHandler(PreparedStatement underlying,
                                                  ConnectionInformation connectionInformation,
                                                  String query) {
        super(underlying);

        PersistentStatementInformation statementInformation = new PersistentStatementInformation(connectionInformation);
        statementInformation.setStatementQuery(query);

        P6HaPreparedStatementExecuteDelegate executeDelegate = new P6HaPreparedStatementExecuteDelegate(statementInformation);
	    P6HaPreparedStatementExecuteBatchDelegate executeBatchDelegate = new P6HaPreparedStatementExecuteBatchDelegate(statementInformation);
	    P6HaPreparedStatementAddBatchDelegate addBatchDelegate = new P6HaPreparedStatementAddBatchDelegate(statementInformation);
        P6HaPreparedStatementSetParameterDelegate setParameterDelegate = new P6HaPreparedStatementSetParameterDelegate(statementInformation);

        addDelegate(
                new MethodNameMatcher("executeBatch"),
		        executeBatchDelegate
        );
        addDelegate(
                new MethodNameMatcher("addBatch"),
                addBatchDelegate
        );
        addDelegate(
                new MethodNameMatcher("execute"),
                executeDelegate
        );
        addDelegate(
                new MethodNameMatcher("executeUpdate"),
                executeDelegate
        );
        addDelegate(
                new MethodNameMatcher("set*"),
                setParameterDelegate
        );
    }
}

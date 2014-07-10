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
import com.p6spy.engine.proxy.GenericInvocationHandler;
import com.p6spy.engine.proxy.MethodNameMatcher;

import java.sql.Connection;

/**
 * User: kataev
 * Date: 10.06.14
 */
public class P6HaConnectionInvocationHandler extends GenericInvocationHandler<Connection> {
    /**
     * Creates a new invocation handler for the given object.
     *
     * @param underlying The object being proxied
     */
    public P6HaConnectionInvocationHandler(Connection underlying) {
        super(underlying);

        ConnectionInformation connectionInformation = new ConnectionInformation();

        P6HaConnectionCommitDelegate commitDelegate = new P6HaConnectionCommitDelegate(connectionInformation);
        P6HaConnectionRollbackDelegate rollbackDelegate = new P6HaConnectionRollbackDelegate(connectionInformation);
        P6HaConnectionPrepareStatementDelegate prepareStatementDelegate = new P6HaConnectionPrepareStatementDelegate(connectionInformation);
        P6HaConnectionCreateStatementDelegate createStatementDelegate = new P6HaConnectionCreateStatementDelegate(connectionInformation);
        P6HaConnectionAutoCommitDelegate autoCommitDelegate = new P6HaConnectionAutoCommitDelegate(connectionInformation);
        P6HaConnectionCloseDelegate closeDelegate = new P6HaConnectionCloseDelegate(connectionInformation);
        // prepare call ?

        addDelegate(
                new MethodNameMatcher("commit"),
                commitDelegate
        );
        addDelegate(
                new MethodNameMatcher("rollback"),
                rollbackDelegate
        );

        // add delegates to return proxies for other methods
        addDelegate(
                new MethodNameMatcher("prepareStatement"),
                prepareStatementDelegate
        );

        addDelegate(
                new MethodNameMatcher("createStatement"),
                createStatementDelegate
        );

        addDelegate(
                new MethodNameMatcher("setAutoCommit"),
                autoCommitDelegate
        );

        addDelegate(
                new MethodNameMatcher("close"),
                closeDelegate
        );
    }
}

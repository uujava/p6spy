package com.p6spy.engine.ha;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.proxy.GenericInvocationHandler;

import java.sql.Statement;

/**
 * User: kataev
 * Date: 10.06.14
 */
public class P6HaStatementInvocationHandler extends GenericInvocationHandler<Statement> {
    /**
     * Creates a new invocation handler for the given object.
     *
     * @param underlying The object being proxied
     */
    public P6HaStatementInvocationHandler(Statement underlying, final ConnectionInformation connectionInformation) {
        super(underlying);

        //TODO
    }
}

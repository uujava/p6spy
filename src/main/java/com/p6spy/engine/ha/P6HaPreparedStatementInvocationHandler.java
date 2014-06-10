package com.p6spy.engine.ha;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.proxy.GenericInvocationHandler;

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

    }
}

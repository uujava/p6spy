package com.p6spy.engine.ha;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.proxy.ProxyFactory;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;

/**
 * User: kataev
 * Date: 10.06.14
 */
public class P6HaConnectionPrepareStatementDelegate extends P6HaConnectionCreateStatementDelegate {

    public P6HaConnectionPrepareStatementDelegate(ConnectionInformation connectionInformation) {
        super(connectionInformation);
    }

    @Override
    public Object invoke(final Object proxy, final Object underlying, final Method method, final Object[] args) throws Throwable {
        PreparedStatement statement = (PreparedStatement) method.invoke(underlying, args);
        String query = (String) args[0];
        P6HaPreparedStatementInvocationHandler invocationHandler = new P6HaPreparedStatementInvocationHandler(statement,
                getConnectionInformation(), query);
        return ProxyFactory.createProxy(statement, invocationHandler);
    }
}

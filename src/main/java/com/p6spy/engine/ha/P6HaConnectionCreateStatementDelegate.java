package com.p6spy.engine.ha;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.proxy.Delegate;
import com.p6spy.engine.proxy.ProxyFactory;

import java.lang.reflect.Method;
import java.sql.Statement;

/**
 * User: kataev
 * Date: 10.06.14
 */
public class P6HaConnectionCreateStatementDelegate implements Delegate {

    private final ConnectionInformation connectionInformation;

    public P6HaConnectionCreateStatementDelegate(ConnectionInformation connectionInformation) {
        this.connectionInformation = connectionInformation;
    }

    @Override
    public Object invoke(Object proxy, Object underlying, Method method, Object[] args) throws Throwable {
        Statement statement = (Statement) method.invoke(underlying, args);
        P6HaStatementInvocationHandler invocationHandler = new P6HaStatementInvocationHandler(statement, connectionInformation);
        return ProxyFactory.createProxy(statement, invocationHandler);
    }

    protected ConnectionInformation getConnectionInformation() {
        return connectionInformation;
    }
}

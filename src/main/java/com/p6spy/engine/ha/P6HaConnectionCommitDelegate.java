package com.p6spy.engine.ha;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.proxy.Delegate;

import java.lang.reflect.Method;

/**
 * User: kataev
 * Date: 10.06.14
 */
public class P6HaConnectionCommitDelegate implements Delegate {

    private final ConnectionInformation connectionInformation;

    public P6HaConnectionCommitDelegate(ConnectionInformation connectionInformation) {
        this.connectionInformation = connectionInformation;
    }

    @Override
    public Object invoke(Object proxy, Object underlying, Method method, Object[] args) throws Throwable {
        // invoke original method
        return method.invoke(underlying, args);
    }
}

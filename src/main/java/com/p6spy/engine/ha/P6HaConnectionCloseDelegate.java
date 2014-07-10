package com.p6spy.engine.ha;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.proxy.Delegate;

import java.lang.reflect.Method;

/**
 * User: kataev
 * Date: 10.07.14
 */
public class P6HaConnectionCloseDelegate implements Delegate {

    private final ConnectionInformation connectionInformation;

    private final HaStatementExecuteListener executeListener;

    public P6HaConnectionCloseDelegate(ConnectionInformation connectionInformation) {
        this.connectionInformation = connectionInformation;
        this.executeListener = P6HaOptions.getActiveInstance().getDbExecuteListener();
    }

    @Override
    public Object invoke(Object proxy, Object underlying, Method method, Object[] args) throws Throwable {
        Object res = method.invoke(underlying, args);
        if (executeListener != null) {
            executeListener.onClose(connectionInformation);
        }
        return res;
    }
}

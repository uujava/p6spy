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

import com.p6spy.engine.common.PersistentStatementInformation;
import com.p6spy.engine.proxy.Delegate;

import java.lang.reflect.Method;
import java.sql.Statement;

/**
 * User: kataev
 * Date: 11.06.14
 */
public class P6HaPreparedStatementSetParameterDelegate implements Delegate {
    private final PersistentStatementInformation statementInformation;

    public P6HaPreparedStatementSetParameterDelegate(PersistentStatementInformation statementInformation) {
        this.statementInformation = statementInformation;
    }

    @Override
    public Object invoke(Object proxy, Object underlying, Method method, Object[] args) throws Throwable {
        // ignore calls to any methods defined on the Statement interface!
        if (!Statement.class.equals(method.getDeclaringClass())) {
            int position = (Integer) args[0];
            Object value = null;
            if (!method.getName().equals("setNull") && args.length > 1) {
                value = args[1];
            }
            statementInformation.setParameterValue(position, value);
        }
        return method.invoke(underlying, args);
    }
}

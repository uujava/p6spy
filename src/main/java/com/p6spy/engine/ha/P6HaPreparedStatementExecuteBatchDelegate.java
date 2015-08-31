/*
 * #%L
 * P6Spy
 * %%
 * Copyright (C) 2002 - 2015 P6Spy
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
import java.sql.ResultSet;

import static java.lang.Boolean.TRUE;

/**
 * User: kataev
 * Date: 31.08.15
 */
public class P6HaPreparedStatementExecuteBatchDelegate implements Delegate {
	private final PersistentStatementInformation statementInformation;

	private final HaStatementExecuteListener executeListener;

	public P6HaPreparedStatementExecuteBatchDelegate(PersistentStatementInformation statementInformation) {
		this.statementInformation = statementInformation;
		this.executeListener = P6HaOptions.getActiveInstance().getDbExecuteListener();
	}

	@Override
	public Object invoke(Object proxy, Object underlying, Method method, Object[] args) throws Throwable {
		Object result = method.invoke(underlying, args);

		if (executeListener != null && result != null && !(result instanceof ResultSet) && !TRUE.equals(result)) {
			executeListener.onExecute(new PersistentStatementInformation(statementInformation));
			statementInformation.clearParameters();
		}
		return result;
	}
}

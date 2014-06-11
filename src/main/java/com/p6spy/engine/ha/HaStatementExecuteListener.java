package com.p6spy.engine.ha;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.common.PersistentStatementInformation;
import com.p6spy.engine.common.StatementParameter;

import java.util.EventListener;
import java.util.List;

/**
 * User: kataev
 * Date: 11.06.14
 */
public interface HaStatementExecuteListener extends EventListener {

    void onAutoCommit(ConnectionInformation connectionInformation);

    void onExecute(PersistentStatementInformation statementInformation);

    void onExecuteBatch(PersistentStatementInformation statementInformation, List<List<StatementParameter>> allBatchParams);

    void onCommit(ConnectionInformation connectionInformation);

    void onRollback(ConnectionInformation connectionInformation);
}

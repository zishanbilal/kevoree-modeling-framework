package org.kevoree.modeling.api.util;

import org.kevoree.modeling.api.Callback;
import org.kevoree.modeling.api.KObject;
import org.kevoree.modeling.api.KOperation;
import org.kevoree.modeling.api.meta.MetaOperation;
import org.kevoree.modeling.api.msg.KMessage;

public interface KOperationManager {

    void registerOperation(MetaOperation operation, KOperation callback, KObject target);

    void call(KObject source, MetaOperation operation, Object[] param, Callback<Object> callback);

    void operationEventReceived(KMessage operationEvent);
}

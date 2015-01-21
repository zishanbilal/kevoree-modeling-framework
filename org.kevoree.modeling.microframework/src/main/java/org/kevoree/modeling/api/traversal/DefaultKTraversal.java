package org.kevoree.modeling.api.traversal;

import org.kevoree.modeling.api.Callback;
import org.kevoree.modeling.api.KObject;
import org.kevoree.modeling.api.KTask;
import org.kevoree.modeling.api.abs.AbstractKTaskWrapper;
import org.kevoree.modeling.api.meta.MetaAttribute;
import org.kevoree.modeling.api.meta.MetaReference;
import org.kevoree.modeling.api.trace.TraceSequence;
import org.kevoree.modeling.api.traversal.actions.*;

/**
 * Created by duke on 18/12/14.
 */
public class DefaultKTraversal implements KTraversal {

    private KObject[] _initObjs;

    private KTraversalAction _initAction;

    private KTraversalAction _lastAction;

    private boolean _terminated = false;

    public DefaultKTraversal(KObject p_root, MetaReference p_ref) {
        _initAction = new KTraverseAction(p_ref);
        _initObjs = new KObject[1];
        _initObjs[0] = p_root;
        _lastAction = _initAction;
    }

    @Override
    public KTraversal traverse(MetaReference p_metaReference) {
        if (_terminated) {
            throw new RuntimeException("Promise is terminated by the call of then method, please create another promise");
        }
        KTraverseAction tempAction = new KTraverseAction(p_metaReference);
        _lastAction.chain(tempAction);
        _lastAction = tempAction;
        return this;
    }

    @Override
    public KTraversal withAttribute(MetaAttribute p_attribute, Object p_expectedValue) {
        if (_terminated) {
            throw new RuntimeException("Promise is terminated by the call of then method, please create another promise");
        }
        KFilterAttributeAction tempAction = new KFilterAttributeAction(p_attribute, p_expectedValue);
        _lastAction.chain(tempAction);
        _lastAction = tempAction;
        return this;
    }

    @Override
    public KTraversal withoutAttribute(MetaAttribute p_attribute, Object p_expectedValue) {
        if (_terminated) {
            throw new RuntimeException("Promise is terminated by the call of then method, please create another promise");
        }
        KFilterNotAttributeAction tempAction = new KFilterNotAttributeAction(p_attribute, p_expectedValue);
        _lastAction.chain(tempAction);
        _lastAction = tempAction;
        return this;
    }

    @Override
    public KTraversal filter(KTraversalFilter p_filter) {
        if (_terminated) {
            throw new RuntimeException("Promise is terminated by the call of then method, please create another promise");
        }
        KFilterAction tempAction = new KFilterAction(p_filter);
        _lastAction.chain(tempAction);
        _lastAction = tempAction;
        return this;
    }

    @Override
    public KTraversal reverse(MetaReference p_metaReference) {
        if (_terminated) {
            throw new RuntimeException("Promise is terminated by the call of then method, please create another promise");
        }
        KReverseAction tempAction = new KReverseAction(p_metaReference);
        _lastAction.chain(tempAction);
        _lastAction = tempAction;
        return this;
    }

    @Override
    public KTraversal parents() {
        if (_terminated) {
            throw new RuntimeException("Promise is terminated by the call of then method, please create another promise");
        }
        KParentsAction tempAction = new KParentsAction();
        _lastAction.chain(tempAction);
        _lastAction = tempAction;
        return this;
    }

    @Override
    public void then(Callback<KObject[]> callback) {
        _terminated = true;
        //set the terminal leaf action
        _lastAction.chain(new KFinalAction(callback));
        //execute the first element of the chain of actions
        _initAction.execute(_initObjs);
    }

    @Override
    public void map(MetaAttribute attribute, Callback<Object[]> callback) {
        _terminated = true;
        //set the terminal leaf action
        _lastAction.chain(new KMapAction(attribute, callback));
        //execute the first element of the chain of actions
        _initAction.execute(_initObjs);
    }

    @Override
    public KTask<KObject[]> taskThen() {
        AbstractKTaskWrapper<KObject[]> task = new AbstractKTaskWrapper<KObject[]>();
        then(task.initCallback());
        return task;
    }

    @Override
    public KTask<Object[]> taskMap(MetaAttribute attribute) {
        AbstractKTaskWrapper<Object[]> task = new AbstractKTaskWrapper<Object[]>();
        map(attribute, task.initCallback());
        return task;
    }


}

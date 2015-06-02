package org.kevoree.modeling.traversal;

import org.kevoree.modeling.KObject;
import org.kevoree.modeling.Callback;
import org.kevoree.modeling.meta.MetaAttribute;
import org.kevoree.modeling.meta.MetaReference;
import org.kevoree.modeling.traversal.actions.*;

public class DefaultKTraversal implements KTraversal {

    private static final String TERMINATED_MESSAGE = "Promise is terminated by the call of done method, please create another promise";

    private KObject[] _initObjs;

    private KTraversalAction _initAction;

    private KTraversalAction _lastAction;

    private boolean _terminated = false;

    public DefaultKTraversal(KObject p_root) {
        this._initObjs = new KObject[1];
        this._initObjs[0] = p_root;
    }

    private KTraversal internal_chain_action(KTraversalAction p_action) {
        if (_terminated) {
            throw new RuntimeException(TERMINATED_MESSAGE);
        }
        if (_initAction == null) {
            _initAction = p_action;
        }
        if (_lastAction != null) {
            _lastAction.chain(p_action);
        }
        _lastAction = p_action;
        return this;
    }


    @Override
    public KTraversal traverse(MetaReference p_metaReference) {
        return internal_chain_action(new KTraverseAction(p_metaReference));
    }

    @Override
    public KTraversal traverseQuery(String p_metaReferenceQuery) {
        return internal_chain_action(new KTraverseQueryAction(p_metaReferenceQuery));
    }

    @Override
    public KTraversal withAttribute(MetaAttribute p_attribute, Object p_expectedValue) {
        return internal_chain_action(new KFilterAttributeAction(p_attribute, p_expectedValue));
    }

    @Override
    public KTraversal withoutAttribute(MetaAttribute p_attribute, Object p_expectedValue) {
        return internal_chain_action(new KFilterNotAttributeAction(p_attribute, p_expectedValue));
    }

    @Override
    public KTraversal attributeQuery(String p_attributeQuery) {
        return internal_chain_action(new KFilterAttributeQueryAction(p_attributeQuery));
    }

    @Override
    public KTraversal filter(KTraversalFilter p_filter) {
        return internal_chain_action(new KFilterAction(p_filter));
    }

    @Override
    public KTraversal collect(MetaReference metaReference, KTraversalFilter continueCondition) {
        return internal_chain_action(new KDeepCollectAction(metaReference, continueCondition));
    }

    @Override
    public void then(Callback<KObject[]> cb) {
        //set the terminal leaf action
        internal_chain_action(new KFinalAction(cb));
        _terminated = true;
        //execute the first element of the chain of actions
        _initAction.execute(_initObjs);
    }

    @Override
    public void map(MetaAttribute attribute, Callback<Object[]> cb) {
        //set the terminal leaf action
        internal_chain_action(new KMapAction(attribute, cb));
        _terminated = true;
        //execute the first element of the chain of actions
        _initAction.execute(_initObjs);
    }


}
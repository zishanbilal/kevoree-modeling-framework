package org.kevoree.modeling.api.abs;

import org.kevoree.modeling.api.Callback;
import org.kevoree.modeling.api.KInfer;
import org.kevoree.modeling.api.KView;
import org.kevoree.modeling.api.meta.MetaClass;
import org.kevoree.modeling.api.time.TimeTree;

/**
 * Created by duke on 09/12/14.
 */
public class AbstractKObjectInfer<A> extends AbstractKObject implements KInfer {

    public AbstractKObjectInfer(KView p_view, long p_uuid, TimeTree p_timeTree, MetaClass p_metaClass) {
        super(p_view, p_uuid, p_timeTree, p_metaClass);
    }

    @Override
    public void learn(Object[] inputs, Object[] results, Callback<Throwable> callback) {

    }

    @Override
    public void infer(Object[] inputs, Callback<Object[]> callback) {

    }
}

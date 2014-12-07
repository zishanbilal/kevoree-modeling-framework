package org.kevoree.modeling.api.abs;

import org.kevoree.modeling.api.KView;
import org.kevoree.modeling.api.meta.MetaClass;
import org.kevoree.modeling.api.time.TimeTree;

/**
 * Created by duke on 07/12/14.
 */
public class DynamicAbstractKObject extends AbstractKObject {

    public DynamicAbstractKObject(KView p_view, long p_uuid, TimeTree p_timeTree, MetaClass p_metaClass) {
        super(p_view, p_uuid, p_timeTree, p_metaClass);
    }

}

package org.kevoree.modeling.api.reflexive;

import org.kevoree.modeling.api.KView;
import org.kevoree.modeling.api.abs.AbstractKObject;
import org.kevoree.modeling.api.meta.MetaClass;
import org.kevoree.modeling.api.rbtree.LongRBTree;

/**
 * Created by duke on 07/12/14.
 */
public class DynamicKObject extends AbstractKObject {

    public DynamicKObject(KView p_view, long p_uuid, MetaClass p_metaClass) {
        super(p_view, p_uuid, p_metaClass);
    }

}

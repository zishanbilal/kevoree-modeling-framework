package org.kevoree.modeling;

import org.kevoree.modeling.memory.KDataManager;
import org.kevoree.modeling.meta.MetaAttribute;
import org.kevoree.modeling.meta.MetaClass;
import org.kevoree.modeling.meta.MetaOperation;
import org.kevoree.modeling.meta.MetaReference;
import org.kevoree.modeling.traversal.KTraversal;

public interface KObject {

    long universe();

    long now();

    long uuid();

    void delete(Callback cb);
    
    void select(String query, Callback<KObject[]> cb);

    void listen(long groupId, KEventListener listener);

    void visitAttributes(KModelAttributeVisitor visitor);

    void visit(KModelVisitor visitor, Callback cb);

    KTimeWalker timeWalker();

    String domainKey();

    MetaClass metaClass();

    void mutate(KActionType actionType, MetaReference metaReference, KObject param);

    void ref(MetaReference metaReference, Callback<KObject[]> cb);

    KTraversal traversal();

    Object get(MetaAttribute attribute);

    Object getByName(String atributeName);

    void set(MetaAttribute attribute, Object payload);

    void setByName(String atributeName, Object payload);

    String toJSON();

    boolean equals(Object other);

    void jump(long time, Callback<KObject> callback);

    MetaReference[] referencesWith(KObject o);

    void call(MetaOperation operation, Object[] params, Callback<Object> cb);

    KDataManager manager();

}
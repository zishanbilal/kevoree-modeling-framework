package org.kevoree.modeling.api;

import org.kevoree.modeling.api.events.ModelElementListener;
import org.kevoree.modeling.api.time.TimeTree;
import org.kevoree.modeling.api.trace.ModelTrace;
import org.kevoree.modeling.api.util.ActionType;

import java.util.List;

/**
 * Created by thomas on 10/2/14.
 */
public interface KObject<A> {
    public boolean isDeleted();

    public boolean isRoot();

    public String path();

    public String metaClassName();

    public String referenceInParent();

    public String key();

    public String dimension();

    public void delete(Callback<Boolean> callback);

    public void parent(Callback<KObject> callback);

    public boolean modelEquals(A similarObj);

    public void deepModelEquals(A similarObj, Callback<Boolean> callback);

    public void findByPath(String query, Callback<KObject> callback);

    public void findByID(String relationName, String idP, Callback<KObject> callback);

    public void select(String query, Callback<List<KObject>> callback);

    public void stream(String query, Callback<KObject> callback);

    /* Listener management */
    public void addModelElementListener(ModelElementListener lst);

    public void removeModelElementListener(ModelElementListener lst);

    public void removeAllModelElementListeners();

    public void addModelTreeListener(ModelElementListener lst);

    public void removeModelTreeListener(ModelElementListener lst);

    public void removeAllModelTreeListeners();

    /* Visit API */
    public void visitNotContained(ModelVisitor visitor);

    public void visitContained(ModelVisitor visitor);

    public void visitAll(ModelVisitor visitor);

    public void deepVisitNotContained(ModelVisitor visitor);

    public void deepVisitContained(ModelVisitor visitor);

    public void deepVisitAll(ModelVisitor visitor);

    public void visitAttributes(ModelAttributeVisitor visitor);

    /* Powerful Trace API, maybe consider to hide TODO */
    public List<ModelTrace> createTraces(A similarObj, boolean isInter, boolean isMerge, boolean onlyReferences, boolean onlyAttributes);

    public List<ModelTrace> toTraces(boolean attributes, boolean references);

    public void mutate(ActionType mutatorType, String refName, Object value, boolean setOpposite, boolean fireEvent, Callback<Boolean> callback);
    /* end to clean zone TODO */

    /* Time navigation */
    public Long now();

    public void jump(Long time, Callback<A> callback);

    public TimeTree timeTree();

}

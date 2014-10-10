package org.kevoree.modeling.microframework.test.poc.impl;

import org.kevoree.modeling.api.Callback;
import org.kevoree.modeling.api.ModelVisitor;
import org.kevoree.modeling.api.meta.MetaAttribute;
import org.kevoree.modeling.api.meta.MetaClass;
import org.kevoree.modeling.api.meta.MetaReference;
import org.kevoree.modeling.api.time.TimeTree;
import org.kevoree.modeling.api.util.AbstractKObject;
import org.kevoree.modeling.microframework.test.poc.Node;
import org.kevoree.modeling.microframework.test.poc.PocFactory;

/**
 * Created by duke on 10/10/14.
 */
public class NodeImpl extends AbstractKObject<Node,PocFactory> implements Node {

    public NodeImpl(PocFactory factory, String metaClassName, Long now, String dimension, TimeTree timeTree) {
        super(factory, metaClassName, now, dimension, timeTree);
    }

    @Override
    public String parentPath() {
        return null;
    }

    @Override
    public void visitNotContained(ModelVisitor visitor, Callback<Throwable> end) {

    }

    @Override
    public void visitContained(ModelVisitor visitor, Callback<Throwable> end) {

    }

    @Override
    public void visitAll(ModelVisitor visitor, Callback<Throwable> end) {

    }

    @Override
    public void deepVisitNotContained(ModelVisitor visitor, Callback<Throwable> end) {

    }

    @Override
    public void deepVisitContained(ModelVisitor visitor, Callback<Throwable> end) {

    }

    @Override
    public void deepVisitAll(ModelVisitor visitor, Callback<Throwable> end) {

    }

    @Override
    public String key() {
        return null;
    }

    @Override
    public MetaClass metaClass() {
        return null;
    }

    @Override
    public MetaAttribute[] metaAttributes() {
        return new MetaAttribute[0];
    }

    @Override
    public MetaReference[] metaContainedReferences() {
        return new MetaReference[0];
    }

    @Override
    public MetaReference[] metaNotContainedReferences() {
        return new MetaReference[0];
    }
}
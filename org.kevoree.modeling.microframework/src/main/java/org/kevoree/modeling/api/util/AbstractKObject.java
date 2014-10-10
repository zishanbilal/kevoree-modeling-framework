package org.kevoree.modeling.api.util;

import org.kevoree.modeling.api.*;
import org.kevoree.modeling.api.events.ModelElementListener;
import org.kevoree.modeling.api.time.TimeTree;
import org.kevoree.modeling.api.trace.ModelTrace;

import java.util.List;

/**
 * Created by duke on 10/9/14.
 */
public abstract class AbstractKObject<A, B extends KFactory> implements KObject<A, B> {

    private B factory;

    private String metaClassName;

    @Override
    public B factory() {
        return factory;
    }

    @Override
    public String metaClassName() {
        return null;
    }

    public AbstractKObject(B factory, String metaClassName, Long now, String dimension, TimeTree timeTree) {
        this.factory = factory;
        this.metaClassName = metaClassName;
        this.now = now;
        this.dimension = dimension;
        this.timeTree = timeTree;
    }

    private boolean isDeleted = false;

    @Override
    public boolean isDeleted() {
        return isDeleted;
    }

    private boolean isRoot = false;

    @Override
    public boolean isRoot() {
        return isRoot;
    }

    private Long now;

    @Override
    public Long now() {
        return now;
    }

    private TimeTree timeTree;

    @Override
    public TimeTree timeTree() {
        return timeTree;
    }

    private String dimension;

    @Override
    public String dimension() {
        return dimension;
    }

    private String path = null;

    @Override
    public String path() {
        return path;
    }

    private String referenceInParent = null;

    @Override
    public String referenceInParent() {
        return referenceInParent;
    }

    @Override
    public void delete(Callback<Boolean> callback) {

    }

    @Override
    public void parent(Callback<KObject> callback) {

    }

    @Override
    public boolean modelEquals(A similarObj) {
        return false;
    }

    @Override
    public void deepModelEquals(A similarObj, Callback<Boolean> callback) {

    }

    @Override
    public void findByID(String relationName, String idP, Callback<KObject> callback) {

    }

    @Override
    public void select(String query, Callback<List<KObject>> callback) {

    }

    @Override
    public void stream(String query, Callback<KObject> callback) {

    }

    @Override
    public void addModelElementListener(ModelElementListener lst) {

    }

    @Override
    public void removeModelElementListener(ModelElementListener lst) {

    }

    @Override
    public void removeAllModelElementListeners() {

    }

    @Override
    public void addModelTreeListener(ModelElementListener lst) {

    }

    @Override
    public void removeModelTreeListener(ModelElementListener lst) {

    }

    @Override
    public void removeAllModelTreeListeners() {

    }

    @Override
    public void visitNotContained(ModelVisitor visitor) {

    }

    @Override
    public void visitContained(ModelVisitor visitor) {

    }

    @Override
    public void visitAll(ModelVisitor visitor) {

    }

    @Override
    public void deepVisitNotContained(ModelVisitor visitor) {

    }

    @Override
    public void deepVisitContained(ModelVisitor visitor) {

    }

    @Override
    public void deepVisitAll(ModelVisitor visitor) {

    }

    @Override
    public void visitAttributes(ModelAttributeVisitor visitor) {

    }

    @Override
    public List<ModelTrace> createTraces(A similarObj, boolean isInter, boolean isMerge, boolean onlyReferences, boolean onlyAttributes) {
        return null;
    }

    @Override
    public List<ModelTrace> toTraces(boolean attributes, boolean references) {
        return null;
    }

    @Override
    public void mutate(ActionType mutatorType, String refName, Object value, boolean setOpposite, boolean fireEvent) {

    }

    @Override
    public void jump(Long time, Callback<A> callback) {

    }


}
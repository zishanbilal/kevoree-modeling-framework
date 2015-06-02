package org.kevoree.modeling.meta;

import org.kevoree.modeling.abs.AbstractMetaAttribute;
import org.kevoree.modeling.extrapolation.DiscreteExtrapolation;

public class MetaInferClass implements MetaClass {

    private static MetaInferClass _INSTANCE = null;

    public static MetaInferClass getInstance() {
        if (_INSTANCE == null) {
            _INSTANCE = new MetaInferClass();
        }
        return _INSTANCE;
    }

    private MetaAttribute[] _attributes = null;

    private MetaReference[] _metaReferences = new MetaReference[0];

    public MetaAttribute getRaw() {
        return _attributes[0];
    }

    public MetaAttribute getCache() {
        return _attributes[1];
    }

    private MetaInferClass() {
        _attributes = new MetaAttribute[2];
        _attributes[0] = new AbstractMetaAttribute("RAW", 0, -1, false, PrimitiveTypes.STRING, new DiscreteExtrapolation());
        _attributes[1] = new AbstractMetaAttribute("CACHE", 1, -1, false, PrimitiveTypes.TRANSIENT, new DiscreteExtrapolation());
    }

    @Override
    public Meta[] metaElements() {
        return new Meta[0];
    }

    @Override
    public Meta meta(int index) {
        if (index == 0 || index == 1) {
            return _attributes[index];
        } else {
            return null;
        }
    }

    @Override
    public MetaAttribute[] metaAttributes() {
        return _attributes;
    }

    @Override
    public MetaReference[] metaReferences() {
        return _metaReferences;
    }

    @Override
    public Meta metaByName(String name) {
        return attribute(name);
    }

    @Override
    public MetaAttribute attribute(String name) {
        if (name == null) {
            return null;
        } else {
            if (name.equals(_attributes[0].metaName())) {
                return _attributes[0];
            } else if (name.equals(_attributes[1].metaName())) {
                return _attributes[1];
            } else {
                return null;
            }
        }
    }

    @Override
    public MetaReference reference(String name) {
        return null;
    }

    @Override
    public MetaOperation operation(String name) {
        return null;
    }

    @Override
    public String metaName() {
        return "KInfer";
    }

    @Override
    public MetaType metaType() {
        return MetaType.CLASS;
    }

    @Override
    public int index() {
        return -1;
    }

}
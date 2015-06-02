package org.kevoree.modeling.abs;

import org.kevoree.modeling.meta.MetaClass;
import org.kevoree.modeling.meta.MetaReference;
import org.kevoree.modeling.meta.MetaType;

public class AbstractMetaReference implements MetaReference {

    private String _name;

    private int _index;

    private boolean _visible;

    private boolean _single;

    private LazyResolver _lazyMetaType;

    private String _op_name;

    private LazyResolver _lazyMetaOrigin;

    public boolean single() {
        return _single;
    }

    public MetaClass type() {
        if (_lazyMetaType != null) {
            return (MetaClass) _lazyMetaType.meta();
        } else {
            return null;
        }
    }

    public MetaReference opposite() {
        if (_op_name != null) {
            return type().reference(_op_name);
        }
        return null;
    }

    @Override
    public MetaClass origin() {
        if (_lazyMetaOrigin != null) {
            return (MetaClass) _lazyMetaOrigin.meta();
        }
        return null;
    }

    public int index() {
        return _index;
    }

    public String metaName() {
        return _name;
    }

    @Override
    public MetaType metaType() {
        return MetaType.REFERENCE;
    }

    @Override
    public boolean visible() {
        return _visible;
    }

    public AbstractMetaReference(String p_name, int p_index, boolean p_visible, boolean p_single, LazyResolver p_lazyMetaType, String op_name, LazyResolver p_lazyMetaOrigin) {
        this._name = p_name;
        this._index = p_index;
        this._visible = p_visible;
        this._single = p_single;
        this._lazyMetaType = p_lazyMetaType;
        this._op_name = op_name;
        this._lazyMetaOrigin = p_lazyMetaOrigin;
    }

}
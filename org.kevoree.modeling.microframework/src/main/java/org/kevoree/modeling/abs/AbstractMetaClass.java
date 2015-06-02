package org.kevoree.modeling.abs;

import org.kevoree.modeling.KConfig;
import org.kevoree.modeling.memory.struct.map.StringHashMap;
import org.kevoree.modeling.meta.Meta;
import org.kevoree.modeling.meta.MetaAttribute;
import org.kevoree.modeling.meta.MetaClass;
import org.kevoree.modeling.meta.MetaOperation;
import org.kevoree.modeling.meta.MetaReference;
import org.kevoree.modeling.meta.MetaType;

public class AbstractMetaClass implements MetaClass {

    private String _name;

    private int _index;

    private Meta[] _meta;

    private MetaAttribute[] _atts;

    private MetaReference[] _refs;

    private StringHashMap<Meta> _indexes = null;

    @Override
    public Meta metaByName(String name) {
        return _indexes.get(name);
    }

    @Override
    public MetaAttribute attribute(String name) {
        if(_indexes == null){
            return null;
        } else {
            Meta resolved = _indexes.get(name);
            if (resolved != null && resolved instanceof AbstractMetaAttribute) {
                return (MetaAttribute) resolved;
            }
            return null;
        }
    }

    @Override
    public MetaReference reference(String name) {
        if(_indexes == null){
            return null;
        } else {
            Meta resolved = _indexes.get(name);
            if (resolved != null && resolved instanceof AbstractMetaReference) {
                return (MetaReference) resolved;
            }
            return null;
        }
    }

    @Override
    public MetaOperation operation(String name) {
        if(_indexes == null){
            return null;
        } else {
            Meta resolved = _indexes.get(name);
            if (resolved != null && resolved instanceof AbstractMetaOperation) {
                return (MetaOperation) resolved;
            }
            return null;
        }
    }

    @Override
    public Meta[] metaElements() {
        return _meta;
    }

    public int index() {
        return _index;
    }

    public String metaName() {
        return _name;
    }

    @Override
    public MetaType metaType() {
        return MetaType.CLASS;
    }

    protected AbstractMetaClass(String p_name, int p_index) {
        this._name = p_name;
        this._index = p_index;
    }

    protected void init(Meta[] p_meta) {
        _indexes = new StringHashMap<Meta>(p_meta.length, KConfig.CACHE_LOAD_FACTOR);
        this._meta = p_meta;
        int nbAtt = 0;
        int nbRef = 0;
        for (int i = 0; i < p_meta.length; i++) {
            if (p_meta[i].metaType().equals(MetaType.ATTRIBUTE)) {
                nbAtt++;
            } else if (p_meta[i].metaType().equals(MetaType.REFERENCE)) {
                nbRef++;
            }
            _indexes.put(p_meta[i].metaName(), p_meta[i]);
        }
        _atts = new MetaAttribute[nbAtt];
        _refs = new MetaReference[nbRef];
        nbAtt = 0;
        nbRef = 0;
        for (int i = 0; i < p_meta.length; i++) {
            if (p_meta[i].metaType().equals(MetaType.ATTRIBUTE)) {
                _atts[nbAtt] = (MetaAttribute) p_meta[i];
                nbAtt++;
            } else if (p_meta[i].metaType().equals(MetaType.REFERENCE)) {
                _refs[nbRef] = (MetaReference) p_meta[i];
                nbRef++;
            }
            _indexes.put(p_meta[i].metaName(), p_meta[i]);
        }

    }

    @Override
    public Meta meta(int index) {
        if (index >= 0 && index < this._meta.length) {
            return this._meta[index];
        } else {
            return null;
        }
    }

    @Override
    public MetaAttribute[] metaAttributes() {
        return this._atts;
    }

    @Override
    public MetaReference[] metaReferences() {
        return this._refs;
    }

}
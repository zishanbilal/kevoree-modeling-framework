package org.kevoree.modeling.ast;

/**
 * Created by gregory.nain on 14/10/2014.
 */
public class MModelAttribute {

    private String name, type;

    private boolean id = false;

    private boolean learned = false;

    private boolean single = true;

    private Integer index = -1;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getAttIndex() {
        return attIndex;
    }

    public void setAttIndex(Integer attIndex) {
        this.attIndex = attIndex;
    }

    private Integer attIndex = -1;

    public MModelAttribute(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public void setId(boolean id) {
        this.id = id;
    }

    public void setSingle(boolean single) {
        this.single = single;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isId() {
        return id;
    }

    public boolean isSingle() {
        return single;
    }

    public boolean isLearned() {
        return learned;
    }

    public void setLearned(boolean learned) {
        this.learned = learned;
    }

    private Double precision = null;

    public Double getPrecision() {
        return precision;
    }

    public void setPrecision(Double precision) {
        this.precision = precision;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MModelAttribute && name.equals(((MModelAttribute) o).name);
    }

}

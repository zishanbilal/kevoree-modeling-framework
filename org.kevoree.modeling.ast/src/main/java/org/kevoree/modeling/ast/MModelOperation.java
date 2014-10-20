package org.kevoree.modeling.ast;

import java.util.ArrayList;

/**
 * Created by gregory.nain on 20/10/14.
 */
public class MModelOperation {

    public ArrayList<MModelOperationParam> inputParams = new ArrayList<MModelOperationParam>();
    public MModelOperationParam returnParam = null;
    public String name;

    public MModelOperation(String name) {
        this.name = name;
    }

    public ArrayList<MModelOperationParam> getInputParams() {
        return inputParams;
    }

    public MModelOperationParam getReturnParam() {
        return returnParam;
    }

    public String getName() {
        return name;
    }
}

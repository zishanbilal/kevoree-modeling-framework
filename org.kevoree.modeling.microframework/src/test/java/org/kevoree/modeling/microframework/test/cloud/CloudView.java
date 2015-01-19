package org.kevoree.modeling.microframework.test.cloud;

import org.kevoree.modeling.api.KView;

/**
 * Created by duke on 10/9/14.
 */
public interface CloudView extends KView {

    public Node createNode();

    public Element createElement();

    @Override
    public CloudUniverse universe();

}

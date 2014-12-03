package org.kevoree.modeling.microframework.test.xmi;

import org.junit.Test;
import org.kevoree.modeling.api.Callback;
import org.kevoree.modeling.api.KObject;
import org.kevoree.modeling.api.ThrowableCallback;
import org.kevoree.modeling.api.data.MemoryKDataBase;
import org.kevoree.modeling.microframework.test.cloud.CloudDimension;
import org.kevoree.modeling.microframework.test.cloud.CloudUniverse;
import org.kevoree.modeling.microframework.test.cloud.CloudView;
import org.kevoree.modeling.microframework.test.cloud.Node;
import org.kevoree.modeling.microframework.test.cloud.Element;

/**
 * Created by gregory.nain on 16/10/2014.
 */
public class SerializerTest {


    @Test
    public void serializeTest() throws InterruptedException {

        CloudUniverse universe = new CloudUniverse();
        universe.connect(null);
        CloudDimension dimension0 = universe.newDimension();
        final CloudView t0 = dimension0.time(0l);
        Node nodeT0 = t0.createNode();
        nodeT0.setName("node0");
        t0.setRoot(nodeT0,null);
        Element child0 = t0.createElement();
        nodeT0.setElement(child0);
        Node nodeT1 = t0.createNode();
        nodeT1.setName("n1");
        nodeT0.addChildren(nodeT1);
        t0.lookup(nodeT0.uuid(), new Callback<KObject>() {
            @Override
            public void on(KObject root) {
                t0.xmi().save(root, new ThrowableCallback<String>() {
                    @Override
                    public void on(String result, Throwable error) {
                        if (error != null) {
                            error.printStackTrace();
                        }
                    }
                });
            }
        });
    }

}
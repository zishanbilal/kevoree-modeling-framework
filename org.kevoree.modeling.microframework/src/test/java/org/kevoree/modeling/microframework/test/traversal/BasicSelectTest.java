package org.kevoree.modeling.microframework.test.traversal;

import org.junit.Test;
import org.kevoree.modeling.api.Callback;
import org.kevoree.modeling.api.KObject;
import org.kevoree.modeling.microframework.test.cloud.CloudUniverse;
import org.kevoree.modeling.microframework.test.cloud.CloudModel;
import org.kevoree.modeling.microframework.test.cloud.CloudView;
import org.kevoree.modeling.microframework.test.cloud.Node;

import org.junit.Assert;

/**
 * Created by duke on 10/27/14.
 */
public class BasicSelectTest {

    @Test
    public void rootSelectTest() throws Exception {
        CloudModel universe = new CloudModel();
        universe.connect(null);

        CloudUniverse dimension0 = universe.newUniverse();
        CloudView t0 = dimension0.time(0l);
        Node node = t0.createNode();
        node.setName("n0");
        t0.setRoot(node, null);
        t0.select("/", new Callback<KObject[]>() {
            @Override
            public void on(KObject[] kObjects) {
                Assert.assertEquals(kObjects[0], node);
            }
        });
        CloudView t1 = dimension0.time(1l);
        t1.select("/", new Callback<KObject[]>() {
            @Override
            public void on(KObject[] kObjects) {
                Assert.assertEquals(node.uuid(),kObjects[0].uuid());
                Assert.assertEquals(t1.now(),kObjects[0].now());
            }
        });

    }


    @Test
    public void selectTest() throws Exception {
        CloudModel universe = new CloudModel();
        universe.connect(null);

        CloudUniverse dimension0 = universe.newUniverse();
        CloudView t0 = dimension0.time(0l);
        Node node = t0.createNode();
        node.setName("n0");
        t0.setRoot(node, null);
        final Node node2 = t0.createNode();
        node2.setName("n1");
        node.addChildren(node2);

        final Node node3 = t0.createNode();
        node3.setName("n2");
        node2.addChildren(node3);


        Node node4 = t0.createNode();
        node4.setName("n4");
        node3.addChildren(node4);

        Node node5 = t0.createNode();
        node5.setName("n5");
        node3.addChildren(node5);

        t0.select("children[]", new Callback<KObject[]>() {
            @Override
            public void on(KObject[] selecteds) {
                Assert.assertEquals(1, selecteds.length);
                Assert.assertEquals(node2, selecteds[0]);
            }
        });

        t0.select("children[name=*]", new Callback<KObject[]>() {
            @Override
            public void on(KObject[] selecteds) {
                Assert.assertEquals(1, selecteds.length);
                Assert.assertEquals(node2, selecteds[0]);
            }
        });

        t0.select("children[name=n*]", new Callback<KObject[]>() {
            @Override
            public void on(KObject[] selecteds) {
                Assert.assertEquals(1, selecteds.length);
                Assert.assertEquals(node2, selecteds[0]);
            }
        });

        t0.select("children[name=n1]", new Callback<KObject[]>() {
            @Override
            public void on(KObject[] selecteds) {
                Assert.assertEquals(1, selecteds.length);
                Assert.assertEquals(node2, selecteds[0]);
            }
        });

        t0.select("children[name=!n1]", new Callback<KObject[]>() {
            @Override
            public void on(KObject[] selecteds) {
                Assert.assertEquals(0, selecteds.length);
            }
        });

        t0.select("children[name!=n1]", new Callback<KObject[]>() {
            @Override
            public void on(KObject[] selecteds) {
                Assert.assertEquals(0, selecteds.length);
            }
        });

        t0.select("children[name=n1]/children[name=n2]", new Callback<KObject[]>() {
            @Override
            public void on(KObject[] selecteds) {
                Assert.assertEquals(1, selecteds.length);
                Assert.assertEquals(node3, selecteds[0]);
            }
        });

        t0.select("/children[name=n1]/children[name=n2]", new Callback<KObject[]>() {
            @Override
            public void on(KObject[] selecteds) {
                Assert.assertEquals(1, selecteds.length);
                Assert.assertEquals(node3, selecteds[0]);
            }
        });

        node.select("children[name=n1]/children[name=n2]", new Callback<KObject[]>() {
            @Override
            public void on(KObject[] selecteds) {
                Assert.assertEquals(1, selecteds.length);
                Assert.assertEquals(node3, selecteds[0]);
            }
        });

        /*
        node.select("/children[name=n1]/children[name=n2]", new Callback<KObject[]>() {
            @Override
            public void on(KObject[] selecteds) {
                Assert.assertEquals(0, selecteds.length);
            }
        });*/

        node.select("children[name=n1]/children[name=n2]/children[name=*]", new Callback<KObject[]>() {
            @Override
            public void on(KObject[] selecteds) {
                Assert.assertEquals(2, selecteds.length);
            }
        });

    }

}
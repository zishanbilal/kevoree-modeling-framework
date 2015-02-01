package org.kevoree.modeling.microframework.test.traversal;

import org.junit.Assert;
import org.junit.Test;
import org.kevoree.modeling.api.Callback;
import org.kevoree.modeling.api.KObject;
import org.kevoree.modeling.api.traversal.KTraversalFilter;
import org.kevoree.modeling.microframework.test.cloud.CloudModel;
import org.kevoree.modeling.microframework.test.cloud.CloudUniverse;
import org.kevoree.modeling.microframework.test.cloud.CloudView;
import org.kevoree.modeling.microframework.test.cloud.Element;
import org.kevoree.modeling.microframework.test.cloud.Node;
import org.kevoree.modeling.microframework.test.cloud.meta.MetaNode;

/**
 * Created by thomas on 19/12/14.
 */
public class PromiseTest {

    @Test
    public void simpleTraversalTest() {
        final CloudModel universe = new CloudModel();
        universe.connect(null);
        final CloudUniverse dimension0 = universe.newUniverse();
        final CloudView t0 = dimension0.time(0l);

        final Node node0 = t0.createNode();
        final Element elem0_0 = t0.createElement();
        node0.setElement(elem0_0);

        t0.setRoot(node0, new Callback<Throwable>() {
            @Override
            public void on(Throwable throwable) {

                final Node node1 = t0.createNode();
                node1.setName("child1");
                final Element elem1_0 = t0.createElement();
                node1.setElement(elem1_0);

                final Node node2 = t0.createNode();
                node2.setName("child2");
                final Element elem2_0 = t0.createElement();
                node2.setElement(elem2_0);

                node0.addChildren(node1);
                node0.addChildren(node2);

                // traversal promise
                node0.traverse(node0.metaClass().metaReference("children")).then(new Callback<KObject[]>() {
                    @Override
                    public void on(KObject[] kObjects) {
                        Assert.assertEquals(kObjects.length, 2);
                    }
                });

                node0.traverse(node0.metaClass().metaReference("children")).withAttribute(MetaNode.ATT_NAME, "child*").map(MetaNode.ATT_NAME, new Callback<Object[]>() {
                    @Override
                    public void on(Object[] objects) {
                        Assert.assertEquals(objects.length, 2);
                    }
                });

                node0.traverse(node0.metaClass().metaReference("children")).withAttribute(MetaNode.ATT_NAME, "child1").map(MetaNode.ATT_NAME, new Callback<Object[]>() {
                    @Override
                    public void on(Object[] objects) {
                        Assert.assertEquals(objects.length, 1);
                        Assert.assertEquals(objects[0], "child1");
                    }
                });

                node0.traverse(node0.metaClass().metaReference("children")).withoutAttribute(MetaNode.ATT_NAME, "child1").map(MetaNode.ATT_NAME, new Callback<Object[]>() {
                    @Override
                    public void on(Object[] objects) {
                        Assert.assertEquals(objects.length, 1);
                        Assert.assertEquals(objects[0], "child2");
                    }
                });

                node0.traverse(node0.metaClass().metaReference("children")).withAttribute(MetaNode.ATT_NAME, null).map(MetaNode.ATT_NAME, new Callback<Object[]>() {
                    @Override
                    public void on(Object[] objects) {
                        Assert.assertEquals(objects.length, 0);
                    }
                });

                node0.traverse(node0.metaClass().metaReference("children")).withAttribute(MetaNode.ATT_NAME, "*").map(MetaNode.ATT_NAME, new Callback<Object[]>() {
                    @Override
                    public void on(Object[] objects) {
                        Assert.assertEquals(objects.length, 2);
                    }
                });

                node0.traverse(node0.metaClass().metaReference("children")).withoutAttribute(MetaNode.ATT_NAME, null).map(MetaNode.ATT_NAME, new Callback<Object[]>() {
                    @Override
                    public void on(Object[] objects) {
                        Assert.assertEquals(objects.length, 2);
                    }
                });

                node0.traverse(node0.metaClass().metaReference("children")).withoutAttribute(MetaNode.ATT_NAME, "*").map(MetaNode.ATT_NAME, new Callback<Object[]>() {
                    @Override
                    public void on(Object[] objects) {
                        Assert.assertEquals(objects.length, 0);
                    }
                });

            }
        });
    }

    @Test
    public void chainedTraversalTest() {
        final CloudModel universe = new CloudModel();
        universe.connect(null);
        final CloudUniverse dimension0 = universe.newUniverse();
        final CloudView t0 = dimension0.time(0l);

        final Node node0 = t0.createNode();
        final Element elem0_0 = t0.createElement();
        node0.setElement(elem0_0);

        t0.setRoot(node0, new Callback<Throwable>() {
            @Override
            public void on(Throwable throwable) {

                final Node node1 = t0.createNode();
                node1.setName("child1");
                final Element elem1_0 = t0.createElement();
                elem1_0.setName("child1_elem1");
                node1.setElement(elem1_0);

                final Node node2 = t0.createNode();
                node2.setName("child2");
                final Element elem2_0 = t0.createElement();
                elem2_0.setName("child2_elem1");
                node2.setElement(elem2_0);

                node0.addChildren(node1);
                node0.addChildren(node2);

                // chained traversal promise
                node0.traverse(node0.metaClass().metaReference("children")).traverse(node0.metaClass().metaReference("element")).then(new Callback<KObject[]>() {
                    @Override
                    public void on(KObject[] kObjects) {
                        Assert.assertEquals(kObjects.length, 2);
                    }
                });

            }
        });
    }

    @Test
    public void filterTest() {
        final CloudModel universe = new CloudModel();
        universe.connect(null);
        final CloudUniverse dimension0 = universe.newUniverse();
        final CloudView t0 = dimension0.time(0l);

        final Node node0 = t0.createNode();
        final Element elem0_0 = t0.createElement();
        node0.setElement(elem0_0);

        t0.setRoot(node0, new Callback<Throwable>() {
            @Override
            public void on(Throwable throwable) {

                final Node node1 = t0.createNode();
                node1.setName("child1");
                final Element elem1_0 = t0.createElement();
                elem1_0.setName("child1_elem1");
                node1.setElement(elem1_0);

                final Node node2 = t0.createNode();
                node2.setName("child2");
                final Element elem2_0 = t0.createElement();
                elem2_0.setName("child2_elem1");
                node2.setElement(elem2_0);

                node0.addChildren(node1);
                node0.addChildren(node2);

                // chained traversal promise
                node0.traverse(node0.metaClass().metaReference("children")).filter(new KTraversalFilter() {
                    @Override
                    public boolean filter(KObject obj) {
                        return ((Node) obj).getName().equals("child1");
                    }
                }).then(new Callback<KObject[]>() {
                    @Override
                    public void on(KObject[] kObjects) {
                        Assert.assertEquals(kObjects.length, 1);
                        Assert.assertEquals(((Node) kObjects[0]).getName(), "child1");

                    }
                });

            }
        });
    }

    @Test
    public void parentTest() {
        final CloudModel model = new CloudModel();
        model.connect(new Callback<Throwable>() {
            @Override
            public void on(Throwable throwable) {
                final CloudUniverse universe = model.newUniverse();
                final CloudView t0 = universe.time(0l);
                final Node node0 = t0.createNode();
                final Element elem0_0 = t0.createElement();
                node0.setElement(elem0_0);
                t0.setRoot(node0, new Callback<Throwable>() {
                    @Override
                    public void on(Throwable throwable) {
                        final Node node1 = t0.createNode();
                        node1.setName("child1");
                        final Element elem1_0 = t0.createElement();
                        elem1_0.setName("child1_elem1");
                        node1.setElement(elem1_0);
                        final Node node2 = t0.createNode();
                        node2.setName("child2");
                        final Element elem2_0 = t0.createElement();
                        elem2_0.setName("child2_elem1");
                        node2.setElement(elem2_0);
                        node0.addChildren(node1);
                        node0.addChildren(node2);
                        // chained traversal promise
                        node0.traverse(node0.metaClass().metaReference("children")).parents().then(new Callback<KObject[]>() {
                            @Override
                            public void on(KObject[] kObjects) {
                                Assert.assertEquals(kObjects.length, 1);
                                Assert.assertEquals(kObjects[0], node0);
                            }
                        });
                        // reverse
                        node0.traverse(node0.metaClass().metaReference("children")).reverse(node0.metaClass().metaReference("children")).then(new Callback<KObject[]>() {
                            @Override
                            public void on(KObject[] kObjects) {
                                Assert.assertEquals(kObjects.length, 1);
                                Assert.assertEquals(kObjects[0], node0);
                            }
                        });
                    }
                });
            }
        });
    }

    @Test
    public void traverseQueryTest() {
        final CloudModel universe = new CloudModel();
        universe.connect(new Callback<Throwable>() {
            @Override
            public void on(Throwable throwable) {
                final CloudUniverse dimension0 = universe.newUniverse();
                final CloudView t0 = dimension0.time(0l);

                final Node node0 = t0.createNode();
                final Element elem0_0 = t0.createElement();
                node0.setElement(elem0_0);

                t0.setRoot(node0, new Callback<Throwable>() {
                    @Override
                    public void on(Throwable throwable) {

                        final Node node1 = t0.createNode();
                        node1.setName("child1");
                        final Element elem1_0 = t0.createElement();
                        elem1_0.setName("child1_elem1");
                        node1.setElement(elem1_0);

                        final Node node2 = t0.createNode();
                        node2.setName("child2");
                        final Element elem2_0 = t0.createElement();
                        elem2_0.setName("child2_elem1");
                        node2.setElement(elem2_0);

                        node0.addChildren(node1);
                        node0.addChildren(node2);


                        // chained traversal promise
                        node0.traverseQuery("children").filter(new KTraversalFilter() {
                            @Override
                            public boolean filter(KObject obj) {
                                return ((Node) obj).getName().equals("child1");
                            }
                        }).then(new Callback<KObject[]>() {
                            @Override
                            public void on(KObject[] kObjects) {
                                Assert.assertEquals(kObjects.length, 1);
                                Assert.assertEquals(((Node) kObjects[0]).getName(), "child1");
                            }
                        });

                        // chained traversal promise
                        node0.traverseQuery("child*").filter(new KTraversalFilter() {
                            @Override
                            public boolean filter(KObject obj) {
                                return ((Node) obj).getName().equals("child1");
                            }
                        }).then(new Callback<KObject[]>() {
                            @Override
                            public void on(KObject[] kObjects) {
                                Assert.assertEquals(kObjects.length, 1);
                                Assert.assertEquals(((Node) kObjects[0]).getName(), "child1");
                            }
                        });

                        // chained traversal promise
                        node0.traverseQuery("*children").filter(new KTraversalFilter() {
                            @Override
                            public boolean filter(KObject obj) {
                                return ((Node) obj).getName().equals("child1");
                            }
                        }).then(new Callback<KObject[]>() {
                            @Override
                            public void on(KObject[] kObjects) {
                                Assert.assertEquals(kObjects.length, 1);
                                Assert.assertEquals(((Node) kObjects[0]).getName(), "child1");
                            }
                        });

                        // chained traversal promise
                        node0.traverseQuery("cc,children").filter(new KTraversalFilter() {
                            @Override
                            public boolean filter(KObject obj) {
                                return ((Node) obj).getName().equals("child1");
                            }
                        }).then(new Callback<KObject[]>() {
                            @Override
                            public void on(KObject[] kObjects) {
                                Assert.assertEquals(kObjects.length, 1);
                                Assert.assertEquals(((Node) kObjects[0]).getName(), "child1");
                            }
                        });

                    }
                });
            }
        });
    }

    @Test
    public void attributeQueryTest() {
        final CloudModel universe = new CloudModel();
        universe.connect(null);
        final CloudUniverse dimension0 = universe.newUniverse();
        final CloudView t0 = dimension0.time(0l);

        final Node node0 = t0.createNode();
        final Element elem0_0 = t0.createElement();
        node0.setElement(elem0_0);

        t0.setRoot(node0, new Callback<Throwable>() {
            @Override
            public void on(Throwable throwable) {

                final Node node1 = t0.createNode();
                node1.setName("child1");
                final Element elem1_0 = t0.createElement();
                node1.setElement(elem1_0);

                final Node node2 = t0.createNode();
                node2.setName("child2");
                final Element elem2_0 = t0.createElement();
                node2.setElement(elem2_0);

                node0.addChildren(node1);
                node0.addChildren(node2);

                
                node0.traverseQuery("children").attributeQuery("name=*").then(new Callback<KObject[]>() {
                    @Override
                    public void on(KObject[] kObjects) {
                        Assert.assertEquals(2, kObjects.length);
                    }
                });

                node0.traverseQuery("children").attributeQuery("name=child1").then(new Callback<KObject[]>() {
                    @Override
                    public void on(KObject[] kObjects) {
                        Assert.assertEquals(1, kObjects.length);
                        Assert.assertEquals("child1", kObjects[0].get(MetaNode.ATT_NAME));
                    }
                });

                node0.traverseQuery("children").attributeQuery("name=child*,value=null").then(new Callback<KObject[]>() {
                    @Override
                    public void on(KObject[] kObjects) {
                        Assert.assertEquals(2, kObjects.length);
                    }
                });

                node0.traverseQuery("children").attributeQuery("name=child*,value!=null").then(new Callback<KObject[]>() {
                    @Override
                    public void on(KObject[] kObjects) {
                        Assert.assertEquals(0, kObjects.length);
                    }
                });


                node0.traverseQuery("children").attributeQuery("name=child*,value=*").then(new Callback<KObject[]>() {
                    @Override
                    public void on(KObject[] kObjects) {
                        Assert.assertEquals(2, kObjects.length);
                    }
                });


                final Node node3 = t0.createNode();
                node3.setName("child3");
                node3.setValue("3");
                node0.addChildren(node3);

                node0.traverseQuery("children").attributeQuery("name=child*,value=*").then(new Callback<KObject[]>() {
                    @Override
                    public void on(KObject[] kObjects) {
                        Assert.assertEquals(3, kObjects.length);
                    }
                });

                node0.traverseQuery("children").attributeQuery("name=child*,value=null").then(new Callback<KObject[]>() {
                    @Override
                    public void on(KObject[] kObjects) {
                        Assert.assertEquals(2, kObjects.length);
                    }
                });

                node0.traverseQuery("children").attributeQuery("name=child*,value!=null").then(new Callback<KObject[]>() {
                    @Override
                    public void on(KObject[] kObjects) {
                        Assert.assertEquals(1, kObjects.length);
                    }
                });


            }
        });
    }

}

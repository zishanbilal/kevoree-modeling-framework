package org.kevoree.modeling.format.xmi;

import org.kevoree.modeling.Callback;
import org.kevoree.modeling.KConfig;
import org.kevoree.modeling.KObject;
import org.kevoree.modeling.memory.struct.map.StringHashMap;

import java.util.ArrayList;

/**
 * Created by duke on 10/27/14.
 */
public class XMILoadingContext {

    public XmlParser xmiReader;

    public KObject loadedRoots = null;

    public ArrayList<XMIResolveCommand> resolvers = new ArrayList<XMIResolveCommand>();

    public StringHashMap<KObject> map = new StringHashMap<KObject>(KConfig.CACHE_INIT_SIZE,KConfig.CACHE_LOAD_FACTOR);

    public StringHashMap<Integer> elementsCount = new StringHashMap<Integer>(KConfig.CACHE_INIT_SIZE,KConfig.CACHE_LOAD_FACTOR);

    public Callback<Throwable> successCallback;

}
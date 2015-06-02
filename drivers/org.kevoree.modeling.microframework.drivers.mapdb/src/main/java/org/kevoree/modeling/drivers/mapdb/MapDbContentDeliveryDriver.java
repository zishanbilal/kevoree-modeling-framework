package org.kevoree.modeling.drivers.mapdb;

import org.kevoree.modeling.*;
import org.kevoree.modeling.memory.KContentKey;
import org.kevoree.modeling.memory.cdn.AtomicOperation;
import org.kevoree.modeling.memory.KContentDeliveryDriver;
import org.kevoree.modeling.memory.cdn.KContentPutRequest;
import org.kevoree.modeling.memory.KDataManager;
import org.kevoree.modeling.util.LocalEventListeners;
import org.kevoree.modeling.msg.KMessage;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.util.Map;

/**
 * Created by duke on 12/05/15.
 */
public class MapDbContentDeliveryDriver implements KContentDeliveryDriver {

    private File directory = null;
    private DB db;
    private Map m;

    public MapDbContentDeliveryDriver(File targetDir) {
        this.directory = targetDir;
    }

    @Override
    public void atomicGetMutate(KContentKey key, AtomicOperation operation, ThrowableCallback<String> callback) {
        String previous = (String) m.get(key.toString());
        if (previous == null) {
            previous = "0";
        }
        String next = operation.mutate(previous);
        m.put(key.toString(), next);
        callback.on(previous, null);
    }

    @Override
    public void get(KContentKey[] keys, ThrowableCallback<String[]> callback) {
        String[] results = new String[keys.length];
        for (int i = 0; i < keys.length; i++) {
            results[i] = (String) m.get(keys[i].toString());
        }
        callback.on(results, null);
    }

    @Override
    public void put(KContentPutRequest request, Callback<Throwable> error) {
        for (int i = 0; i < request.size(); i++) {
            m.put(request.getKey(i).toString(), request.getContent(i).toString());
        }
        error.on(null);
    }

    @Override
    public void remove(String[] keys, Callback<Throwable> error) {

    }

    @Override
    public void connect(Callback<Throwable> callback) {
        db = DBMaker.newMemoryDirectDB().transactionDisable().asyncWriteFlushDelay(100).newFileDB(directory).closeOnJvmShutdown().make();
        m = db.getTreeMap("test");
    }

    @Override
    public void close(Callback<Throwable> callback) {
        db.close();
        db = null;
        m = null;
    }

    private LocalEventListeners localEventListeners = new LocalEventListeners();

    @Override
    public void registerListener(long p_groupId, KObject p_origin, KEventListener p_listener) {
        localEventListeners.registerListener(p_groupId, p_origin, p_listener);
    }

    @Override
    public void registerMultiListener(long p_groupId, KUniverse p_origin, long[] p_objects, KEventMultiListener p_listener) {
        localEventListeners.registerListenerAll(p_groupId, p_origin.key(), p_objects, p_listener);
    }

    @Override
    public void unregisterGroup(long p_groupId) {
        localEventListeners.unregister(p_groupId);
    }

    @Override
    public void send(KMessage msgs) {
        //No Remote send since LevelDB do not provide message brokering
        localEventListeners.dispatch(msgs);
    }

    @Override
    public void setManager(KDataManager manager) {
        localEventListeners.setManager(manager);
    }

}
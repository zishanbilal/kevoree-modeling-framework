package org.kevoree.modeling.databases.leveldb;

import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.WriteBatch;
import org.kevoree.modeling.api.Callback;
import org.kevoree.modeling.api.KEventListener;
import org.kevoree.modeling.api.ThrowableCallback;
import org.kevoree.modeling.api.data.cache.KCache;
import org.kevoree.modeling.api.data.cache.KContentKey;
import org.kevoree.modeling.api.data.cache.MultiLayeredMemoryCache;
import org.kevoree.modeling.api.data.cdn.AtomicOperation;
import org.kevoree.modeling.api.data.cdn.KContentDeliveryDriver;
import org.kevoree.modeling.api.data.cdn.KContentPutRequest;
import org.kevoree.modeling.api.data.manager.KDataManager;
import org.kevoree.modeling.api.msg.KMessage;
import org.kevoree.modeling.api.util.LocalEventListeners;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by duke on 11/4/14.
 */
public class LevelDbContentDeliveryDriver implements KContentDeliveryDriver {

    private Options options = new Options().createIfMissing(true);

    private DB db;

    private final Lock lock = new ReentrantLock();

    public LevelDbContentDeliveryDriver(String storagePath) throws IOException {
        File location = new File(storagePath);
        if (!location.exists()) {
            location.mkdirs();
        }
        File targetDB = new File(location, "data");
        db = JniDBFactory.factory.open(targetDB, options);
    }

    @Override
    public void atomicGetMutate(KContentKey key, AtomicOperation operation, ThrowableCallback<String> callback) {
        String result = JniDBFactory.asString(db.get(JniDBFactory.bytes(key.toString())));
        String mutated = operation.mutate(result);
        WriteBatch batch = db.createWriteBatch();
        batch.put(JniDBFactory.bytes(key.toString()), JniDBFactory.bytes(mutated));
        db.write(batch);
        callback.on(result, null);
    }

    @Override
    public void get(KContentKey[] keys, ThrowableCallback<String[]> callback) {
        String[] result = new String[keys.length];
        for (int i = 0; i < keys.length; i++) {
            result[i] = JniDBFactory.asString(db.get(JniDBFactory.bytes(keys[i].toString())));
        }
        if (callback != null) {
            callback.on(result, null);
        }
    }

    @Override
    public void put(KContentPutRequest request, Callback<Throwable> error) {

        WriteBatch batch = db.createWriteBatch();
        for (int i = 0; i < request.size(); i++) {
            batch.put(JniDBFactory.bytes(request.getKey(i).toString()), JniDBFactory.bytes(request.getContent(i)));
        }
        db.write(batch);
        if (error != null) {
            error.on(null);
        }
    }

    @Override
    public void remove(String[] keys, Callback<Throwable> error) {
        try {
            for (int i = 0; i < keys.length; i++) {
                db.delete(JniDBFactory.bytes(keys[i]));
            }
            if (error != null) {
                error.on(null);
            }
        } catch (Exception e) {
            if (error != null) {
                error.on(e);
            }
        }
    }

    @Override
    public void close(Callback<Throwable> error) {
        db.write(db.createWriteBatch());
        try {
            db.close();
            if (error != null) {
                error.on(null);
            }
        } catch (IOException e) {
            if (error != null) {
                error.on(e);
            }
        }
    }

    private MultiLayeredMemoryCache _cache = new MultiLayeredMemoryCache();

    @Override
    public KCache cache() {
        return _cache;
    }

    private LocalEventListeners localEventListeners = new LocalEventListeners();

    @Override
    public void registerListener(Object p_origin, KEventListener p_listener, Object p_scope) {
        localEventListeners.registerListener(p_origin, p_listener, p_scope);
    }

    @Override
    public void unregister(KEventListener p_listener) {
        localEventListeners.unregister(p_listener);
    }


    @Override
    public void send(KMessage[] msgs) {

    }

    @Override
    public void setManager(KDataManager manager) {

    }

    @Override
    public void connect(Callback<Throwable> callback) {
        //noop
        if (callback != null) {
            callback.on(null);
        }
    }

}
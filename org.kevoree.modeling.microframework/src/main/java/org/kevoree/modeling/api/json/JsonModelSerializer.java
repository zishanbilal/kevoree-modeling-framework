package org.kevoree.modeling.api.json;

import org.kevoree.modeling.api.Callback;
import org.kevoree.modeling.api.KObject;
import org.kevoree.modeling.api.KModelVisitor;
import org.kevoree.modeling.api.KVisitResult;
import org.kevoree.modeling.api.abs.AbstractKObject;
import org.kevoree.modeling.api.data.cache.KCacheEntry;
import org.kevoree.modeling.api.data.manager.AccessMode;
import org.kevoree.modeling.api.data.manager.JsonRaw;

public class JsonModelSerializer {

    public static void serialize(final KObject model, final Callback<String> callback) {
        ((AbstractKObject) model)._manager.getRoot(model.universe(), model.now(), new Callback<KObject>() {
            @Override
            public void on(final KObject rootObj) {
                boolean isRoot = false;
                if (rootObj != null) {
                    isRoot = rootObj.uuid() == model.uuid();
                }
                final StringBuilder builder = new StringBuilder();
                builder.append("[\n");
                printJSON(model, builder, isRoot);
                model.visit(new KModelVisitor() {
                    @Override
                    public KVisitResult visit(KObject elem) {
                        boolean isRoot2 = false;
                        if (rootObj != null) {
                            isRoot2 = rootObj.uuid() == elem.uuid();
                        }
                        builder.append(",\n");
                        try {
                            printJSON(elem, builder, isRoot2);
                        } catch (Exception e) {
                            e.printStackTrace();
                            builder.append("{}");
                        }
                        return KVisitResult.CONTINUE;
                    }
                },new Callback<Throwable>() {
                    @Override
                    public void on(Throwable throwable) {
                        builder.append("\n]\n");
                        callback.on(builder.toString());
                    }
                });
            }
        });
    }

    public static void printJSON(KObject elem, StringBuilder builder, boolean isRoot) {
        if (elem != null) {
            KCacheEntry raw = ((AbstractKObject) elem)._manager.entry(elem, AccessMode.READ);
            if (raw != null) {
                builder.append(JsonRaw.encode(raw, elem.uuid(), elem.metaClass(), isRoot));
            }
        }
    }

}


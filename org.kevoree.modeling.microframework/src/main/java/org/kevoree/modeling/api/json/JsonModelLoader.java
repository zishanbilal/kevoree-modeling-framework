package org.kevoree.modeling.api.json;

import org.kevoree.modeling.api.Callback;
import org.kevoree.modeling.api.KObject;
import org.kevoree.modeling.api.KView;
import org.kevoree.modeling.api.abs.AbstractKObject;
import org.kevoree.modeling.api.data.AccessMode;
import org.kevoree.modeling.api.data.JsonRaw;
import org.kevoree.modeling.api.meta.MetaAttribute;
import org.kevoree.modeling.api.meta.MetaReference;
import org.kevoree.modeling.api.meta.MetaType;
import org.kevoree.modeling.api.time.TimeTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 28/08/13
 * Time: 13:08
 */

public class JsonModelLoader {

    private static void loadObjects(Lexer lexer, final KView factory, final Callback<List<KObject>> callback) {
        final List<KObject> loaded = new ArrayList<KObject>();
        final List<Map<String, Object>> alls = new ArrayList<Map<String, Object>>();
        Map<String, Object> content = new HashMap<String, Object>();
        String currentAttributeName = null;
        Set<String> arrayPayload = null;
        JsonToken currentToken = lexer.nextToken();
        while (currentToken.tokenType() != Type.EOF) {
            if (currentToken.tokenType().equals(Type.LEFT_BRACKET)) {
                arrayPayload = new HashSet<String>();
            } else if (currentToken.tokenType().equals(Type.RIGHT_BRACKET)) {
                content.put(currentAttributeName, arrayPayload);
                arrayPayload = null;
                currentAttributeName = null;
            } else if (currentToken.tokenType().equals(Type.LEFT_BRACE)) {
                content = new HashMap<String, Object>();
            } else if (currentToken.tokenType().equals(Type.RIGHT_BRACE)) {
                alls.add(content);
                content = new HashMap<String, Object>();
            } else if (currentToken.tokenType().equals(Type.VALUE)) {
                if (currentAttributeName == null) {
                    currentAttributeName = currentToken.value().toString();
                } else {
                    if (arrayPayload == null) {
                        content.put(currentAttributeName, currentToken.value().toString());
                        currentAttributeName = null;
                    } else {
                        arrayPayload.add(currentToken.value().toString());
                    }
                }
            }
            currentToken = lexer.nextToken();
        }
        long[] keys = new long[alls.size()];
        for (int i = 0; i < alls.size(); i++) {
            Long kid = Long.parseLong(alls.get(i).get(JsonModelSerializer.KEY_UUID).toString());
            keys[i] = kid;
        }
        /*
        factory.dimension().universe().storage().timeTrees(keys, new Callback<TimeTree[]>() {
            @Override
            public void on(TimeTree[] timeTrees) {
                for (int i = 0; i < alls.size(); i++) {
                    Map<String, Object> elem = alls.get(i);
                    String meta = elem.get(JsonModelSerializer.KEY_META).toString();
                    Long kid = Long.parseLong(elem.get(JsonModelSerializer.KEY_UUID).toString());
                    boolean isRoot = false;
                    Object root = elem.get(JsonModelSerializer.KEY_ROOT);
                    if (root != null) {
                        isRoot = root.toString().equals("true");
                    }
                    TimeTree timeTree = timeTrees[i];
                    timeTree.insert(factory.now());
                    KObject current = factory.createProxy(factory.metaClass(meta), timeTree, kid);
                    if (isRoot) {
                        ((AbstractKObject) current).setRoot(true);
                    }
                    loaded.add(current);
                    Object[] payloadObj = factory.dimension().universe().storage().raw(current, AccessMode.WRITE);
                    String[] elemKeys = elem.keySet().toArray(new String[elem.size()]);
                    for (int j = 0; j < elemKeys.length; j++) {
                        String k = elemKeys[j];
                        MetaAttribute att = current.metaAttribute(k);
                        if (att != null) {
                            Object contentPayload = elem.get(k);
                            if (contentPayload != null) {
                                payloadObj[att.index()] = att.strategy().load(contentPayload.toString(), att, factory.now());
                            }
                        } else {
                            MetaReference ref = current.metaReference(k);
                            if (ref != null) {
                                if (ref.single()) {
                                    Long refPayloadSingle;
                                    try {
                                        refPayloadSingle = Long.parseLong(elem.get(k).toString());
                                        payloadObj[ref.index()] = refPayloadSingle;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    try {
                                        Set<Long> convertedRaw = new HashSet<Long>();
                                        Set<String> plainRawSet = (Set<String>) elem.get(k);
                                        String[] plainRawList = plainRawSet.toArray(new String[plainRawSet.size()]);
                                        for (int l = 0; l < plainRawList.length; l++) {
                                            String plainRaw = plainRawList[l];
                                            try {
                                                Long converted = Long.parseLong(plainRaw);
                                                convertedRaw.add(converted);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        payloadObj[ref.index()] = convertedRaw;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
                if (callback != null) {
                    callback.on(loaded);
                }
            }
        });
        */
    }


    public static void load(KView _factory, String payload, final Callback<Throwable> callback) {
        if (payload == null) {
            callback.on(null);
        } else {
            Lexer lexer = new Lexer(payload);
            JsonToken currentToken = lexer.nextToken();
            if (currentToken.tokenType() != Type.LEFT_BRACKET) {
                callback.on(null);
            } else {
                loadObjects(lexer, _factory, new Callback<List<KObject>>() {
                    @Override
                    public void on(List<KObject> kObjects) {
                        callback.on(null);
                    }
                });
            }
        }
    }

}


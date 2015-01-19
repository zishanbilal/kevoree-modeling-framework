package org.kevoree.modeling.api.json;

import org.kevoree.modeling.api.Callback;
import org.kevoree.modeling.api.KObject;
import org.kevoree.modeling.api.ModelVisitor;
import org.kevoree.modeling.api.ThrowableCallback;
import org.kevoree.modeling.api.VisitResult;
import org.kevoree.modeling.api.data.AccessMode;
import org.kevoree.modeling.api.data.JsonRaw;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 28/08/13
 * Time: 11:08
 */

public class JsonModelSerializer {

    public static final String KEY_META = "@meta";

    public static final String KEY_UUID = "@uuid";

    public static final String KEY_ROOT = "@root";

    public static final String PARENT_META = "@parent";

    public static final String PARENT_REF_META = "@ref";

    public static final String INBOUNDS_META = "@inbounds";

    public static final String TIME_META = "@time";

    public static final String DIM_META = "@universe";

    //@Override
    public static void serialize(KObject model, final ThrowableCallback<String> callback) {
        final StringBuilder builder = new StringBuilder();
        builder.append("[\n");
        printJSON(model, builder);
        model.graphVisit(new ModelVisitor() {
            @Override
            public VisitResult visit(KObject elem) {
                builder.append(",");
                try {
                    printJSON(elem, builder);
                } catch (Exception e) {
                    e.printStackTrace();
                    builder.append("{}");
                }
                return VisitResult.CONTINUE;
            }
        }, new Callback<Throwable>() {
            @Override
            public void on(Throwable throwable) {
                builder.append("]\n");
                callback.on(builder.toString(), throwable);
            }
        });
    }

    public static void printJSON(KObject elem, StringBuilder builder) {
        if (elem != null) {
            Object[] raw = elem.view().universe().model().storage().raw(elem, AccessMode.READ);
            if(raw!=null){
                builder.append(JsonRaw.encode(raw,elem.uuid(),elem.metaClass()));
            }

            /*
            builder.append("{\n");
            builder.append("\t\"" + KEY_META + "\" : \"");
            builder.append(elem.metaClass().metaName());
            builder.append("\"\n");
            builder.append("\t,\"" + KEY_UUID + "\" : \"");
            builder.append(elem.uuid());
            builder.append("\"\n");
            if (elem.isRoot()) {
                builder.append("\t,\"" + KEY_ROOT + "\" : \"");
                builder.append("true");
                builder.append("\"\n");
            }
            int metaAttLength = elem.metaClass().metaAttributes().length;
            int metaRefLength = elem.metaClass().metaReferences().length;
            for (int i = 0; i < metaAttLength; i++) {
                Object payload = elem.get(elem.metaClass().metaAttributes()[i]);
                if (payload != null) {
                    builder.append("\t");
                    builder.append(",\"");
                    builder.append(elem.metaClass().metaAttributes()[i].metaName());
                    builder.append("\" : \"");
                    builder.append(elem.metaClass().metaAttributes()[i].metaType().save(payload));
                    builder.append("\"\n");
                }
            }
            for (int i = 0; i < metaRefLength; i++) {
                Object[] raw = elem.view().universe().model().storage().raw(elem, AccessMode.READ);
                Object payload = null;
                if (raw != null) {
                    payload = raw[elem.metaClass().metaReferences()[i].index()];
                }
                if (payload != null) {
                    builder.append("\t,\"");
                    builder.append(elem.metaClass().metaReferences()[i].metaName());
                    builder.append("\" :");
                    if (elem.metaClass().metaReferences()[i].single()) {
                        builder.append("\"");
                        builder.append(payload.toString());
                        builder.append("\"\n");
                    } else {
                        Set<Long> elems = (Set<Long>) payload;
                        Long[] elemsArr = elems.toArray(new Long[elems.size()]);
                        boolean isFirst = true;
                        builder.append(" [");
                        for (int j = 0; j < elemsArr.length; j++) {
                            if (!isFirst) {
                                builder.append(",");
                            }
                            builder.append("\"");
                            builder.append(elemsArr[j] + "");
                            builder.append("\"");
                            isFirst = false;
                        }
                        builder.append("]\n");
                    }
                }
            }
            builder.append("}\n");
            */
        }
    }

}


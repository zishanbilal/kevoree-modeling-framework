package org.kevoree.modeling.format.json;

import org.kevoree.modeling.KType;
import org.kevoree.modeling.format.json.JsonString;
import org.kevoree.modeling.meta.impl.MetaReference;
import org.kevoree.modeling.memory.struct.segment.KMemorySegment;
import org.kevoree.modeling.memory.struct.segment.impl.HeapMemorySegment;
import org.kevoree.modeling.format.json.JsonFormat;
import org.kevoree.modeling.format.json.JsonObjectReader;
import org.kevoree.modeling.meta.KMeta;
import org.kevoree.modeling.meta.KMetaAttribute;
import org.kevoree.modeling.meta.KMetaClass;
import org.kevoree.modeling.meta.KMetaModel;
import org.kevoree.modeling.meta.MetaType;
import org.kevoree.modeling.meta.KPrimitiveTypes;

public class JsonRaw {

    public static Object convert(String payload, KType type) {
        if (type == KPrimitiveTypes.STRING) {
            return JsonString.unescape(payload);
        }
        if (type == KPrimitiveTypes.LONG) {
            return Long.parseLong(payload);
        }
        if (type == KPrimitiveTypes.INT) {
            return Integer.parseInt(payload);
        }
        if (type == KPrimitiveTypes.BOOL) {
            return Boolean.parseBoolean(payload);
        }
        if (type == KPrimitiveTypes.SHORT) {
            return Short.parseShort(payload);
        }
        if (type == KPrimitiveTypes.DOUBLE) {
            return Double.parseDouble(payload);
        }
        if (type == KPrimitiveTypes.FLOAT) {
            return Float.parseFloat(payload);
        }
        return null;
    }

    /*
    public static boolean decode(String payload, long now, KMetaModel metaModel, final HeapMemorySegment entry) {
        if (payload == null) {
            return false;
        }
        JsonObjectReader objectReader = new JsonObjectReader();
        objectReader.parseObject(payload);
        //Consistency check
        if (objectReader.get(JsonFormat.KEY_META) == null) {
            return false;
        } else {
            //Init metaClass before everything
            KMetaClass metaClass = metaModel.metaClassByName(objectReader.get(JsonFormat.KEY_META).toString());
            //Init the Raw manager
            entry.init(metaClass);
            //Now Fill the Raw Storage
            String[] metaKeys = objectReader.keys();
            for (int i = 0; i < metaKeys.length; i++) {
                KMeta metaElement = metaClass.metaByName(metaKeys[i]);
                Object insideContent = objectReader.get(metaKeys[i]);
                if (insideContent != null) {
                    if (metaElement != null && metaElement.metaType().equals(MetaType.ATTRIBUTE)) {
                        //TODO INFER ATT
                        KMetaAttribute metaAttribute = (KMetaAttribute) metaElement;
                        Object saved = convert(insideContent.toString(), metaAttribute.attributeType());
                        if (saved != null) {
                            entry.set(metaElement.index(), saved, metaClass);
                        }
                    } else if (metaElement != null && metaElement instanceof MetaReference) {
                        try {
                            String[] plainRawSet = objectReader.getAsStringArray(metaKeys[i]);
                            long[] convertedRaw = new long[plainRawSet.length];
                            for (int l = 0; l < plainRawSet.length; l++) {
                                try {
                                    convertedRaw[l] = Long.parseLong(plainRawSet[l]);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            entry.set(metaElement.index(), convertedRaw, metaClass);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            entry.setClean(metaModel);
            return true;
        }
    }*/

    /**
     * @native ts
     * var builder = {};
     * builder["@class"] = p_metaClass.metaName();
     * builder["@uuid"] = uuid;
     * if(isRoot){ builder["@root"] = true; }
     * var metaElements = p_metaClass.metaElements();
     * for(var i=0;i<metaElements.length;i++){
     * if (metaElements[i] != null && metaElements[i].metaType() === org.kevoree.modeling.meta.MetaType.ATTRIBUTE) {
     *      var metaAttribute = <org.kevoree.modeling.meta.KMetaAttribute>metaElements[i];
     *      if(metaAttribute.attributeType() == org.kevoree.modeling.meta.KPrimitiveTypes.CONTINUOUS){
     *            builder[metaAttribute.metaName()] = raw.getInfer(metaAttribute.index(),p_metaClass);
     *      } else {
     *            builder[metaAttribute.metaName()] = raw.get(metaAttribute.index(),p_metaClass);
     *      }
     * } else {
     *     builder[metaElements[i].metaName()] = raw.getRef(metaAttribute.index(),p_metaClass);
     * }
     * }
     * return JSON.stringify(builder);
     */
    public static String encode(KMemorySegment raw, long uuid, KMetaClass p_metaClass, boolean isRoot) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"@class\":\"");
        builder.append(p_metaClass.metaName());
        builder.append("\",\"@uuid\":");
        builder.append(uuid);
        if (isRoot) {
            builder.append(",\"" + JsonFormat.KEY_ROOT + "\":true");
        }
        KMeta[] metaElements = p_metaClass.metaElements();
        for (int i = 0; i < metaElements.length; i++) {
            KMeta loopMeta = metaElements[i];
            if (loopMeta != null && loopMeta.metaType().equals(MetaType.ATTRIBUTE)) {
                KMetaAttribute metaAttribute = (KMetaAttribute) loopMeta;
                if (metaAttribute.attributeType() == KPrimitiveTypes.CONTINUOUS) {
                    double[] inferAtt = raw.getInfer(loopMeta.index(), p_metaClass);
                    if (inferAtt != null) {
                        builder.append(",\"");
                        builder.append(loopMeta.metaName());
                        builder.append("\":[");
                        for (int j = 0; j < inferAtt.length; j++) {
                            if (j != 0) {
                                builder.append(",");
                            }
                            builder.append(inferAtt[j]);
                        }
                        builder.append("]");
                    }
                } else {
                    Object payload_res = raw.get(loopMeta.index(), p_metaClass);
                    if (payload_res != null) {
                        builder.append(",\"");
                        builder.append(loopMeta.metaName());
                        builder.append("\":\"");
                        if (metaAttribute.attributeType() == KPrimitiveTypes.STRING) {
                            builder.append(JsonString.encode(payload_res.toString()));
                        } else {
                            builder.append(payload_res.toString());
                        }
                        builder.append("\"");
                    }
                }
            } else if (loopMeta != null && loopMeta.metaType().equals(MetaType.REFERENCE)) {
                long[] refPayload = raw.getRef(loopMeta.index(), p_metaClass);
                if (refPayload != null) {
                    builder.append(",\"");
                    builder.append(loopMeta.metaName());
                    builder.append("\":[");
                    for (int j = 0; j < refPayload.length; j++) {
                        if (j != 0) {
                            builder.append(",");
                        }
                        builder.append(refPayload[j]);
                    }
                    builder.append("]");
                }
            }
        }
        builder.append("}");
        return builder.toString();
    }


}
package org.kevoree.modeling.msg;

import org.kevoree.modeling.memory.KContentKey;

public class KOperationResultMessage implements KMessage {

    public long id;
    public String value;
    public KContentKey key;

    @Override
    public String json() {
        StringBuilder buffer = new StringBuilder();
        KMessageHelper.printJsonStart(buffer);
        KMessageHelper.printType(buffer, type());
        KMessageHelper.printElem(id, KMessageLoader.ID_NAME, buffer);
        KMessageHelper.printElem(key, KMessageLoader.KEY_NAME, buffer);
        KMessageHelper.printElem(value, KMessageLoader.VALUE_NAME, buffer);
        KMessageHelper.printJsonEnd(buffer);
        return buffer.toString();
    }

    @Override
    public int type() {
        return KMessageLoader.OPERATION_RESULT_TYPE;
    }
}
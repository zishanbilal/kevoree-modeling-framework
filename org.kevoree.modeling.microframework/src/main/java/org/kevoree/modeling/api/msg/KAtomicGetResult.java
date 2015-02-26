package org.kevoree.modeling.api.msg;

/**
 * Created by duke on 24/02/15.
 */
public class KAtomicGetResult implements KMessage {

    public Long id;

    public String value;

    @Override
    public String json() {
        StringBuilder buffer = new StringBuilder();
        KMessageHelper.printJsonStart(buffer);
        KMessageHelper.printType(buffer,type());
        KMessageHelper.printElem(id, KMessageLoader.ID_NAME, buffer);
        KMessageHelper.printElem(value, KMessageLoader.VALUE_NAME, buffer);
        KMessageHelper.printJsonEnd(buffer);
        return buffer.toString();
    }

    @Override
    public int type() {
        return KMessageLoader.ATOMIC_OPERATION_RESULT_TYPE;
    }
}

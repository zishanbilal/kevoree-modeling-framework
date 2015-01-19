package org.kevoree.modeling.api.traversal.actions;

import org.kevoree.modeling.api.Callback;
import org.kevoree.modeling.api.KObject;
import org.kevoree.modeling.api.KView;
import org.kevoree.modeling.api.abs.AbstractKObject;
import org.kevoree.modeling.api.data.AccessMode;
import org.kevoree.modeling.api.meta.MetaReference;
import org.kevoree.modeling.api.traversal.KTraversalAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by duke on 18/12/14.
 */
public class KReverseAction implements KTraversalAction {

    private KTraversalAction _next;

    private MetaReference _reference;

    public KReverseAction(MetaReference p_reference) {
        this._reference = p_reference;
    }

    @Override
    public void chain(KTraversalAction p_next) {
        _next = p_next;
    }

    @Override
    public void execute(KObject[] p_inputs) {
        if (p_inputs == null || p_inputs.length == 0) {
            _next.execute(p_inputs);
            return;
        } else {
            KView currentView = p_inputs[0].view();
            List<Long> nextIds = new ArrayList<Long>();
            for (int i = 0; i < p_inputs.length; i++) {
                try {
                    AbstractKObject loopObj = (AbstractKObject) p_inputs[i];
                    Object[] raw = currentView.universe().model().storage().raw(loopObj, AccessMode.READ);


                    if (_reference == null) {
                        for (int j = 0; j < loopObj.metaClass().metaReferences().length; j++) {
                            MetaReference ref = loopObj.metaClass().metaReferences()[j];
                            Object resolved = raw[ref.index()];
                            if (resolved != null) {
                                if (resolved instanceof Set) {
                                    Set<Long> resolvedCasted = (Set<Long>) resolved;
                                    Long[] resolvedArr = resolvedCasted.toArray(new Long[resolvedCasted.size()]);
                                    for (int k = 0; k < resolvedArr.length; k++) {
                                        Long idResolved = resolvedArr[k];
                                        if (idResolved != null) {
                                            nextIds.add(idResolved);
                                        }
                                    }
                                } else {
                                    try {
                                        nextIds.add((Long) resolved);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } else {
                        MetaReference translatedRef = loopObj.internal_transpose_ref(_reference);
                        if (translatedRef != null) {
                            Object resolved = raw[translatedRef.index()];
                            if (resolved != null) {
                                if (resolved instanceof Set) {
                                    Set<Long> resolvedCasted = (Set<Long>) resolved;
                                    Long[] resolvedArr = resolvedCasted.toArray(new Long[resolvedCasted.size()]);
                                    for (int j = 0; j < resolvedArr.length; j++) {
                                        Long idResolved = resolvedArr[j];
                                        if (idResolved != null) {
                                            nextIds.add(idResolved);
                                        }
                                    }
                                } else {
                                    try {
                                        nextIds.add((Long) resolved);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //call
            currentView.lookupAll(nextIds.toArray(new Long[nextIds.size()]), new Callback<KObject[]>() {
                @Override
                public void on(KObject[] kObjects) {
                    _next.execute(kObjects);
                }
            });
        }
    }

}
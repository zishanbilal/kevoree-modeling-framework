package org.kevoree.modeling.api;

/**
 * Created by duke on 21/01/15.
 */
public interface KCurrentDefer<A> extends KDefer<A> {

    public String[] resultKeys();

    public Object resultByName(String name);

    public Object resultByDefer(KDefer defer);

    public void addDeferResult(A result);

    public void clearResults();

}

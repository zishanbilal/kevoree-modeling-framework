package org.kevoree.modeling.api;

import org.kevoree.modeling.api.time.TimeTree;
import org.kevoree.modeling.api.time.TimeView;

import java.util.Set;

/**
 * Created by duke on 9/30/14.
 */

public interface Dimension {

    public String key();

    public void parent(Callback<Dimension> callback);

    public void children(Callback<Set<Dimension>> callback);

    public void fork(Callback<Dimension> callback);

    public void save(Callback<Boolean> callback);

    public void delete(Callback<Boolean> callback);

    public void unload(Callback<Boolean> callback);

    public TimeView time(Long timePoint);

    public TimeTree globalTimeTree();

    public TimeTree timeTree(String path);

}
package org.kevoree.modeling.api.abs;

import org.kevoree.modeling.api.Callback;
import org.kevoree.modeling.api.KConfig;
import org.kevoree.modeling.api.KDefer;
import org.kevoree.modeling.api.KTimeWalker;
import org.kevoree.modeling.api.data.cache.KCacheObject;
import org.kevoree.modeling.api.data.cache.KContentKey;
import org.kevoree.modeling.api.data.manager.DefaultKDataManager;
import org.kevoree.modeling.api.data.manager.ResolutionHelper;
import org.kevoree.modeling.api.map.LongLongHashMap;
import org.kevoree.modeling.api.rbtree.IndexRBTree;
import org.kevoree.modeling.api.rbtree.TreeNode;

public class AbstractTimeWalker implements KTimeWalker {

    public AbstractTimeWalker(AbstractKObject p_origin) {
        this._origin = p_origin;
    }

    private AbstractKObject _origin = null;

    private void internal_times(final long start, final long end, Callback<long[]> cb) {
        KContentKey[] keys = new KContentKey[2];
        keys[0] = KContentKey.createGlobalUniverseTree();
        keys[1] = KContentKey.createUniverseTree(_origin.uuid());
        final DefaultKDataManager manager = (DefaultKDataManager) _origin._manager;
        manager.bumpKeysToCache(keys, new Callback<KCacheObject[]>() {
            @Override
            public void on(KCacheObject[] kCacheObjects) {
                final LongLongHashMap objUniverse = (LongLongHashMap) kCacheObjects[1];
                if (kCacheObjects[0] == null || kCacheObjects[1] == null) {
                    cb.on(null);
                } else {
                    final long[] collectedUniverse = ResolutionHelper.universeSelectByRange((LongLongHashMap) kCacheObjects[0], (LongLongHashMap) kCacheObjects[1], start, end, _origin.universe());
                    KContentKey[] timeTreeToLoad = new KContentKey[collectedUniverse.length];
                    for (int i = 0; i < collectedUniverse.length; i++) {
                        timeTreeToLoad[i] = KContentKey.createTimeTree(collectedUniverse[i], _origin.uuid());
                    }
                    manager.bumpKeysToCache(timeTreeToLoad, new Callback<KCacheObject[]>() {
                        @Override
                        public void on(KCacheObject[] timeTrees) {
                            LongLongHashMap collector = new LongLongHashMap(KConfig.CACHE_INIT_SIZE, KConfig.CACHE_LOAD_FACTOR);
                            long previousDivergenceTime = end;
                            for (int i = 0; i < collectedUniverse.length; i++) {
                                IndexRBTree timeTree = (IndexRBTree) timeTrees[i];
                                if (timeTree != null) {
                                    long currentDivergenceTime = objUniverse.get(collectedUniverse[i]);
                                    TreeNode initNode;
                                    if (i == 0) { //first iteration, right side inclusive
                                        initNode = timeTree.previousOrEqual(previousDivergenceTime);
                                    } else {
                                        initNode = timeTree.previous(previousDivergenceTime);
                                    }
                                    while (initNode != null && initNode.getKey() >= currentDivergenceTime) {
                                        collector.put(collector.size(), initNode.getKey());
                                        initNode = initNode.previous();
                                    }
                                    previousDivergenceTime = currentDivergenceTime;
                                }
                            }
                            long[] orderedTime = new long[collector.size()];
                            for (int i = 0; i < collector.size(); i++) {
                                orderedTime[i] = collector.get(i);
                            }
                            cb.on(orderedTime);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void allTimes(Callback<long[]> cb) {
        internal_times(KConfig.BEGINNING_OF_TIME, KConfig.END_OF_TIME, cb);
    }

    @Override
    public void timesBefore(long endOfSearch, Callback<long[]> cb) {
        internal_times(KConfig.BEGINNING_OF_TIME, endOfSearch, cb);
    }

    @Override
    public void timesAfter(long beginningOfSearch, Callback<long[]> cb) {
        internal_times(beginningOfSearch, KConfig.END_OF_TIME, cb);
    }

    @Override
    public void timesBetween(long beginningOfSearch, long endOfSearch, Callback<long[]> cb) {
        internal_times(beginningOfSearch, endOfSearch, cb);
    }

}
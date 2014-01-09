package org.kevoree.modeling.api.time

import org.kevoree.modeling.api.KMFContainer
import org.kevoree.modeling.api.persistence.PersistenceKMFFactory
import org.kevoree.modeling.api.persistence.KMFContainerProxy
import org.kevoree.modeling.api.trace.TraceSequence
import java.util.Date

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 11/11/2013
 * Time: 16:35
 */

trait TimeAwareKMFFactory : PersistenceKMFFactory {

    fun shiftElem(path: String, relativeNow: TimePoint) {
        //TODO
    }
    fun shiftQuery(query: String, relativeNow: TimePoint) {
        //TODO
    }

    var relativeTime: TimePoint
    var queryMap: MutableMap<String, TimePoint>
    var timedElement: MutableMap<String, TimePoint>
    var relativityStrategy: RelativeTimeStrategy

    fun setPrevious(p: TimePoint) {
        datastore!!.put(TimeSegment.PREVIOUS.name(), relativeTime.toString(), p.toString())
    }

    override fun persist(elem: KMFContainer) {
        if (datastore != null) {
            val traces = elem.toTraces(true, true)
            val traceSeq = compare.createSequence()
            traceSeq.populate(traces)
            var currentNow = (elem as? TimeAwareKMFContainer)?.now
            if (currentNow == null) {
                currentNow = relativeTime
            }
            val currentPath = elem.path()!!
            datastore!!.put(TimeSegment.RAW.name(), "$currentNow/$currentPath", traceSeq.exportToString())
            datastore!!.put(TimeSegment.LATEST.name(), currentPath, currentNow.toString())
            val previousType = datastore!!.get(TimeSegment.TYPE.name(), elem.path()!!)
            if (previousType == null) {
                datastore!!.put(TimeSegment.TYPE.name(), elem.path()!!, elem.metaClassName())

            }
            if (elem is KMFContainerProxy) {
                elem.originFactory = this
            }
        }
    }

    fun removeVersion(t: TimePoint) {
        //TODO

    }

    override fun remove(elem: KMFContainer) {
        if (datastore != null) {
            datastore!!.remove(TimeSegment.RAW.name(), elem.path()!!);
            datastore!!.remove(TimeSegment.TYPE.name(), elem.path()!!);
            datastore!!.remove(TimeSegment.LATEST.name(), elem.path()!!);
        }
    }

    private fun resolvedTimeFromParams(path: String): TimePoint? {
        for (staticPathV in timedElement) {
            if (staticPathV.key == path) {
                return staticPathV.value
            }
        }
        //TO QUERY when upding Kotlin
        /*
        for(staticPathV in queryMap){
            if("".match(staticPathV.key)){
                return staticPathV.value
            }
        } */
        return null
    }


    fun lookupFromTime(path: String, time: TimePoint): KMFContainer? {
        return internal_lookupFrom(path, null, time)
    }

    override fun lookupFrom(path: String, origin: KMFContainer?): KMFContainer? {
        return internal_lookupFrom(path, origin, null)
    }

    fun internal_lookupFrom(path: String, origin: KMFContainer?, time: TimePoint?): KMFContainer? {

        var path2 = path
        if (path2 == "/") {
            path2 = ""
        }
        if (path2.startsWith("/")) {
            path2 = path2.substring(1)
        }
        var currentTime: TimePoint? = (origin as? TimeAwareKMFContainer)?.now
        if (time != null) {
            currentTime = time
        }

        when(relativityStrategy) {
            RelativeTimeStrategy.BIG_BANG_FIRST -> {
                currentTime = relativeTime
                val resolved = resolvedTimeFromParams(path2)
                if (resolved != null) {
                    currentTime = resolved
                }
            }
            RelativeTimeStrategy.LATEST_FIRST -> {
                currentTime = TimePoint.create(datastore!!.get(TimeSegment.LATEST.name(), path2)!!)
                val resolved = resolvedTimeFromParams(path2)
                if (resolved != null) {
                    currentTime = resolved
                }
            }
            RelativeTimeStrategy.RELATIVE_FIRST -> {
                if (currentTime == null) {
                    currentTime = relativeTime
                }
            }
            else -> {
                //
            }
        }

        if (currentTime == null) {
            //try last
            currentTime = TimePoint.create(datastore!!.get(TimeSegment.LATEST.name(), path2)!!)
        }

        val composedKey = currentTime!!.toString() + path2
        if (elem_cache.containsKey(composedKey)) {
            return elem_cache.get(composedKey)
        }
        if (datastore != null) {
            val typeName = datastore!!.get(TimeSegment.TYPE.name(), path2)
            if (typeName != null) {
                val elem = create(typeName) as TimeAwareKMFContainer
                elem_cache.put(composedKey, elem)
                elem.originFactory = this
                elem.isResolved = false
                elem.now = currentTime!!  //must before OriginPath
                elem.setOriginPath(path2)
                return elem
            } else {
                throw Exception("Empty Type Name for " + path2);
            }
        }
        return null
    }

    override fun getTraces(origin: KMFContainer): TraceSequence? {
        val currentNow = (origin as? TimeAwareKMFContainer)!!.now
        val currentPath = origin.path()!!
        var composedKey = "$currentNow/$currentPath"
        var sequence = compare.createSequence()
        var traces = datastore?.get(TimeSegment.RAW.name(), composedKey)
        if (traces != null) {
            sequence.populateFromString(traces!!)
            return sequence
        }
        //try with previous timepoint
        val previousTimePoint = (origin as? TimeAwareKMFContainer)!!.previousTimePoint
        if (previousTimePoint != null) {
            composedKey = "$previousTimePoint/$currentPath"
            traces = datastore?.get(TimeSegment.RAW.name(), composedKey)
            if (traces != null) {
                sequence.populateFromString(traces!!)
                return sequence
            }
        }
        //try with previous version of the current version
        return resolvePreviousGetTrace(origin, relativeTime.toString(), sequence)
    }

    private fun resolvePreviousGetTrace(origin: KMFContainer, current: String, sequence: TraceSequence): TraceSequence? {
        var previous = datastore?.get(TimeSegment.PREVIOUS.name(), current.toString())
        if (previous == null) {
            return null;
        }
        val currentPath = origin.path()!!
        var composedKey = "$current/$currentPath"
        var traces = datastore?.get(TimeSegment.RAW.name(), composedKey)
        if (traces != null) {
            sequence.populateFromString(traces!!)
            return sequence
        } else {
            return resolvePreviousGetTrace(origin, previous!!, sequence)
        }
    }


}

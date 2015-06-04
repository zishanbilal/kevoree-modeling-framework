package org.kevoree.modeling.extrapolation;

import org.kevoree.modeling.KObject;
import org.kevoree.modeling.abs.AbstractKObject;
import org.kevoree.modeling.extrapolation.maths.PolynomialFitEjml;
import org.kevoree.modeling.memory.AccessMode;
import org.kevoree.modeling.memory.KCacheElementSegment;
import org.kevoree.modeling.meta.MetaAttribute;
import org.kevoree.modeling.meta.MetaClass;
import org.kevoree.modeling.meta.PrimitiveTypes;

public class PolynomialExtrapolation implements Extrapolation {

    private static int _maxDegree=20;


    @Override
    public Object extrapolate(KObject current, MetaAttribute attribute) {
        KCacheElementSegment raw = ((AbstractKObject) current)._manager.segment(current, AccessMode.READ);
         if (raw != null) {
            Double extrapolatedValue = extrapolateValue(raw.getInfer(attribute.index(),current.metaClass()), current.now(),raw.originTime());
            if (attribute.attributeType() == PrimitiveTypes.DOUBLE) {
                return extrapolatedValue;
            } else if (attribute.attributeType() == PrimitiveTypes.LONG) {
                return extrapolatedValue.longValue();
            } else if (attribute.attributeType() == PrimitiveTypes.FLOAT) {
                return extrapolatedValue.floatValue();
            } else if (attribute.attributeType() == PrimitiveTypes.INT) {
                return extrapolatedValue.intValue();
            } else if (attribute.attributeType() == PrimitiveTypes.SHORT) {
                return extrapolatedValue.shortValue();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    //Encoded polynomial: Degree, Number of samples, step, last time, and list of weights
    private final static int DEGREE=0;
    private final static int NUMSAMPLES=1;
    private final static int STEP=2;
    private final static int LASTTIME=3;
    private final static int WEIGHTS=4;

    private Double extrapolateValue(double[] encodedPolynomial, long time, long timeOrigin) {
        if(encodedPolynomial==null){
            return 0.0;
        }

        double result = 0;
        double power = 1;
        if(encodedPolynomial[STEP]==0){
            return encodedPolynomial[WEIGHTS];
        }
        double t=(time-timeOrigin)/encodedPolynomial[STEP];
        for (int j = 0; j < encodedPolynomial[DEGREE]; j++) {
            result += encodedPolynomial[j+WEIGHTS] * power;
            power = power * t;
        }
        return result;
    }

    private double maxErr(double precision, int degree) {
        double tol = precision;
    /*    if (_prioritization == Prioritization.HIGHDEGREES) {
            tol = precision / Math.pow(2, _maxDegree - degree);
        } else if (_prioritization == Prioritization.LOWDEGREES) {*/
            tol = precision / Math.pow(2, degree + 0.5);
       /* } else if (_prioritization == Prioritization.SAMEPRIORITY) {
            tol = precision * degree * 2 / (2 * _maxDegree);
        }*/
        return tol;
    }


    public boolean insert(long time, double value, long timeOrigin, KCacheElementSegment raw, int index, double precision, MetaClass metaClass) {
        double[] encodedPolynomial = raw.getInfer(index, metaClass);
        if (encodedPolynomial == null) {
            initial_feed(time, value, raw, index, metaClass);
            return true;
        }

        //Set the step
        if (encodedPolynomial[NUMSAMPLES] == 1) {
            encodedPolynomial[STEP] = time-raw.originTime();
        }


        int deg=encodedPolynomial.length-WEIGHTS-1;
        int num= (int)encodedPolynomial[NUMSAMPLES];

        double maxError = maxErr(precision,deg);
        //If the current model fits well the new value, return
        if (Math.abs(extrapolateValue(encodedPolynomial, time,timeOrigin ) - value) <= maxError) {
            raw.setInferElem(index,NUMSAMPLES,encodedPolynomial[NUMSAMPLES]+1,metaClass);
            raw.setInferElem(index,LASTTIME,time-timeOrigin,metaClass);
            return true;
        }
        //If not, first check if we can increase the degree
        int newMaxDegree = Math.min(num - 1, _maxDegree);
        if (deg < newMaxDegree) {
            deg++;
            int ss = Math.min(deg * 2, num);
            double[] times = new double[ss + 1];
            double[] values = new double[ss + 1];
            for (int i = 0; i < ss; i++) {
                times[i] = (i * num*(encodedPolynomial[LASTTIME]-timeOrigin) / (ss*encodedPolynomial[STEP]));
                values[i] = internal_extrapolate(times[i], encodedPolynomial);
            }
            times[ss] = (time-timeOrigin)/encodedPolynomial[STEP];
            values[ss] = value;
            PolynomialFitEjml pf = new PolynomialFitEjml(deg);
            pf.fit(times, values);
            if (tempError(pf.getCoef(), times, values) <= maxError) {
                raw.extendInfer(index,encodedPolynomial.length+1,metaClass);
                for (int i = 0; i < pf.getCoef().length; i++) {
                    raw.setInferElem(index,i+WEIGHTS,pf.getCoef()[i],metaClass);
                }
                raw.setInferElem(index,DEGREE,deg,metaClass);
                raw.setInferElem(index,NUMSAMPLES,num+1,metaClass);
                raw.setInferElem(index,LASTTIME,time-timeOrigin,metaClass);
                return true;
            }
        }
        return false;

    }

    private double tempError(double[] computedWeights, double[] times, double[] values) {
        double maxErr = 0;
        double temp;
        double ds;
        for (int i = 0; i < times.length; i++) {
            temp = Math.abs(values[i] - test_extrapolate(times[i], computedWeights));
            if (temp > maxErr) {
                maxErr = temp;
            }
        }
        return maxErr;
    }

    private double test_extrapolate(double time, double[] weights) {
        double result = 0;
        double power = 1;
        for (int j = 0; j < weights.length; j++) {
            result += weights[j] * power;
            power = power * time;
        }
        return result;
    }

    private double internal_extrapolate(double t, double[] encodedPolynomial) {
        double result = 0;
        double power = 1;
        if(encodedPolynomial[STEP]==0){
            return encodedPolynomial[WEIGHTS];
        }
        for (int j = 0; j < encodedPolynomial[DEGREE]; j++) {
            result += encodedPolynomial[j+WEIGHTS] * power;
            power = power * t;
        }
        return result;
    }

    private void initial_feed(long time, double value, KCacheElementSegment raw, int index, MetaClass metaClass) {
        //Create initial array of the constant elements + 1 for weights.
        raw.extendInfer(index,WEIGHTS+1,metaClass); //Create N constants and 1 for the weights
        raw.setInferElem(index,DEGREE,0,metaClass); //polynomial degree of 0
        raw.setInferElem(index,NUMSAMPLES,1,metaClass); //contains 1 sample
        raw.setInferElem(index,LASTTIME,0,metaClass); //the last point in time is 0 = time origin
        raw.setInferElem(index,STEP,0,metaClass); //Number of step
        raw.setInferElem(index,WEIGHTS,value,metaClass);
    }



    @Override
    public void mutate(KObject current, MetaAttribute attribute, Object payload) {
        KCacheElementSegment raw = ((AbstractKObject) current)._manager.segment(current, AccessMode.READ);

            if (!insert(current.now(), castNumber(payload), raw.originTime(),raw, attribute.index(), attribute.precision(), current.metaClass())) {
                //PolynomialModel pol = createPolynomialModel(previousPol.lastIndex(), attribute.precision());
               // pol.insert(previousPol.lastIndex(), previousPol.extrapolate(previousPol.lastIndex()));
                //pol.insert(current.now(), castNumber(payload));
                KCacheElementSegment raw2=((AbstractKObject) current)._manager.segment(current, AccessMode.WRITE);
                long prevTime = (long)raw.getInferElem(attribute.index(),LASTTIME,current.metaClass())+raw.originTime();
                double val = extrapolateValue(raw.getInfer(attribute.index(),current.metaClass()),prevTime,raw.originTime());
                insert(prevTime,val,prevTime,raw2,attribute.index(),attribute.precision(),current.metaClass());
                insert(current.now(), castNumber(payload), raw2.originTime(),raw2, attribute.index(), attribute.precision(), current.metaClass());

                } else {
                //TODO Value fit the previous polynomial, but if degrees has changed we have to set the object to dirty for the next save batch

            }

    }

    @Override
    public String save(Object cache, MetaAttribute attribute) {
        return null;
    }

    @Override
    public Object load(String payload, MetaAttribute attribute, long now) {
        return null;
    }


    /**
     * @native ts
     * return +payload;
     */
    private Double castNumber(Object payload) {
        if (payload instanceof Double) {
            return (Double) payload;
        } else {
            return Double.parseDouble(payload.toString());
        }
    }


    private static PolynomialExtrapolation INSTANCE;

    public static Extrapolation instance() {
        if (INSTANCE == null) {
            INSTANCE = new PolynomialExtrapolation();
        }
        return INSTANCE;
    }



}

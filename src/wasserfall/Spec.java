/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wasserfall;

import FFT.Complex;

/**
 *
 * @author marcel
 */
public class Spec {
    double fmin;
    double hzperbin;
    Complex[] specdata;
    
    DataBlock source;

    Spec(double fmin, double hzperbin, Complex[] specdata, DataBlock source) {
        this.fmin = fmin;
        this.hzperbin = hzperbin;
        this.specdata = specdata;
        this.source = source;
    }

    public double getFmin() {
        return fmin;
    }
    
    public double getFmax() {
        return getFmin() + getFRange();
    }
    
    public double getFRange(){
        return getHzperbin() * (double) getSpecdata().length;
    }

    public double getHzperbin() {
        return hzperbin;
    }

    public Complex[] getSpecdata() {
        return specdata;
    }

    public double[] toPSD() {
        double[] out = new double[specdata.length];
        for(int i = 0; i < specdata.length; i++){
            out[i] = specdata[i].abs() / (double) DataBlock.BLOCKSIZE;
        }
        return out;
    }

    double indexToFreq(int i) {
        return getFmin() + getHzperbin() * i;
    }

    public DataBlock getSource() {
        return source;
    }
    
    

}

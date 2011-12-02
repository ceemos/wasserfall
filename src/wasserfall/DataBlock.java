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
public class DataBlock {
    public static final int BLOCKSIZE = 256;

    protected float samplesPerSecond;
    protected double ts_start;
    protected Complex[] data;
    
    public DataBlock(float samplesPerSecond, double ts_start, Complex[] c) {
        this.samplesPerSecond = samplesPerSecond;
        this.ts_start = ts_start;
        data = c;
    }
    
    protected DataBlock(){
        
    }

    public void setData(Complex[] data) {
        this.data = data;
    }

    public Complex[] getData() {
        return data;
    }

    public float getSamplesPerSecond() {
        return samplesPerSecond;
    }
    
    public double getStart(){
        return ts_start;
    }
    
    public double getLength(){
        return data.length / samplesPerSecond;
    }
    
    public double getEnd(){
        return getStart() + getEnd();
    }
    
    public int getSize(){
        return data.length;
    }
    
    
}

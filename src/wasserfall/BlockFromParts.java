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
public class BlockFromParts extends DataBlock {
    
    protected int actuallen = 0;

    public BlockFromParts(float samplesPerSecond, double ts_start) {
        this.samplesPerSecond = samplesPerSecond;
        this.ts_start = ts_start;
        data = new Complex[BLOCKSIZE];
    }

    public DataBlock resampleAndAppend(DataBlock db) {
        // 1. Resample
        double scale = db.samplesPerSecond / getSamplesPerSecond();
        Complex[] res = new Complex[(int) (db.getSize() / scale)];
        DataBlock out = new DataBlock(samplesPerSecond, ts_start, res);
        if(scale == 2){
            for(int i = 0; i < db.getSize() / scale; i++){
                res[i] = db.getData()[i*2].plus(db.getData()[i*2+1]).times(0.5);
            }
        } else if(scale == 1){
            System.arraycopy(db.data, 0, res, 0, db.getSize());
        } else {
            throw new UnsupportedOperationException("noch kein echtes Resampling");
        }
        
        // 2. add
        if(isReady()){
            removePart(res.length);
        }
        addData(res);
        
        return out;
    }
    
    public void addData(Complex[] d){
        System.arraycopy(d, 0, data, actuallen, d.length);
        actuallen = actuallen + d.length;
    }

    public boolean isReady() {
        return actuallen >= BLOCKSIZE;
    }

    public void removePart(int size) {
        actuallen = actuallen - size;
        ts_start = ts_start + (size / samplesPerSecond);
        System.arraycopy(data, size, data, 0, data.length - size);
    }
    
}

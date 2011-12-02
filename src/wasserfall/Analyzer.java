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
public class Analyzer {
    private SpecSink sink;
    private Analyzer next;
    
    private BlockFromParts parts = null;

    public Analyzer(SpecSink sink, Analyzer next) {
        this.sink = sink;
        this.next = next;
        
    }
    
    public Analyzer(SpecSink sink, Analyzer next, float samplesPerSecond) {
        this.sink = sink;
        this.next = next;
        parts = new BlockFromParts(samplesPerSecond, 0); // Hackish?
    }

    void consumeData(DataBlock db) {
        
        if(parts == null){
            parts = new BlockFromParts(db.getSamplesPerSecond() / 2, db.getStart());
        }

        DataBlock resampled = parts.resampleAndAppend(db);
        
        if(next != null){
            next.consumeData(resampled);
        }
        if(parts.isReady()){
            doAnalysis(parts);
        }
        
    }

    protected void doAnalysis(DataBlock db) {
        // TODO: Fenster?
        Complex[] res = applyWindow(db.getData());
        
        // pure java
        //res = FFT.FFT.fft(res);
        // native
        res = nativefft.NativeFFT.getInstance().fft(res); 
        
        // Hochpass + Spiegelf. entfernen 
        Complex[] specdata = new Complex[DataBlock.BLOCKSIZE / 4];
        System.arraycopy(res, DataBlock.BLOCKSIZE / 4, specdata, 0, specdata.length);
        
        double hzperbin = db.getSamplesPerSecond() / DataBlock.BLOCKSIZE;
        double fmin = db.getSamplesPerSecond() / 4;
        
        Spec s = new Spec(fmin, hzperbin, specdata, db);
        
        sink.takeSpec(s);
    }
    
    private Complex[] applyWindow(Complex[] data){
        Complex[] res = new Complex[data.length];
        int size = data.length;
        for(int i = 0; i < size; i++){
            double window = Math.sin((double) i / (double) size * Math.PI);
            res[i] = data[i].times(window);
        }
        return res;
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wasserfall;

import FFT.Complex;
import fftscope.MP3Source;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author marcel
 */
public class WasserfallController {
    
    public static void main(String[] args){
        //String filename = "04-Hit That.short.mp3";
        String filename = "METALLICA - ORION.mp3";
        //String filename = "01-THE WHITE STRIPES _ SEVEN NATION ARMY.mp3";
        new WasserfallController(filename).start();
    }
    
    private final String file;
    private MP3Source source;
    private WasserfallDrawer drawer;
    private ArrayList<Analyzer> analyzers;

    private WasserfallController(String filename) {
        file = filename;
    }

    private void start() {
        try{
            source = new MP3Source(new File(file));
            drawer = new WasserfallDrawer(new File(file + ".png"));
            
            // native
            nativefft.NativeFFT.init(DataBlock.BLOCKSIZE);
            
            analyzers = new ArrayList<Analyzer>();
            Analyzer a = new Analyzer(drawer, null); // Kettenende
            analyzers.add(a);
            for(int i = 0; i < 7; i++){
                Analyzer b = new Analyzer(drawer, a); // Kette
                analyzers.add(b);
                a = b;
            }
            a = new Analyzer(drawer, a, source.getSamplesPerSecond()); // Kettenanfang
            analyzers.add(a);
            
            drawer.init(source.getSamplesPerSecond(), analyzers.size() + 1,  source.getLengthInSamples());
            
            pumpData(a);
            
            //drawer.drawGrids();
            drawer.save();
        
        
        } catch(Exception e){
            e.printStackTrace();
        }
        
    }

    private void pumpData(Analyzer a) {
        Float[] data;
        for(int n = 0;(data = source.getBlock(DataBlock.BLOCKSIZE*2)) != null; n++){
            Complex[] c = new Complex[DataBlock.BLOCKSIZE];
            c = compFromReal(data);
            double ts_start = n * DataBlock.BLOCKSIZE / source.getSamplesPerSecond();
            DataBlock db = new DataBlock(source.getSamplesPerSecond(), ts_start, c); 
            a.consumeData(db);
        }
    }
    
    private Complex[] compFromReal(Float[] data) {
        Complex[] c = new Complex[data.length/2];
        for(int i = 0; i < data.length/2; i++){
            c[i] = new Complex((data[i*2] + data[i*2+1])/2, 0); // stereo verschmelzen
        }
        return c;
    }
 
}

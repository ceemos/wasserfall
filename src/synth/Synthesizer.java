/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synth;

import java.util.ArrayList;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;

/**
 *
 * @author marcel
 */
public class Synthesizer {
    
    ArrayList<Tone> tones;
    AudioDevice device;
    double samplesPerSecond;
    
    double pos;
    double volume = 20000;

    public Synthesizer(AudioDevice device, double samplesPerSecond) {
        this.tones = new ArrayList<Tone>();
        this.device = device;
        this.pos = 0;
        this.samplesPerSecond = samplesPerSecond;
    }
    
    public void addTone(Tone t){
        tones.add(t);
    }
    
    public void play(int nSamples){
        short[] out = new short[nSamples];
        ArrayList<Tone> tonestokill = new ArrayList<Tone>();
        for(int n = 0; n < nSamples; n++){
            double val = 0;
            double tfakt = 2*Math.PI * pos;
            for(Tone t : tones){
                if(t.amp != 0){
                if(t.ts_start < pos){
                    if(t.ts_end > pos){
                        // play
                        double phi = t.freq * tfakt;
                        // sin-Synth.
                        //double a = t.amp * Math.sin(phi);
                        // square-Synth.
                        double a = 0;
                        int cycle = (int) (phi / 2 / Math.PI);
                        phi -= (double) cycle * Math.PI * 2;
//                        if(phi < Math.PI){ // square
//                            a = t.amp;
//                        } else {
//                            a = -t.amp;
//                        }
                        // dreieck
                        if(phi < Math.PI/2){
                            a = t.amp * phi;
                        } else if(phi < Math.PI){
                            a = t.amp * (Math.PI - phi);
                        } else if(phi < 3 * Math.PI/2){
                            a = - t.amp * (phi - Math.PI);
                        } else {
                            a = -t.amp * (2*Math.PI - phi);
                        }
                        // trapez
                        if(a > t.amp) a = t.amp;
                        if(a < -t.amp) a = -t.amp;
                        

                        val += a;
                    } else {
                        // kill
                        tonestokill.add(t);
                    }
                }
                }
            }
            tones.removeAll(tonestokill);
//            val = val < 0 ? -5 : 5; // 1 bit...
            out[n] = (short) (val * volume);
            pos = pos + 1.0/samplesPerSecond;
        }
        output(out);
    }

    private void output(short[] out) {
        try {
            device.write(out, 0, out.length);
        } catch (JavaLayerException javaLayerException) {
        }
    }
    
    
    
    
    
}

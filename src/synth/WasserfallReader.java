/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synth;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import wasserfall.DataBlock;
import wasserfall.NoteCalc;

/**
 *
 * @author marcel
 */
public class WasserfallReader {
    
    BufferedImage bim;
    HashMap<Integer, Tone> toene;
    
    Synthesizer s;
    File file;
    
    double samplesPerSecond = 44100;
    double secondsperpixel;

    public WasserfallReader(File file, Synthesizer s) {
        this.file = file;
        this.s = s;
    }

    public void start() {
        loadImage();
        initTones();
        play();
    }

    private void loadImage() {
        try {
            bim = ImageIO.read(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void initTones() {
        int notemax = (int) NoteCalc.FtoNote(samplesPerSecond/2);
        int height = bim.getHeight();
        double notesperpixel = (double) notemax / (double) height;
        secondsperpixel = DataBlock.BLOCKSIZE / samplesPerSecond;
        
        toene = new HashMap<Integer, Tone>();
        
        for(int i = 0; i < height; i+=1){
            double note = notesperpixel * i;
            double f = NoteCalc.NoteToF(note);
            Tone t = new Tone(0, Double.MAX_VALUE, f, 0);
            toene.put(i, t);
            s.addTone(t);
        } 
    }

    private void play() {
        long tstart = System.nanoTime();
        for(int t = 0; t < bim.getWidth(); t+=1){
            for(int y = 0; y < bim.getHeight(); y+=1){
                int c = bim.getRGB(t, y);
                Color color = new Color(c);
                float[] hsb = new float[3];
                hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
                float sat = hsb[1];
                if(sat < 0.2){
                    sat = 0;
                } else {
                    //sat = 0.5f;
                }
                double amp = (Math.pow(1024, sat) - 1) / 1024;
                toene.get(y).setAmp(amp);
            }
            s.play(DataBlock.BLOCKSIZE);
//            double tsoll = secondsperpixel * (t+1);
//            double tist = (System.nanoTime()-tstart) / 1e9;
//            System.out.println("deltaT: " + (tsoll - tist));
        }
    }
    
    
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fftscope;

import FFT.Complex;
import FFT.FFT;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author marcel
 */
public class Wasserfall {
    
    DataSource ds;
    BufferedImage bim = null;
    int blocksize = 2048;
    int nblocks = 5000;
    
    float stretch = 1;
    
    int samplerate = 44100;
    float hzperbin = samplerate / blocksize;
    
    public static void main(String[] args) throws IOException{
        // FFT-Test
//        Complex[] c = new Complex[1024];
//        for(int i = 0; i < 1024; i++){
//            c[i] = new Complex(1, 0);
//        }
//        c = FFT.fft(c);
//        System.out.println("0: " + c[0] + " 1: " + c[1] + " 2: " + c[2] + " 1023:" + c[1023]);
//        System.exit(0);
        
        String filename = "04-Hit That.mp3";
        Wasserfall w = new Wasserfall();
        w.ds = new MP3Source(new File(filename));
        w.render();
        ImageIO.write(w.bim, "png", new File(filename + ".png"));
    }

    private void render() {
        bim = new BufferedImage(nblocks, (int) (FtoNote(samplerate/2)*stretch), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bim.createGraphics();
        
        Float[] lastblock = null;
        
        int sekunde = 0;
        Float[] data;
        for(int n = 0;(data = ds.getBlock(blocksize*2)) != null;n++){
            // Stereo!
            Complex[] comp = compFromReal(data);
            applyWindow(comp);
            comp = FFT.fft(comp);
            float[] power = absValue(comp);
            float max = 0;
            int fmax = 0;
            for(int i = 1; i < blocksize / 2; i++){
                fmax = power[i] > max ? i : fmax;
                max = power[i] > max ? power[i] : max; 
                
                putPixel(g, n, (int)(FtoNote(i*hzperbin)*stretch), 0.7f , normlog(power[i]), 1f);
  
            }
            if(lastblock != null){
                Complex[] comp2 = compFromReal(data, lastblock);
                applyWindow(comp2);
                comp2 = FFT.fft(comp2);
                float[] power2 = absValue(comp2);
                for(int i = 1; i < blocksize / 2; i++){
                    putPixel(g, n, (int)(FtoNote(i*hzperbin/2)*stretch), 0.7f , normlog(power2[i]), 1f);
                }
            }
            lastblock = data;
            
            if(n * blocksize / samplerate > sekunde){
                putSecondMarker(g, n, sekunde);
                sekunde++;
            }
            
            System.out.println("Frame " + n + "; FPeak: " + fmax + " Value: " + max);
        }
        
        // Frequenz-Hilfslinien
//        int a = 440;
//        int oktave = 0;
//        putFMarker(g, (int) ((a * 1 << oktave++) / hzperbin));
//        putFMarker(g, (int) ((a * 1 << oktave++) / hzperbin));
//        putFMarker(g, (int) ((a * 1 << oktave++) / hzperbin));
//        putFMarker(g, (int) ((a * 1 << oktave++) / hzperbin));
//        putFMarker(g, (int) ((a * 1 << oktave++) / hzperbin));
//        putFMarker(g, (int) ((a * 1 << oktave++) / hzperbin));
    }

    private Complex[] compFromReal(Float[] data) {
        Complex[] c = new Complex[data.length/2];
        for(int i = 0; i < data.length/2; i++){
            c[i] = new Complex((data[i*2] + data[i*2+1])/2, 0); // stereo verschmelzen
        }
        return c;
    }
    
    private Complex[] compFromReal(Float[] data, Float[] lastblock) {
        Complex[] c = new Complex[data.length];
        for(int i = 0; i < data.length/2; i++){
            c[i] = new Complex((data[i*2] + data[i*2+1])/2, 0); // stereo verschmelzen
        }
        int offset = data.length / 2;
        for(int i = 0; i < lastblock.length/2; i++){
            c[i+offset] = new Complex((lastblock[i*2] + lastblock[i*2+1])/2, 0); // stereo verschmelzen
        }
        return c;
    }
    
    private void applyWindow(Complex[] data){
        int size = data.length;
        for(int i = 0; i < size; i++){
            double window = Math.sin((double) i / (double) size * Math.PI);
            data[i] = data[i].times(window);
        }
    }

    private float[] absValue(Complex[] comp) {
        float[] r = new float[comp.length/2]; // nur der untere teil -> real to real
        for(int i = 0; i < comp.length/2; i++){
            r[i] = (float) Math.sqrt(comp[i].re() * comp[i].re() + comp[i].im() * comp[i].im()) / (float) blocksize;
        }
        return r;
    }

    private void putPixel(Graphics2D g, int x, int y, float h, float s, float b) {
        Color c = Color.getHSBColor(h, s, b);
        g.setColor(c);
        g.drawLine(x, y, x, y);
    }

    private void putSecondMarker(Graphics2D g, int x, float v) {
        Color c = new Color(1, 1, 0, 0.5f);
        g.setColor(c);
        g.drawLine(x, 0, x, blocksize/2);
        String s = String.format("%.1f", v);
        g.setFont(new Font("Sans", 0, 10));
        g.drawString(s, x, 200);
    }
    
    private void putFMarker(Graphics2D g, int y) {
        Color c = new Color(1, 0.7f, 0, 0.5f);
        g.setColor(c);
        g.drawLine(0, y, nblocks, y);
    }

    private float normlog(float f) {
        return (float) (Math.log(f+0.019) +4) /4;
    }
    
    private static double FtoNote(float f){
        return (12*Math.log(f/440)/Math.log(2))+57;
    }
    
}

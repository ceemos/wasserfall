/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wasserfall;

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
public class WasserfallDrawer implements SpecSink {
    
    File file;
    BufferedImage bim;
    Graphics2D g;
    
    double fscale = 1;
    double pixelsPerSecond = 0;

    public WasserfallDrawer(File file) {
        this.file = file;
    }

    public void takeSpec(Spec data) {
        double[] real = data.toPSD();
        for(int i = 0; i < real.length; i++){
            double fcurrent = data.indexToFreq(i);
            int x = xfromTime(data.getSource().getStart() + data.getSource().getLength() / 2);
            int y_low = yfromFreq(fcurrent - data.hzperbin / 2);
            int y_hi = yfromFreq(fcurrent + data.hzperbin / 2);
            Color c = colorfromAmp(real[i]);
            g.setColor(c);
            g.drawLine(x, y_low, x, y_hi);
        }
        
    }
    
    private int yfromFreq(double f) {
        return (int) (NoteCalc.FtoNote(f) * fscale);
        //return (int) (f * fscale);
    }

    private int xfromTime(double time) {
        return (int) (time * pixelsPerSecond);
    }

    private Color colorfromAmp(double amp) {
        return Color.getHSBColor(0.7f - normlogB(amp), normlog(amp), 1f);
        //return new Color(0, 0, 1, normlog(amp));
    }
    

    void init(float samplesPerSecond, int nOktaven, int lengthInSamples) {
        int width = lengthInSamples / DataBlock.BLOCKSIZE;
        int notemax = (int) NoteCalc.FtoNote(samplesPerSecond/2);
        int height = DataBlock.BLOCKSIZE / 4 * notemax / 12;
        
        bim = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        fscale = 1;
        fscale = height / yfromFreq(samplesPerSecond / 2);
        
        pixelsPerSecond = width / (lengthInSamples / samplesPerSecond);
        
        g = bim.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, width, height);
    }
    
    public void drawGrids(){
        for(int i = 0; i < bim.getWidth()/pixelsPerSecond; i++){
            putSecondMarker(i);
        }
        for(int i = 1; i < 150; i+=4){
            putNoteMarker(i);
        }
    }

    public void save() {
        g.dispose();
        try {
            ImageIO.write(bim, "png", file);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void putSecondMarker(float second) {
        Color c = new Color(1, 1, 0, 0.5f);
        g.setColor(c);
        int x = xfromTime(second);
        g.drawLine(x, 0, x, bim.getHeight());
        int min = (int) (second / 60);
        String s = String.format("%dm%.1fs", min, second - min * 60);
        g.setFont(new Font("Sans", 0, 10));
        for(int i = 1; i < 150; i+=12){
            int y = yfromFreq(NoteCalc.NoteToF(i));
            g.drawString(s, x + 1, y + 10);
        }
        
    }

    private void putNoteMarker(int note) {
        Color c = new Color(1, 0.7f, 0, 0.5f);
        g.setColor(c);
        double f = (double) NoteCalc.NoteToF(note);
        int y = yfromFreq(f);
        g.drawLine(0, y, bim.getWidth(), y);
        String s = String.format("(%s) %.1f Hz", NoteCalc.nameForNote(note), f);
        g.setFont(new Font("Sans", 0, 10));
        // abwechselnd versetzt, zum Platz sparen
        for(int i = note % 2; i < bim.getWidth()/pixelsPerSecond; i+=2){
            int x = xfromTime(i);
            g.drawString(s, x + 50, y);
        }
        
    }

    private float normlog(double f) {
        int a = 10;
        return (float) (Math.log(f + 1.0/(1<<a)) / Math.log(2) / a) + 1;
    }
    
    private float normlogB(double f) {
        int a = 3;
        return (float) (Math.log(f + 1.0/(1<<a)) / Math.log(2) / a) + 1;
    }

}

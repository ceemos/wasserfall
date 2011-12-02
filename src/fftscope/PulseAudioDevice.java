/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fftscope;

import java.io.DataOutputStream;
import java.io.IOException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;

/**
 *
 * @author marcel
 */
public class PulseAudioDevice implements AudioDevice {
    
    private String command = "pacat --rate=$R --channels=$C --format=s16be --client-name=Java-Streamer";
    private DataOutputStream dos = null;
    private Process p;
    
    private int outputFrequency;
    private int outputChannels;


    private  boolean isopen = false;

    public PulseAudioDevice() {
    }

    public PulseAudioDevice(int outputFrequency, int outputChannels) {
        this.outputFrequency = outputFrequency;
        this.outputChannels = outputChannels;
    }
    
    public void open(Decoder dcdr) throws JavaLayerException {
        outputFrequency = dcdr.getOutputFrequency();
        outputChannels = dcdr.getOutputChannels();
        
        isopen = true;
    }

    private void startPacat() {
        command = command.replace("$R", outputFrequency + "");
        command = command.replace("$C", outputChannels + "");
        try {
            p = Runtime.getRuntime().exec(command.split(" "));
            dos = new DataOutputStream(p.getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean isOpen() {
        return isopen;
    }

    public void write(short[] shorts, int off, int len) throws JavaLayerException {
        if(dos == null){
            startPacat();
        }
        try {
            for (int n = off; n < (off + len); n++) {
                dos.writeShort(shorts[n]);
            }
        } catch (IOException iOException) {
            iOException.printStackTrace();
            throw new JavaLayerException("PAOut-write fehlgeschlagen");
        }
    }

    public void close() {
        if (p != null) {
            p.destroy();
        }
        isopen = false;
    }

    public void flush() {
        try {
            dos.flush();
        } catch (Exception ex) {
        }
    }

    public int getPosition() {
        return (int) (System.nanoTime() / 1000000);
    }


}

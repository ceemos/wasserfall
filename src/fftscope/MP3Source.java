/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fftscope;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.Player;

/**
 *
 * @author marcel
 */
public class MP3Source implements DataSource {
    
    FileInputStream fis;
    Player p;
    DataOutputStream dos;
    PipedOutputStream pos;
    PipedInputStream pis;
    DataInputStream dis;
    boolean eof = false;
    
    Decoder decoder;

    public MP3Source(File file) throws FileNotFoundException {
        fis = new FileInputStream(file);
        
        try {
            pos = new PipedOutputStream();
            dos = new DataOutputStream(pos);
            pis = new PipedInputStream(pos, 32 * 1024);
            dis = new DataInputStream(pis);
        } catch (IOException iOException) {
            throw new RuntimeException("No Piping today");
        }
        
        AudioDevice ad = new AudioDevice() {

            public void open(Decoder dcdr) throws JavaLayerException {
                decoder = dcdr;
            }

            public boolean isOpen() {
                return true;
            }

            public void write(short[] shorts, int off, int len) throws JavaLayerException {
                try {
                    for (int i = off; i < len; i++) {
                        dos.writeShort(shorts[i]);
                    }
                } catch (IOException iOException) {
                    throw new JavaLayerException(iOException.getMessage());
                }
            }

            public void close() {
                eof = true;
            }

            public void flush() {
                
            }

            public int getPosition() {
                System.out.println("GetPosition stub.");
                return 0;
            }
        };
        try {
            p = new Player(fis, ad);
        } catch(JavaLayerException jle){
            throw new RuntimeException("Something went terribly wrong");
        }
    }

    public Float[] getBlock(int nSamples) {
        Float[] data = new Float[nSamples];
        try {
            for (int i = 0; i < nSamples; i++) {
                while (dis.available() == 0 && !eof){
                    try {
                        p.play(1);
                    } catch (JavaLayerException javaLayerException) {
                        return null;
                    }
                }
                if(eof){
                    return null;
                }
                float v = dis.readShort();
                v /= 1 << 15;
                data[i] = v;
            }
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
        return data;
    }

    public float getSamplesPerSecond() {
        if (decoder.getOutputFrequency() == 0) {
            try {
                p.play(1);
            } catch (JavaLayerException javaLayerException) {
            }
        }
        return decoder.getOutputFrequency();
    }

    public int getLengthInSamples() {
        // schätzung!
        try {
            return (int) (fis.getChannel().size() * getSamplesPerSecond() / (16 * 1024));
        } catch (IOException e){
            return -1;
        }
    }
    
}

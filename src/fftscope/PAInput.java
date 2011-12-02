/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fftscope;

import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author marcel
 */
public class PAInput implements DataSource {
    
    static final String command = "parec --rate=48000 --channels=1 --format=s16le";

    private DataInputStream dis = null;

    public PAInput() {
        try {
            Process p = Runtime.getRuntime().exec(command.split(" "));
            dis = new DataInputStream(p.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }



    public Float[] getBlock(int nSamples) {

        Float[] buf = new Float[nSamples];
        for (int i = 0; i < buf.length; i++) {
            try {
                short sample = dis.readShort();
                buf[i] = (float) sample / (float) (1 << 15);
                //buf[i] *= 100;

            } catch (IOException iOException) {
            }
        }
        return buf;

    }

    public float getSamplesPerSecond() {
        return 48000;
    }

    public int getLengthInSamples() {
        return -1;
    }

}

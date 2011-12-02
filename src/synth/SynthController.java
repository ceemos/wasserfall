
package synth;

import fftscope.PulseAudioDevice;
import java.io.File;

/**
 *
 * @author marcel
 */
public class SynthController {
    public static void main(String[] args){
        PulseAudioDevice device = new PulseAudioDevice(44100, 1);
        Synthesizer s = new Synthesizer(device, 44100);
        WasserfallReader wr = new WasserfallReader(new File("METALLICA - ORION.mp3.png"), s);
        wr.start();
    }
}

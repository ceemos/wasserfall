package synth;

/**
 *
 * @author marcel
 */
public class Tone {
    double ts_start;
    double ts_end;
    double freq;
    double amp;

    public Tone(double ts_start, double ts_end, double freq, double amp) {
        this.ts_start = ts_start;
        this.ts_end = ts_end;
        this.freq = freq;
        this.amp = amp;
    }

    public double getEnd() {
        return ts_end;
    }

    public double getStart() {
        return ts_start;
    }

    public double getFreq() {
        return freq;
    }

    public double getAmp() {
        return amp;
    }

    public void setAmp(double amp) {
        this.amp = amp;
    }
    
}

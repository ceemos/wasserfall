/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wasserfall;

/**
 *
 * @author marcel
 */
public class NoteCalc {
    
    private static String[] notes = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    
    
    public static double FtoNote(double f) {
        return (12 * Math.log(f / 440) / Math.log(2)) + 57;
    }
    
    public static double NoteToF(double note) {
        return 55 * Math.pow(2, (note - 21)/12);
    }
    
    public static String nameForNote(int note){
        return notes[note % 12];
    }
    
}

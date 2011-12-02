/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fftscope;

/**
 *
 * @author marcel
 */
public interface DataSource {

    public Float[] getBlock(int nSamples);
    
    public float getSamplesPerSecond();
    
    public int getLengthInSamples();

}

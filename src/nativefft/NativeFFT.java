package nativefft;

import FFT.Complex;

public class NativeFFT 
{
    
  private native void displayMessage();
  private native void initFFT(int bs);
  private native int doFFT(double[] data_in, double[] data_out);
  static 
  {
    System.loadLibrary("nativefft"); 
  }
  
  private static NativeFFT instance;

    public static NativeFFT getInstance() {
        return instance;
    }
    
    public static void init(int blocksize){
        instance = new NativeFFT(blocksize);
    }
  
  
  private int bs;
  private double[] out;
  private double[] in;
  
  public NativeFFT(int blocksize){
      initFFT(blocksize);
      bs = blocksize;
      out = new double[blocksize];
      in = new double[blocksize];
  }
  
  /**
   * atm blos real2complex, auch wenns net so aussieht
   * @param data
   * @return 
   */
  public Complex[] fft(Complex[] data){ 
      for(int i = 0; i < bs; i++){
          in[i] = data[i].re();
      }
      doFFT(in, out);
      Complex[] c = new Complex[bs/2];
      for(int i = 0; i < bs/2; i++){
          c[i] = new Complex(out[i*2], out[i*2+1]);
      }
      return c;
  }
}
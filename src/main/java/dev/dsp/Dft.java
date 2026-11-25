package dev.dsp;

import java.util.Objects; 

 //Implementazione diretta O(N^2) della DFT e Inversa IDFT
public class Dft {
   
    private Dft() {
    }

    public static Complex[] forward(Signal signal) {
        Objects.requireNonNull(signal, "signal"); 
        return forward(signal.samples()); 
    }

    public static Complex[] forward(double[] samples) {
        Objects.requireNonNull(samples, "samples");
        int n = samples.length;
        Complex[] w = twiddles(n, -1);
        Complex[] spectrum = new Complex[n];

        for (int k = 0; k < n; k++) {
            Complex sum = Complex.ZERO;
            for (int t = 0; t < n; t++) {
                // Il cast a long previene l'overflow intero prima del modulo quando N > 46340
                int index = (int) ((long) k * t % n);
                sum = sum.add(w[index].scale(samples[t]));
            }
            spectrum[k] = sum;
        }
        return spectrum;
    }

    public static Complex[] inverse(Complex[] spectrum) {
        Objects.requireNonNull(spectrum, "spectrum");
        int n = spectrum.length;
        Complex[] w = twiddles(n, +1);
        Complex[] timeDomain = new Complex[n];

        for (int t = 0; t < n; t++) {
            Complex sum = Complex.ZERO;
            for (int k = 0; k < n; k++) {
                int index = (int) ((long) k * t % n);
                sum = sum.add(spectrum[k].mul(w[index]));
            }
            timeDomain[t] = sum.scale(1.0 / n);
        }
        return timeDomain;
    }

    //Antitrasforma uno spettro complesso estraendo la parte reale
    public static Signal inverseToSignal(Complex[] spectrum, double sampleRate) {
        Complex[] complexSamples = inverse(spectrum);
        double[] realSamples = new double[complexSamples.length];
        for (int i = 0; i < complexSamples.length; i++) {
            realSamples[i] = complexSamples[i].re();
        }
        return new Signal(realSamples, sampleRate);
    }

    //Calcola gli N fattori di rotazione  w[j] = e^(sign * 2*pi*i * j / n).
    private static Complex[] twiddles(int n, int sign) {
        Complex[] w = new Complex[n];
        for (int j = 0; j < n; j++) {
            w[j] = Complex.fromPolar(1.0, sign * 2.0 * Math.PI * j / n);
        }
        return w;
    }
}
    

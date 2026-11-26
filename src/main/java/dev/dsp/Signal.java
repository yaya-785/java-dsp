package dev.dsp;

import java.util.Arrays;
import java.util.Objects;

//Rappresentazione segnale discreto reale nel dominio del tempo
public final class Signal {

    private final double[] samples; 
    private final double sampleRate; //Freq di campionamento fs in Hz

    public Signal(double[] samples, double sampleRate) {
        if (samples == null || samples.length == 0) {
            throw new IllegalArgumentException("Samples array must not be null or empty"); 
        }

        //Double.isFinite esclude NaN, +inf e -inf
        if (!Double.isFinite(sampleRate) || sampleRate <= 0.0) {
            throw new IllegalArgumentException("sample rate must be positive and finite"); 
        }
        this.samples = Arrays.copyOf(samples, samples.length);
        this.sampleRate = sampleRate; 
    }

    //Genera un'onda sinusoidale pura x[n] = A * sin(2 * pi * f * n / fs)
    public static Signal sine(double frequencyHz, double amplitude, int numSamples, double sampleRate) {
        if (numSamples <= 0) {
            throw new IllegalArgumentException("Number of samples must be positive"); 
        }
        if (!Double.isFinite(sampleRate) || sampleRate <= 0.0) {
            throw new IllegalArgumentException("Sample rate must be positive and finite"); 
        }
        if (!Double.isFinite(frequencyHz) || frequencyHz < 0.0 || frequencyHz > sampleRate / 2.0) {
            throw new IllegalArgumentException("Frequency myst be in [0, sampleRate/2] to avoid aliasing"); 
        }
        if (!Double.isFinite(amplitude)) {
            throw new IllegalArgumentException("Amplitude must be finite"); 
        }

        double[] data = new double [numSamples]; 
        double angularStep = 2.0 * Math.PI * frequencyHz / sampleRate; 
        for (int n = 0; n < numSamples; n++) {
            data[n] = amplitude * Math.sin(angularStep * n); 
        }
        return new Signal(data, sampleRate); 
    }

    //Somma punto a punto due segnali con identica lunghezza e frequenza di campionamento
    public Signal add(Signal other) {
        Objects.requireNonNull(other, "other"); 
        if (this.length() != other.length()) {
            throw new IllegalArgumentException("Signal must have the same length"); 
        }
        if (Double.compare(this.sampleRate, other.sampleRate) != 0) {
            throw new IllegalArgumentException("Signals must have the same sample reate"); 
        }
        double[] sum = new double[this.length()]; 
        for (int i = 0; i < sum.length; i++) {
            sum[i] = this.samples[i] + other.samples[i]; 
        }
        return new Signal(sum, this.sampleRate); 
    }

    //Restituiscee una copia dei cmapioni
    public double[] samples() {
        return Arrays.copyOf(samples, samples.length); 
    }

    public double sample(int index) {
        return samples[index]; 
    }

    public int length() {
        return samples.length; 
    }

    public double sampleRate() {
        return sampleRate; 
    }

    //Durata temporale del segnale in secondi (N /fs) 
    public double duration() {
        return samples.length / sampleRate; 
    }

    public double binToFrequency (int binIndex) {
        if (binIndex <0 || binIndex >= samples.length) {

            throw new IllegalArgumentException("Bin index out of range: " + binIndex); 
        }
        return (binIndex * sampleRate) / samples.length; 
    }

    @Override 
    public boolean equals(Object o) {
        if (this == o) return true; 
        if (!(o instanceof Signal other)) return false; 
        return Double.compare(sampleRate, other.sampleRate) == 0 && Arrays.equals(samples, other.samples); 
    }

    @Override 
    public int hashCode() {
        return 31 * Arrays.hashCode(samples) + Double.hashCode(sampleRate);
    }

    @Override 
    public String toString() {
        return "Signal[length=" + samples.length + ", sampleRate=" + sampleRate + " Hz]"; 
    }
    
}

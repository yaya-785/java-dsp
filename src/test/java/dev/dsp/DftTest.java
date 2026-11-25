package dev.dsp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

class DftTest {

    private static final double EPSILON = 1e-9;

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 13, 31})
    void sineOnBinPeaksAtThatBinAndItsMirror(int bin) {
        int n = 64;
        double amplitude = 2.0;

        Complex[] spectrum = Dft.forward(Signal.sine(bin, amplitude, n, n));

        // Per un segnale reale, l'energia si divide equamente tra bin k e N - k
        double expectedPeak = amplitude * n / 2.0;
        for (int k = 0; k < n; k++) {
            double expected = (k == bin || k == n - bin) ? expectedPeak : 0.0;
            assertEquals(expected, spectrum[k].abs(), EPSILON, "bin " + k);
        }
    }

    @Test
    void compositeSignalSeparatesFrequencies() {
        int n = 64;
        Signal combined = Signal.sine(4.0, 1.0, n, 64.0)
                .add(Signal.sine(15.0, 3.0, n, 64.0));

        Complex[] spectrum = Dft.forward(combined);

        for (int k = 0; k < n; k++) {
            double expected = 0.0;
            if (k == 4 || k == n - 4) expected = 1.0 * n / 2.0;
            if (k == 15 || k == n - 15) expected = 3.0 * n / 2.0;
            assertEquals(expected, spectrum[k].abs(), EPSILON, "bin " + k);
        }
    }

    @Test
    void roundTripIdftRecoversOriginalSignal() {
        double sampleRate = 100.0;
        // N = 50 (non potenza di 2) per testare la generalità della DFT
        Signal original = Signal.sine(7.0, 1.5, 50, sampleRate)
                .add(Signal.sine(23.0, 0.8, 50, sampleRate));

        Signal reconstructed = Dft.inverseToSignal(Dft.forward(original), sampleRate);

        assertArrayEquals(original.samples(), reconstructed.samples(), EPSILON);
    }

    @Test
    void impulseHasFlatSpectrum() {
        double[] impulse = new double[8];
        impulse[0] = 1.0;

        Complex[] spectrum = Dft.forward(impulse);

        for (Complex c : spectrum) {
            assertEquals(1.0, c.re(), EPSILON);
            assertEquals(1.0, c.abs(), EPSILON);
        }
    }

    @Test
    void constantSignalConcentratesInDc() {
        double[] constant = new double[8];
        Arrays.fill(constant, 3.0);

        Complex[] spectrum = Dft.forward(constant);

        assertEquals(24.0, spectrum[0].re(), EPSILON);
        for (int k = 1; k < spectrum.length; k++) {
            assertEquals(0.0, spectrum[k].abs(), EPSILON, "bin " + k);
        }
    }

    @Test
    void spectrumOfRealSignalIsHermitian() {
        // 7 Hz con fs=100 e N=50 non cade su un bin intero: verifica parte reale e immaginaria coniugata
        Complex[] x = Dft.forward(Signal.sine(7.0, 1.0, 50, 100.0));
        int n = x.length;

        for (int k = 1; k < n; k++) {
            assertEquals(x[k].re(), x[n - k].re(), EPSILON, "re " + k);
            assertEquals(x[k].im(), -x[n - k].im(), EPSILON, "im " + k);
        }
    }

    @Test
    void parsevalEnergyIsPreserved() {
        Signal signal = Signal.sine(7.0, 1.5, 50, 100.0)
                .add(Signal.sine(23.0, 0.8, 50, 100.0));
        int n = signal.length();

        double timeEnergy = 0.0;
        for (double s : signal.samples()) {
            timeEnergy += s * s;
        }

        double freqEnergy = 0.0;
        for (Complex c : Dft.forward(signal)) {
            freqEnergy += c.abs() * c.abs();
        }

        assertEquals(timeEnergy, freqEnergy / n, EPSILON);
    }

    @Test
    void frequencyBetweenBinsLeaksEnergy() {
        // 5.5 Hz cade esattamente a metà tra bin 5 e bin 6 -> spectral leakage
        Complex[] spectrum = Dft.forward(Signal.sine(5.5, 1.0, 64, 64.0));

        assertTrue(spectrum[20].abs() > 0.1, "Expected spectral leakage far from the peak");
    }

    @Test
    void nullAndEmptyInputs() {
        assertThrows(NullPointerException.class, () -> Dft.forward((double[]) null));
        assertThrows(NullPointerException.class, () -> Dft.forward((Signal) null));
        assertThrows(NullPointerException.class, () -> Dft.inverse(null));
        assertEquals(0, Dft.forward(new double[0]).length);
    }
}
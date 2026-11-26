package dev.dsp;

import org.junit.jupiter.api.Test; 
import static org.junit.jupiter.api.Assertions.*; 

public class SignalTest {
    private static final double EPSILON = 1e-9; 

    //Immutabilità e costruttore

    @Test
    void constructorAndGetterEnforceImmutability() {
        double[] raw = {1.0, 2.0, 3.0, 4.0};
        Signal signal = new Signal(raw, 100.0);

        // Mutare l'array originale non deve alterare il Signal
        raw[0] = 999.0;
        assertEquals(1.0, signal.sample(0), EPSILON);

        // Mutare l'array restituito dal getter non deve alterare il Signal
        double[] exported = signal.samples();
        exported[1] = -999.0;
        assertEquals(2.0, signal.sample(1), EPSILON);
    }

    @Test
    void rejectsInvalidConstructorArguments() {
        assertThrows(IllegalArgumentException.class, () -> new Signal(null, 100.0));
        assertThrows(IllegalArgumentException.class, () -> new Signal(new double[0], 100.0));
        assertThrows(IllegalArgumentException.class, () -> new Signal(new double[]{1.0}, 0.0));
        assertThrows(IllegalArgumentException.class, () -> new Signal(new double[]{1.0}, -44100.0));
        assertThrows(IllegalArgumentException.class, () -> new Signal(new double[]{1.0}, Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> new Signal(new double[]{1.0}, Double.POSITIVE_INFINITY));
    }

    @Test
    void sampleThrowsOutOfBounds() {
        Signal signal = new Signal(new double[]{1.0, 2.0}, 10.0);

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> signal.sample(-1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> signal.sample(2));
    }

    //sine()

    @Test
    void sineProducesExpectedValues() {
        // 1 Hz campionato a 4 Hz: un periodo in 4 campioni -> 0, A, 0, -A
        Signal signal = Signal.sine(1.0, 2.0, 4, 4.0);

        assertArrayEquals(new double[]{0.0, 2.0, 0.0, -2.0}, signal.samples(), EPSILON);
        assertEquals(4, signal.length());
        assertEquals(4.0, signal.sampleRate(), EPSILON);

        
    }


    @Test
    void sineWithZeroFrequencyIsAllZeros() {
        Signal signal = Signal.sine(0.0, 5.0, 8, 100.0);

        assertArrayEquals(new double[8], signal.samples(), EPSILON);
    }

    @Test
    void sineEnforcesNyquistLimit() {
        // Frequenza esattamente al limite di Nyquist (fs / 2 = 50 Hz) è valida
        assertDoesNotThrow(() -> Signal.sine(50.0, 1.0, 100, 100.0));

        // Frequenza oltre Nyquist causa aliasing e deve essere rifiutata
        assertThrows(IllegalArgumentException.class, () -> Signal.sine(50.1, 1.0, 100, 100.0));
        assertThrows(IllegalArgumentException.class, () -> Signal.sine(-5.0, 1.0, 100, 100.0));
    }

    @Test
    void sineRejectsInvalidParameters() {
        assertThrows(IllegalArgumentException.class, () -> Signal.sine(1.0, 1.0, 0, 100.0));
        assertThrows(IllegalArgumentException.class, () -> Signal.sine(1.0, 1.0, -5, 100.0));
        assertThrows(IllegalArgumentException.class, () -> Signal.sine(1.0, 1.0, 16, 0.0));
        assertThrows(IllegalArgumentException.class, () -> Signal.sine(1.0, 1.0, 16, Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> Signal.sine(Double.NaN, 1.0, 16, 100.0));
        assertThrows(IllegalArgumentException.class, () -> Signal.sine(1.0, Double.NaN, 16, 100.0));
        assertThrows(IllegalArgumentException.class, () -> Signal.sine(1.0, Double.POSITIVE_INFINITY, 16, 100.0));
    }

    //add()
    @Test
    void addSumsPointByPointWithoutModifyingOperands() {
        Signal a = new Signal(new double[]{1.0, 2.0, 3.0}, 10.0);
        Signal b = new Signal(new double[]{10.0, 20.0, 30.0}, 10.0);

        Signal sum = a.add(b);

        assertArrayEquals(new double[]{11.0, 22.0, 33.0}, sum.samples(), EPSILON);
        assertEquals(10.0, sum.sampleRate(), EPSILON);
        assertArrayEquals(new double[]{1.0, 2.0, 3.0}, a.samples(), EPSILON);
        assertArrayEquals(new double[]{10.0, 20.0, 30.0}, b.samples(), EPSILON);
    }

    @Test
    void addRejectsMismatchedSignals() {
        Signal s1 = Signal.sine(10.0, 1.0, 64, 100.0);
        Signal differentLength = Signal.sine(10.0, 1.0, 32, 100.0);
        Signal differentRate = Signal.sine(10.0, 1.0, 64, 200.0);

        assertThrows(NullPointerException.class, () -> s1.add(null));
        assertThrows(IllegalArgumentException.class, () -> s1.add(differentLength));
        assertThrows(IllegalArgumentException.class, () -> s1.add(differentRate));
    }

    //bin
    @Test
    void metadataAndBinConversionAreAccurate() {
        Signal signal = new Signal(new double[100], 200.0);

        assertEquals(0.5, signal.duration(), EPSILON);
        assertEquals(0.0, signal.binToFrequency(0), EPSILON);
        assertEquals(50.0, signal.binToFrequency(25), EPSILON);
        assertThrows(IllegalArgumentException.class, () -> signal.binToFrequency(-1));
        assertThrows(IllegalArgumentException.class, () -> signal.binToFrequency(100));
    }

    //HashCode and toString

    @Test
    void equalsAndHashCodeFollowValueSemantics() {
        Signal a = Signal.sine(5.0, 2.0, 32, 64.0);
        Signal b = Signal.sine(5.0, 2.0, 32, 64.0);
        Signal c = Signal.sine(6.0, 2.0, 32, 64.0);
        Signal differentRate = new Signal(a.samples(), 128.0);

        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode()); 
        assertNotEquals(a, c); 
        assertNotEquals(a, differentRate);
        assertNotEquals(a, null);
        assertNotEquals(a, "not a signal");
    }

    @Test
    void toStringMentionsLengthAndSampleRate() {
        Signal signal = new Signal(new double[]{1.0, 2.0, 3.0}, 10.0);

        String text = signal.toString();

        assertTrue(text.contains("3"));
        assertTrue(text.contains("10.0"));
    }
}

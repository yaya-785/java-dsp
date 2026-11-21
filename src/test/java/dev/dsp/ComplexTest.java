package dev.dsp;

import org.junit.jupiter.api.Test; 
import static org.junit.jupiter.api.Assertions.*;


public class ComplexTest {
    private static final double EPSILON = 1e-9;

    @Test
    void testAddeSub() {
        Complex a = new Complex(3.0, 4.0); 
        Complex b = new Complex(1.0, -2.0); 

        Complex sum = a.add(b); 
        assertEquals(4.0, sum.re(), EPSILON); 
        assertEquals(2.0, sum.im(), EPSILON); 

        Complex diff = a.sub(b); 
        assertEquals(2.0, diff.re(), EPSILON); 
        assertEquals(6.0, diff.im(), EPSILON); 
    }

    @Test
    void testMuleConj() {
        Complex a = new Complex(3.0, 4.0); 
        Complex product = a.mul(a.conjugate()); 

        assertEquals(25.0, product.re(), EPSILON); 
        assertEquals(0.0, product.im(), EPSILON); 
    }

    @Test
    void testMagePhase() {
        Complex a = new Complex(3.0, 4.0); 
        assertEquals(5.0, a.abs(), EPSILON); 

        Complex euler = Complex.fromPolar(1.0, Math.PI);
        assertEquals(-1.0, euler.re(), EPSILON); 
        assertEquals(0.0, euler.im(), EPSILON); 
        assertEquals(Math.PI, euler.phase(), EPSILON); 
    }
}

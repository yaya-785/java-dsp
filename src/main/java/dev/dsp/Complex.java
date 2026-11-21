package dev.dsp; 

//Rappresentazione di un numero complesso (re + i * im)

public record Complex(double re, double im) {
    public static final Complex ZERO = new Complex(0.0, 0.0); 
    public static final Complex ONE = new Complex(1.0, 0.0);
    public static final Complex I = new Complex(0.0, 1.0); 

    //Numero complesso con Eulero r*e^(i*theta)
    public static Complex fromPolar(double magnitude, double phaseRad) {
        return new Complex(
            magnitude * Math.cos(phaseRad),
            magnitude * Math.sin(phaseRad)
        ); 
    }

    public Complex add(Complex other) {
        return new Complex(this.re + other.re, this.im + other.im); 
    }

    public Complex sub(Complex other) {
        return new Complex(this.re - other.re, this.im - other.im); 
    }

    //Moltiplicazione complessi 
    public Complex mul(Complex other) {
        double real = this.re * other.re - this.im * other.im; 
        double imag = this.re * other.im + this.im * other.re; 
        return new Complex(real, imag); 
    }

    public Complex scale(double factor) {
        return new Complex(this.re * factor, this.im * factor); 
    }

    public Complex conjugate() {
        return new Complex(this.re, -this.im); 
    }

    public double abs() {
        return Math.hypot(this.re, this.im); 
    }

    public double phase() {
        return Math.atan2(this.im, this.re); //Angolo in radianti in [-pi,pi]
    }
    
}

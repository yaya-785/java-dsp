# java-dsp
 
A digital signal processing library written in **pure Java**, built from scratch and without external DSP dependencies. The goal is to implement the core algorithms (DFT, FFT, filters, spectrograms) in a clean, well-tested and well-documented way, and to understand the theory behind every line.
 
![build](https://github.com/yaya-785/java-dsp/actions/workflows/ci.yml/badge.svg)
![java](https://img.shields.io/badge/Java-17+-blue)
 
## Goals
 
- **Correctness first**: every algorithm is validated against known results (analytical values, NumPy, reference implementations).
- **Readable code**: immutable value types, small classes, Javadoc on the public API.
- **Measured performance**: benchmarks (JMH) to compare naive and optimized implementations.
- **Documented design**: trade-offs and numerical considerations are written down in `DESIGN.md` (coming soon).
## Status
 
Work in progress, developed week by week. Each item below corresponds to small, atomic commits with tests.
 
- [x] **Week 0 – Foundations**: Maven project, JUnit 5, CI, `Complex` numbers
- [ ] **Week 1 – Fourier transform**: `Signal`, naive DFT, inverse DFT, round-trip tests
- [ ] **Week 2 – FFT**: radix-2 Cooley-Tukey (recursive, then iterative), zero-padding, JMH benchmarks
- [ ] **Week 3 – Filters**: convolution (direct and FFT-based), window functions, FIR and IIR design
- [ ] **Week 4 – Visualization**: WAV I/O, STFT, spectrogram, denoising example, `v1.0.0`
## Project structure
 
```
java-dsp/
├── pom.xml
├── README.md
└── src/
    ├── main/java/com/dev/dsp/
    │   └── Complex.java
    │   └── Signal.java 
    │   └── Dft.java
    └── test/java/com/dev/dsp/
        └── ComplexTest.java
        └── SignalTest.java
        └── DftTest.java

```
 
## Getting started
 
Requirements: **JDK 17+** and **Maven 3.8+**.
 
```bash
git clone https://github.com/yaya-785/java-dsp.git
cd java-dsp
mvn test
```
 
## What is available today
 
### `Complex`
 
An immutable complex number type with arithmetic operations and polar construction. It is the building block for the Fourier transforms that come next. 
 
```java
Complex i = Complex.fromPolar(1.0, Math.PI / 2); // e^(i*pi/2)
Complex minusOne = i.mul(i);                      // i * i = -1
System.out.println(minusOne.abs());               // ~1.0
```
 
## Design principles
 
- **Immutability**: value types never change after construction, which keeps the code easy to reason about and thread-safe.
- **Fail fast**: invalid arguments are rejected with clear exceptions.
- **Test against theory**: unit tests check known analytical results and tolerances, not just "it runs".
## Commit convention
 
This repository follows [Conventional Commits](https://www.conventionalcommits.org/): `feat:`, `fix:`, `test:`, `docs:`, `refactor:`, `chore:`, `ci:`, `bench:`.
 
## License
 
Released under the MIT License.
 
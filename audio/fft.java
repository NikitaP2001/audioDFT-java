package audio;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class fft  {

        private fft_cpx[] twiddles;
        private List<Integer> factors = new ArrayList<>();
        private int nfft;
        private boolean isInverse;
        private static final int FIRST_FACTOR = 4;

        fft_cpx[] in;
        fft_cpx[] out;

        public static final double PI = Math.PI;

        public static double[] dbFromCpx(fft_cpx[] freq) {
                double[] amp = new double[freq.length];
                for (int i = 0; i < freq.length; i++) {
                        double re = freq[i].real;
                        double im = freq[i].imag;
                        amp[i] = 20 * Math.abs(Math.log10(Math.sqrt(re * re + im * im)));
                }
                return amp;
        }

        public static double[] movingAverage(double[] arr, int wndSize) {
                int n = arr.length;
                double[] result = new double[n];

                for (int i = 0; i < n; i++) {
                        double sum = 0;
                        int count = 0;
                        int wndBeg = Math.max(0, i - wndSize + 1);
                        int wndEnd = Math.min(n - 1, i + wndSize - 1);
                        for (int j = wndBeg; j <= wndEnd; j++) {
                                sum += arr[j];
                                count++;
                        }
                        result[i] = sum / count;
                }
                return result;
        }

        public static fft_cpx[] movingAverage(fft_cpx[] arr, int windowSize) {
        int n = arr.length;
        fft_cpx[] result = new fft_cpx[n];

        for (int i = 0; i < n; i++) {
                double sumReal = 0.0;
                double sumImag = 0.0;

                int count = 0;
                int wndBeg = Math.max(0, i - windowSize + 1);
                int wndEnd = Math.min(n - 1, i + windowSize - 1);
                for (int j = wndBeg; j <= wndEnd; j++) {
                        sumReal += arr[j].real;
                        sumImag += arr[j].imag;
                        count++;
                }

                result[i] = new fft_cpx();
                result[i].real = sumReal / count;
                result[i].imag = sumImag / count;
        }

        return result;
        }

        public fft_cpx[] forward(int[] x) {
                nfft = x.length / 2;
                in = new fft_cpx[nfft];
                out = new fft_cpx[nfft];
                for (int i = 0; i < in.length; i++) {
                        in[i] = new fft_cpx();
                        in[i].real = Double.valueOf(x[2 * i]);
                        in[i].imag = Double.valueOf(x[2 * i + 1]);
                        out[i] = new fft_cpx();
                }

                isInverse = false;
                init();
                work(0, 0, 1, 0);
                return out;
        }

        public int[] inverse(fft_cpx[] X) {
                nfft = X.length;
                isInverse = true;
                in = X;
                out = Stream.generate(() -> new fft_cpx()).limit(nfft).toArray(fft_cpx[]::new);
                init();
                work(0, 0, 1, 0);
                int[] result = new int[nfft * 2];
                for (int i = 0; i < nfft; i++) {
                        result[i * 2] = (int)(out[i].real * 1.f / nfft);
                        result[i * 2 + 1] = (int)(out[i].imag * 1.f / nfft);
                }
                return result;
        }

        private void bfly2(int pos, int fstride, int m) {
                int pos2 = pos + m;
                int twPos = 0;
                fft_cpx t = new fft_cpx();

                do {
                        t.mul(out[pos2], twiddles[twPos]);
                        twPos += fstride;
                        out[pos2].sub(out[pos], t);
                        out[pos].add_to(t);
                        pos2 += 1;
                        pos += 1;
                } while (--m != 0);
        }

        private void bfly3(int pos, int fstride, int m) {
                int k = m;
                int m2 = 2 * m;
                int tw1 = 0, tw2 = 0;
                fft_cpx[] scratch = Stream.generate(() -> new fft_cpx()).limit(5).toArray(fft_cpx[]::new);
                fft_cpx epi3;
                epi3 = twiddles[fstride * m];

                do{
                        scratch[1].mul(out[pos + m], twiddles[tw1]);
                        scratch[2].mul(out[pos + m2], twiddles[tw2]);

                        scratch[3].add(scratch[1], scratch[2]);
                        scratch[0].sub(scratch[1], scratch[2]);
                        tw1 += fstride;
                        tw2 += fstride * 2;

                        out[pos + m].real = out[pos].real - scratch[3].real * .5f;
                        out[pos + m].imag = out[pos].imag - scratch[3].imag * .5f;

                        scratch[0].smul(epi3.imag);

                        out[pos].add_to(scratch[3]);

                        out[pos + m2].real = out[pos + m].real + scratch[0].imag;
                        out[pos + m2].imag = out[pos + m].imag - scratch[0].real;

                        out[pos + m].real -= scratch[0].imag;
                        out[pos + m].imag += scratch[0].real;

                        pos += 1;
                } while (--k != 0);
        }

        private void bfly4(int pos, int fstride, int m) {

                int tw1 = 0, tw2 = 0, tw3 = 0;
                fft_cpx[] scratch = Stream.generate(() -> new fft_cpx()).limit(6).toArray(fft_cpx[]::new);
                int k = m;
                int m2 = 2 * m;
                int m3 = 3 * m;

                do {

                        scratch[0].mul(out[pos + m], twiddles[tw1]);
                        scratch[1].mul(out[pos + m2], twiddles[tw2]);
                        scratch[2].mul(out[pos + m3], twiddles[tw3]);

                        scratch[5].sub(out[pos], scratch[1]);
                        out[pos].add_to(scratch[1]);
                        scratch[3].add(scratch[0], scratch[2]);
                        scratch[4].sub(scratch[0], scratch[2]);
                        out[pos + m2].sub(out[pos], scratch[3]);
                        tw1 += fstride;
                        tw2 += fstride * 2;
                        tw3 += fstride * 3;
                        out[pos].add_to(scratch[3]);

                        if (isInverse) {
                                out[pos + m].real = scratch[5].real - scratch[4].imag;
                                out[pos + m].imag = scratch[5].imag + scratch[4].real;
                                out[pos + m3].real = scratch[5].real + scratch[4].imag;
                                out[pos + m3].imag = scratch[5].imag - scratch[4].real;
                        } else {
                                out[pos + m].real = scratch[5].real + scratch[4].imag;
                                out[pos + m].imag = scratch[5].imag - scratch[4].real;
                                out[pos + m3].real = scratch[5].real - scratch[4].imag;
                                out[pos + m3].imag = scratch[5].imag + scratch[4].real;
                        }
                        pos += 1;
                } while (--k != 0);
        }

        private void bfly5(int pos, int fstride, int m) {
                int pos1 = pos + m;
                int pos2 = pos1 + m;
                int pos3 = pos2 + m;
                int pos4 = pos3 + m;
                fft_cpx[] scratch = Stream.generate(() -> new fft_cpx()).limit(13).toArray(fft_cpx[]::new);
                fft_cpx ya = twiddles[fstride * m];
                fft_cpx yb = twiddles[2 * fstride * m];

                for (int u = 0; u < m; u++) {
                        scratch[0] = out[pos];

                        scratch[1].mul(out[pos1], twiddles[u*fstride]);
                        scratch[2].mul(out[pos2], twiddles[2*u*fstride]);
                        scratch[3].mul(out[pos3], twiddles[3*u*fstride]);
                        scratch[4].mul(out[pos4], twiddles[4*u*fstride]);

                        scratch[7].add(scratch[1], scratch[4]);
                        scratch[10].sub(scratch[1], scratch[4]);
                        scratch[8].add(scratch[2], scratch[3]);
                        scratch[9].sub(scratch[2], scratch[3]);

                        out[pos].real += scratch[7].real + scratch[8].real;
                        out[pos].imag += scratch[7].imag + scratch[8].imag;

                        scratch[5].real = scratch[0].real + scratch[7].real * ya.real + scratch[8].real * yb.real;
                        scratch[5].imag = scratch[0].imag + scratch[7].imag * ya.real + scratch[8].imag * yb.real;

                        scratch[6].real =  scratch[10].imag * ya.imag + scratch[9].imag * yb.imag;
                        scratch[6].imag = -scratch[10].real * ya.imag - scratch[9].real * yb.imag;

                        out[pos1].sub(scratch[5], scratch[6]);
                        out[pos4].add(scratch[5], scratch[6]);

                        scratch[11].real = scratch[0].real + scratch[7].real * yb.real + scratch[8].real * ya.real;
                        scratch[11].imag = scratch[0].imag + scratch[7].imag * yb.real + scratch[8].imag * ya.real;
                        scratch[12].real = -scratch[10].imag * yb.imag + scratch[9].imag * ya.imag;
                        scratch[12].imag = scratch[10].real * yb.imag - scratch[9].real * ya.imag;

                        out[pos2].add(scratch[11], scratch[12]);
                        out[pos3].sub(scratch[11], scratch[12]);
                        pos += 1;
                        pos1 += 1;
                        pos2 += 1;
                        pos3 += 1;
                        pos4 += 1;
                }
        }

        private void bfly_generic(int pos, int fstride, int m, int p) {
                int k;
                fft_cpx t = new fft_cpx();
                int Norig = nfft;

                fft_cpx[] scratch = new fft_cpx[p];

                for (int u = 0; u < m; u++) {
                        k = u;
                        for (int q1 = 0 ; q1 < p ; q1++) {
                                scratch[q1] = out[pos + k];
                                k += m;
                        }

                        k = u;
                        for (int q1 = 0 ; q1 < p; q1++) {
                                int twidx = 0;
                                out[pos + k] = scratch[0];
                                for (int q = 1; q < p; q++) {
                                        twidx += fstride * k;
                                        if (twidx >= Norig) 
                                                twidx -= Norig;
                                        t.mul(scratch[q], twiddles[twidx]);
                                        out[pos + k].add_to(t);
                                }
                                k += m;
                        }
                }
        }

        private void recombine(int outPos, int p, int m, int fstride) {

                switch (p) {
                        case 2: 
                                bfly2(outPos, fstride, m); 
                                break;
                        case 3: 
                                bfly3(outPos, fstride, m); 
                                break;
                        case 4: 
                                bfly4(outPos, fstride, m); 
                                break;
                        case 5: 
                                bfly5(outPos, fstride, m); 
                                break;
                        default: 
                                bfly_generic(outPos, fstride, m, p);
                                break;
                }
        } 

        private void work(int inPos, int outPos, 
        int fstride, int fPos) {

                int p = (fPos != factors.size()) ? factors.get(fPos++) : 0;
                int m = (fPos != factors.size()) ? factors.get(fPos++) : 0;
                int foutEnd = outPos + p * m;

                if (fstride == 1 && p <= 5 && m != 1) {

                        for (int k = 0; k < p; k++) {
                                int newOutPos = outPos + k * m;
                                int newInPos = inPos + fstride * k;
                                work(newInPos, newOutPos, fstride * p, fPos);
                        }

                } else if (m == 1) {
                        for (int pos = outPos; pos < foutEnd; pos++, inPos += fstride)
                                out[pos] = in[inPos];
                } else {
                        for (int pos = outPos; pos != foutEnd; pos += m, inPos += fstride)
                                work(inPos, pos, fstride * p, fPos);
                }

                recombine(outPos, p, m, fstride);
        }

        private void init() {
                twiddles = new fft_cpx[nfft];

                for (int i = 0; i < nfft; i++) {
                        double phase = 2*PI*i/nfft * (isInverse ? 1 : -1);
                        twiddles[i] = new fft_cpx();
                        twiddles[i].cexp(phase);
                }
                initFactors(nfft);
        }

        private static int nextPower(int power) {
                switch (power) {
                        case 4: 
                                return 2; 
                        case 2:
                                return 3;
                        default:
                                return power + 2;
                }
        }

        private void initFactors(int n) {
                factors.clear();
                int power = FIRST_FACTOR;
                double floor_sqrt = Math.floor(Math.sqrt(n));
               
                do {
                        while (n % power != 0) {
                                if (power > floor_sqrt)
                                        power = n;
                                else
                                        power = nextPower(power);
                        }
                        n /= power;
                        factors.add(power);
                        factors.add(n);
                } while (n > 1);
        }
}
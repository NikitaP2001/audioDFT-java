package audio;

class fft_ctx {
        double real;
        double imag;
}

public class fft  {

        public static final double PI = Math.PI;

        public static double[] inverse(fft_ctx[] X) {

        }

        public static fft_ctx[] forward(double[] x) {
                int n = x.length;
                boolean invert = false;

                for (int i = 1, j = 0; i < n; i++) {
                        int bit = n >> 1;
                        for (; (j & bit) != 0; bit >>= 1)
                                j ^= bit;
                        j ^= bit;

                        if (i < j)
                                swap(x, i, j);
                }

                for (int len = 2; len <= n; len <<= 1) {
                        double ang = 2 * PI / len * (invert ? -1 : 1);
                        fft_ctx wlen {Math.cos(ang), Math.sin(ang)};
                        for (int i = 0; i < n; i += len) {
                                fft_ctx  w(1);
                                for (int j = 0; j < len / 2; j++) {
                                        fft_ctx u = a[i+j], v = a[i+j+len/2] * w;
                                        a[i+j] = u + v;
                                        a[i+j+len/2] = u - v;
                                        w *= wlen;
                                }
                        }
                }

                if (invert) {
                        for (cd & x : a)
                        x /= n;
                }
        }

        public static double hanningWindow(int sample, int pos, int size) {
                return sample*0.5d*(1.d-Math.cos(2.d*PI*pos/(size-1.f)));
        }

        private static <T> 
        void swap(T[] arr, int i, int j) {
                T temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
        }
       
}
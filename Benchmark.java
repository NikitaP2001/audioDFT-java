import java.util.Random;

import audio.*;;

public class Benchmark {
        static final int startTest = 2048;
        static final int endTest = 10000000;
        static final int step = 2;

	private fft inst = new fft();

	public static void main(String []args) {

		Benchmark app = new Benchmark();
                app.run();
	}

        public void run() {
                for (int size = startTest; size < endTest; size *= step) {
                        int[] sample = generateSample(size);
                        long startTime = System.nanoTime();
                        fft_cpx[] result = inst.forward(sample);
                        long endTime = System.nanoTime();
                        int[] inv = inst.inverse(result);
                        for (int i = 0; i < size; i++) {
                                if (Math.abs(sample[i] - inv[i]) > 10) {
                                        System.out.println("Inverse failed");
                                        System.exit(1);
                                }
                                        
                        }

                        long duration = (endTime - startTime); 
                        double durationInMillis = (double) duration / 1000000;

                        System.out.println("dim: " + size + ", time: " + durationInMillis + "ms");
                }
        }

        private int[] generateSample(int sampleSize) {
                int[] sample = new int[sampleSize];
                Random rand = new Random(); 
                for (int i = 0; i < sampleSize; i++)
                        sample[i] = rand.nextInt(256);
                return sample;
        }
}

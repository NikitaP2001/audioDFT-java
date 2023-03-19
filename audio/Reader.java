package audio;

import java.io.File;
import java.io.ByteArrayOutputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;

public class Reader {

        public static byte[] readWavContent(File wavFile) throws Exception {
		AudioInputStream audioIn = AudioSystem.getAudioInputStream(wavFile);
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();

		byte[] buff = new byte[1024];
		for (int read = 0; (read = audioIn.read(buff)) > 0; ) {
		    bytesOut.write(buff, 0, read);
		}
		bytesOut.flush();

		return bytesOut.toByteArray();
	}

	public static int getWavBytesPerSample(File wavFile) throws Exception {
		AudioInputStream audioIn = AudioSystem.getAudioInputStream(wavFile);
		AudioFormat format = audioIn.getFormat();
		int bits = format.getSampleSizeInBits();
		if (bits % 8 != 0)
			throw new Exception("Unsupported sample size");
		return bits / 8;
	}

	public static float getSamplesPerSecond(File wavFile) throws Exception {
		AudioInputStream audioIn = AudioSystem.getAudioInputStream(wavFile);
		AudioFormat format = audioIn.getFormat();
		return format.getSampleRate();
	}

        private static void printAudioFileInfo(File audioFile) {
		try {
			System.out.println("Audio info");
			System.out.println("Samples per second: " + getSamplesPerSecond(audioFile));
			byte[] content = readWavContent(audioFile);
			int sampleSize = getWavBytesPerSample(audioFile);
			System.out.println("Samples count: " + content.length / sampleSize);
			Thread.sleep(1000);
			for (int i = 0; i < content.length / sampleSize; i++) {
				System.out.printf("%d: ", i);
				for (int j = 0; j < sampleSize; j++)
					System.out.printf("0x%02X ", content[i + j]);
				System.out.println();
			}
		} catch (Exception ex) {
			System.out.println("Error reading file");
		}
	}

}
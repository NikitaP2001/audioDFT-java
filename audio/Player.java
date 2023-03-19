package audio;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.LineUnavailableException;

public class Player {

        public void playMusic(String fileName) {
                File audioFile = new File(fileName);
                new Thread(new Runnable() {
                        public void run() {
                                playOneChannel(audioFile);
                        }
                }).start();
	}

	private static void playOneChannel(File audioFile) {
		try {
			byte[] content = Reader.readWavContent(audioFile);
			playBytes(content);
		} catch (Exception ex) {
			System.out.println("unable to play");
		}
	}

	private static void playBytes(byte[] samples) {
		try {
			AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);

			DataLine.Info datalineinfo = new DataLine.Info(SourceDataLine.class, format);
			if (!AudioSystem.isLineSupported(datalineinfo)) {
				System.out.println("Line matching " + datalineinfo + " is not supported.");
			} else {
				System.out.println("Line matching " + datalineinfo + " is supported.");

				SourceDataLine sourcedataline = (SourceDataLine) AudioSystem.getLine(datalineinfo);
				sourcedataline.open(format);
				sourcedataline.start();

				int framesizeinbytes = format.getFrameSize();
				int bufferlengthinframes = sourcedataline.getBufferSize() / 8;
				int bufferlengthinbytes = bufferlengthinframes * framesizeinbytes;
				byte[] sounddata = new byte[bufferlengthinbytes];
				int numberofbytesread = 0;
				sourcedataline.write(samples, 0, samples.length);

				sourcedataline.stop();
				sourcedataline.close();
			}

		} catch (LineUnavailableException ex) {
			System.out.println(ex.getMessage());
		}
		
	}
}
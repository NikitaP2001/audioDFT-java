package audio;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class Player {
	AudioFormat playFormat;
	SourceDataLine sourcedataline;

	public Player(AudioFormat format) {
		try {
			playFormat = format;
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, playFormat);
			if (!AudioSystem.isLineSupported(info))
				throw new Exception("Line not supported");
			sourcedataline = (SourceDataLine)AudioSystem.getLine(info);
			sourcedataline.open(playFormat);
			sourcedataline.start();
		} catch (Exception ex) {
			System.out.println("Play failed: " + ex.getMessage());
			System.exit(0);
		}
	}

	public void play(SoundTrack track) {
		byte[] soundData = formDataLine(track);
		sourcedataline.write(soundData, 0, soundData.length);
	}

	private byte[] formDataLine(SoundTrack track) {
		byte[] data = new byte[track.length * playFormat.getFrameSize()];

		for (int iFrame = 0; iFrame < track.length; iFrame++)
			putFrame(track, iFrame, data);	

		return data;
	}

	private void putFrame(SoundTrack track, int iFrame, byte[] data) {
		int sampleSize = playFormat.getSampleSizeInBits() / 8;
		int nChannels = playFormat.getChannels();
		int offset = iFrame * playFormat.getFrameSize();

		for (int iChan = 0; iChan < nChannels; iChan++) {
			ByteBuffer buffer = ByteBuffer.wrap(data, offset, sampleSize);
			int sample = track.getSample(iFrame, iChan);
			if (playFormat.isBigEndian())
				putSampleBigEnd(buffer, sample);
			else
				putSampleLitEnd(buffer, sample);
			offset += sampleSize;
		}
	}

	private void putSampleBigEnd(ByteBuffer buffer, int sample) {
		int totalSize = buffer.remaining();
		byte[] iBytes = new byte[totalSize];
		for (int ib = 0; ib < totalSize; ib++) {
			byte val = (byte)(sample & 0xFF);
			iBytes[totalSize - ib - 1] = val;
			sample >>= Byte.SIZE;
		}
		for (byte digit : iBytes)
			buffer.put(digit);
	}

	private void putSampleLitEnd(ByteBuffer buffer, int sample) {
		int totalSize = buffer.remaining();
		for (int ib = 0; ib < totalSize; ib++) {
			byte val = (byte)(sample & 0xFF);
			buffer.put(val);
			sample >>= Byte.SIZE;
		}
	}


        
	protected void finilize() throws Throwable {
		soundLine.drain();
		sourcedataline.close();
	}
}
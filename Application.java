import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.io.File;
import java.io.IOException;

import ui.WavGraphing;
import audio.Reader;
import audio.SoundTrack;
import audio.Player;

class Application {

	private String audioFileName = "";

	// private WavGraphing graphFrame = new WavGraphing();

	private Reader audioReader = null;

	public static void main(String []args) {

		Application app = new Application(args);
		app.run();

	}

	public Application(String[] args) {
		processArgs(args);	
	}

	public void run() {

		try {
			
			audioReader = new Reader(new File(audioFileName));
			AudioFormat format = audioReader.getFormat();

			AudioFormat playFormat = new AudioFormat(format.getEncoding(), format.getSampleRate(), 
			format.getSampleSizeInBits(), format.getChannels(), format.getFrameSize(), format.getFrameRate(), false);

			List<SoundTrack> tracks = readAudioFile(1024);

			var th = new Thread(new Runnable() {
				public void run() {
					Player pl = new Player(playFormat);
					for (SoundTrack track : tracks)
						pl.play(track);
				}
			});
			th.start();
			th.join();
			// graphFrame.display();
			// graphFrame.exec();
			System.out.println("recorded "  + tracks.size() + " lines");
			System.out.println("Total size: " + tracks.size() * 1024);

		} catch (UnsupportedAudioFileException ex) {
			System.out.println("Unsupported: " + ex.getMessage());
			System.exit(0);
		} catch (IOException ex) {
			System.out.println("Unsupported: " + ex.getMessage());
			System.exit(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private List<SoundTrack> readAudioFile(int trackSize) {
		List<SoundTrack> tracks = new ArrayList<>();
		try {
			SoundTrack res = null;
			do { 
				res = audioReader.read(trackSize);
				if (res.length != 0)
					tracks.add(res);
			} while (!res.isEmpty());
		} catch (IOException ex) {
			System.out.println("Error: " + ex.getMessage());
			System.exit(0);
		}
		return tracks;
	}

		private void processArgs(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-f") && i + 1 < args.length)
				audioFileName = args[i + 1];
			
		}
	}
};


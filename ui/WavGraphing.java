package ui;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import javax.swing.JFrame;

import audio.*;

public class WavGraphing extends JFrame {    
	private static final Dimension MIN_DIM = new Dimension(640, 480);

	private GraphingPanel wavePlot = new GraphingPanel("Amplitude Time");
	private GraphingPanel frequencyPlot = new GraphingPanel("Amplitude Frequency");
	private AudioFormat playFormat;

	private Reader audioReader = null;
	private List<SoundTrack> tracks;

	public WavGraphing(String audioFileName) {

		super("Audio Graphing");

		setGeometry();	
		add(wavePlot);    
		add(frequencyPlot);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		initSound(audioFileName);
	}

	private void setGeometry() {
		setMinimumSize(MIN_DIM);
		setLayout(new GridLayout(2, 1));    
	}

	private void initSound(String fileName) {
		try {
			audioReader = new Reader(new File(fileName));
			AudioFormat format = audioReader.getFormat();

			playFormat = new AudioFormat(format.getEncoding(), format.getSampleRate(), 
			format.getSampleSizeInBits(), format.getChannels(), format.getFrameSize(), format.getFrameRate(), false);

			tracks = readAudioFile(2048);

		} catch (UnsupportedAudioFileException ex) {
			System.out.println("Unsupported: " + ex.getMessage());
		} catch (IOException ex) {
			System.out.println("Unsupported: " + ex.getMessage());
		}
	}

	public void display() {   

		setVisible(true);	

	}

	public void exec() {

		try {
			Player pl = new Player(playFormat);
			fft inst = new fft();
			while (true) {

				for (SoundTrack track : tracks) {
					int[] timedata = track.channels.get(0);
					fft_cpx[] result = inst.forward(timedata);
					double[] dbFreq = fft.dbFromCpx(Arrays.copyOf(result, result.length / 2));

					var thFreq = drawFreqAsync(dbFreq);
					var thTime = drawTimeAsync(timedata);
					thFreq.start();
					thTime.start();

					
					pl.play(track);

					thFreq.join();
					thTime.join();
				}

				Thread.sleep(5);
			}

		} catch (InterruptedException ex) {
			
		}
		
	}

	private Thread drawFreqAsync(double[] freqData) {
		var th = new Thread(new Runnable() {
			public void run() {
				
				double[] sFreqData = fft.movingAverage(freqData, 4);
				frequencyPlot.sampleToQueue(sFreqData);	
				frequencyPlot.draw();
			}
		});
		return th;
	}

	private Thread drawTimeAsync(int[] timedata) {
		var th = new Thread(new Runnable() {
			public void run() {
				double[] dtd = Arrays.stream(timedata).asDoubleStream().toArray();
				
				dtd = fft.movingAverage(dtd, 10);
				wavePlot.sampleToQueue(dtd);	
				wavePlot.draw();
			}
		});
		return th;
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
} 
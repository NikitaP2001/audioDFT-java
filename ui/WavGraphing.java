package ui;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
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

			tracks = readAudioFile(8192);

			/*
			
			*/
			
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
					int[] data = track.channels.get(0);

					fft_cpx[] result = inst.forward(data);
					double[] ampFreq = fft.amplitude(result);
					var th = drawSampleAsync(ampFreq);
					th.start();

					
					// int[] orig = inst.inverse(result);
					pl.play(track);

					th.join();
				}

				Thread.sleep(5);
			}

		} catch (InterruptedException ex) {
			
		}
		
	}

	private Thread drawSampleAsync(double[] data) {
		var th = new Thread(new Runnable() {
			public void run() {
				
				for (int i = 0; i < data.length; i++) {
					data[i] = Math.log10(data[i]);
				}
				wavePlot.sampleToQueue(data);	
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
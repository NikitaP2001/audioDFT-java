package ui;

import java.io.InterruptedIOException;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JFrame;

public class WavGraphing extends JFrame {    
	private static final Dimension MIN_DIM = new Dimension(640, 480);

	private GraphingPanel wavePlot = new GraphingPanel("Amplitude Time");
	private GraphingPanel frequencyPlot = new GraphingPanel("Amplitude Frequency");

	public WavGraphing() {

		super("Audio Graphing");

		setSize(640, 480);    
		setMinimumSize(MIN_DIM);
		setLayout(new GridLayout(2, 1));    
		add(wavePlot);    
		add(frequencyPlot);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void display() {   

		setVisible(true);	

	}

	public void exec() {

		try {
			while (true) {
				wavePlot.drawShape();

				Thread.sleep(1);
			}

		} catch (InterruptedException ex) {
			
		}
		
	}
} 
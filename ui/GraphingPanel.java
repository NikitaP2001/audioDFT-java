package ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.geom.Point2D;
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

import ui.ThemeProvider.Element;

class GraphingPanel extends JPanel {
	private double pltHeight = 100;
	private Plot plot = new Plot(100, pltHeight);
	private TitledBorder blackline = BorderFactory.createTitledBorder("Graphing Panel");
	private double[] sample;
	private int disk = 1000;

	public GraphingPanel(String title) {
		blackline.setTitleFont(new Font("Arial", Font.ITALIC, 16));
		blackline.setTitle(title);
		setBorder(blackline);
		setLayout(new GridLayout(1, 1));
		add(plot);
		updateStyle();

		plot.setDiscretization(4096);
	}

	public void sampleToQueue(double[] values) {
		if (disk != values.length) {
			disk = values.length;
			plot.setDiscretization(disk);
		}
		sample = values;
	}

	public void draw() {
		if (sample != null && sample.length != 0) {
			double maxVal = Arrays.stream(sample).max().getAsDouble();
			double minVal = Arrays.stream(sample).min().getAsDouble();
			double newHeight = maxVal - minVal;
			if (newHeight / pltHeight > 1.25 || newHeight / pltHeight < 0.85) {
				pltHeight = newHeight * 3;
				plot.setDim(100, newHeight * 1.5);

			}

			plot.setViewPos(new Point2D.Double(0, minVal));

			plot.addPoints(sample);
			plot.draw();
		}
	}

	private void updateStyle() {
		setBackground(ThemeProvider.getColor(Element.Background));
		blackline.setTitleColor(ThemeProvider.getColor(ThemeProvider.Element.Text));
	}
}

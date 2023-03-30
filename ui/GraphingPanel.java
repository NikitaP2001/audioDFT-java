package ui;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.geom.Point2D;

import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;


import ui.ThemeProvider.Element;

class GraphingPanel extends JPanel {
	private Plot plot = new Plot(100, 100);
	TitledBorder blackline = BorderFactory.createTitledBorder("Graphing Panel");

	public GraphingPanel(String title) {
		blackline.setTitleFont(new Font("Arial", Font.ITALIC, 16));
		blackline.setTitle(title);
		setBorder(blackline);
		setLayout(new GridLayout(1, 1));
		add(plot);
		updateStyle();


		plot.setViewPos(new Point2D.Double(0, -50));
		double[] pltData = {10, 20, 30, 40, 50, 45, 30, 20, -10, -20};
		plot.addPoints(pltData);
	}

	public void draw() {
		plot.draw();
	}

	private void updateStyle() {
		setBackground(ThemeProvider.getColor(Element.Background));
		blackline.setTitleColor(ThemeProvider.getColor(ThemeProvider.Element.Text));
	}
}

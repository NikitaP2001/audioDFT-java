package ui;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;

import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;


import ui.ThemeProvider.Element;

class GraphingPanel extends JPanel {

	TitledBorder blackline = BorderFactory.createTitledBorder("Graphing Panel");

	public GraphingPanel(String title) {

		blackline.setTitleFont(new Font("Arial", Font.ITALIC, 32));
		blackline.setTitle(title);
		setBorder(blackline);

		updateStyle();

		drawShape();
	}

	private void drawShape() {
		Path2D path = new Path2D.Float();
		path.moveTo(20, 20);
		path.lineTo(100, 100);
		path.lineTo(100, 120);
		path.lineTo(120, 120);
		path.closePath();
		Graphics2D panelGraphics = (Graphics2D)getGraphics();
		panelGraphics.draw(path);
	}

	private void updateStyle() {

		setBackground(ThemeProvider.getColor(Element.Background));
		blackline.setTitleColor(ThemeProvider.getColor(ThemeProvider.Element.Text));
	}
}

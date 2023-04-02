package ui;

import java.util.Arrays;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

import ui.ThemeProvider.Element;

class Plot extends JPanel {
	private static final int curveWidth = 3;
	private static final Color BG_TRANSPARENT = new Color(0, 0, 0, 0);

	private int pointsInView = 10;
	private double virtHeight;
	private double virtWidth;
	private Point2D viewPoint = new Point2D.Double(0, 0);
	private Path2D path = new Path2D.Double();

	private BufferedImage buffer;

	private double[] shownPoints = new double[0];

	public Plot(double width, double height) {
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int width = getWidth();
				int height = getHeight();
				BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = newImage.createGraphics();
				g2d.drawImage(buffer, 0, 0, width, height, null);
				g2d.dispose();
				buffer = newImage;
			}
		});
		virtHeight = height;
		virtWidth = width;
		setOpaque(false);
		updateStyle();
	}

	public void setDim(double width, double height) {
		virtHeight = height;
		virtWidth = width;
	}

	public void setViewPos(Point2D leftBottom) {
		viewPoint = leftBottom;
	}

	public void setDiscretization(int nPoints) {
		pointsInView = nPoints;
	}

	public void addPoints(double[] points) {
		double step = virtWidth / pointsInView;
		double oXPos = viewPoint.getX();
		if (points.length == 0 || points.length > pointsInView)
			throw new IllegalArgumentException("wrong points amount");

		shownPoints = (shownPoints.length + points.length > pointsInView)
		? appendWithOverlap(shownPoints, points, pointsInView)
		: appendPoints(shownPoints, points);
		
		path.reset();
		path.moveTo(oXPos, shownPoints[0]);
		for (int i = 0; i < shownPoints.length; i++) {
			path.lineTo(oXPos, shownPoints[i]);
			oXPos += step;
		}
	}

	private void clearImage(BufferedImage bufferedImage) {
		Graphics2D g = bufferedImage.createGraphics();
		g.setBackground(BG_TRANSPARENT);
		g.clearRect(0, 0, bufferedImage.getWidth(),
		bufferedImage.getHeight());
		g.dispose();
    	}

	public void draw() {
		if (buffer == null)
			buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		clearImage(buffer);
		Graphics2D bufGraphics = (Graphics2D)buffer.getGraphics();
		bufGraphics.setStroke(new BasicStroke(curveWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		bufGraphics.setColor(ThemeProvider.getColor(Element.PlotCurve));
		AffineTransform transform = getTransform();
		Shape plotShape = path.createTransformedShape(transform);
		bufGraphics.draw(plotShape);
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.drawImage(buffer, 0, 0, null);
		g2d.dispose();
	}

	private static double[] appendWithOverlap(double[] first, double[] second, int maxLength) {
		int overlapLen = first.length + second.length - maxLength;
		double[] overlapPoints = Arrays.copyOfRange(second, second.length - maxLength, second.length);
		second = Arrays.copyOfRange(second, 0, second.length - maxLength);
		first = Arrays.copyOfRange(first, overlapLen, first.length);
		first = appendPoints(overlapPoints, first);
		return appendPoints(first, second);
	}

	private static double[] appendPoints(double[] first, double[] second) {
		double[] result = new double[first.length + second.length];
		System.arraycopy(first, 0, result, 0, first.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	private AffineTransform getTransform() {
		double scaleOx = getWidth() / virtWidth;
		double scaleOy = - getHeight() / virtHeight;
		double shiftOy = getHeight() - viewPoint.getY() * scaleOy;
		double shiftOx = - viewPoint.getX() * scaleOx;
		return new AffineTransform(scaleOx, 0, 0, scaleOy, shiftOx, shiftOy);
	}

	private void updateStyle() {
		setBackground(BG_TRANSPARENT);
	}
}
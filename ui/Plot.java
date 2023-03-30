package ui;

import java.util.Arrays;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
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

	private double[] shownPoints = new double[0];

	public Plot(double width, double height) {
		virtHeight = height;
		virtWidth = width;
		updateStyle();
	}

	void setDim(double width, double height) {
		virtHeight = height;
		virtWidth = width;
	}

	void setViewPos(Point2D leftBottom) {
		viewPoint = leftBottom;
	}

	void setDiscretization(int nPoints) {
		pointsInView = nPoints;
	}

	void addPoints(double[] points) {
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

	void draw() {
		AffineTransform transform = getTransform();
		Graphics2D plotGraphics = (Graphics2D)getGraphics();
		plotGraphics.setStroke(new BasicStroke(curveWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		plotGraphics.setColor(ThemeProvider.getColor(Element.PlotCurve));
		Shape plotShape = path.createTransformedShape(transform);
		plotGraphics.draw(plotShape);
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
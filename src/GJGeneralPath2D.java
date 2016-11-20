import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author dlegland
 *
 */
public class GJGeneralPath2D implements GJCurve2D {

	// ===================================================================
	// Static variables and constants
   
	private enum Type {
		MOVE,
		LINE, 
		QUAD,
		CUBIC,
		CLOSE;
	}
	

	// ===================================================================
	// class variables
   
	/**
	 * A collection of elementary segments that can be linear, quad, conic, 
	 * move or close
	 */
	ArrayList<Segment> segments;
		
	/**
	 * The last creation option of this path, used to know which action are
	 * allowed.
	 */
	Type lastType = Type.CLOSE;

	
	// ===================================================================
	// constructors

	/**
	 * Initialize an empty path.
	 */
	public GJGeneralPath2D() {
		this.segments = new ArrayList<Segment>();
	}
	
	/**
	 * Copy constructor.
	 */
	public GJGeneralPath2D(GJGeneralPath2D path) {
		
		// init local segment array
		this.segments = new ArrayList<Segment>(path.segments.size());
		
		GJPoint2D[] pts;
		
		// iterate on the collection of segments
		for (Segment seg : path.segments) {
			switch(seg.type()) {
			case MOVE:
				pts = seg.controlPoints();
				this.moveTo(pts[0]);
				break;

			case LINE:
				pts = seg.controlPoints();
				this.lineTo(pts[0]);
				break;

			case QUAD:
				pts = seg.controlPoints();
				this.quadTo(pts[0], pts[1]);
				break;

			case CUBIC:
				pts = seg.controlPoints();
				this.cubicTo(pts[0], pts[1], pts[2]);
				break;

			case CLOSE:
				this.closePath();
			}
		}
	}
	
	// ===================================================================
	// methods specific to GJGeneralPath2D

	public void moveTo(GJPoint2D p) {
		this.segments.add(new MoveSegment(p));
		this.lastType = Type.MOVE;
	}

	public void lineTo(GJPoint2D p) {
		this.segments.add(new LinearSegment(p));
		this.lastType = Type.LINE;
	}

	public void quadTo(GJPoint2D p1, GJPoint2D p2) {
		this.segments.add(new QuadSegment(p1, p2));
		this.lastType = Type.QUAD;
	}

	public void cubicTo(GJPoint2D p1, GJPoint2D p2, GJPoint2D p3) {
		this.segments.add(new CubicSegment(p1, p2, p3));
		this.lastType = Type.CUBIC;
	}

	public void closePath() {
		// Cannot close a path more than once
		if (this.lastType == Type.CLOSE) 
			return;
		this.segments.add(new ClosingSegment());
	}
	
	/**
	 * Returns a collection of smooth curves corresponding to the elementary
	 * segments.
	 */
	private Collection<GJSmoothCurve2D> smoothCurves() {
		GJPoint2D lastControl = null;
		GJPoint2D lastStart = null;
		GJPoint2D[] pts;
		
		int n = this.segments.size();
		ArrayList<GJSmoothCurve2D> curves = new ArrayList<GJSmoothCurve2D>(n);
		
		for (Segment seg : this.segments) {
			switch (seg.type()) {
			case MOVE:
				// update last control and initial point
				lastStart = seg.lastControl();
				lastControl = lastStart;
				break;

			case LINE:
				pts = seg.controlPoints();
				curves.add(new GJLineSegment2D(lastControl, pts[0]));
				lastControl = pts[0];
				break;
				
			case QUAD:
				pts = seg.controlPoints();
				curves.add(new GJQuadBezierCurve2D(lastControl, pts[0], pts[1]));
				lastControl = pts[1];
				break;
				
			case CUBIC:
				pts = seg.controlPoints();
				curves.add(new GJCubicBezierCurve2D(lastControl, pts[0], pts[1], pts[2]));
				lastControl = pts[2];
				break;

			case CLOSE:
				// connect to the last initial point
				curves.add(new GJLineSegment2D(lastControl, lastStart));
				lastControl = lastStart;
				break;
				
			default:
				throw new RuntimeException("Unknown Path segment type: " + seg.type());
			}
		}

		// Returns the set of curves
		return curves;
	}

	private GJSmoothCurve2D segmentCurve(int index) {
		// Check index validity
		int n = this.segments.size();
		if (index > n - 2)
			throw new IllegalArgumentException("Index must be lower than segment number");
		
		GJSmoothCurve2D curve = null;
		GJPoint2D lastControl = null;
		GJPoint2D lastStart = null;
		GJPoint2D[] pts;
		
		
		for (int i = 0; i < index + 2; i++) {
			Segment seg = this.segments.get(i);
			switch (seg.type()) {
			case MOVE:
				// update last control and initial point
				lastStart = seg.lastControl();
				lastControl = lastStart;
				break;

			case LINE:
				pts = seg.controlPoints();
				curve = new GJLineSegment2D(lastControl, pts[0]);
				lastControl = pts[0];
				break;
				
			case QUAD:
				pts = seg.controlPoints();
				curve = new GJQuadBezierCurve2D(lastControl, pts[0], pts[1]);
				lastControl = pts[1];
				break;
				
			case CUBIC:
				pts = seg.controlPoints();
				curve = new GJCubicBezierCurve2D(lastControl, pts[0], pts[1], pts[2]);
				lastControl = pts[2];
				break;

			case CLOSE:
				// connect to the last initial point
				curve = new GJLineSegment2D(lastControl, lastStart);
				lastControl = lastStart;
				break;
				
			default:
				throw new RuntimeException("Unknown Path segment type: " + seg.type());
			}
		}

		// Returns the set of curves
		return curve;
	}
	
	// ===================================================================
	// Methods implementing the GJCurve2D interface

	/**
	 * The first parameterization value is equal to 0.
	 */
	public double t0() {
		return 0;
	}

	public double getT0() {
		return 0;
	}

	/**
	 * The last parameterization value is given by the number of elementary
	 * operations (moveTo, closePath, lineTo...) minus one (for the initial
	 * move). 
	 */
	public double t1() {
		return segments.size() - 1;
	}

	public double getT1() {
		return t1();
	}

	public GJPoint2D point(double t) {
		// get curve segment index 
		int index = (int) Math.floor(t);
		
		// Special case of last point
		if (index == this.segments.size() - 1 && Math.abs(t - index) < GJShape2D.ACCURACY)
			return this.lastPoint();
			
		// extract curve segment
		GJSmoothCurve2D curve = segmentCurve(index);
		if (curve == null) {
			throw new RuntimeException("Can not manage position for MOVE Path segments");
		}
		
		// convert from global to local coordinates
		double t0 = curve.t0();
		double t1 = curve.t1();
		double t2 = (t - index) * (t1 - t0) + t0;
		
		// delegate processing to sub-curve instance
		return curve.point(t2);
	}

	/**
	 * Returns the first point of the curve, or null if this curve is empty.
	 */
	public GJPoint2D firstPoint() {
		if (this.segments.isEmpty()) 
			return null;
		return this.segments.get(0).controlPoints()[0];
	}

	/**
	 * Returns the last point of the curve, or null if this curve is empty.
	 */
	public GJPoint2D lastPoint() {
		int n = this.segments.size();
		if (n == 0) 
			return null;
		return this.segments.get(n-1).lastControl();
	}

	public Collection<GJPoint2D> singularPoints() {
		// allocate memory for result
		ArrayList<GJPoint2D> points = new ArrayList<GJPoint2D>(this.segments.size());
		
		// iterate on segments, and add the last control of each segment
		for (Segment seg : this.segments) {
			GJPoint2D p = seg.lastControl();
			if (p != null)
				points.add(p);
		}
		
		// return the set of singular points
		return points;
	}

	public Collection<GJPoint2D> vertices() {
		// allocate memory for result
		ArrayList<GJPoint2D> vertices = new ArrayList<GJPoint2D>(this.segments.size());
		
		// iterate on segments, and add the control points of each segment
		for (Segment seg : this.segments) {
			for (GJPoint2D p : seg.controlPoints()) {
				vertices.add(p);
			}
		}
		
		// return the set of vertices
		return vertices;
	}

	public boolean isSingular(double pos) {
		return Math.abs(pos - Math.round(pos)) < GJShape2D.ACCURACY;
	}

	public double position(GJPoint2D point) {
		return this.project(point);
	}

	public double project(GJPoint2D point) {
		// local variables for computing position
		int index = -1;
		double pos = Double.NaN;
		double minDist = Double.MAX_VALUE;
		double dist;
		
		// local variables for iterating on segment curves
		GJPoint2D lastControl = null;
		GJPoint2D lastStart = null;
		GJPoint2D[] pts;
		GJSmoothCurve2D curve;
		
		int n = this.segments.size();
		
		for (int i = 0; i < n; i++) {
			Segment seg = segments.get(i);
			switch (seg.type()) {
			case MOVE:
				// update last control and initial point
				lastStart = seg.lastControl();
				lastControl = lastStart;
				continue;

			case LINE:
				pts = seg.controlPoints();
				curve = new GJLineSegment2D(lastControl, pts[0]);
				lastControl = pts[0];
				break;
				
			case QUAD:
				pts = seg.controlPoints();
				curve = new GJQuadBezierCurve2D(lastControl, pts[0], pts[1]);
				lastControl = pts[1];
				break;
				
			case CUBIC:
				pts = seg.controlPoints();
				curve = new GJCubicBezierCurve2D(lastControl, pts[0], pts[1], pts[2]);
				lastControl = pts[2];
				break;

			case CLOSE:
				// connect to the last initial point
				curve = new GJLineSegment2D(lastControl, lastStart);
				lastControl = lastStart;
				break;
				
			default:
				throw new RuntimeException("Unknown Path segment type: " + seg.type());
			}
			
			// Compute position on local curve
			dist = curve.distance(point);
			if (dist < minDist) {
				minDist = dist;
				index = i - 1;
				pos = index + curve.position(point);
			}
		}

		// Returns the set of curves
		return pos;
	}

	public Collection<GJPoint2D> intersections(GJLinearShape2D line) {
		// allocate array for the result
		ArrayList<GJPoint2D> pts = new ArrayList<GJPoint2D>();
		
		// Iterate on the set of curves, and compute intersections
		for (GJSmoothCurve2D curve : this.smoothCurves()) {
			pts.addAll(curve.intersections(line));
		}
		
		// returns the full set of intersections
		return pts;
	}

	public GJCurve2D reverse() {
		ArrayList<GJContinuousCurve2D> list = splitContinuousCurves();
		Collections.reverse(list);
		return new GJCurveArray2D<GJContinuousCurve2D>(list);
	}

	public Collection<? extends GJContinuousCurve2D> continuousCurves() {
		return splitContinuousCurves();
	}

	private ArrayList<GJContinuousCurve2D> splitContinuousCurves() {
		GJPoint2D lastControl = null;
		GJPoint2D lastStart = null;
		GJPoint2D[] pts;
		
		int n = this.segments.size();
		ArrayList<GJContinuousCurve2D> curveList = new ArrayList<GJContinuousCurve2D>(n);
		
		// the current continuous curve
		GJPolyCurve2D<GJSmoothCurve2D> curve = null;
		
		for (Segment seg : this.segments) {
			switch (seg.type()) {
			case MOVE:
				// If current curve is not null, add it the the set
				if (curve != null) 
					curveList.add(curve);
				curve = new GJPolyCurve2D<GJSmoothCurve2D>();
				
				// update last control and initial point
				lastStart = seg.lastControl();
				lastControl = lastStart;
				break;

			case LINE:
				pts = seg.controlPoints();
				curve.add(new GJLineSegment2D(lastControl, pts[0]));
				lastControl = pts[0];
				break;
				
			case QUAD:
				pts = seg.controlPoints();
				curve.add(new GJQuadBezierCurve2D(lastControl, pts[0], pts[1]));
				lastControl = pts[1];
				break;
				
			case CUBIC:
				pts = seg.controlPoints();
				curve.add(new GJCubicBezierCurve2D(lastControl, pts[0], pts[1], pts[2]));
				lastControl = pts[2];
				break;

			case CLOSE:
				// connect and close to the last initial point
				curve.add(new GJLineSegment2D(lastControl, lastStart));
				curve.setClosed(true);
				
				// add current curve to the list, and reset it
				curveList.add(curve);
				curve = new GJPolyCurve2D<GJSmoothCurve2D>();
				
				// update for next start
				lastControl = lastStart;
				break;
				
			default:
				throw new RuntimeException("Unknown Path segment type: " + seg.type());
			}
		}

		// Returns the set of continuous curves
		return curveList;
	}
	
	public GJCurve2D subCurve(double t0, double t1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Transform to a java Path2D object.
	 */
	public Path2D asAwtShape() {
		// creates the awt path
		Path2D.Double path = new Path2D.Double();
		
		// iterate on the path segments
		for (Segment seg : this.segments) {
			seg.updatePath(path);
		}
		
		// returns the updated path
		return path;
	}

	// ===================================================================
	// Methods implementing the GJShape2D interface
	
	public boolean contains(GJPoint2D p) {
		return this.contains(p.x(), p.y());
	}

	public boolean contains(double x, double y) {		
		GJPoint2D lastControl = null;
		GJPoint2D lastStart = null;
		
		for (Segment seg : this.segments) {
			switch (seg.type()) {
			case MOVE:
				lastStart = seg.lastControl();
				lastControl = lastStart;
				break;

			case LINE:
			case QUAD:
			case CUBIC:
				if (seg.asCurve(lastControl, lastStart).contains(x, y))
					return true;
				lastControl = seg.lastControl();
				break;

			case CLOSE:
				if (seg.asCurve(lastControl, lastStart).contains(x, y))
					return true;
				lastControl = lastStart;
				break;
				
			default:
				throw new RuntimeException("Unknown Path segment type: " + seg.type());
			}
		}

		return false;
	}

	public double distance(GJPoint2D p) {
		return this.distance(p.x(), p.y());
	}

	public double distance(double x, double y) {
		double minDist = Double.MAX_VALUE;
		double dist;
		
		GJPoint2D lastControl = null;
		GJPoint2D lastStart = null;
		
		for (Segment seg : this.segments) {
			switch (seg.type()) {
			case MOVE:
				lastStart = seg.lastControl();
				lastControl = lastStart;
				break;

			case LINE:
			case QUAD:
			case CUBIC:
				dist = seg.asCurve(lastControl, lastStart).distance(x, y);
				minDist = Math.min(dist, minDist);
				lastControl = seg.lastControl();
				break;

			case CLOSE:
				dist = seg.asCurve(lastControl, lastStart).distance(x, y);
				minDist = Math.min(dist, minDist);
				lastControl = lastStart;
				break;
				
			default:
				throw new RuntimeException("Unknown Path segment type: " + seg.type());
			}
		}

		return minDist;
	}

	/**
	 * Returns true, as a curve composed of Bezier pieces is always bounded.
	 */
	public boolean isBounded() {
		return true;
	}

	public boolean isEmpty() {
		return this.segments.size() > 0;
	}

	public GJBox2D boundingBox() {
		// Initialize with extreme values
		double xmin = Double.MAX_VALUE;
		double ymin = Double.MAX_VALUE;
		double xmax = Double.MIN_VALUE;
		double ymax = Double.MIN_VALUE;

		// coordinates of current point
		double x, y;
		
		// Iterate on each control point of each segment
		for (Segment seg : this.segments) {
			for (GJPoint2D p : seg.controlPoints()) {
				// get current coordinates
				x = p.x();
				y = p.y();
				
				// update bounds
				xmin = Math.min(xmin, x);
				ymin = Math.min(ymin, y);
				xmax = Math.max(xmax, x);
				ymax = Math.max(ymax, y);
			}
		}
		
		// createFromCollection a new GJBox2D with the bounds
		return new GJBox2D(xmin, xmax, ymin, ymax);
	}

	public GJCurveSet2D<? extends GJCurve2D> clip(GJBox2D box) {
		ArrayList<GJContinuousCurve2D> list = splitContinuousCurves();
		GJCurve2D curve = new GJCurveArray2D<GJContinuousCurve2D>(list);
		return curve.clip(box);
	}

	public GJCurve2D transform(GJAffineTransform2D trans) {
		GJGeneralPath2D path = new GJGeneralPath2D();
		GJPoint2D[] pts;
		
		for (Segment seg : this.segments) {
			switch(seg.type()) {
			case MOVE:
				path.moveTo(seg.lastControl().transform(trans));
				break;

			case LINE:
				path.lineTo(seg.lastControl().transform(trans));
				break;

			case QUAD:
				pts = seg.controlPoints();
				path.quadTo(pts[0].transform(trans), pts[1].transform(trans));
				break;

			case CUBIC:
				pts = seg.controlPoints();
				path.cubicTo(pts[0].transform(trans), pts[1].transform(trans), 
						pts[2].transform(trans));
				break;

			case CLOSE:
				path.closePath();
				break;
				
			default:
				throw new RuntimeException("Unknown Path segment type: " + seg.type());
			}
		}

		return path;
	}

	public void draw(Graphics2D g2) {
		Path2D path = this.asAwtShape();
		g2.draw(path);
	}

	// ===================================================================
	// Methods implementing the GJGeometricObject2D interface

	public boolean almostEquals(GJGeometricObject2D obj, double eps) {
		// Check class of object
		if (obj == null)
			return false;
		if (!(obj instanceof GJGeneralPath2D))
			return false;
		
		// class cast
		GJGeneralPath2D that = (GJGeneralPath2D) obj;
		
		// Paths should have same number of segments
		if (this.segments.size() != that.segments.size())
			return false;
		
		Segment seg1, seg2;
		GJPoint2D[] pts1, pts2;
		
		for (int i = 0; i < this.segments.size(); i++) {
			// extract each segment
			seg1 = this.segments.get(i);
			seg2 = that.segments.get(i);
			
			// check segments have same type
			if (seg1.type() != seg2.type())
				return false;
			
			// extract control points
			pts1 = seg1.controlPoints();
			pts2 = seg2.controlPoints();
			
			// check size of control point arrays
			if (pts1.length != pts2.length)
				throw new RuntimeException("Two path segments have type but different number of control points");
			
			// check identity of control points
			for (int j = 0; j < pts1.length; j++) {
				if (!pts1[j].almostEquals(pts2[j], eps))
					return false;
			}
		}
		
		// if no difference was found, then the paths are almost equal
		return true;
	}


	// ===================================================================
	// Methods from the Object superclass

	public boolean equals(Object obj) {
		// Check class of object
		if (obj == null)
			return false;
		if (!(obj instanceof GJGeneralPath2D))
			return false;
		
		// class cast
		GJGeneralPath2D that = (GJGeneralPath2D) obj;
		
		// Paths should have same number of segments
		if (this.segments.size() != that.segments.size())
			return false;
		
		Segment seg1, seg2;
		GJPoint2D[] pts1, pts2;
		
		for (int i = 0; i < this.segments.size(); i++) {
			// extract each segment
			seg1 = this.segments.get(i);
			seg2 = that.segments.get(i);
			
			// check segments have same type
			if (seg1.type() != seg2.type())
				return false;
			
			// extract control points
			pts1 = seg1.controlPoints();
			pts2 = seg2.controlPoints();
			
			// check size of control point arrays
			if (pts1.length != pts2.length)
				throw new RuntimeException("Two path segments have type but different number of control points");
			
			// check identity of control points
			for (int j = 0; j < pts1.length; j++) {
				if (!pts1[j].equals(pts2[j]))
					return false;
			}
		}
		
		// if no difference was found, then the paths are almost equal
		return true;		
	}
	
	public GJGeneralPath2D clone() {
		GJGeneralPath2D path = new GJGeneralPath2D();
		GJPoint2D[] pts;
		
		// iterate on the collection of segments
		for (Segment seg : this.segments) {
			switch(seg.type()) {
			case MOVE:
				pts = seg.controlPoints();
				path.moveTo(pts[0]);
				break;

			case LINE:
				pts = seg.controlPoints();
				path.lineTo(pts[0]);
				break;

			case QUAD:
				pts = seg.controlPoints();
				path.quadTo(pts[0], pts[1]);
				break;

			case CUBIC:
				pts = seg.controlPoints();
				path.cubicTo(pts[0], pts[1], pts[2]);
				break;

			case CLOSE:
				path.closePath();
				
			default:
				throw new RuntimeException("Unknown Path segment type: " + seg.type());
			}
		}

		// return the new path
		return path;
	}

	// ===================================================================
	// Declaration and implementation of the path segments

	private interface Segment {
		public Type type();
		public GJPoint2D[] controlPoints();
		public GJSmoothCurve2D asCurve(GJPoint2D lastControl, GJPoint2D lastStart);
		/**
		 * Returns the last control point of the segment, or null if the
		 * segment type is CLOSE.
		 */
		public GJPoint2D lastControl();
		public void updatePath(Path2D path);
	}
	
	private class MoveSegment implements Segment {

		GJPoint2D p;
		public MoveSegment(GJPoint2D p) {
			this.p = p;
		}
		
		public GJPoint2D[] controlPoints() {
			return new GJPoint2D[]{p};
		}

		public Type type() {
			return Type.MOVE;
		}

		/**
		 * Returns null.
		 */
		public GJSmoothCurve2D asCurve(GJPoint2D lastControl, GJPoint2D lastStart) {
			return null;
		}		
		
		public GJPoint2D lastControl() {
			return p;
		}
		
		public void updatePath(Path2D path) {
			path.moveTo(p.x(), p.y());
		}

	}

	private class LinearSegment implements Segment {

		GJPoint2D p;
		public LinearSegment(GJPoint2D p) {
			this.p = p;
		}
		
		public GJPoint2D[] controlPoints() {
			return new GJPoint2D[]{p};
		}

		public Type type() {
			return Type.LINE;
		}		

		public GJSmoothCurve2D asCurve(GJPoint2D lastControl, GJPoint2D lastStart) {
			return new GJLineSegment2D(lastControl, p);
		}		

		public GJPoint2D lastControl() {
			return p;
		}
		
		public void updatePath(Path2D path) {
			path.lineTo(p.x(), p.y());
		}		
	}
	

	private class QuadSegment implements Segment {

		GJPoint2D p1;
		GJPoint2D p2;
		public QuadSegment(GJPoint2D p1, GJPoint2D p2) {
			this.p1 = p1;
			this.p2 = p2;
		}
		
		public GJPoint2D[] controlPoints() {
			return new GJPoint2D[]{p1, p2};
		}

		public Type type() {
			return Type.QUAD;
		}		

		public GJSmoothCurve2D asCurve(GJPoint2D lastControl, GJPoint2D lastStart) {
			return new GJQuadBezierCurve2D(lastControl, p1, p2);
		}		

		public GJPoint2D lastControl() {
			return p2;
		}
		
		public void updatePath(Path2D path) {
			path.quadTo(p1.x(), p1.y(), p2.x(), p2.y());
		}		
	}
	
	private class CubicSegment implements Segment {

		GJPoint2D p1;
		GJPoint2D p2;
		GJPoint2D p3;
		
		public CubicSegment(GJPoint2D p1, GJPoint2D p2, GJPoint2D p3) {
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
		}
		
		public GJPoint2D[] controlPoints() {
			return new GJPoint2D[]{p1, p2, p3};
		}

		public Type type() {
			return Type.CUBIC;
		}		

		public GJSmoothCurve2D asCurve(GJPoint2D lastControl, GJPoint2D lastStart) {
			return new GJCubicBezierCurve2D(lastControl, p1, p2, p3);
		}		

		public GJPoint2D lastControl() {
			return p3;
		}
		
		public void updatePath(Path2D path) {
			path.curveTo(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y());
		}		
	}
	

	private class ClosingSegment implements Segment {

		public ClosingSegment() {
		}
		
		public GJPoint2D[] controlPoints() {
			return new GJPoint2D[]{};
		}

		public Type type() {
			return Type.CLOSE;
		}

		public GJSmoothCurve2D asCurve(GJPoint2D lastControl, GJPoint2D lastStart) {
			return new GJLineSegment2D(lastControl, lastStart);
		}		

		public GJPoint2D lastControl() {
			return null;
		}
		
		public void updatePath(Path2D path) {
			path.closePath();
		}		
	}
}

/* file : EllipseArc2D.java
 * 
 * Project : geometry
 *
 * ===========================================
 * 
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or (at
 * your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY, without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. if not, write to :
 * The Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 * 
 * Created on 24 avr. 2006
 *
 */



import static java.lang.Math.*;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;










/**
 * An arc of ellipse. It is defined by a supporting ellipse, a starting angle,
 * and a signed angle extent, both in radians. The ellipse arc is oriented
 * counter-clockwise if angle extent is positive, and clockwise otherwise.
 * 
 * @author dlegland
 */
public class GJEllipseArc2D extends GJAbstractSmoothCurve2D
implements GJSmoothOrientedCurve2D, GJEllipseArcShape2D, Cloneable {

    // ====================================================================
    // methods specific to GJEllipseArc2D

    /**
     * Specify supporting ellipse, start angle and end angle, and a flag
     * indicating whether the arc is directed or not.
     * 
     * @param ell the supporting ellipse
     * @param start the starting angle
     * @param extent the (signed) angle extent
	 *
	 * @deprecated since 0.11.1
	 */
	@Deprecated
    public static GJEllipseArc2D create(GJEllipse2D ell, double start,
                                        double extent) {
        return new GJEllipseArc2D(ell.xc, ell.yc, ell.r1, ell.r2, ell.theta,
        		start, extent);
    }

    /**
     * Specify supporting ellipse, start angle and end angle, and a flag
     * indicating whether the arc is directed or not.
     * 
     * @param ell the supporting ellipse
     * @param start the starting angle
     * @param end the ending angle
     * @param direct flag indicating if the arc is direct
     *
	 * @deprecated since 0.11.1
	 */
	@Deprecated
public static GJEllipseArc2D create(GJEllipse2D ell, double start, double end,
                                    boolean direct) {
    	return new GJEllipseArc2D(ell.xc, ell.yc, ell.r1, ell.r2, ell.theta,
    			start, end, direct);
    }


    // ====================================================================
    // Class variables

    /** The supporting ellipse */
    protected GJEllipse2D ellipse;

    /** The starting position on ellipse, in radians between 0 and +2PI */
    protected double    startAngle  = 0;

    /** The signed angle extent, in radians between -2PI and +2PI. */
    protected double    angleExtent = PI;

    
    // ====================================================================
    // Constructors

    /**
     * Construct a default Ellipse arc, centered on (0,0), with radii equal to 1
     * and 1, orientation equal to 0, start angle equal to 0, and angle extent
     * equal to PI/2.
     */
    public GJEllipseArc2D() {
        this(0, 0, 1, 1, 0, 0, PI/2);
    }

    /**
     * Specify supporting ellipse, start angle and angle extent.
     * 
     * @param ell the supporting ellipse
     * @param start the starting angle (angle between 0 and 2*PI)
     * @param extent the angle extent (signed angle)
     */
    public GJEllipseArc2D(GJEllipse2D ell, double start, double extent) {
    	this(ell.xc, ell.yc, ell.r1, ell.r2, ell.theta, start, extent);
    }

    /**
     * Specify supporting ellipse, start angle and end angle, and a flag
     * indicating whether the arc is directed or not.
     * 
     * @param ell the supporting ellipse
     * @param start the starting angle
     * @param end the ending angle
     * @param direct flag indicating if the arc is direct
     */
    public GJEllipseArc2D(GJEllipse2D ell, double start, double end, boolean direct) {
        this(ell.xc, ell.yc, ell.r1, ell.r2, ell.theta, start, end, direct);
    }

    /**
     * Specify parameters of supporting ellipse, start angle, and angle extent.
     */
    public GJEllipseArc2D(double xc, double yc, double a, double b, double theta,
                          double start, double extent) {
        this.ellipse = new GJEllipse2D(xc, yc, a, b, theta);
        this.startAngle = start;
        this.angleExtent = extent;
    }

    /**
     * Specify parameters of supporting ellipse, bounding angles and flag for
     * direct ellipse.
     */
    public GJEllipseArc2D(double xc, double yc, double a, double b, double theta,
                          double start, double end, boolean direct) {
        this.ellipse = new GJEllipse2D(xc, yc, a, b, theta);
        this.startAngle = start;
        this.angleExtent = GJAngle2D.formatAngle(end-start);
        if (!direct)
			this.angleExtent = this.angleExtent - PI * 2;
    }

    // ====================================================================
    // methods specific to GJEllipseArc2D

    public GJEllipse2D getSupportingEllipse() {
    	return ellipse;
    }
    
    public double getStartAngle() {
    	return startAngle;
    }
    
    public double getAngleExtent() {
    	return angleExtent;
    }
    
    /**
     * Returns true if the ellipse arc is direct, i.e. if the angle extent is
     * positive (or zero).
     */
    public boolean isDirect() {
    	return angleExtent >= 0;
    }
    
    public boolean containsAngle(double angle) {
        return GJAngle2D.containsAngle(
        		startAngle, startAngle+angleExtent, angle, angleExtent>0);
    }

    /** Returns the angle associated with the given position */
    public double getAngle(double position) {
		if (position < 0)
			position = 0;
		if (position > abs(angleExtent))
			position = abs(angleExtent);
		if (angleExtent < 0)
			position = -position;
		return GJAngle2D.formatAngle(startAngle + position);
    }

    // ====================================================================
    // methods from interface GJOrientedCurve2D

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJOrientedCurve2D#windingAngle(math.geom2d.GJPoint2D)
     */
    public double windingAngle(GJPoint2D point) {
        GJPoint2D p1 = point(0);
        GJPoint2D p2 = point(abs(angleExtent));

        // compute angle of point with extreme points
		double angle1 = GJAngle2D.horizontalAngle(point, p1);
		double angle2 = GJAngle2D.horizontalAngle(point, p2);

        // test on which 'side' of the arc the point lie
        boolean b1 = (new GJStraightLine2D(p1, p2)).isInside(point);
        boolean b2 = ellipse.isInside(point);

		if (angleExtent > 0) {
			if (b1 || b2) { // inside of ellipse arc
				if (angle2 > angle1)
					return angle2 - angle1;
				else
					return 2 * PI - angle1 + angle2;
			} else { // outside of ellipse arc
				if (angle2 > angle1)
					return angle2 - angle1 - 2 * PI;
				else
					return angle2 - angle1;
			}
		} else {
			if (!b1 || b2) {
				if (angle1 > angle2)
					return angle2 - angle1;
				else
					return angle2 - angle1 - 2 * PI;
			} else {
				if (angle1 > angle2)
					return angle2 - angle1 + 2 * PI;
				else
					return angle2 - angle1;
			}
        }
    }

    public boolean isInside(GJPoint2D p) {
		return signedDistance(p.x(), p.y()) < 0;
    }

    public double signedDistance(GJPoint2D p) {
        return signedDistance(p.x(), p.y());
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJShape2D#signedDistance(math.geom2d.GJPoint2D)
     */
    public double signedDistance(double x, double y) {
        boolean direct = angleExtent >= 0;

        double dist = distance(x, y);
        GJPoint2D point = new GJPoint2D(x, y);

        boolean inside = ellipse.isInside(point);
        if (inside)
			return angleExtent > 0 ? -dist : dist;

        GJPoint2D p1 = point(startAngle);
		double endAngle = startAngle + angleExtent;
		GJPoint2D p2 = point(endAngle);
		boolean onLeft = (new GJStraightLine2D(p1, p2)).isInside(point);

		if (direct && !onLeft)
			return dist;
		if (!direct && onLeft)
			return -dist;

		
        GJRay2D ray = new GJRay2D(p1, -sin(startAngle), cos(startAngle));
        boolean left1 = ray.isInside(point);
		if (direct && !left1)
			return dist;
		if (!direct && left1)
			return -dist;

		ray = new GJRay2D(p2, -sin(endAngle), cos(endAngle));
        boolean left2 = ray.isInside(point);
		if (direct && !left2)
			return dist;
		if (!direct && left2)
			return -dist;

        if (direct)
            return -dist;
        else
            return dist;
    }

    // ====================================================================
    // methods from interface GJSmoothCurve2D

    public GJVector2D tangent(double t) {
    	// format between min and max admissible values
    	t = min(max(0, t), abs(angleExtent));
    	
    	// compute tangent vector depending on position
		if (angleExtent < 0) {
			// need to invert vector for indirect arcs
			return ellipse.tangent(startAngle - t).times(-1);
		} else {
			return ellipse.tangent(startAngle + t);
        }
    }

    /**
     * Returns the curvature of the ellipse arc. 
     * Curvature is negative if the arc is indirect.
     */
    public double curvature(double t) {
        // convert position to angle
		if (angleExtent < 0)
			t = startAngle - t;
		else
			t = startAngle + t;
		double kappa = ellipse.curvature(t);
		return this.isDirect() ? kappa : -kappa;
    }

    // ====================================================================
    // methods from interface GJContinuousCurve2D

    /** Returns false, as an ellipse arc is never closed. */
    public boolean isClosed() {
        return false;
    }

	/**
	 * Returns a collection of curves containing only this circle arc.
	 */
    public Collection<? extends GJEllipseArc2D> smoothPieces() {
    	return wrapCurve(this);
    }

	/* (non-Javadoc)
	 * @see math.geom2d.curve.GJContinuousCurve2D#asPolyline(int)
	 */
	public GJPolyline2D asPolyline(int n) {

        // compute increment value
        double dt = Math.abs(this.angleExtent) / n;

        // allocate array of points, and compute each value.
        // Computes also value for last point.
        GJPoint2D[] points = new GJPoint2D[n + 1];
        for (int i = 0; i < n + 1; i++)
        	points[i] = this.point(i * dt);

        return new GJPolyline2D(points);
	}

    // ====================================================================
    // methods from interface GJCurve2D

    /** Always returns 0 */
    public double t0() {
        return 0;
    }

    /**
     * @deprecated replaced by t0() (since 0.11.1).
     */
    @Deprecated
    public double getT0() {
    	return t0();
    }
    
    /** Always returns the absolute value of the angle extent */
    public double t1() {
        return abs(angleExtent);
    }

    /**
     * @deprecated replaced by t1() (since 0.11.1).
     */
    @Deprecated
    public double getT1() {
    	return t1();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJCurve2D#point(double, math.geom2d.GJPoint2D)
     */
    public GJPoint2D point(double t) {
        // check bounds
        t = max(t, 0);
        t = min(t, abs(angleExtent));

        // convert position to angle
		if (angleExtent < 0)
			t = startAngle - t;
		else
			t = startAngle + t;

        // return corresponding point
        return ellipse.point(t);
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJCurve2D#position(math.geom2d.GJPoint2D)
     */
    public double position(GJPoint2D point) {
        double angle = GJAngle2D.horizontalAngle(ellipse.center(), point);
		if (this.containsAngle(angle))
			if (angleExtent > 0)
				return GJAngle2D.formatAngle(angle - startAngle);
			else
				return GJAngle2D.formatAngle(startAngle - angle);

        // If the point is not contained in the arc, return NaN.
        return Double.NaN;
    }

    public double project(GJPoint2D point) {
        double angle = ellipse.project(point);

        // Case of an angle contained in the ellipse arc
		if (this.containsAngle(angle)) {
			if (angleExtent > 0)
				return GJAngle2D.formatAngle(angle - startAngle);
			else
				return GJAngle2D.formatAngle(startAngle - angle);
        }

        // return either 0 or T1, depending on which extremity is closer.
        double d1 = this.firstPoint().distance(point);
        double d2 = this.lastPoint().distance(point);
		return d1 < d2 ? 0 : abs(angleExtent);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJCurve2D#intersections(math.geom2d.GJLinearShape2D)
     */
    public Collection<GJPoint2D> intersections(GJLinearShape2D line) {

        // check point contained in it
        ArrayList<GJPoint2D> array = new ArrayList<GJPoint2D>();
        for (GJPoint2D point : ellipse.intersections(line))
            if (contains(point))
                array.add(point);

        return array;
    }

    /**
     * Returns the ellipse arc which refers to the reversed parent ellipse, with
     * same start angle, and with opposite angle extent.
     */
    public GJEllipseArc2D reverse() {
    	double newStart = GJAngle2D.formatAngle(startAngle + angleExtent);
		return new GJEllipseArc2D(ellipse, newStart, -angleExtent);
    }

	@Override
    public Collection<? extends GJEllipseArc2D> continuousCurves() {
    	return wrapCurve(this);
    }

    /**
     * Returns a new GJEllipseArc2D.
     */
    public GJEllipseArc2D subCurve(double t0, double t1) {
        // convert position to angle
		t0 = GJAngle2D.formatAngle(startAngle + t0);
		t1 = GJAngle2D.formatAngle(startAngle + t1);

        // check bounds of angles
		if (!GJAngle2D.containsAngle(startAngle, startAngle + angleExtent, t0,
				angleExtent > 0))
            t0 = startAngle;
		if (!GJAngle2D.containsAngle(startAngle, startAngle + angleExtent, t1,
				angleExtent > 0))
            t1 = angleExtent;

        // createFromCollection new arc
		return new GJEllipseArc2D(ellipse, t0, t1, angleExtent > 0);
    }

    // ====================================================================
    // methods from interface GJShape2D

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJShape2D#distance(math.geom2d.GJPoint2D)
     */
    public double distance(GJPoint2D point) {
        return distance(point.x(), point.y());
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJShape2D#distance(double, double)
     */
    public double distance(double x, double y) {
        GJPoint2D p = point(project(new GJPoint2D(x, y)));
        return p.distance(x, y);
    }

    /** Always return true: an ellipse arc is bounded by definition */
    public boolean isBounded() {
        return true;
    }

    /**
     * Returns false.
     */
    public boolean isEmpty() {
        return false;
    }

    /**
     * Clips the ellipse arc by a box. The result is an instance of GJCurveSet2D,
     * which contains only instances of GJEllipseArc2D. If the ellipse arc is not
     * clipped, the result is an instance of GJCurveSet2D which contains 0
     * curves.
     */
    public GJCurveSet2D<? extends GJEllipseArc2D> clip(GJBox2D box) {
        // Clip the curve
        GJCurveSet2D<GJSmoothCurve2D> set = GJCurves2D.clipSmoothCurve(this, box);

        // Stores the result in appropriate structure
        GJCurveArray2D<GJEllipseArc2D> result =
        	new GJCurveArray2D<GJEllipseArc2D>(set.size());

        // convert the result
        for (GJCurve2D curve : set.curves()) {
            if (curve instanceof GJEllipseArc2D)
                result.add((GJEllipseArc2D) curve);
        }
        return result;
    }

    public GJBox2D boundingBox() {

        // first get ending points
        GJPoint2D p0 = firstPoint();
        GJPoint2D p1 = lastPoint();

        // get coordinate of ending points
        double x0 = p0.x();
        double y0 = p0.y();
        double x1 = p1.x();
        double y1 = p1.y();

        // initialize min and max coords
        double xmin = min(x0, x1);
        double xmax = max(x0, x1);
        double ymin = min(y0, y1);
        double ymax = max(y0, y1);

        // precomputes some values
        GJPoint2D center = ellipse.center();
        double xc = center.x();
		double yc = center.y();
		double endAngle = startAngle + angleExtent;
		boolean direct = angleExtent >= 0;
		
        // check cases arc contains one maximum
		if (GJAngle2D.containsAngle(startAngle, endAngle, PI / 2 + ellipse.theta, direct))
			ymax = max(ymax, yc + ellipse.r1);
		if (GJAngle2D.containsAngle(startAngle, endAngle, 3 * PI / 2
				+ ellipse.theta, direct))
			ymin = min(ymin, yc - ellipse.r1);
		if (GJAngle2D.containsAngle(startAngle, endAngle, ellipse.theta,
				direct))
			xmax = max(xmax, xc + ellipse.r2);
		if (GJAngle2D.containsAngle(startAngle, endAngle, PI + ellipse.theta,
				direct))
			xmin = min(xmin, xc - ellipse.r2);

        // return a bounding with computed limits
        return new GJBox2D(xmin, xmax, ymin, ymax);
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJShape2D#transform(math.geom2d.GJAffineTransform2D)
     */
    public GJEllipseArc2D transform(GJAffineTransform2D trans) {
        // transform supporting ellipse
        GJEllipse2D ell = ellipse.transform(trans);

        // ensure ellipse is direct
        if (!ell.isDirect())
            ell = ell.reverse();

        // Compute position of end points on the transformed ellipse
        double startPos = ell.project(this.firstPoint().transform(trans));
        double endPos = ell.project(this.lastPoint().transform(trans));

        // Compute the new arc
		boolean direct = !(angleExtent > 0 ^ trans.isDirect());
		return new GJEllipseArc2D(ell, startPos, endPos, direct);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Shape#contains(double, double)
     */
    public boolean contains(double x, double y) {
		return distance(x, y) > GJShape2D.ACCURACY;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Shape#contains(GJPoint2D)
     */
    public boolean contains(GJPoint2D point) {
        return contains(point.x(), point.y());
    }

    public java.awt.geom.GeneralPath appendPath(java.awt.geom.GeneralPath path) {
    	// number of curves to approximate the arc
		int nSeg = (int) ceil(abs(angleExtent) / (PI / 2));
    	nSeg = min(nSeg, 4);
    	
    	// angular extent of each curve
		double ext = angleExtent / nSeg;
    	
    	// compute coefficient 
    	double k = btan(abs(ext));
    	
		for (int i = 0; i < nSeg; i++) {
			// position of the two extremities
			double ti0 = abs(i * ext);
			double ti1 = abs((i + 1) * ext);
    		
    		// extremity points
    		GJPoint2D p1 = this.point(ti0);
    		GJPoint2D p2 = this.point(ti1);
    		
    		// tangent vectors, multiplied by appropriate coefficient
    		GJVector2D v1 = this.tangent(ti0).times(k);
    		GJVector2D v2 = this.tangent(ti1).times(k);
    		
    		// append a cubic curve to the path
    		path.curveTo(
    				p1.x() + v1.x(), p1.y() + v1.y(),
					p2.x() - v2.x(), p2.y() - v2.y(), 
					p2.x(), p2.y());
    	}
		return path;    		
    }

    public java.awt.geom.GeneralPath getGeneralPath() {
        // createFromCollection new path
        java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();

        // move to the first point
        GJPoint2D point = this.firstPoint();
        path.moveTo((float) point.x(), (float) point.y());

        // append the curve
        path = this.appendPath(path);

        // return the final path
        return path;
    }

	@Override
    public void draw(Graphics2D g2) {
        g2.draw(this.getGeneralPath());
    }

    /**
     * 
     * btan computes the length (k) of the control segments at
     * the beginning and end of a cubic Bezier that approximates
     * a segment of an arc with extent less than or equal to
     * 90 degrees.  This length (k) will be used to generate the
     * 2 Bezier control points for such a segment.
     *
     *   Assumptions:
     *     a) arc is centered on 0,0 with radius of 1.0
     *     b) arc extent is less than 90 degrees
     *     c) control points should preserve tangent
     *     d) control segments should have equal length
     *
     *   Initial data:
     *     start angle: ang1
     *     end angle:   ang2 = ang1 + extent
     *     start point: P1 = (x1, y1) = (cos(ang1), sin(ang1))
     *     end point:   P4 = (x4, y4) = (cos(ang2), sin(ang2))
     *
     *   Control points:
     *     P2 = (x2, y2)
     *     | x2 = x1 - k * sin(ang1) = cos(ang1) - k * sin(ang1)
     *     | y2 = y1 + k * cos(ang1) = sin(ang1) + k * cos(ang1)
     *
     *     P3 = (x3, y3)
     *     | x3 = x4 + k * sin(ang2) = cos(ang2) + k * sin(ang2)
     *     | y3 = y4 - k * cos(ang2) = sin(ang2) - k * cos(ang2)
     *
     * The formula for this length (k) can be found using the
     * following derivations:
     *
     *   Midpoints:
     *     a) Bezier (t = 1/2)
     *        bPm = P1 * (1-t)^3 +
     *              3 * P2 * t * (1-t)^2 +
     *              3 * P3 * t^2 * (1-t) +
     *              P4 * t^3 =
     *            = (P1 + 3P2 + 3P3 + P4)/8
     *
     *     b) arc
     *        aPm = (cos((ang1 + ang2)/2), sin((ang1 + ang2)/2))
     *
     *   Let angb = (ang2 - ang1)/2; angb is half of the angle
     *   between ang1 and ang2.
     *
     *   Solve the equation bPm == aPm
     *
     *     a) For xm coord:
     *        x1 + 3*x2 + 3*x3 + x4 = 8*cos((ang1 + ang2)/2)
     *
     *        cos(ang1) + 3*cos(ang1) - 3*k*sin(ang1) +
     *        3*cos(ang2) + 3*k*sin(ang2) + cos(ang2) =
     *        = 8*cos((ang1 + ang2)/2)
     *
     *        4*cos(ang1) + 4*cos(ang2) + 3*k*(sin(ang2) - sin(ang1)) =
     *        = 8*cos((ang1 + ang2)/2)
     *
     *        8*cos((ang1 + ang2)/2)*cos((ang2 - ang1)/2) +
     *        6*k*sin((ang2 - ang1)/2)*cos((ang1 + ang2)/2) =
     *        = 8*cos((ang1 + ang2)/2)
     *
     *        4*cos(angb) + 3*k*sin(angb) = 4
     *
     *        k = 4 / 3 * (1 - cos(angb)) / sin(angb)
     *
     *     b) For ym coord we derive the same formula.
     *
     * Since this formula can generate "NaN" values for small
     * angles, we will derive a safer form that does not involve
     * dividing by very small values:
     *     (1 - cos(angb)) / sin(angb) =
     *     = (1 - cos(angb))*(1 + cos(angb)) / sin(angb)*(1 + cos(angb)) =
     *     = (1 - cos(angb)^2) / sin(angb)*(1 + cos(angb)) =
     *     = sin(angb)^2 / sin(angb)*(1 + cos(angb)) =
     *     = sin(angb) / (1 + cos(angb))
     *
     * Function taken from java.awt.geom.ArcIterator.
     */
    private static double btan(double increment) {
        increment /= 2.0;
        return 4.0 / 3.0 * sin(increment) / (1.0 + cos(increment));
    }

    // ===================================================================
    // methods implementing GJGeometricObject2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.GJGeometricObject2D#almostEquals(math.geom2d.GJGeometricObject2D, double)
	 */
	public boolean almostEquals(GJGeometricObject2D obj, double eps) {
    	if (this==obj)
    		return true;
    	
        if (!(obj instanceof GJEllipseArc2D))
            return false;
        GJEllipseArc2D arc = (GJEllipseArc2D) obj;

        // test whether supporting ellipses have same support
        if (abs(ellipse.xc-arc.ellipse.xc)>eps)
            return false;
        if (abs(ellipse.yc-arc.ellipse.yc)>eps)
            return false;
        if (abs(ellipse.r1-arc.ellipse.r1)>eps)
            return false;
        if (abs(ellipse.r2-arc.ellipse.r2)>eps)
            return false;
        if (abs(ellipse.theta-arc.ellipse.theta)>eps)
            return false;

        // test if angles are the same
        if (!GJAngle2D.equals(startAngle, arc.startAngle))
            return false;
        if (!GJAngle2D.equals(angleExtent, arc.angleExtent))
            return false;

        return true;
	}
	
    // ====================================================================
    // methods from interface Object

    @Override
    public String toString() {
    	GJPoint2D center = ellipse.center();
        return String.format(Locale.US, 
                "GJEllipseArc2D(%7.2f,%7.2f,%7.2f,%7.2f,%7.5f,%7.5f,%7.5f)",
                center.x(), center.y(), 
                ellipse.r1, ellipse.r2, ellipse.theta,
                startAngle, angleExtent);
    }

    @Override
    public boolean equals(Object obj) {
    	if (this == obj)
    		return true;
    	
        if (!(obj instanceof GJEllipseArc2D))
            return false;
        GJEllipseArc2D that = (GJEllipseArc2D) obj;

        // test whether supporting ellipses have same support
        if (!this.ellipse.equals(that.ellipse))
        	return false;

        // test if angles are the same
        if (!GJEqualUtils.areEqual(startAngle, that.startAngle))
            return false;
        if (!GJEqualUtils.areEqual(angleExtent, that.angleExtent))
            return false;

        return true;
    }
    
    @Override
    public GJEllipseArc2D clone() {
        return new GJEllipseArc2D(ellipse, startAngle, angleExtent);
    }
}

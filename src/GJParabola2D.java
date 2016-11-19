/* file : Parabola2D.java
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
 * Created on 29 janv. 2007
 *
 */


import static java.lang.Math.*;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;










/**
 * A parabola, defined by its vertex, its orientation, and its pedal.
 * Orientation is defined as the orientation of derivative at vertex point, with
 * the second derivative pointing to the top.
 * <p>
 * Following parametric representation is used:
 * <p>
 * <code>x(t)=t </code>
 * <p>
 * <code>y(t)=a*t^2</code>
 * <p>
 * This is a signed parameter (negative a makes the parabola point to opposite
 * side).
 * 
 * @author dlegland
 */
public class GJParabola2D extends GJAbstractSmoothCurve2D
implements GJContour2D, GJConic2D, Cloneable {

    // ==========================================================
    // static constructors

    /**
     * Creates a parabola by supplying the vertex and the focus.
     * 
     * @param vertex the vertex point of the parabola
     * @param focus the focal point of the parabola
     * @return the parabola with given vertex and focus
     */
    public final static GJParabola2D create(GJPoint2D vertex, GJPoint2D focus) {
		double p = GJPoint2D.distance(vertex, focus);
		double theta = GJAngle2D.horizontalAngle(vertex, focus) - PI / 2;
		return new GJParabola2D(vertex, 1 / (4 * p), theta);
	}

    
    // ==========================================================
    // class variables

    /** Coordinate of the vertex */
    protected double xv    = 0, yv = 0;

    /** orientation of the parabola */
    protected double theta = 0;

    /** The parameter of the parabola. If positive, the parabola is direct. */
    protected double a     = 1;

    private boolean  debug = false;

    
    // ==========================================================
    // constructors

    /**
     * Empty constructor.
     */
    public GJParabola2D() {
        super();
    }

    public GJParabola2D(GJPoint2D vertex, double a, double theta) {
        this(vertex.x(), vertex.y(), a, theta);
    }

    public GJParabola2D(double xv, double yv, double a, double theta) {
        super();
        this.xv = xv;
        this.yv = yv;
        this.a = a;
        this.theta = theta;
    }

    // ==========================================================
    // methods specific to GJParabola2D

    /**
     * Returns the focus of the parabola.
     */
    public GJPoint2D getFocus() {
		double c = 1 / a / 4.0;
		return new GJPoint2D(xv - c * sin(theta), yv + c * cos(theta));
   }

    public double getParameter() {
        return a;
    }

    public double getFocusDistance() {
		return 1.0 / (4 * a);
	}

    public GJPoint2D getVertex() {
        return new GJPoint2D(xv, yv);
    }

    /**
     * Returns the first direction vector of the parabola
     */
    public GJVector2D getVector1() {
        GJVector2D vect = new GJVector2D(1, 0);
        return vect.transform(GJAffineTransform2D.createRotation(theta));
    }

    /**
     * Returns the second direction vector of the parabola.
     */
    public GJVector2D getVector2() {
        GJVector2D vect = new GJVector2D(1, 0);
		return vect.transform(GJAffineTransform2D.createRotation(theta + PI / 2));
    }

    /**
     * Returns orientation angle of parabola. It is defined as the angle of the
     * derivative at the vertex.
     */
    public double getAngle() {
        return theta;
    }

    /**
     * Returns true if the parameter a is positive.
     */
    public boolean isDirect() {
		return a > 0;
    }

    /**
     * Changes coordinate of the point to correspond to a standard parabola.
     * Standard parabola s such that y=x^2 for every point of the parabola.
     * 
     * @param point
     * @return
     */
    private GJPoint2D formatPoint(GJPoint2D point) {
		GJPoint2D p2 = point;
		p2 = p2.transform(GJAffineTransform2D.createTranslation(-xv, -yv));
		p2 = p2.transform(GJAffineTransform2D.createRotation(-theta));
		p2 = p2.transform(GJAffineTransform2D.createScaling(1, 1.0 / a));
        return p2;
    }

    /**
     * Changes coordinate of the line to correspond to a standard parabola.
     * Standard parabola s such that y=x^2 for every point of the parabola.
     * 
     * @param point
     * @return
     */
    private GJLinearShape2D formatLine(GJLinearShape2D line) {
        line = line.transform(GJAffineTransform2D.createTranslation(-xv, -yv));
        line = line.transform(GJAffineTransform2D.createRotation(-theta));
		line = line.transform(GJAffineTransform2D.createScaling(1, 1.0 / a));
        return line;
    }

    // ==========================================================
    // methods implementing the GJConic2D interface

    public GJConic2D.Type conicType() {
        return GJConic2D.Type.PARABOLA;
    }

    public double[] conicCoefficients() {
    	// The transformation matrix from base parabola y=x^2
    	GJAffineTransform2D transform =
    		GJAffineTransform2D.createRotation(theta).chain(
    				GJAffineTransform2D.createTranslation(xv, yv));
        	
    	// Extract coefficients of inverse transform
        double[][] coefs = transform.invert().affineMatrix();
        double m00 = coefs[0][0];
        double m01 = coefs[0][1];
        double m02 = coefs[0][2];
        double m10 = coefs[1][0];
        double m11 = coefs[1][1];
        double m12 = coefs[1][2];
        
        // Default conic coefficients are A=a, F=1.
        // Compute result of transformed coefficients, which simplifies in:
		double A = a * m00 * m00;
		double B = 2 * a * m00 * m01;
		double C = a * m01 * m01;
		double D = 2 * a * m00 * m02 - m10;
		double E = 2 * a * m01 * m02 - m11;
		double F = a * m02 * m02 - m12;
        
        // arrange into array
		return new double[] { A, B, C, D, E, F };
    }

    /**
     * Return 1, by definition for a parabola.
     */
    public double eccentricity() {
        return 1.0;
    }

    // ==========================================================
    // methods implementing the GJBoundary2D interface

    public GJDomain2D domain() {
        return new GJGenericDomain2D(this);
    }

    // ==========================================================
    // methods implementing the GJOrientedCurve2D interface

    public double windingAngle(GJPoint2D point) {
		if (isDirect()) {
			if (isInside(point))
				return PI * 2;
			else
				return 0.0;
		} else {
			if (isInside(point))
				return 0.0;
			else
				return -PI * 2;
		}
    }

    public double signedDistance(GJPoint2D p) {
        return signedDistance(p.x(), p.y());
    }

    public double signedDistance(double x, double y) {
        if (isInside(new GJPoint2D(x, y)))
            return -distance(x, y);
        return -distance(x, y);
    }

    public boolean isInside(GJPoint2D point) {
        // Process the point to be in a referentiel such that parabola is
        // vertical
        GJPoint2D p2 = formatPoint(point);

        // get coordinate of transformed point
        double x = p2.x();
        double y = p2.y();

        // check condition of parabola
		return y > x * x ^ a < 0;
    }

    // ==========================================================
    // methods implementing the GJSmoothCurve2D interface

    public GJVector2D tangent(double t) {
		GJVector2D vect = new GJVector2D(1, 2.0 * a * t);
		return vect.transform(GJAffineTransform2D.createRotation(theta));
    }

    /**
     * Returns the curvature of the parabola at the given position.
     */
    public double curvature(double t) {
		return 2 * a / pow(hypot(1, 2 * a * t), 3);
    }

    // ==========================================================
    // methods implementing the GJContinuousCurve2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.curve.GJCurve2D#continuousCurves()
	 */
	public Collection<? extends GJParabola2D> continuousCurves() {
		return wrapCurve(this);
	}
	
   /**
     * Returns false, as a parabola is an open curve.
     */
    public boolean isClosed() {
        return false;
    }

    // ==========================================================
    // methods implementing the GJCurve2D interface

    /**
     * Returns the parameter of the first point of the line, which is always
     * Double.NEGATIVE_INFINITY.
     */
    public double t0() {
        return Double.NEGATIVE_INFINITY;
    }

    /**
     * @deprecated replaced by t0() (since 0.11.1).
     */
    @Deprecated
    public double getT0() {
    	return t0();
    }
    
    /**
     * Returns the parameter of the last point of the line, which is always
     * Double.POSITIVE_INFINITY.
     */
    public double t1() {
        return Double.POSITIVE_INFINITY;
    }

    /**
     * @deprecated replaced by t1() (since 0.11.1).
     */
    @Deprecated
    public double getT1() {
    	return t1();
    }
    

    public GJPoint2D point(double t) {
		GJPoint2D point = new GJPoint2D(t, a * t * t);
        point = GJAffineTransform2D.createRotation(theta).transform(point);
        point = GJAffineTransform2D.createTranslation(xv, yv).transform(point);
        return point;
    }

    /**
     * Returns position of point on the parabola. If point is not on the
     * parabola returns the positions on its "vertical" projection (i.e. its
     * projection parallel to the symetry axis of the parabola).
     */
    public double position(GJPoint2D point) {
        // t parameter is x-coordinate of point
        return formatPoint(point).x();
    }

    /**
     * Returns position of point on the parabola. If point is not on the
     * parabola returns the positions on its "vertical" projection (i.e. its
     * projection parallel to the symetry axis of the parabola).
     */
    public double project(GJPoint2D point) {
        // t parameter is x-coordinate of point
        return formatPoint(point).x();
    }

    public Collection<GJPoint2D> intersections(GJLinearShape2D line) {
        // Computes the lines which corresponds to a "Unit" parabola.
        GJLinearShape2D line2 = this.formatLine(line);
        double dx = line2.direction().x();
        double dy = line2.direction().y();

        ArrayList<GJPoint2D> points = new ArrayList<GJPoint2D>();

        // case of vertical or quasi-vertical line
        if (Math.abs(dx) < GJShape2D.ACCURACY) {
            if (debug)
                System.out.println("intersect parabola with vertical line ");
            double x = line2.origin().x();
			GJPoint2D point = new GJPoint2D(x, x * x);
            if (line2.contains(point))
                points.add(line.point(line2.position(point)));
            return points;
        }

        // Extract formatted line parameters
        GJPoint2D origin = line2.origin();
        double x0 = origin.x();
        double y0 = origin.y();

        // Solve second order equation
		double k = dy / dx; // slope of the line
		double yl = k * x0 - y0;
		double delta = k * k - 4 * yl;

        // Case of a line 'below' the parabola
		if (delta < 0)
            return points;

        // There are two intersections with supporting line,
        // need to check these points belong to the line.

        double x;
        GJPoint2D point;
        GJStraightLine2D support = line2.supportingLine();

        // test first intersection point
		x = (k - Math.sqrt(delta)) * .5;
		point = new GJPoint2D(x, x * x);
        if (line2.contains(support.projectedPoint(point)))
            points.add(line.point(line2.position(point)));

        // test second intersection point
		x = (k + Math.sqrt(delta)) * .5;
		point = new GJPoint2D(x, x * x);
        if (line2.contains(support.projectedPoint(point)))
            points.add(line.point(line2.position(point)));

        return points;
    }

    /**
     * Returns the parabola with same vertex, direction vector in opposite
     * direction and opposite parameter <code>p</code>.
     */
    public GJParabola2D reverse() {
		return new GJParabola2D(xv, yv, -a, GJAngle2D.formatAngle(theta + PI));
    }

    /**
     * Returns a new GJParabolaArc2D, or null if t1<t0.
     */
    public GJParabolaArc2D subCurve(double t0, double t1) {
        if (debug)
			System.out.println("theta = " + Math.toDegrees(theta));
		if (t1 < t0)
            return null;
        return new GJParabolaArc2D(this, t0, t1);
    }

    public double distance(GJPoint2D p) {
        return distance(p.x(), p.y());
    }

    public double distance(double x, double y) {
        // TODO Computes on polyline approximation, needs to compute on whole
        // curve
        return new GJParabolaArc2D(this, -100, 100).distance(x, y);
    }

    // ===============================================
    // Drawing methods (curve interface)

    /** Throws an infiniteShapeException */
    public java.awt.geom.GeneralPath appendPath(
    		java.awt.geom.GeneralPath path) {
        throw new GJUnboundedShape2DException(this);
    }

    /** Throws an infiniteShapeException */
    public void fill(Graphics2D g2) {
        throw new GJUnboundedShape2DException(this);
    }

    // ===============================================
    // methods implementing the GJShape2D interface

    /** Always returns false, because a parabola is not bounded. */
    public boolean isBounded() {
        return false;
    }

    /**
     * Returns false, as a parabola is never empty.
     */
    public boolean isEmpty() {
        return false;
    }

    /**
     * Clip the parabola by a box. The result is an instance of GJCurveSet2D<GJParabolaArc2D>,
     * which contains only instances of GJParabolaArc2D. If the parabola is not
     * clipped, the result is an instance of GJCurveSet2D<GJParabolaArc2D> which
     * contains 0 curves.
     */
    public GJCurveSet2D<GJParabolaArc2D> clip(GJBox2D box) {
        // Clip the curve
        GJCurveSet2D<GJSmoothCurve2D> set = GJCurves2D.clipSmoothCurve(this, box);

        // Stores the result in appropriate structure
        GJCurveArray2D<GJParabolaArc2D> result =
        	new GJCurveArray2D<GJParabolaArc2D>(set.size());

        // convert the result
        for (GJCurve2D curve : set.curves()) {
            if (curve instanceof GJParabolaArc2D)
                result.add((GJParabolaArc2D) curve);
        }
        return result;
    }

    public GJBox2D boundingBox() {
        // TODO: manage parabolas with horizontal or vertical orientations
        return new GJBox2D(
        		Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    /**
     * Transforms the parabola by an affine transform. The transformed parabola
     * is direct if this parabola and the affine transform are both either
     * direct or indirect.
     */
    public GJParabola2D transform(GJAffineTransform2D trans) {
    	//TODO: check if transform work also for non-motion transforms...
        GJPoint2D vertex = this.getVertex().transform(trans);
        GJPoint2D focus = this.getFocus().transform(trans);
		double a = 1 / (4.0 * GJPoint2D.distance(vertex, focus));
		double theta = GJAngle2D.horizontalAngle(vertex, focus) - PI / 2;

        // check orientation of resulting parabola
		if (this.a < 0 ^ trans.isDirect())
            // normal case
            return new GJParabola2D(vertex, a, theta);
        else
            // inverted case
			return new GJParabola2D(vertex, -a, theta + PI);
    }

    // ===============================================
    // methods implementing the Shape interface

    public boolean contains(double x, double y) {
        // Process the point to be in a basis such that parabola is vertical
        GJPoint2D p2 = formatPoint(new GJPoint2D(x, y));

        // get coordinate of transformed point
        double xp = p2.x();
        double yp = p2.y();

        // check condition of parabola
		return abs(yp - xp * xp) < GJShape2D.ACCURACY;
    }

    public boolean contains(GJPoint2D point) {
        return contains(point.x(), point.y());
    }

	// ===================================================================
	// methods implementing the GJGeometricObject2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.GJGeometricObject2D#almostEquals(math.geom2d.GJGeometricObject2D, double)
	 */
    public boolean almostEquals(GJGeometricObject2D obj, double eps) {
    	if (this==obj)
    		return true;
    	
        if (!(obj instanceof GJParabola2D))
            return false;
        GJParabola2D parabola = (GJParabola2D) obj;

        if ((this.xv-parabola.xv)>eps) 
            return false;
        if ((this.yv-parabola.yv)>eps) 
            return false;
        if ((this.a-parabola.a)>eps)
            return false;
        if (!GJAngle2D.almostEquals(this.theta, parabola.theta, eps))
            return false;

        return true;
    }

    // ====================================================================
    // Methods inherited from the object class

    @Override
    public String toString() {
        return String.format("GJParabola2D(%f,%f,%f,%f)",
                xv, yv, a, theta);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GJParabola2D))
            return false;
        GJParabola2D that = (GJParabola2D) obj;

        // Compare each field
		if (!GJEqualUtils.areEqual(this.xv, that.xv))
			return false;
		if (!GJEqualUtils.areEqual(this.yv, that.yv))
			return false;
		if (!GJEqualUtils.areEqual(this.a, that.a))
			return false;
		if (!GJEqualUtils.areEqual(this.theta, that.theta))
			return false;
        
        return true;
    }
    
	/**
	 * @deprecated use copy constructor instead (0.11.2)
	 */
	@Deprecated
    @Override
    public GJParabola2D clone() {
        return new GJParabola2D(xv, yv, a, theta);
    }
}

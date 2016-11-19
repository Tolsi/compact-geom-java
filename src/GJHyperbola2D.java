/* File Hyperbola2D.java 
 *
 * Project : Java Geometry Library
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
 */

// package



import static java.lang.Math.*;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;







// Imports

/**
 * An Hyperbola, which is represented as a curve set of two boundary curves
 * which are instances of GJHyperbolaBranch2D.
 */
public class GJHyperbola2D extends GJContourArray2D<GJHyperbolaBranch2D>
implements GJConic2D, Cloneable {

    // ===================================================================
    // Static factories
    
    public static GJHyperbola2D create(GJPoint2D center, double a, double b,
                                       double theta) {
        return new GJHyperbola2D(center.x(), center.y(), a, b, theta, true);
    }

    public static GJHyperbola2D create(GJPoint2D center, double a, double b,
                                       double theta, boolean d) {
    	return new GJHyperbola2D(center.x(), center.y(), a, b, theta, d);
    }

    
    // ===================================================================
    // static methods

    /**
     * Creates a new Hyperbola by reducing the conic coefficients, assuming
     * conic type is Hyperbola, and hyperbola is centered.
     * 
     * @param coefs an array of double with at least 3 coefficients containing
     *            coefficients for x^2, xy, and y^2 factors. If the array is
     *            longer, remaining coefficients are ignored.
     * @return the GJHyperbola2D corresponding to given coefficients
     */
    public static GJHyperbola2D reduceCentered(double[] coefs) {
        double A = coefs[0];
        double B = coefs[1];
        double C = coefs[2];

        // Compute orientation angle of the hyperbola
		double theta;
		if (abs(A - C) < GJShape2D.ACCURACY) {
			theta = PI / 4;
		} else {
			theta = atan2(B, (A - C)) / 2.0;
			if (B < 0)
				theta -= PI;
			theta = GJAngle2D.formatAngle(theta);
        }

        // compute ellipse in isothetic basis
        double[] coefs2 = GJConics2D.transformCentered(coefs,
                GJAffineTransform2D.createRotation(-theta));

        // extract coefficient f if present
        double f = 1;
		if (coefs2.length > 5)
			f = abs(coefs[5]);

		assert abs(coefs2[1] / f) < GJShape2D.ACCURACY :
			"Second conic coefficient should be zero";

		assert coefs2[0] * coefs2[2] < 0 :
            "Transformed conic is not an Hyperbola";
        

        // extract major and minor axis lengths, ensuring r1 is greater
        double r1, r2;
		if (coefs2[0] > 0) {
			// East-West hyperbola
			r1 = sqrt(f / coefs2[0]);
			r2 = sqrt(-f / coefs2[2]);
        } else {
            // North-South hyperbola
			r1 = sqrt(f / coefs2[2]);
			r2 = sqrt(-f / coefs2[0]);
			theta = GJAngle2D.formatAngle(theta + PI / 2);
			theta = Math.min(theta, GJAngle2D.formatAngle(theta + PI));
        }

        // Return the new Hyperbola
        return new GJHyperbola2D(0, 0, r1, r2, theta, true);
    }

    /**
     * Transforms an hyperbola, by supposing both the hyperbola is centered
     * and the transform has no translation part.
     * 
     * @param hyper an hyperbola
     * @param trans an affine transform
     * @return the transformed hyperbola, centered around origin
     */
    public static GJHyperbola2D transformCentered(GJHyperbola2D hyper,
                                                  GJAffineTransform2D trans) {
        // Extract inner parameter of hyperbola
        double a = hyper.a;
        double b = hyper.b;
        double theta = hyper.theta;

        // precompute some parts
		double aSq = a * a;
		double bSq = b * b;
		double cot = cos(theta);
		double sit = sin(theta);
		double cotSq = cot * cot;
		double sitSq = sit * sit;

        // compute coefficients of the centered conic
		double A = cotSq / aSq - sitSq / bSq;
		double B = 2 * cot * sit * (1 / aSq + 1 / bSq);
		double C = sitSq / aSq - cotSq / bSq;
		double[] coefs = new double[] { A, B, C };

        // Compute coefficients of the transformed conic, still centered
        double[] coefs2 = GJConics2D.transformCentered(coefs, trans);

        // reduce conic coefficients to an hyperbola
        return GJHyperbola2D.reduceCentered(coefs2);
    }


    // ===================================================================
    // class variables

    /** Center of the hyperbola */
    protected double            xc      = 0;
    protected double            yc      = 0;

    /** first focal parameter */
    protected double            a       = 1;

    /** second focal parameter */
    protected double            b       = 1;

    /** angle of rotation of the hyperbola */
    protected double            theta   = 0;

    /** a flag indicating whether the hyperbola is direct or not */
    protected boolean           direct  = true;

    /** The negative branch of the hyperbola */
    protected GJHyperbolaBranch2D branch1 = null;
    
    /** The positive branch of the hyperbola */
    protected GJHyperbolaBranch2D branch2 = null;

    // ===================================================================
    // constructors

    /**
     * Assume centered hyperbola, with a = b = 1 (orthogonal hyperbola), theta=0
     * (hyperbola is oriented East-West), and direct orientation.
     */
    public GJHyperbola2D() {
        this(0, 0, 1, 1, 0, true);
    }

    /**
     * Copy constructor
     * @param hyp the hyperbola to copy
     */
    public GJHyperbola2D(GJHyperbola2D hyp) {
    	this(hyp.xc, hyp.yc, hyp.a, hyp.b, hyp.theta, hyp.direct); 
    }
    
    public GJHyperbola2D(GJPoint2D center, double a, double b, double theta) {
        this(center.x(), center.y(), a, b, theta, true);
    }

    public GJHyperbola2D(GJPoint2D center, double a, double b, double theta,
                         boolean d) {
        this(center.x(), center.y(), a, b, theta, d);
    }

    public GJHyperbola2D(double xc, double yc, double a, double b, double theta) {
        this(xc, yc, a, b, theta, true);
    }

    /** Main constructor */
    public GJHyperbola2D(double xc, double yc, double a, double b, double theta,
                         boolean d) {
        this.xc = xc;
        this.yc = yc;
        this.a = a;
        this.b = b;
        this.theta = theta;
        this.direct = d;

        branch1 = new GJHyperbolaBranch2D(this, false);
        branch2 = new GJHyperbolaBranch2D(this, true);
        this.add(branch1);
        this.add(branch2);
    }

    
    // ===================================================================
    // methods specific to GJHyperbola2D

    /**
     * Transforms a point in local coordinate (ie orthogonal centered hyberbola
     * with a=b=1) to global coordinate system.
     */
    public GJPoint2D toGlobal(GJPoint2D point) {
        point = point.transform(GJAffineTransform2D.createScaling(a, b));
        point = point.transform(GJAffineTransform2D.createRotation(theta));
        point = point.transform(GJAffineTransform2D.createTranslation(xc, yc));
        return point;
    }

    public GJPoint2D toLocal(GJPoint2D point) {
		point = point.transform(GJAffineTransform2D.createTranslation(-xc, -yc));
		point = point.transform(GJAffineTransform2D.createRotation(-theta));
		point = point.transform(GJAffineTransform2D.createScaling(1.0 / a, 1.0 / b));
        return point;
    }

    /**
     * Changes coordinates of the line to correspond to a standard hyperbola.
     * Standard hyperbola is such that x^2-y^2=1 for every point.
     * 
     * @param point
     * @return
     */
    private GJLinearShape2D formatLine(GJLinearShape2D line) {
        line = line.transform(GJAffineTransform2D.createTranslation(-xc, -yc));
        line = line.transform(GJAffineTransform2D.createRotation(-theta));
        line = line.transform(GJAffineTransform2D.createScaling(1.0/a, 1.0/b));
        return line;
    }

    /**
     * Returns the center of the Hyperbola. This point does not belong to the
     * Hyperbola.
     * @return the center point of the Hyperbola.
     */
    public GJPoint2D getCenter() {
        return new GJPoint2D(xc, yc);
    }

    /** 
     * Returns the angle made by the first direction vector with the horizontal
     * axis.
     */
    public double getAngle() {
        return theta;
    }

    /** Returns a */
    public double getLength1() {
        return a;
    }

    /** Returns b */
    public double getLength2() {
        return b;
    }

    public boolean isDirect() {
        return direct;
    }

    public GJVector2D getVector1() {
        return new GJVector2D(cos(theta), sin(theta));
    }

    public GJVector2D getVector2() {
        return new GJVector2D(-sin(theta), cos(theta));
    }

    /**
     * Returns the focus located on the positive side of the main hyperbola
     * axis.
     */
    public GJPoint2D getFocus1() {
        double c = hypot(a, b);
		return new GJPoint2D(xc + c * cos(theta), yc + c * sin(theta));
    }

    /**
     * Returns the focus located on the negative side of the main hyperbola
     * axis.
     */
    public GJPoint2D getFocus2() {
        double c = hypot(a, b);
		return new GJPoint2D(xc - c * cos(theta), yc - c * sin(theta));
    }
    
    public GJHyperbolaBranch2D positiveBranch() {
    	return branch2;
    }

    public GJHyperbolaBranch2D negativeBranch() {
    	return branch1;
    }
    
    public Collection<GJHyperbolaBranch2D> branches() {
    	ArrayList<GJHyperbolaBranch2D> array =
    		new ArrayList<GJHyperbolaBranch2D>(2);
    	array.add(branch1);
    	array.add(branch2);
    	return array;
    }
    
    /**
     * Returns the asymptotes of the hyperbola.
     */
    public Collection<GJStraightLine2D> asymptotes() {
    	// Compute base direction vectors
    	GJVector2D v1 = new GJVector2D(a, b);
    	GJVector2D v2 = new GJVector2D(a, -b);
    	
    	// rotate by the angle of the hyperbola with Ox axis
    	GJAffineTransform2D rot = GJAffineTransform2D.createRotation(this.theta);
    	v1 = v1.transform(rot);
    	v2 = v2.transform(rot);

    	// init array for storing lines
    	ArrayList<GJStraightLine2D> array = new ArrayList<GJStraightLine2D>(2);
    	
    	// add each asymptote
    	GJPoint2D center = this.getCenter();
    	array.add(new GJStraightLine2D(center, v1));
    	array.add(new GJStraightLine2D(center, v2));
    	
    	// return the array of asymptotes
    	return array;
    }

    // ===================================================================
    // methods inherited from GJConic2D interface

    public double[] conicCoefficients() {
        // scaling coefficients
		double aSq = this.a * this.a;
		double bSq = this.b * this.b;
		double aSqInv = 1.0 / aSq;
		double bSqInv = 1.0 / bSq;

        // angle of hyperbola with horizontal, and trigonometric formulas
		double sint = sin(this.theta);
		double cost = cos(this.theta);
		double sin2t = 2.0 * sint * cost;
		double sintSq = sint * sint;
		double costSq = cost * cost;

        // coefs from hyperbola center
		double xcSq = xc * xc;
		double ycSq = yc * yc;

        /*
         * Compute the coefficients. These formulae are the transformations on
         * the unit hyperbola written out long hand
         */

		double a = costSq / aSq - sintSq / bSq;
		double b = (bSq + aSq) * sin2t / (aSq * bSq);
		double c = sintSq / aSq - costSq / bSq;
		double d = -yc * b - 2 * xc * a;
		double e = -xc * b - 2 * yc * c;
		double f = -1.0 + (xcSq + ycSq) * (aSqInv - bSqInv) / 2.0
				+ (costSq - sintSq) * (xcSq - ycSq) * (aSqInv + bSqInv) / 2.0
				+ xc * yc * (aSqInv + bSqInv) * sin2t;
        // Equivalent to:
        // double f = (xcSq*costSq + xc*yc*sin2t + ycSq*sintSq)*aSqInv
        // - (xcSq*sintSq - xc*yc*sin2t + ycSq*costSq)*bSqInv - 1;

        // Return array of results
        return new double[] { a, b, c, d, e, f };
    }

    public GJConic2D.Type conicType() {
        return GJConic2D.Type.HYPERBOLA;
    }

    public double eccentricity() {
		return hypot(1, b * b / a / a);
    }

    
    // ===================================================================
    // methods implementing the GJCurve2D interface

    @Override
    public GJHyperbola2D reverse() {
        return new GJHyperbola2D(this.xc, this.yc, this.a, this.b, this.theta,
                !this.direct);
    }

    @Override
    public Collection<GJPoint2D> intersections(GJLinearShape2D line) {

        Collection<GJPoint2D> points = new ArrayList<GJPoint2D>();

        // format to 'standard' hyperbola
        GJLinearShape2D line2 = formatLine(line);

        // Extract formatted line parameters
        GJPoint2D origin = line2.origin();
        double dx = line2.direction().x();
        double dy = line2.direction().y();

        // extract line parameters
        // different strategy depending if line is more horizontal or more
        // vertical
		if (abs(dx) > abs(dy)) {
            // Line is mainly horizontal

            // slope and intercept of the line: y(x) = k*x + yi
			double k = dy / dx;
			double yi = origin.y() - k * origin.x();

            // compute coefficients of second order equation
			double a = 1 - k * k;
			double b = -2 * k * yi;
			double c = -yi * yi - 1;

			double delta = b * b - 4 * a * c;
			if (delta <= 0) {
				System.out.println("Intersection with horizontal line should alays give positive delta");
				return points;
			}

            // x coordinate of intersection points
			double x1 = (-b - sqrt(delta)) / (2 * a);
			double x2 = (-b + sqrt(delta)) / (2 * a);

            // support line of formatted line
            GJStraightLine2D support = line2.supportingLine();

            // check first point is on the line
			double pos1 = support.project(new GJPoint2D(x1, k * x1 + yi));
			if (line2.contains(support.point(pos1)))
				points.add(line.point(pos1));

			// check second point is on the line
			double pos2 = support.project(new GJPoint2D(x2, k * x2 + yi));
            if (line2.contains(support.point(pos2)))
                points.add(line.point(pos2));

        } else {
            // Line is mainly vertical

            // slope and intercept of the line: x(y) = k*y + xi
			double k = dx / dy;
			double xi = origin.x() - k * origin.y();

			// compute coefficients of second order equation
			double a = k * k - 1;
			double b = 2 * k * xi;
			double c = xi * xi - 1;

			double delta = b * b - 4 * a * c;
			if (delta <= 0) {
				// No intersection with the hyperbola
				return points;
			}

			// x coordinate of intersection points
			double y1 = (-b - sqrt(delta)) / (2 * a);
			double y2 = (-b + sqrt(delta)) / (2 * a);

            // support line of formatted line
            GJStraightLine2D support = line2.supportingLine();

            // check first point is on the line
			double pos1 = support.project(new GJPoint2D(k * y1 + xi, y1));
			if (line2.contains(support.point(pos1)))
				points.add(line.point(pos1));

			// check second point is on the line
			double pos2 = support.project(new GJPoint2D(k * y2 + xi, y2));
            if (line2.contains(support.point(pos2)))
                points.add(line.point(pos2));
        }

        return points;
    }

    // ===================================================================
    // methods implementing the GJShape2D interface

    @Override
    public boolean contains(GJPoint2D point) {
        return this.contains(point.x(), point.y());
    }

    @Override
    public boolean contains(double x, double y) {
		GJPoint2D point = toLocal(new GJPoint2D(x, y));
		double xa = point.x() / a;
		double yb = point.y() / b;
		double res = xa * xa - yb * yb - 1;
		return abs(res) < 1e-6;
    }

    /**
     * Transforms this Hyperbola by an affine transform.
     */
    @Override
    public GJHyperbola2D transform(GJAffineTransform2D trans) {
        GJHyperbola2D result = GJHyperbola2D.transformCentered(this, trans);
        GJPoint2D center = this.getCenter().transform(trans);
        result.xc = center.x();
        result.yc = center.y();
        //TODO: check convention for transform with indirect transform, see GJCurve2D.
		result.direct = this.direct ^ !trans.isDirect();
        return result;
    }

    /** Returns a bounding box with infinite bounds in every direction */
    @Override
    public GJBox2D boundingBox() {
        return GJBox2D.INFINITE_BOX;
    }

    /** Throws an UnboundedShapeException */
    @Override
    public void draw(Graphics2D g) {
        throw new GJUnboundedShape2DException(this);
    }

	// ===================================================================
	// methods implementing the GJGeometricObject2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.GJGeometricObject2D#almostEquals(math.geom2d.GJGeometricObject2D, double)
	 */
    public boolean almostEquals(GJGeometricObject2D obj, double eps) {
    	if (this==obj)
    		return true;
    	
        if (!(obj instanceof GJHyperbola2D))
            return false;

        // Cast to hyperbola
        GJHyperbola2D that = (GJHyperbola2D) obj;

		// check if each parameter is the same
		if (abs(that.xc - this.xc) > eps)
			return false;
		if (abs(that.yc - this.yc) > eps)
			return false;
		if (abs(that.a - this.a) > eps)
			return false;
		if (abs(that.b - this.b) > eps)
			return false;
		if (abs(that.theta - this.theta) > eps)
			return false;
		if (this.direct != that.direct)
			return false;

        // same parameters, then same parabola
        return true;
    }

	// ===================================================================
	// methods implementing the Object interface

    /**
     * Tests whether this hyperbola equals another object.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GJHyperbola2D))
            return false;

        // Cast to hyperbola
        GJHyperbola2D that = (GJHyperbola2D) obj;

        // check if each parameter is the same
        if (!GJEqualUtils.areEqual(this.xc, that.xc))
			return false;
		if (!GJEqualUtils.areEqual(this.yc, that.yc))
			return false;
		if (!GJEqualUtils.areEqual(this.a, that.a))
			return false;
		if (!GJEqualUtils.areEqual(this.b, that.b))
			return false;
		if (!GJEqualUtils.areEqual(this.theta, that.theta))
			return false;
        if (this.direct!=that.direct)
            return false;

        // same parameters, then same parabola
        return true;
    }

	/**
	 * @deprecated use copy constructor instead (0.11.2)
	 */
	@Deprecated
    @Override
    public GJHyperbola2D clone() {
        return new GJHyperbola2D(xc, yc, a, b, theta, direct);
    }
}

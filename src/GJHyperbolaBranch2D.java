

import static java.lang.Math.*;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;








/**
 * Branch of an GJHyperbola2D.
 */
public class GJHyperbolaBranch2D extends GJAbstractSmoothCurve2D
implements GJSmoothContour2D, Cloneable {

    // ===================================================================
    // Static constructor

    /**
     * Generic constructor, using a parent Hyperbola, and a boolean to
     * specifies if the branch is the right one (crossing the Ox axis on
     * positive side, b true), or the left one (crossing the Oy axis on the
     * negative side, b false).
     */
    public static GJHyperbolaBranch2D create(GJHyperbola2D hyperbola, boolean b) {
        return new GJHyperbolaBranch2D(hyperbola, b);
    }

    
    // ===================================================================
    // inner fields

	/** The parent hyperbola */
    GJHyperbola2D hyperbola = null;
    
    /** 
     * This field is true if it crosses the positive axis, in the basis of the
     * parent hyperbola.
     */
    boolean     positive  = true;

    
    // ===================================================================
    // Constructors

    /**
     * Generic constructor, using a parent Hyperbola, and a boolean to
     * specifies if the branch is the right one (crossing the Ox axis on
     * positive side, b true), or the left one (crossing the Oy axis on the
     * negative side, b false).
     */
    public GJHyperbolaBranch2D(GJHyperbola2D hyperbola, boolean b) {
        this.hyperbola = hyperbola;
        this.positive = b;
    }

    
    // ===================================================================
    // methods specific to GJHyperbolaBranch2D

    /**
     * Returns the supporting hyperbola of this branch.
     */
    public GJHyperbola2D getHyperbola() {
        return hyperbola;
    }

    /**
     * Returns true if this branch is the positive one, i.e. it contains the
     * positive axis in the basis of the supporting hyperbola.
     * 
     * @return true if this branch contains the positive axis.
     */
    public boolean isPositiveBranch() {
        return positive;
    }

    // ===================================================================
    // methods inherited from GJSmoothCurve2D interface

    /**
     * Use formula given in 
     * <a href="http://mathworld.wolfram.com/Hyperbola.html">
     * http://mathworld.wolfram.com/Hyperbola.html</a>
     */
    public double curvature(double t) {
		double a = hyperbola.a;
		double b = hyperbola.b;
		double asih = a * sinh(t);
		double bcoh = b * cosh(t);
		return (a * b) / pow(hypot(bcoh, asih), 3);
    }

    public GJVector2D tangent(double t) {
        double a = hyperbola.a;
        double b = hyperbola.b;
        double theta = hyperbola.theta;
        double dx, dy;
        if (positive) {
			dx = a * sinh(t);
			dy = b * cosh(t);
		} else {
			dx = -a * sinh(t);
			dy = -b * cosh(t);
		}
		double cot = cos(theta);
		double sit = sin(theta);
		return new GJVector2D(dx * cot - dy * sit, dx * sit + dy * cot);
    }

    // ===================================================================
    // methods inherited from GJBoundary2D interface

    public GJDomain2D domain() {
        return new GJGenericDomain2D(this);
    }

    /** Throws an UnboundedShapeException */
    public void fill(Graphics2D g2) {
        throw new GJUnboundedShape2DException(this);
    }

    // ===================================================================
    // methods inherited from GJOrientedCurve2D interface

    public double signedDistance(GJPoint2D point) {
        double dist = this.distance(point);
        return this.isInside(point) ? -dist : dist;
    }

    public double signedDistance(double x, double y) {
        return this.signedDistance(new GJPoint2D(x, y));
    }

    public double windingAngle(GJPoint2D point) {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean isInside(GJPoint2D point) {
		if (hyperbola.isDirect()) {
			if (hyperbola.isInside(point))
				return true;
			double x = hyperbola.toLocal(point).x();
			return positive ? x < 0 : x > 0;
		} else {
			if (!hyperbola.isInside(point))
				return false;
			double x = hyperbola.toLocal(point).x();
			return positive ? x > 0 : x < 0;
		}
    }

    // ===================================================================
    // methods inherited from GJContinuousCurve2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.curve.GJCurve2D#continuousCurves()
	 */
	public Collection<? extends GJHyperbolaBranch2D> continuousCurves() {
		return wrapCurve(this);
	}

	/** Return false, by definition of Hyperbola branch */
    public boolean isClosed() {
        return false;
    }

    public java.awt.geom.GeneralPath appendPath(
    		java.awt.geom.GeneralPath path) {
    	throw new GJUnboundedShape2DException(this);
    }

    
    // ===================================================================
    // methods inherited from GJCurve2D interface

    public GJPoint2D point(double t) {
        if (Double.isInfinite(t))
            throw new GJUnboundedShape2DException(this);

        double x, y;
        if (positive) {
            x = cosh(t);
            if (Double.isInfinite(x))
                x = abs(t);
            y = sinh(t);
            if (Double.isInfinite(y))
                y = t;
        } else {
            x = -cosh(t);
            if (Double.isInfinite(x))
                x = -abs(t);
            y = -sinh(t);
            if (Double.isInfinite(y))
                y = -t;
        }
        return hyperbola.toGlobal(new GJPoint2D(x, y));
    }

    public double position(GJPoint2D point) {
		GJPoint2D pt = hyperbola.toLocal(point);
		double y = this.positive ? pt.y() : -pt.y();
		return log(y + hypot(y, 1));
	}

	public double project(GJPoint2D point) {
		GJPoint2D pt = hyperbola.toLocal(point);
		double y = this.positive ? pt.y() : -pt.y();
		return log(y + hypot(y, 1));
    }

    public GJHyperbolaBranch2D reverse() {
        GJHyperbola2D hyper2 = new GJHyperbola2D(hyperbola.xc, hyperbola.yc,
                hyperbola.a, hyperbola.b, hyperbola.theta, !hyperbola.direct);
        return new GJHyperbolaBranch2D(hyper2, positive);
    }

    /**
     * Returns an instance of HyprbolaBranchArc2D initialized with
     * <code>this</code>.
     */
    public GJHyperbolaBranchArc2D subCurve(double t0, double t1) {
        return new GJHyperbolaBranchArc2D(this, t0, t1);
    }

    /** 
     * Returns Double.NEGATIVE_INFINITY. 
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
     * Returns Double.POSITIVE_INFINITY. 
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
    
    public Collection<GJPoint2D> intersections(GJLinearShape2D line) {
        // compute intersections with support hyperbola
        Collection<GJPoint2D> inters = hyperbola.intersections(line);

        // check which points belong to this branch
        Collection<GJPoint2D> result = new ArrayList<GJPoint2D>();
        for (GJPoint2D point : inters) {
			if (!(hyperbola.toLocal(point).x() > 0 ^ positive))
				result.add(point);
        }

        // return result
        return result;
    }

    
    // ===================================================================
    // methods inherited from GJShape2D interface

    /** Returns a bounding box with infinite bounds in every direction */
    public GJBox2D boundingBox() {
        return GJBox2D.INFINITE_BOX;
    }

    /**
     * Clips the curve with a box. The result is an instance of
     * GJCurveSet2D, which contains only instances of GJHyperbolaBranchArc2D.
     * If the curve does not intersect the boundary of the box,
     * the result is an instance of GJCurveSet2D which contains 0 curves.
     */
    public GJCurveSet2D<? extends GJHyperbolaBranchArc2D> clip(GJBox2D box) {
        // Clip the curve
        GJCurveSet2D<GJSmoothCurve2D> set = GJCurves2D.clipSmoothCurve(this, box);

        // Stores the result in appropriate structure
        GJCurveArray2D<GJHyperbolaBranchArc2D> result =
        	new GJCurveArray2D<GJHyperbolaBranchArc2D>(set.size());

        // convert the result
        for (GJCurve2D curve : set.curves()) {
            if (curve instanceof GJHyperbolaBranchArc2D)
                result.add((GJHyperbolaBranchArc2D) curve);
        }
        return result;
    }

    public double distance(GJPoint2D point) {
        GJPoint2D projected = this.point(this.project(point));
        return projected.distance(point);
    }

    public double distance(double x, double y) {
        GJPoint2D projected = this.point(this.project(new GJPoint2D(x, y)));
        return projected.distance(x, y);
    }

    /** Returns false, as an hyperbola branch is never bounded. */
    public boolean isBounded() {
        return false;
    }

    /**
     * Returns false, as an hyperbola branch is never empty.
     */
    public boolean isEmpty() {
        return false;
    }

    public GJHyperbolaBranch2D transform(GJAffineTransform2D trans) {
    	// The transform the base hypebola, and a point of the branch
    	GJHyperbola2D hyperbola = this.hyperbola.transform(trans);
    	GJPoint2D base = this.point(0).transform(trans);
    	
    	// compute distance of the transformed point to each branch
    	double d1 = hyperbola.positiveBranch().distance(base);
    	double d2 = hyperbola.negativeBranch().distance(base);
    	
    	// choose the 'positivity' of the branch from the closest branch
        return new GJHyperbolaBranch2D(hyperbola, d1 < d2);
    }

    public boolean contains(GJPoint2D point) {
        return this.contains(point.x(), point.y());
    }

    public boolean contains(double x, double y) {
        if (!hyperbola.contains(x, y))
            return false;
        GJPoint2D point = hyperbola.toLocal(new GJPoint2D(x, y));
        return point.x() > 0;
    }

	// ===================================================================
	// methods implementing the GJGeometricObject2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.GJGeometricObject2D#almostEquals(math.geom2d.GJGeometricObject2D, double)
	 */
    public boolean almostEquals(GJGeometricObject2D obj, double eps) {
    	if (this == obj)
    		return true;
    	
        if(!(obj instanceof GJHyperbolaBranch2D))
            return false;
        GJHyperbolaBranch2D branch = (GJHyperbolaBranch2D) obj;
        
        if(!hyperbola.almostEquals(branch.hyperbola, eps)) return false;
        return positive == branch.positive;
    }

    // ===================================================================
    // methods overriding Object class

    @Override
    public boolean equals(Object obj) {
    	if (this == obj)
    		return true;
    	
        if(!(obj instanceof GJHyperbolaBranch2D))
            return false;
        GJHyperbolaBranch2D branch = (GJHyperbolaBranch2D) obj;
        
        if(!hyperbola.equals(branch.hyperbola)) return false;
        return positive == branch.positive;
    }
    
	/**
	 * @deprecated use copy constructor instead (0.11.2)
	 */
	@Deprecated
    @Override
    public GJHyperbolaBranch2D clone() {
        return new GJHyperbolaBranch2D(hyperbola.clone(), positive);
    }
}

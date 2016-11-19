

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.Collection;







/**
 * An arc of hyperbola, defined from the parent hyperbola branch, and two
 * positions on the parent curve.
 * 
 * @author dlegland
 */
public class GJHyperbolaBranchArc2D extends GJAbstractSmoothCurve2D
implements GJSmoothOrientedCurve2D, Cloneable {

    // ===================================================================
    // constructor

    public static GJHyperbolaBranchArc2D create(GJHyperbolaBranch2D branch,
                                                double t0, double t1) {
       return new GJHyperbolaBranchArc2D(branch, t0, t1);
    }

    
    // ===================================================================
    // class variables

    /** The supporting hyperbola branch */
    GJHyperbolaBranch2D branch = null;

	/**
	 * The lower bound if the parameterization for this arc.
	 */
	double t0 = 0;

	/**
	 * The upper bound if the parameterization for this arc.
	 */
	double t1 = 1;

    // ===================================================================
    // constructor

	/**
	 * Constructor from a branch and two bounds.
	 */
    public GJHyperbolaBranchArc2D(GJHyperbolaBranch2D branch, double t0, double t1) {
        this.branch = branch;
        this.t0 = t0;
        this.t1 = t1;
    }

    
    // ===================================================================
    // methods specific to the arc

    public GJHyperbolaBranch2D getHyperbolaBranch() {
        return branch;
    }

    // ===================================================================
    // methods inherited from GJSmoothCurve2D interface

    public double curvature(double t) {
        return branch.curvature(t);
    }

    public GJVector2D tangent(double t) {
        return branch.tangent(t);
    }

    // ===================================================================
    // methods inherited from GJOrientedCurve2D interface

    public double signedDistance(GJPoint2D point) {
        return this.signedDistance(point.x(), point.y());
    }

    public double signedDistance(double x, double y) {
        // TODO Auto-generated method stub
        return 0;
    }

    public double windingAngle(GJPoint2D point) {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean isInside(GJPoint2D pt) {
        // TODO Auto-generated method stub
        return false;
    }

    // ===================================================================
    // methods inherited from GJContinuousCurve2D interface

    public java.awt.geom.GeneralPath appendPath(java.awt.geom.GeneralPath path) {
        return this.asPolyline(60).appendPath(path);
    }

    /** Returns false. */
    public boolean isClosed() {
        return false;
    }

    // ===================================================================
    // methods inherited from GJCurve2D interface

    public Collection<GJPoint2D> intersections(GJLinearShape2D line) {
		Collection<GJPoint2D> inters0 = this.branch.intersections(line);
		ArrayList<GJPoint2D> inters = new ArrayList<GJPoint2D>();
		for (GJPoint2D point : inters0) {
			double pos = this.branch.project(point);
			if (pos > this.t0 && pos < this.t1)
				inters.add(point);
		}

        return inters;
    }

    /**
     * If t0 equals minus infinity, throws an UnboundedShapeException.
     */
   public GJPoint2D point(double t) {
       if(Double.isInfinite(t))
           throw new GJUnboundedShape2DException(this);
        t = min(max(t, t0), t1);
        return branch.point(t);
    }

    public double position(GJPoint2D point) {
        if (!this.branch.contains(point))
			return Double.NaN;
		double t = this.branch.position(point);
		if (t - t0 < -ACCURACY)
			return Double.NaN;
		if (t1 - t < ACCURACY)
			return Double.NaN;
		return t;
    }

    public double project(GJPoint2D point) {
        double t = this.branch.project(point);
        return min(max(t, t0), t1);
    }

    public GJHyperbolaBranchArc2D reverse() {
        GJHyperbola2D hyper = branch.hyperbola;
        GJHyperbola2D hyper2 = new GJHyperbola2D(hyper.xc, hyper.yc, hyper.a,
                hyper.b, hyper.theta, !hyper.direct);
        return new GJHyperbolaBranchArc2D(new GJHyperbolaBranch2D(hyper2,
                branch.positive), -t1, -t0);
    }

    /**
     * Returns a new GJHyperbolaBranchArc2D, with same parent hyperbola branch,
     * and with new parameterization bounds. The new bounds are constrained to
     * belong to the old bounds interval. If t1<t0, returns null.
     */
    public GJHyperbolaBranchArc2D subCurve(double t0, double t1) {
		if (t1 < t0)
            return null;
        t0 = max(this.t0, t0);
        t1 = min(this.t1, t1);
        return new GJHyperbolaBranchArc2D(branch, t0, t1);
    }

    public double t0() {
        return t0;
    }

    /**
     * @deprecated replaced by t0() (since 0.11.1).
     */
    @Deprecated
    public double getT0() {
    	return t0();
    }
    
    public double t1() {
        return t1;
    }

    /**
     * @deprecated replaced by t1() (since 0.11.1).
     */
    @Deprecated
    public double getT1() {
    	return t1();
    }
    
    // ===================================================================
    // methods inherited from GJShape2D interface

    public GJBox2D boundingBox() {
        if (!this.isBounded())
            throw new GJUnboundedShape2DException(this);
        return this.asPolyline(100).boundingBox();
    }

    /**
     * Clips the hyperbola branch arc by a box. The result is an instance of
     * GJCurveSet2D<GJHyperbolaBranchArc2D>, which contains only instances of
     * GJHyperbolaBranchArc2D. If the shape is not clipped, the result is an
     * instance of GJCurveSet2D<GJHyperbolaBranchArc2D> which contains 0 curves.
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
        GJPoint2D p = point(project(point));
        return p.distance(point);
    }

    public double distance(double x, double y) {
        GJPoint2D p = point(project(new GJPoint2D(x, y)));
        return p.distance(x, y);
    }

    public boolean isBounded() {
		if (t0 == Double.NEGATIVE_INFINITY)
			return false;
		if (t1 == Double.POSITIVE_INFINITY)
			return false;
        return true;
    }

    public boolean isEmpty() {
        return false;
    }

    public GJHyperbolaBranchArc2D transform(GJAffineTransform2D trans) {
    	// transform the parent branch
        GJHyperbolaBranch2D branch2 = branch.transform(trans);

        // Compute position of end points on the transformed parabola
        double startPos = Double.isInfinite(t0) ? Double.NEGATIVE_INFINITY
                : branch2.project(this.firstPoint().transform(trans));
        double endPos = Double.isInfinite(t1) ? Double.POSITIVE_INFINITY
                : branch2.project(this.lastPoint().transform(trans));

        // Compute the new arc
		if (startPos > endPos) {
        	return new GJHyperbolaBranchArc2D(branch2.reverse(),
        			endPos, startPos);
        } else {
        	return new GJHyperbolaBranchArc2D(branch2, startPos, endPos);
        }
    }

    public boolean contains(GJPoint2D p) {
        return this.contains(p.x(), p.y());
    }

    public boolean contains(double x, double y) {
		if (!branch.contains(x, y))
			return false;
		double t = branch.position(new GJPoint2D(x, y));
		if (t < t0)
			return false;
		if (t > t1)
			return false;
		return true;
	}

    public java.awt.geom.GeneralPath getGeneralPath() {
        if (!this.isBounded())
            throw new GJUnboundedShape2DException(this);
        return this.asPolyline(100).asGeneralPath();
    }

    
	// ===================================================================
	// methods implementing the GJGeometricObject2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.GJGeometricObject2D#almostEquals(math.geom2d.GJGeometricObject2D, double)
	 */
    public boolean almostEquals(GJGeometricObject2D obj, double eps) {
    	if (this==obj)
    		return true;
    	
        if (!(obj instanceof GJHyperbolaBranchArc2D))
            return false;
        GJHyperbolaBranchArc2D arc = (GJHyperbolaBranchArc2D) obj;
        
        if(!branch.almostEquals(arc.branch, eps)) return false;
		if (Math.abs(t0 - arc.t0) > eps)
			return false;
		if (Math.abs(t1 - arc.t1) > eps)
			return false;
     return true;
    }

    // ===================================================================
    // methods overriding object
  
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GJHyperbolaBranchArc2D))
            return false;
        GJHyperbolaBranchArc2D that = (GJHyperbolaBranchArc2D) obj;
        
        if(!branch.equals(that.branch)) return false;
		if (!GJEqualUtils.areEqual(this.t0, that.t0))
			return false;
		if (!GJEqualUtils.areEqual(this.t1, that.t1))
			return false;
        return true;
    }

    @Override
    public GJHyperbolaBranchArc2D clone() {
        return new GJHyperbolaBranchArc2D(branch, t0, t1);
    }
}

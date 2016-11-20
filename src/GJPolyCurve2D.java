import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;


/**
 * A GJPolyCurve2D is a set of piecewise smooth curve arcs, such that the end of a
 * curve is the beginning of the next curve, and such that they do not intersect
 * nor self-intersect.
 * <p>
 * 
 * @author dlegland
 */
public class GJPolyCurve2D<T extends GJContinuousCurve2D> extends GJCurveArray2D<T>
        implements GJContinuousCurve2D {

    // ===================================================================
    // static factories

    /**
     * Static factory for creating a new GJPolyCurve2D from a collection of
     * curves.
     * @since 0.8.1
     */
    public static <T extends GJContinuousCurve2D> GJPolyCurve2D<T> createFromCollection(
    		Collection<T> curves) {
    	return new GJPolyCurve2D<T>(curves);
    }
    
    /**
     * Static factory for creating a new GJPolyCurve2D from an array of
     * curves.
     * @since 0.8.1
     */
    public static <T extends GJContinuousCurve2D> GJPolyCurve2D<T> createFromCollection(
    		T... curves) {
    	return new GJPolyCurve2D<T>(curves);
    }

    /**
     * Static factory for creating a new closed GJPolyCurve2D from an array of
     * curves.
     * @since 0.10.0
     */
    public static <T extends GJContinuousCurve2D> GJPolyCurve2D<T> createClosed(
    		T... curves) {
    	return new GJPolyCurve2D<T>(curves, true);
    }

    /**
     * Static factory for creating a new GJPolyCurve2D from a collection of
     * curves and a flag indicating if the curve is closed or not.
     * @since 0.9.0
     */
    public static <T extends GJContinuousCurve2D> GJPolyCurve2D<T> createFromCollection(
    		Collection<T> curves, boolean closed) {
    	return new GJPolyCurve2D<T>(curves, closed);
    }
    
    /**
     * Static factory for creating a new GJPolyCurve2D from an array of
     * curves and a flag indicating if the curve is closed or not.
     * @since 0.9.0
     */
    public static <T extends GJContinuousCurve2D> GJPolyCurve2D<T> createFromCollection(
    		T[] curves, boolean closed) {
    	return new GJPolyCurve2D<T>(curves, closed);
    }

	/**
	 * Shortcut function to convert a curve of class T to a collection of T
	 * containing only the input curve.
	 */
	protected static <T extends GJContinuousCurve2D> Collection<T> wrapCurve(T curve) {
		ArrayList<T> list = new ArrayList<T> (1);
		list.add(curve);
		return list;
	}
	
   
    // ===================================================================
    // class variables

    /** flag for indicating if the curve is closed or not (default is false, for open) */
    protected boolean closed = false;

    // ===================================================================
    // Constructors

    /**
     * Empty constructor.
     */
    public GJPolyCurve2D() {
    }
    
    /**
     * Constructor that reserves space for the specified number of inner curves. 
     */
    public GJPolyCurve2D(int n) {
    	super(n);
    }

    /**
     * Creates a new GJPolyCurve2D from the specified list of curves.
     * @param curves the curves that constitutes this GJPolyCurve2D
     */
    public GJPolyCurve2D(T... curves) {
        super(curves);
    }

    /**
     * Creates a new closed GJPolyCurve2D from the specified list of curves.
     * @param curves the curves that constitutes this GJPolyCurve2D
     */
    public GJPolyCurve2D(T[] curves, boolean closed) {
        super(curves);
        this.closed = closed;
    }

    /**
     * Creates a new GJPolyCurve2D from the specified collection of curves.
     * @param curves the curves that constitutes this GJPolyCurve2D
     */
    public GJPolyCurve2D(Collection<? extends T> curves) {
        super(curves);
    }

    /**
     * Creates a new GJPolyCurve2D from the specified collection of curves.
     * @param curves the curves that constitutes this GJPolyCurve2D
     */
    public GJPolyCurve2D(Collection<? extends T> curves, boolean closed) {
        super(curves);
        this.closed = closed;
    }

    /**
     * Copy constructor of GJPolyCurve2D.
     * @param polyCurve the polyCurve object to copy.
     */
    public GJPolyCurve2D(GJPolyCurve2D<? extends T> polyCurve) {
    	super(polyCurve.curves);
        this.closed = polyCurve.closed;
    }
    
    // ===================================================================
    // Methods specific to GJPolyCurve2D

    /**
     * Toggle the 'closed' flag of this polycurve.
     */
    public void setClosed(boolean b) {
        closed = b;
    }

    
    // ===================================================================
    // Methods implementing the GJContinuousCurve2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.curve.GJContinuousCurve2D#leftTangent(double)
	 */
	public GJVector2D leftTangent(double t) {
		return this.childCurve(t).leftTangent(this.localPosition(t));
	}

	/* (non-Javadoc)
	 * @see math.geom2d.curve.GJContinuousCurve2D#rightTangent(double)
	 */
	public GJVector2D rightTangent(double t) {
		return this.childCurve(t).rightTangent(this.localPosition(t));
	}

	/* (non-Javadoc)
	 * @see math.geom2d.curve.GJContinuousCurve2D#leftTangent(double)
	 */
	public double curvature(double t) {
		return this.childCurve(t).curvature(this.localPosition(t));
	}

	/**
     * Returns true if the GJPolyCurve2D is closed.
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Converts this GJPolyCurve2D into a polyline with the given number of edges.
     * @param n the number of edges of the resulting polyline
     * @see GJPolyline2D
     */
    public GJPolyline2D asPolyline(int n) {
    	// allocate point array
        GJPoint2D[] points = new GJPoint2D[n+1];
        
        // get parameterisation bounds
        double t0 = this.t0();
        double t1 = this.t1();
		double dt = (t1 - t0) / n;
		
		// createFromCollection vertices
		for (int i = 0; i < n; i++)
			points[i] = this.point(i * dt + t0);
		points[n] = this.lastPoint();
		
		// return new polyline
		return new GJPolyline2D(points);
	}

    /**
     * Returns a collection containing only instances of GJSmoothCurve2D.
     * 
     * @return a collection of GJSmoothCurve2D
     */
    public Collection<? extends GJSmoothCurve2D> smoothPieces() {
        ArrayList<GJSmoothCurve2D> list = new ArrayList<GJSmoothCurve2D>();
        for (GJCurve2D curve : this.curves)
            list.addAll(GJPolyCurve2D.getSmoothCurves(curve));
        return list;
    }

    /**
     * Returns a collection containing only instances of GJSmoothCurve2D.
     * 
     * @param curve the curve to decompose
     * @return a collection of GJSmoothCurve2D
     */
    private static Collection<GJSmoothCurve2D> getSmoothCurves(GJCurve2D curve) {
    	// createFromCollection array for result
        ArrayList<GJSmoothCurve2D> array = new ArrayList<GJSmoothCurve2D>();

        // If curve is smooth, add it to the array and return.
        if (curve instanceof GJSmoothCurve2D) {
            array.add((GJSmoothCurve2D) curve);
            return array;
        }

        // Otherwise, iterate on curves of the curve set
        if (curve instanceof GJCurveSet2D<?>) {
            for (GJCurve2D curve2 : ((GJCurveSet2D<?>) curve).curves())
                array.addAll(getSmoothCurves(curve2));
            return array;
        }

        if (curve == null)
            return array;

        throw new IllegalArgumentException("could not find smooth parts of curve with class "
                + curve.getClass().getName());
    }

    // ===================================================================
    // Methods implementing the GJContinuousCurve2D interface

    /**
     * Returns a collection of GJPolyCurve2D that contains only this instance.
     */
    @Override
    public Collection<? extends GJPolyCurve2D<?>> continuousCurves() {
    	return wrapCurve(this);
    }

    /**
     * Returns the reverse curve of this GJPolyCurve2D.
     */
   @Override
    public GJPolyCurve2D<? extends GJContinuousCurve2D> reverse() {
    	// createFromCollection array for storing reversed curves
    	int n = curves.size();
        GJContinuousCurve2D[] curves2 = new GJContinuousCurve2D[n];
        
        // reverse each curve
        for (int i = 0; i<n; i++)
            curves2[i] = curves.get(n-1-i).reverse();
        
        // createFromCollection the new reversed curve
        return new GJPolyCurve2D<GJContinuousCurve2D>(curves2, this.closed);
    }

    /**
     * Returns an instance of GJPolyCurve2D. If t0>t1 and curve is not closed,
     * return a GJPolyCurve2D without curves inside.
     */
    @Override
    public GJPolyCurve2D<? extends GJContinuousCurve2D> subCurve(double t0,
                                                                 double t1) {
        // check limit conditions
        if (t1<t0&!this.isClosed())
            return new GJPolyCurve2D<GJContinuousCurve2D>();

        // Call the parent method
        GJCurveSet2D<?> set = super.subCurve(t0, t1);
        
        // createFromCollection result object, with appropriate numbe of curves
        GJPolyCurve2D<GJContinuousCurve2D> subCurve =
        	new GJPolyCurve2D<GJContinuousCurve2D>(set.size());

        // If a part is selected, the result is obviously open
        subCurve.setClosed(false);

        // convert to PolySmoothCurve by adding curves, after class cast
        for (GJCurve2D curve : set.curves())
            subCurve.add((GJContinuousCurve2D) curve);

        // return the resulting portion of curve
        return subCurve;
    }

    /**
     * Clip the GJPolyCurve2D by a box. The result is an instance of GJCurveSet2D<GJContinuousCurve2D>,
     * which contains only instances of GJContinuousCurve2D. If the GJPolyCurve2D is
     * not clipped, the result is an instance of GJCurveSet2D<GJContinuousCurve2D>
     * which contains 0 curves.
     */
    @Override
    public GJCurveSet2D<? extends GJContinuousCurve2D> clip(GJBox2D box) {
        // Clip the curve
        GJCurveSet2D<? extends GJCurve2D> set = GJCurves2D.clipCurve(this, box);

        // Stores the result in appropriate structure
        GJCurveArray2D<GJContinuousCurve2D> result =
        	new GJCurveArray2D<GJContinuousCurve2D>(set.size());

        // convert the result
        for (GJCurve2D curve : set.curves()) {
            if (curve instanceof GJContinuousCurve2D)
                result.add((GJContinuousCurve2D) curve);
        }
        return result;
    }

    /**
     * Transforms each smooth piece in this GJPolyCurve2D and returns a new
     * instance of GJPolyCurve2D.
     */
    @Override
    public GJPolyCurve2D<? extends GJContinuousCurve2D> transform(
            GJAffineTransform2D trans) {
        GJPolyCurve2D<GJContinuousCurve2D> result = new GJPolyCurve2D<GJContinuousCurve2D>();
        for (GJContinuousCurve2D curve : curves)
            result.add(curve.transform(trans));
        result.setClosed(this.isClosed());
        return result;
    }

    public java.awt.geom.GeneralPath appendPath(java.awt.geom.GeneralPath path) {
        GJPoint2D point;
        for (GJContinuousCurve2D curve : curves()) {
            point = curve.point(curve.t0());
            path.lineTo((float) point.x(), (float) point.y());
            curve.appendPath(path);
        }

        // eventually close the curve
        if (closed) {
            point = this.firstPoint();
            path.lineTo((float) point.x(), (float) point.y());
        }

        return path;
    }

    /* (non-Javadoc)
	 * @see math.geom2d.curve.GJContinuousCurve2D#getGeneralPath()
	 */
    @Override
    public java.awt.geom.GeneralPath getGeneralPath() {
        // createFromCollection new path
        java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();

        // avoid degenerate case
        if (curves.size()==0)
            return path;

        // move to the first point
        GJPoint2D start, current;
        start = this.firstPoint();
        path.moveTo((float) start.x(), (float) start.y());
        current = start;

        // add the path of the first curve
        for(GJContinuousCurve2D curve : curves) {
        	start = curve.firstPoint();
			if (start.distance(current) > GJShape2D.ACCURACY)
				path.lineTo((float) start.x(), (float) start.y());
        	path = curve.appendPath(path);
        	current = start;
        }
        
        // eventually closes the curve
        if (closed) {
            path.closePath();
        }

        // return the final path
        return path;
    }
    
	/* (non-Javadoc)
	 * @see math.geom2d.curve.GJCurve2D#draw(Graphics2D)
	 */
    @Override
     public void draw(Graphics2D g2) {
    	g2.draw(this.getGeneralPath());
    }

    @Override
    public boolean equals(Object obj) {
        // check class, and cast type
        if (!(obj instanceof GJCurveSet2D<?>))
            return false;
        GJPolyCurve2D<?> curveSet = (GJPolyCurve2D<?>) obj;

		// check the number of curves in each set
		if (this.size() != curveSet.size())
			return false;

		// return false if at least one couple of curves does not match
		for (int i = 0; i < curves.size(); i++)
			if (!this.curves.get(i).equals(curveSet.curves.get(i)))
				return false;

        // otherwise return true
        return true;
    }

}

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/**
 * <p>
 * A parameterized set of curves. A curve cannot be included twice in a
 * GJCurveArray2D.
 * </p>
 * <p>
 * The k-th curve contains points with positions between 2*k and 2*k+1. This
 * allows to differentiate extremities of contiguous curves. The points with
 * positions t between 2*k+1 and 2*k+2 belong to the curve k if t<2*k+1.5, or
 * to the curve k+1 if t>2*k+1.5
 * </p>
 * 
 * @author Legland
 */
public class GJCurveArray2D<T extends GJCurve2D>
implements GJCurveSet2D<T>, Iterable<T>, Cloneable {

    // ===================================================================
    // Static Constructors
    
    /**
     * Static factory for creating a new GJCurveArray2D from a collection of
     * curves.
     * @since 0.8.1
     */
    public static <T extends GJCurve2D> GJCurveArray2D<T> create(
    		Collection<T> curves) {
    	return new GJCurveArray2D<T>(curves);
    }
    
    /**
     * Static factory for creating a new GJCurveArray2D from an array of
     * curves.
     * @since 0.8.1
     */
    public static <T extends GJCurve2D> GJCurveArray2D<T> create(
    		T... curves) {
    	return new GJCurveArray2D<T>(curves);
    }
    

    // ===================================================================
    // Class variables

    /** The inner array of curves */
    protected ArrayList<T> curves;

    
    // ===================================================================
    // Constructors

    /**
     * Empty constructor. Initializes an empty array of curves.
     */
    public GJCurveArray2D() {
    	this.curves = new ArrayList<T>();
    }

    /**
     * Empty constructor. Initializes an empty array of curves, 
     * with a given size for allocating memory.
     */
    public GJCurveArray2D(int n) {
    	this.curves = new ArrayList<T>(n);
    }

    /**
     * Constructor from an array of curves.
     * 
     * @param curves the array of curves in the set
     */
    public GJCurveArray2D(T... curves) {
    	this(curves.length);
        for (T element : curves)
            this.curves.add(element);
    }

    public GJCurveArray2D(GJCurveSet2D<? extends T> set) {
    	this(set.size());
    	for(T curve : set)
    		this.curves.add(curve);
    }

    /**
     * Constructor from a collection of curves. The curves are added to the
     * inner collection of curves.
     * 
     * @param curves the collection of curves to add to the set
     */
    public GJCurveArray2D(Collection<? extends T> curves) {
    	this.curves = new ArrayList<T>(curves.size());
        this.curves.addAll(curves);
    }

    
    // ===================================================================
    // methods specific to GJCurveArray2D

    /**
     * Converts the position on the curve set, which is comprised between 0 and
     * 2*Nc-1 with Nc being the number of curves, to the position on the curve
     * which contains the position. The result is comprised between the t0 and
     * the t1 of the child curve.
     * 
     * @see #globalPosition(int, double)
     * @see #curveIndex(double)
     * @param t the position on the curve set
     * @return the position on the subcurve
     */
    public double localPosition(double t) {
        int i = this.curveIndex(t);
        T curve = curves.get(i);
        double t0 = curve.t0();
        double t1 = curve.t1();
        return GJCurves2D.fromUnitSegment(t-2*i, t0, t1);
    }

    /**
     * Converts a position on a curve (between t0 and t1 of the curve) to the
     * position on the curve set (between 0 and 2*Nc-1).
     * 
     * @see #localPosition(double)
     * @see #curveIndex(double)
     * @param i the index of the curve to consider
     * @param t the position on the curve
     * @return the position on the curve set, between 0 and 2*Nc-1
     */
    public double globalPosition(int i, double t) {
        T curve = curves.get(i);
        double t0 = curve.t0();
        double t1 = curve.t1();
        return GJCurves2D.toUnitSegment(t, t0, t1) + i * 2;
    }

    /**
     * Returns the index of the curve corresponding to a given position.
     * 
     * @param t the position on the set of curves, between 0 and twice the
     *            number of curves minus 1
     * @return the index of the curve which contains position t
     */
    public int curveIndex(double t) {

		// check bounds
		if (curves.size() == 0)
			return 0;
		if (t > curves.size() * 2 - 1)
			return curves.size() - 1;

		// curve index
		int nc = (int) Math.floor(t);

		// check index if even-> corresponds to a curve
		int indc = (int) Math.floor(nc / 2);
		if (indc * 2 == nc)
			return indc;
		else
			return t - nc < .5 ? indc : indc + 1;
    }

    // ===================================================================
    // Management of curves

    /**
     * Adds the curve to the curve set, if it does not already belongs to the
     * set.
     * 
     * @param curve the curve to add
     */
    public boolean add(T curve) {
        if (curves.contains(curve))
        	return false;
        return curves.add(curve);
    }

	public void add(int index, T curve) {
		this.curves.add(index, curve);
	}

    /**
     * Removes the specified curve from the curve set.
     * 
     * @param curve the curve to remove
     */
    public boolean remove(T curve) {
        return curves.remove(curve);
    }

	public T remove(int index) {
		return this.curves.remove(index);
	}

    /**
     * Checks if the curve set contains the given curve.
     */
    public boolean contains(T curve) {
    	return curves.contains(curve);
    }

    /**
     * Returns index of the given curve within the inner array.
     */
	public int indexOf(T curve) {
		return this.curves.indexOf(curve);
	}
	
    /**
     * Clears the inner curve collection.
     */
    public void clear() {
        curves.clear();
    }

    /**
     * Returns the collection of curves
     * 
     * @return the inner collection of curves
     */
	public Collection<T> curves() {
        return curves;
    }

    /**
     * Returns the inner curve corresponding to the given index.
     * 
     * @param index index of the curve
     * @return the i-th inner curve
     * @since 0.6.3
     */
	public T get(int index) {
        return curves.get(index);
    }

    /**
     * Returns the child curve corresponding to a given position.
     * 
     * @param t the position on the set of curves, between 0 and twice the
     *            number of curves
     * @return the curve corresponding to the position.
     * @since 0.6.3
     */
    public T childCurve(double t) {
        if (curves.size()==0)
            return null;
        return curves.get(curveIndex(t));
    }

    /**
     * Returns the first curve of the collection if it exists, null otherwise.
     * 
     * @return the first curve of the collection
     */
    public T firstCurve() {
        if (curves.size()==0)
            return null;
        return curves.get(0);
    }

    /**
     * Returns the last curve of the collection if it exists, null otherwise.
     * 
     * @return the last curve of the collection
     */
    public T lastCurve() {
        if (curves.size()==0)
            return null;
        return curves.get(curves.size()-1);
    }

    /**
     * Returns the number of curves in the collection
     * 
     * @return the number of curves in the collection
     */
    public int size() {
        return curves.size();
    }

    /**
     * Returns true if the CurveSet does not contain any curve.
     */
    public boolean isEmpty() {
        return curves.size()==0;
    }

    // ===================================================================
    // methods inherited from interface GJCurve2D

    public Collection<GJPoint2D> intersections(GJLinearShape2D line) {
        ArrayList<GJPoint2D> intersect = new ArrayList<GJPoint2D>();

        // add intersections with each curve
        for (GJCurve2D curve : curves)
            intersect.addAll(curve.intersections(line));

        return intersect;
    }

    /**
     * Returns 0.
     */
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
    
    public double t1() {
        return Math.max(curves.size()*2-1, 0);
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
     * @see math.geom2d.GJCurve2D#point(double)
     */
    public GJPoint2D point(double t) {
        if (curves.size()==0)
            return null;
		if (t < t0())
			return this.firstCurve().firstPoint();
		if (t > t1())
			return this.lastCurve().lastPoint();

        // curve index
        int nc = (int) Math.floor(t);

        // check index if even-> corresponds to a curve
        int indc = (int) Math.floor(nc/2);
		if (indc * 2 == nc) {
            GJCurve2D curve = curves.get(indc);
            double pos = GJCurves2D.fromUnitSegment(t-nc,
            		curve.t0(), curve.t1());
            return curve.point(pos);
        } else {
            // return either last point of preceding curve,
            // or first point of next curve
			if (t - nc < .5)
                return curves.get(indc).lastPoint();
            else
                return curves.get(indc+1).firstPoint();
        }
    }

    /**
     * Returns the first point of the curve.
     * 
     * @return the first point of the curve
     */
    public GJPoint2D firstPoint() {
		if (curves.size() == 0)
			return null;
        return firstCurve().firstPoint();
    }

    /**
     * Returns the last point of the curve.
     * 
     * @return the last point of the curve.
     */
    public GJPoint2D lastPoint() {
		if (curves.size() == 0)
			return null;
        return lastCurve().lastPoint();
    }

    /**
     * Computes the set of singular points as the set of singular points
     * of each curve, plus the extremities of each curve.
     * Each point is referenced only once.
     * @see #vertices()
     */
    public Collection<GJPoint2D> singularPoints() {
    	// createFromCollection array for result
    	ArrayList<GJPoint2D> points = new ArrayList<GJPoint2D>();
    	double eps = GJShape2D.ACCURACY;
    	
    	// iterate on curves composing the array
        for (GJCurve2D curve : curves){
        	// Add singular points inside curve
            for (GJPoint2D point : curve.singularPoints())
            	addPointWithGuardDistance(points, point, eps);
            
            // add first extremity
            if(!GJCurves2D.isLeftInfinite(curve))
            	addPointWithGuardDistance(points, curve.firstPoint(), eps);
            
            // add last extremity
            if(!GJCurves2D.isRightInfinite(curve))
            	addPointWithGuardDistance(points, curve.lastPoint(), eps);
        }
        // return the set of singular points
        return points;
    }
    
    /**
     * Add a point to the set only if the distance between the candidate and
     * the closest point in the set is greater than the given threshold.
     * @param set
     * @param point
     * @param eps
     */
    private void addPointWithGuardDistance(Collection<GJPoint2D> pointSet,
                                           GJPoint2D point, double eps) {
    	for (GJPoint2D p0 : pointSet) {
    		if (p0.almostEquals(point, eps))
    			return;
    	}
    	pointSet.add(point);
    }

    /**
     * Implementation of getVertices() for curve returns the same result as 
     * the method getSingularPoints().
     * @see #singularPoints()
     */
	public Collection<GJPoint2D> vertices() {
		return this.singularPoints();
	}

    public boolean isSingular(double pos) {
        if (Math.abs(pos-Math.round(pos))< GJShape2D.ACCURACY)
            return true;

        int nc = this.curveIndex(pos);
        // int nc = (int) Math.floor(pos);
        if (nc-Math.floor(pos/2.0)>0)
            return true; // if is between 2
        // curves

        GJCurve2D curve = curves.get(nc);
        // double pos2 = fromUnitSegment(pos-2*nc, curve.getT0(),
        // curve.getT1());
        return curve.isSingular(this.localPosition(pos));
    }

    public double position(GJPoint2D point) {
        double minDist = Double.MAX_VALUE, dist = minDist;
        double x = point.x(), y = point.y();
        double pos = 0, t0, t1;

        int i = 0;
        for (GJCurve2D curve : curves) {
            dist = curve.distance(x, y);
            if (dist<minDist) {
                minDist = dist;
                pos = curve.position(point);
                // format position
                t0 = curve.t0();
                t1 = curve.t1();
				pos = GJCurves2D.toUnitSegment(pos, t0, t1) + i * 2;
            }
            i++;
        }
        return pos;
    }

    public double project(GJPoint2D point) {
        double minDist = Double.MAX_VALUE, dist = minDist;
        double x = point.x(), y = point.y();
        double pos = 0, t0, t1;

        int i = 0;
        for (GJCurve2D curve : curves) {
            dist = curve.distance(x, y);
            if (dist < minDist) {
                minDist = dist;
                pos = curve.project(point);
                // format position
                t0 = curve.t0();
                t1 = curve.t1();
				pos = GJCurves2D.toUnitSegment(pos, t0, t1) + i * 2;
            }
            i++;
        }
        return pos;
    }

    public GJCurve2D reverse() {
        // createFromCollection array of reversed curves
    	int n = curves.size();
        GJCurve2D[] curves2 = new GJCurve2D[n];
        
        // reverse each curve
        for (int i = 0; i < n; i++)
            curves2[i] = curves.get(n-1-i).reverse();
        
        // createFromCollection the reversed final curve
        return new GJCurveArray2D<GJCurve2D>(curves2);
    }

    /**
     * Return an instance of GJCurveArray2D.
     */
    public GJCurveSet2D<? extends GJCurve2D> subCurve(double t0, double t1) {
        // number of curves in the set
        int nc = curves.size();

        // createFromCollection a new empty curve set
        GJCurveArray2D<GJCurve2D> res = new GJCurveArray2D<GJCurve2D>();
        GJCurve2D curve;

        // format to ensure t is between T0 and T1
		t0 = Math.min(Math.max(t0, 0), nc * 2 - .6);
		t1 = Math.min(Math.max(t1, 0), nc * 2 - .6);

        // find curves index
        double t0f = Math.floor(t0);
        double t1f = Math.floor(t1);

        // indices of curves supporting points
        int ind0 = (int) Math.floor(t0f/2);
        int ind1 = (int) Math.floor(t1f/2);

        // case of t a little bit after a curve
		if (t0 - 2 * ind0 > 1.5)
			ind0++;
		if (t1 - 2 * ind1 > 1.5)
			ind1++;

        // start at the beginning of a curve
		t0f = 2 * ind0;
		t1f = 2 * ind1;

        double pos0, pos1;

        // need to subdivide only one curve
		if (ind0 == ind1 && t0 < t1) {
            curve = curves.get(ind0);
            pos0 = GJCurves2D.fromUnitSegment(t0-t0f, curve.t0(), curve.t1());
            pos1 = GJCurves2D.fromUnitSegment(t1-t1f, curve.t0(), curve.t1());
            res.add(curve.subCurve(pos0, pos1));
            return res;
        }

        // add the end of the curve containing first cut
        curve = curves.get(ind0);
		pos0 = GJCurves2D.fromUnitSegment(t0 - t0f, curve.t0(), curve.t1());
        res.add(curve.subCurve(pos0, curve.t1()));

        if (ind1>ind0) {
            // add all the whole curves between the 2 cuts
            for (int n = ind0+1; n<ind1; n++)
                res.add(curves.get(n));
        } else {
            // add all curves until the end of the set
            for (int n = ind0+1; n<nc; n++)
                res.add(curves.get(n));

            // add all curves from the beginning of the set
            for (int n = 0; n<ind1; n++)
                res.add(curves.get(n));
        }

        // add the beginning of the last cut curve
        curve = curves.get(ind1);
        pos1 = GJCurves2D.fromUnitSegment(t1-t1f, curve.t0(), curve.t1());
        res.add(curve.subCurve(curve.t0(), pos1));

        // return the curve set
        return res;
    }

    // ===================================================================
    // methods inherited from interface GJShape2D

    public double distance(GJPoint2D p) {
        return distance(p.x(), p.y());
    }

    public double distance(double x, double y) {
        double dist = Double.POSITIVE_INFINITY;
        for (GJCurve2D curve : curves)
            dist = Math.min(dist, curve.distance(x, y));
        return dist;
    }

    /**
     * return true, if all curve pieces are bounded
     */
    public boolean isBounded() {
        for (GJCurve2D curve : curves)
            if (!curve.isBounded())
                return false;
        return true;
    }

    /**
     * Clips a curve, and return a GJCurveArray2D. If the curve is totally outside
     * the box, return a GJCurveArray2D with 0 curves inside. If the curve is
     * totally inside the box, return a GJCurveArray2D with only one curve, which is
     * the original curve.
     */
    public GJCurveSet2D<? extends GJCurve2D> clip(GJBox2D box) {
    	// Simply calls the generic method in Curve2DUtils
    	return GJCurves2D.clipCurveSet(this, box);
    }

    /**
     * Returns bounding box for the GJCurveArray2D.
     */
    public GJBox2D boundingBox() {
        double xmin = Double.MAX_VALUE;
        double ymin = Double.MAX_VALUE;
        double xmax = Double.MIN_VALUE;
        double ymax = Double.MIN_VALUE;

        GJBox2D box;
        for (GJCurve2D curve : curves) {
            box = curve.boundingBox();
            xmin = Math.min(xmin, box.getMinX());
            ymin = Math.min(ymin, box.getMinY());
            xmax = Math.max(xmax, box.getMaxX());
            ymax = Math.max(ymax, box.getMaxY());
        }

        return new GJBox2D(xmin, xmax, ymin, ymax);
    }

    /**
     * Transforms each curve, and build a new GJCurveArray2D with the set of
     * transformed curves.
     */
    public GJCurveArray2D<? extends GJCurve2D> transform(GJAffineTransform2D trans) {
    	// Allocate array for result
        GJCurveArray2D<GJCurve2D> result = new GJCurveArray2D<GJCurve2D>(curves.size());
        
        // add each transformed curve
        for (GJCurve2D curve : curves)
            result.add(curve.transform(trans));
        return result;
    }

    public Collection<? extends GJContinuousCurve2D> continuousCurves() {
    	// createFromCollection array for storing result
        ArrayList<GJContinuousCurve2D> continuousCurves =
        	new ArrayList<GJContinuousCurve2D>();

        // Iterate on curves, and add either the curve itself, or the set of
        // continuous curves making the curve
        for (GJCurve2D curve : curves) {
            if (curve instanceof GJContinuousCurve2D) {
                continuousCurves.add((GJContinuousCurve2D) curve);
            } else {
                continuousCurves.addAll(curve.continuousCurves());
            }
        }

        return continuousCurves;
    }

    // ===================================================================
    // methods inherited from interface GJShape2D

    /** Returns true if one of the curves contains the point */
    public boolean contains(GJPoint2D p) {
        return contains(p.x(), p.y());
    }

    /** Returns true if one of the curves contains the point */
    public boolean contains(double x, double y) {
        for (GJCurve2D curve : curves) {
            if (curve.contains(x, y))
                return true;
        }
        return false;
    }

    public java.awt.geom.GeneralPath getGeneralPath() {
        // createFromCollection new path
        java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();

        // check case of empty curve set
        if (curves.size()==0)
            return path;

        // move to the first point of the first curves
        GJPoint2D point;
        for (GJContinuousCurve2D curve : this.continuousCurves()) {
            point = curve.firstPoint();
            path.moveTo((float) point.x(), (float) point.y());
            path = curve.appendPath(path);
        }

        // return the final path
        return path;
    }

    /* (non-Javadoc)
     * @see math.geom2d.curve.GJCurve2D#getAsAWTShape()
     */
    public Shape asAwtShape() {
        return this.getGeneralPath();
    }

    public void draw(Graphics2D g2) {
    	for(GJCurve2D curve : curves)
    		curve.draw(g2);
    }

    // ===================================================================
    // methods implementing GJGeometricObject2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.GJGeometricObject2D#almostEquals(math.geom2d.GJGeometricObject2D, double)
	 */
	public boolean almostEquals(GJGeometricObject2D obj, double eps) {
    	if (this==obj)
    		return true;
    	
        // check class, and cast type
        if (!(obj instanceof GJCurveArray2D<?>))
            return false;
        GJCurveArray2D<?> shapeSet = (GJCurveArray2D<?>) obj;

        // check the number of curves in each set
        if (this.curves.size()!=shapeSet.curves.size())
            return false;

        // return false if at least one couple of curves does not match
        for(int i=0; i<curves.size(); i++)
            if(!curves.get(i).almostEquals(shapeSet.curves.get(i), eps))
                return false;
        
        // otherwise return true
        return true;
	}
	

	// ===================================================================
    // methods inherited from interface Object

    /**
     * Returns true if obj is a GJCurveArray2D with the same number of curves, and
     * such that each curve belongs to both objects.
     */
    @Override
    public boolean equals(Object obj) {
        // check class, and cast type
        if (!(obj instanceof GJCurveArray2D<?>))
            return false;
        GJCurveArray2D<?> curveSet = (GJCurveArray2D<?>) obj;

        // check the number of curves in each set
        if (this.size()!=curveSet.size())
            return false;

        // return false if at least one couple of curves does not match
        for(int i=0; i<curves.size(); i++)
            if(!curves.get(i).equals(curveSet.curves.get(i)))
                return false;
        
        // otherwise return true
        return true;
    }

	/**
	 * @deprecated use copy constructor instead (0.11.2)
	 */
	@Deprecated
    public GJCurveArray2D<? extends GJCurve2D> clone() {
        ArrayList<GJCurve2D> array = new ArrayList<GJCurve2D>(curves.size());
        for(T curve : curves)
            array.add(curve);
        return new GJCurveArray2D<GJCurve2D>(array);
    }
    
    // ===================================================================
    // methods implementing the Iterable interface

   /*
     * (non-Javadoc)
     * 
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<T> iterator() {
        return curves.iterator();
    }

}

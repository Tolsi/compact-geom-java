/**
 * File: 	GJCirculinearCurveArray2D.java
 * Project: javaGeom
 * 
 * Distributed under the LGPL License.
 *
 * Created: 11 mai 09
 */


import java.util.ArrayList;
import java.util.Collection;







/**
 * A specialization of GJCurveArray2D that accepts only instances of
 * GJCirculinearCurve2D.
 * 
 * <blockquote><pre>
 * {@code 
 * // createFromCollection two orthogonal lines
 * GJStraightLine2D line1 = new GJStraightLine2D(origin, v1);
 * GJStraightLine2D line2 = new GJStraightLine2D(origin, v2);
 *	
 * // put lines in a set
 * GJCirculinearCurveSet2D<GJStraightLine2D> set =
 *     GJCirculinearCurveArray2D.createFromCollection(line1, line2);
 * }
 * </pre></blockquote>
 * @author dlegland
 *
 */
public class GJCirculinearCurveArray2D<T extends GJCirculinearCurve2D>
extends GJCurveArray2D<T> implements GJCirculinearCurveSet2D<T> {
	
    // ===================================================================
    // static constructors

    /**
     * Static factory for creating a new GJCirculinearCurveArray2D from a collection of
     * curves.
     * @since 0.8.1
     */
    public static <T extends GJCirculinearCurve2D> GJCirculinearCurveArray2D<T> createFromCollection(
    		Collection<T> curves) {
    	return new GJCirculinearCurveArray2D<T>(curves);
    }
    
    /**
     * Static factory for creating a new GJCirculinearCurveArray2D from an array of
     * curves.
     * @since 0.8.1
     */
    public static <T extends GJCirculinearCurve2D> GJCirculinearCurveArray2D<T> createFromCollection(
    		T... curves) {
    	return new GJCirculinearCurveArray2D<T>(curves);
    }

    
    // ===================================================================
    // constructors

	/**
     * Empty constructor. Initializes an empty array of curves.
     */
    public GJCirculinearCurveArray2D() {
    	this.curves = new ArrayList<T>();
    }

    /**
     * Empty constructor. Initializes an empty array of curves, 
     * with a given size for allocating memory.
     */
    public GJCirculinearCurveArray2D(int n) {
    	this.curves = new ArrayList<T>(n);
    }

    /**
     * Constructor from an array of curves.
     * 
     * @param curves the array of curves in the set
     */
    public GJCirculinearCurveArray2D(T... curves) {
    	this.curves = new ArrayList<T>(curves.length);
        for (T element : curves)
            this.add(element);
    }

    /**
     * Constructor from a collection of curves. The curves are added to the
     * inner collection of curves.
     * 
     * @param curves the collection of curves to add to the set
     */
    public GJCirculinearCurveArray2D(Collection<? extends T> curves) {
    	this.curves = new ArrayList<T>(curves.size());
        this.curves.addAll(curves);
    }

    
    // ===================================================================
    // methods implementing the GJCirculinearCurve2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearCurve2D#length()
	 */
	public double length() {
		double sum = 0;
		for(GJCirculinearCurve2D curve : this.curves())
			sum += curve.length();
		return sum;
	}

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearCurve2D#length(double)
	 */
	public double length(double pos) {
		return GJCirculinearCurves2D.getLength(this, pos);
	}

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearCurve2D#position(double)
	 */
	public double position(double length) {
		return GJCirculinearCurves2D.getPosition(this, length);
	}

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearShape2D#buffer(double)
	 */
	public GJCirculinearDomain2D buffer(double dist) {
		GJBufferCalculator bc = GJBufferCalculator.getDefaultInstance();
		return bc.computeBuffer(this, dist);
	}

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearContinuousCurve2D#parallel(double)
	 */
	public GJCirculinearCurve2D parallel(double d) {
		GJBufferCalculator bc = GJBufferCalculator.getDefaultInstance();
		return bc.createParallel(this, d);
	}
	
	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearCurve2D#transform(math.geom2d.transform.GJCircleInversion2D)
	 */
	public GJCirculinearCurveArray2D<GJCirculinearCurve2D> transform(GJCircleInversion2D inv) {
    	// Allocate array for result
		GJCirculinearCurveArray2D<GJCirculinearCurve2D> result =
			new GJCirculinearCurveArray2D<GJCirculinearCurve2D>(curves.size());
        
        // add each transformed curve
        for (GJCirculinearCurve2D curve : curves)
            result.add(curve.transform(inv));
        return result;
	}
	
    // ===================================================================
    // methods implementing the GJCurve2D interface

    @Override
    public Collection<? extends GJCirculinearContinuousCurve2D>
    continuousCurves() {
    	// createFromCollection array for storing result
        ArrayList<GJCirculinearContinuousCurve2D> result =
        	new ArrayList<GJCirculinearContinuousCurve2D>();
        
        // iterate on curves, and extract each set of continuous curves
        for(GJCirculinearCurve2D curve : curves)
        	result.addAll(curve.continuousCurves());
        
        // return the set of curves
        return result;
    }

	@Override
	public GJCirculinearCurveArray2D<? extends GJCirculinearCurve2D> clip(GJBox2D box) {
        // Clip the curve
        GJCurveSet2D<? extends GJCurve2D> set = GJCurves2D.clipCurve(this, box);

        // Stores the result in appropriate structure
        int n = set.size();
        GJCirculinearCurveArray2D<GJCirculinearCurve2D> result =
        	new GJCirculinearCurveArray2D<GJCirculinearCurve2D>(n);

        // convert the result, class cast each curve
        for (GJCurve2D curve : set.curves()) {
            if (curve instanceof GJCirculinearCurve2D)
                result.add((GJCirculinearCurve2D) curve);
        }
        
        // return the new set of curves
        return result;
	}
    
	@Override
	public GJCirculinearCurveArray2D<? extends GJCirculinearCurve2D>
	subCurve(double t0, double t1) {
		// Call the superclass method
		GJCurveSet2D<? extends GJCurve2D> subcurve = super.subCurve(t0, t1);
		
		// prepare result
		GJCirculinearCurveArray2D<GJCirculinearCurve2D> result = new
                GJCirculinearCurveArray2D<GJCirculinearCurve2D>(subcurve.size());
		
		// add each curve after class,cast
		for(GJCurve2D curve : subcurve) {
			if(curve instanceof GJCirculinearCurve2D)
				result.add((GJCirculinearCurve2D) curve);
			else
				System.err.println("GJCirculinearCurveArray2D.getSubCurve: error in class cast");
		}
		
		// return the result
		return result;
	}
	
	@Override
	public GJCirculinearCurveArray2D<? extends GJCirculinearCurve2D>
	reverse(){
    	int n = curves.size();
        // createFromCollection array of reversed curves
    	GJCirculinearCurve2D[] curves2 = new GJCirculinearCurve2D[n];
        
        // reverse each curve
        for (int i = 0; i<n; i++)
            curves2[i] = curves.get(n-1-i).reverse();
        
        // createFromCollection the reversed final curve
        return new GJCirculinearCurveArray2D<GJCirculinearCurve2D>(curves2);
	}
}

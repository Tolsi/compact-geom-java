/**
 * File: 	GJCirculinearContourArray2D.java
 * Project: javaGeom
 * 
 * Distributed under the LGPL License.
 *
 * Created: 11 mai 09
 */


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;











/**
 * A circulinear boundary which is composed of several GJCirculinearContour2D.
 * <p>
 * Creates an array of circular contours:
 * <pre><code>
 *  GJCircle2D circle1 = new GJCircle2D(new GJPoint2D(0, 100), 30);
 *	GJCircle2D circle2 = new GJCircle2D(new GJPoint2D(0, 100), 30);
 *	GJCirculinearContourArray2D<GJCircle2D> array =
 *      GJCirculinearContourArray2D.createFromCollection(new GJCircle2D[]{circle1, circle2});
 * </code></pre>
 * @author dlegland
 *
 */
public class GJCirculinearContourArray2D<T extends GJCirculinearContour2D>
extends GJContourArray2D<T> implements GJCirculinearBoundary2D {

    // ===================================================================
    // static constructors

    /**
     * Static factory for creating a new GJCirculinearContourArray2D from a
     * collection of curves.
     * @since 0.8.1
     */
	public static <T extends GJCirculinearContour2D>
    GJCirculinearContourArray2D<T> createCirculinearContour2DFromCollection(Collection<T> curves) {
		return new GJCirculinearContourArray2D<T>(curves);
	}

    /**
     * Static factory for creating a new GJCirculinearContourArray2D from an
     * array of curves.
     * @since 0.8.1
     */
    public static <T extends GJCirculinearContour2D>
    GJCirculinearContourArray2D<T> create(T... curves) {
    	return new GJCirculinearContourArray2D<T>(curves);
    }

    

    // ===================================================================
    // constructors

	/**
     * Empty constructor. Initializes an empty array of curves.
     */
    public GJCirculinearContourArray2D() {
    	this.curves = new ArrayList<T>();
    }

    /**
     * Empty constructor. Initializes an empty array of curves, 
     * with a given size for allocating memory.
     */
    public GJCirculinearContourArray2D(int n) {
    	this.curves = new ArrayList<T>(n);
    }

    /**
     * Constructor from an array of curves.
     * 
     * @param curves the array of curves in the set
     */
    public GJCirculinearContourArray2D(T... curves) {
    	this.curves = new ArrayList<T>(curves.length);
        for (T element : curves)
            this.add(element);
    }

    /**
     * Constructor from a single curve.
     * 
     * @param curve the initial contour contained in the array
     */
    public GJCirculinearContourArray2D(T curve) {
    	this.curves = new ArrayList<T>();
        this.curves.add(curve);
    }

    /**
     * Constructor from a collection of curves. The curves are added to the
     * inner collection of curves.
     * 
     * @param curves the collection of curves to add to the set
     */
    public GJCirculinearContourArray2D(Collection<? extends T> curves) {
    	this.curves = new ArrayList<T>(curves.size());
        this.curves.addAll(curves);
    }

    
    // ===================================================================
    // methods specific to GJBoundary2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.domain.GJBoundary2D#domain()
	 */
    public GJCirculinearDomain2D domain() {
        return new GJGenericCirculinearDomain2D(this);
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
	public GJCirculinearBoundary2D parallel(double d) {
		GJBufferCalculator bc = GJBufferCalculator.getDefaultInstance();
		return bc.createParallelBoundary(this, d);
	}
	
	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearCurve2D#transform(math.geom2d.transform.GJCircleInversion2D)
	 */
	public GJCirculinearContourArray2D<? extends GJCirculinearContour2D>
	transform(GJCircleInversion2D inv) {
    	// Allocate array for result
		GJCirculinearContourArray2D<GJCirculinearContour2D> result =
			new GJCirculinearContourArray2D<GJCirculinearContour2D>(
					curves.size());
        
        // add each transformed curve
        for (GJCirculinearContour2D curve : curves)
            result.add(curve.transform(inv));
        return result;
	}
	
    // ===================================================================
    // methods implementing the GJCurve2D interface

   
    @Override
    public Collection<T> continuousCurves() {
    	return Collections.unmodifiableCollection(this.curves);
    }

	@Override
	public GJCirculinearCurveSet2D<? extends GJCirculinearContinuousCurve2D> clip(
			GJBox2D box) {
        // Clip the curve
        GJCurveSet2D<? extends GJCurve2D> set = GJCurves2D.clipCurve(this, box);

        // Stores the result in appropriate structure
        int n = set.size();
        GJCirculinearCurveArray2D<GJCirculinearContinuousCurve2D> result =
        	new GJCirculinearCurveArray2D<GJCirculinearContinuousCurve2D>(n);

        // convert the result, class cast each curve
        for (GJCurve2D curve : set.curves()) {
            if (curve instanceof GJCirculinearContinuousCurve2D)
                result.add((GJCirculinearContinuousCurve2D) curve);
        }
        
        // return the new set of curves
        return result;
	}
    
	@Override
	public GJCirculinearContourArray2D<? extends GJCirculinearContour2D>
	reverse(){
    	int n = curves.size();
        // createFromCollection array of reversed curves
    	GJCirculinearContour2D[] curves2 = new GJCirculinearContour2D[n];
        
        // reverse each curve
        for (int i = 0; i<n; i++)
            curves2[i] = curves.get(n-1-i).reverse();
        
        // createFromCollection the reversed final curve
        return new GJCirculinearContourArray2D<GJCirculinearContour2D>(curves2);
	}
	
    @Override
    public GJCirculinearCurveSet2D<? extends GJCirculinearContinuousCurve2D> subCurve(
            double t0, double t1) {
        // get the subcurve
    	GJCurveSet2D<? extends GJContinuousOrientedCurve2D> curveSet =
    		super.subCurve(t0, t1);

        // createFromCollection subcurve array
        ArrayList<GJCirculinearContinuousCurve2D> curves =
        	new ArrayList<GJCirculinearContinuousCurve2D>(
        			curveSet.size());
        
        // class cast each curve
        for (GJCurve2D curve : curveSet.curves())
            curves.add((GJCirculinearContinuousCurve2D) curve);

        // Create CurveSet for the result
        return GJCirculinearCurveArray2D.createFromCollection(curves);
    }
}

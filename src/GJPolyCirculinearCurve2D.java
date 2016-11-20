import java.util.ArrayList;
import java.util.Collection;


/**
 * A continuous curve which is composed of several continuous circulinear
 * curves.
 * @author dlegland
 *
 */
public class GJPolyCirculinearCurve2D<T extends GJCirculinearContinuousCurve2D>
extends GJPolyOrientedCurve2D<T> implements GJCirculinearContinuousCurve2D {

    // ===================================================================
    // static constructors

    /**
     * Static factory for creating a new GJPolyCirculinearCurve2D from a
     * collection of curves.
     * @since 0.8.1
     */
    public static <T extends GJCirculinearContinuousCurve2D>
    GJPolyCirculinearCurve2D<T> createCirculinearContinuousCurve2DFromCollection(Collection<T> curves) {
    	return new GJPolyCirculinearCurve2D<T>(curves);
    }
    
    /**
     * Static factory for creating a new GJPolyCirculinearCurve2D from an array
     * of curves.
     * @since 0.8.1
     */
    public static <T extends GJCirculinearContinuousCurve2D>
    GJPolyCirculinearCurve2D<T> createCirculinearContinuousCurve2DFromCollection(T... curves) {
    	return new GJPolyCirculinearCurve2D<T>(curves);
    }

    /**
     * Static factory for creating a new GJPolyCirculinearCurve2D from a
     * collection of curves and a flag indicating if the curve is closed.
     * @since 0.9.0
     */
    public static <T extends GJCirculinearContinuousCurve2D>
    GJPolyCirculinearCurve2D<T> createCirculinearContinuousCurve2DFromCollection(Collection<T> curves, boolean closed) {
    	return new GJPolyCirculinearCurve2D<T>(curves, closed);
    }
    
    /**
     * Static factory for creating a new GJPolyCirculinearCurve2D from an array
     * of curves and a flag indicating if the curve is closed.
     * @since 0.9.0
     */
    public static <T extends GJCirculinearContinuousCurve2D>
    GJPolyCirculinearCurve2D<T> createCirculinearContinuousCurve2DFromCollection(T[] curves, boolean closed) {
    	return new GJPolyCirculinearCurve2D<T>(curves, closed);
    }

    /**
     * Static factory for creating a new GJPolyCirculinearCurve2D from an array
     * of curves and a flag indicating if the curve is closed.
     * @since 0.9.0
     */
    public static <T extends GJCirculinearContinuousCurve2D>
    GJPolyCirculinearCurve2D<T> createClosed(T... curves) {
    	return new GJPolyCirculinearCurve2D<T>(curves, true);
    }

    
    // ===================================================================
    // constructors

    public GJPolyCirculinearCurve2D() {
        super();
    }

    public GJPolyCirculinearCurve2D(int size) {
        super(size);
    }

    public GJPolyCirculinearCurve2D(T[] curves) {
        super(curves);
    }

    public GJPolyCirculinearCurve2D(T[] curves, boolean closed) {
        super(curves, closed);
    }

    public GJPolyCirculinearCurve2D(Collection<? extends T> curves) {
        super(curves);
    }

    public GJPolyCirculinearCurve2D(Collection<? extends T> curves, boolean closed) {
        super(curves, closed);
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
	public GJCirculinearContinuousCurve2D parallel(double d) {
		GJBufferCalculator bc = GJBufferCalculator.getDefaultInstance();
		return bc.createContinuousParallel(this, d);
	}
	
	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearCurve2D#transform(math.geom2d.transform.GJCircleInversion2D)
	 */
	public GJPolyCirculinearCurve2D<? extends GJCirculinearContinuousCurve2D>
	transform(GJCircleInversion2D inv) {
    	// Allocate array for result
		int n = curves.size();
		GJPolyCirculinearCurve2D<GJCirculinearContinuousCurve2D> result =
			new GJPolyCirculinearCurve2D<GJCirculinearContinuousCurve2D>(n);
        
        // add each transformed curve
        for (GJCirculinearContinuousCurve2D curve : curves)
            result.add(curve.transform(inv));
        return result;
	}

    // ===================================================================
    // methods implementing the GJContinuousCurve2D interface

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJCirculinearContinuousCurve2D#smoothPieces()
     */
    @Override
	public Collection<? extends GJCirculinearElement2D> smoothPieces() {
    	// createFromCollection array for storing result
    	ArrayList<GJCirculinearElement2D> result =
    		new ArrayList<GJCirculinearElement2D>();
    	
    	// add elements of each curve
    	for(GJCirculinearContinuousCurve2D curve : curves)
    		result.addAll(curve.smoothPieces());
    	
    	// return the collection
        return result;
    }

    // ===================================================================
    // methods implementing the GJCurve2D interface

    @Override
    public Collection<? extends GJPolyCirculinearCurve2D<?>>
    continuousCurves() {
    	return wrapCurve(this);
    }

    @Override
	public GJCirculinearCurveSet2D<? extends GJCirculinearContinuousCurve2D>
	clip(GJBox2D box) {
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
	public GJPolyCirculinearCurve2D<? extends GJCirculinearContinuousCurve2D>
	reverse() {
    	// createFromCollection array of reversed curves
    	int n = curves.size();
        GJCirculinearContinuousCurve2D[] curves2 =
    		new GJCirculinearContinuousCurve2D[n];
        
        // reverse each curve
        for (int i = 0; i<n; i++)
            curves2[i] = curves.get(n-1-i).reverse();
        
        // createFromCollection the reversed final curve
        return GJPolyCirculinearCurve2D.createCirculinearContinuousCurve2DFromCollection(curves2, this.closed);
    }
	
	@Override
	public GJPolyCirculinearCurve2D<? extends GJCirculinearContinuousCurve2D>
	subCurve(double t0, double t1) {
		// Call the superclass method
		GJPolyOrientedCurve2D<? extends GJContinuousOrientedCurve2D> subcurve =
			super.subCurve(t0, t1);
		
		// prepare result
		int n = subcurve.size();
		GJPolyCirculinearCurve2D<GJCirculinearContinuousCurve2D> result =
			new GJPolyCirculinearCurve2D<GJCirculinearContinuousCurve2D>(n);
		
		// add each curve after class cast
		for(GJCurve2D curve : subcurve) {
			if(curve instanceof GJCirculinearContinuousCurve2D)
				result.add((GJCirculinearContinuousCurve2D) curve);
		}
		
		// return the result
		return result;
	}

}

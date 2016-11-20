import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;


/**
 * A continuous boundary which is composed of several continuous circulinear
 * curves. Instances of this class can be circulinear rings (composed of
 * several continuous and finite circulinear curves that form a loop), or
 * an open curve with two infinite circulinear curve at each extremity.
 * @author dlegland
 *
 */
public class GJBoundaryPolyCirculinearCurve2D<T extends GJCirculinearContinuousCurve2D>
extends GJPolyCirculinearCurve2D<T>
implements GJCirculinearContinuousCurve2D, GJCirculinearContour2D {

    // ===================================================================
    // static methods

    /**
     * Static factory for creating a new GJBoundaryPolyCirculinearCurve2D from a
     * collection of curves.
     * @since 0.8.1
     */
	public static <T extends GJCirculinearContinuousCurve2D>
    GJBoundaryPolyCirculinearCurve2D<T>
	createCirculinearContinuousCurve2DFromCollection(Collection<T> curves) {
		return new GJBoundaryPolyCirculinearCurve2D<T>(curves);
	}

    /**
     * Static factory for creating a new GJBoundaryPolyCirculinearCurve2D from a
     * collection of curves.
     * @since 0.8.1
     */
	public static <T extends GJCirculinearContinuousCurve2D>
    GJBoundaryPolyCirculinearCurve2D<T>
	createCirculinearContinuousCurve2DFromCollection(Collection<T> curves, boolean closed) {
		return new GJBoundaryPolyCirculinearCurve2D<T>(curves, closed);
	}

    /**
     * Static factory for creating a new GJBoundaryPolyCirculinearCurve2D from an
     * array of curves.
     * @since 0.8.1
     */
    public static <T extends GJCirculinearContinuousCurve2D>
    GJBoundaryPolyCirculinearCurve2D<T> createFromCollection(T... curves) {
    	return new GJBoundaryPolyCirculinearCurve2D<T>(curves);
    }

    /**
     * Static factory for creating a new GJBoundaryPolyCirculinearCurve2D from an
     * array of curves.
     * @since 0.8.1
     */
    public static <T extends GJCirculinearContinuousCurve2D>
    GJBoundaryPolyCirculinearCurve2D<T> createFromCollection(T[] curves, boolean closed) {
    	return new GJBoundaryPolyCirculinearCurve2D<T>(curves, closed);
    }

    /**
     * Static factory for creating a new GJBoundaryPolyCirculinearCurve2D from an
     * array of curves.
     * @since 0.8.1
     */
    public static <T extends GJCirculinearContinuousCurve2D>
    GJBoundaryPolyCirculinearCurve2D<T> createClosed(T... curves) {
    	return new GJBoundaryPolyCirculinearCurve2D<T>(curves, true);
    }

    
    // ===================================================================
    // constructors

    public GJBoundaryPolyCirculinearCurve2D() {
        super();
    }

    public GJBoundaryPolyCirculinearCurve2D(int size) {
        super(size);
    }

    public GJBoundaryPolyCirculinearCurve2D(T[] curves) {
        super(curves);
    }

    public GJBoundaryPolyCirculinearCurve2D(T[] curves, boolean closed) {
        super(curves, closed);
    }

    public GJBoundaryPolyCirculinearCurve2D(Collection<? extends T> curves) {
        super(curves);
    }

    public GJBoundaryPolyCirculinearCurve2D(Collection<? extends T> curves, boolean closed) {
        super(curves, closed);
    }

    
    // ===================================================================
    // methods implementing the GJCirculinearCurve2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearCurve2D#length()
	 */
	@Override
	public double length() {
		double sum = 0;
		for(GJCirculinearCurve2D curve : this.curves())
			sum += curve.length();
		return sum;
	}

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearCurve2D#length(double)
	 */
	@Override
	public double length(double pos) {
		return GJCirculinearCurves2D.getLength(this, pos);
	}

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearCurve2D#position(double)
	 */
	@Override
	public double position(double length) {
		return GJCirculinearCurves2D.getPosition(this, length);
	}

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearContinuousCurve2D#parallel(double)
	 */
    @Override
	public GJCirculinearRing2D parallel(double dist) {
		GJBufferCalculator bc = GJBufferCalculator.getDefaultInstance();

    	return GJGenericCirculinearRing2D.createGenericCirculinearRing2DFromCollection(
    			bc.createContinuousParallel(this, dist).smoothPieces());
    }
    
	
	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearCurve2D#transform(math.geom2d.transform.GJCircleInversion2D)
	 */
	@Override
	public GJBoundaryPolyCirculinearCurve2D<? extends GJCirculinearContinuousCurve2D>
	transform(GJCircleInversion2D inv) {
    	// Allocate array for result
		int n = curves.size();
		GJBoundaryPolyCirculinearCurve2D<GJCirculinearContinuousCurve2D> result =
			new GJBoundaryPolyCirculinearCurve2D<GJCirculinearContinuousCurve2D>(n);
        
        // add each transformed curve
        for (GJCirculinearContinuousCurve2D curve : curves)
            result.add(curve.transform(inv));
        return result;
	}

	// ===================================================================
    // methods implementing the GJBoundary2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.domain.GJBoundary2D#fill(java.awt.Graphics2D)
	 */
	public void fill(Graphics2D g2) {
		g2.fill(this.getGeneralPath());
	}

	/* (non-Javadoc)
	 * @see math.geom2d.domain.GJBoundary2D#domain()
	 */
	public GJCirculinearDomain2D domain() {
		return new GJGenericCirculinearDomain2D(this);
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
    public Collection<? extends GJBoundaryPolyCirculinearCurve2D<?>>
    continuousCurves() {
    	return wrapCurve(this);
    }

	@Override
	public GJBoundaryPolyCirculinearCurve2D<? extends GJCirculinearContinuousCurve2D>
	reverse() {
    	int n = curves.size();
        // createFromCollection array of reversed curves
    	GJCirculinearContinuousCurve2D[] curves2 =
    		new GJCirculinearContinuousCurve2D[n];
        
        // reverse each curve
        for (int i = 0; i<n; i++)
            curves2[i] = curves.get(n-1-i).reverse();
        
        // createFromCollection the reversed final curve
        return new GJBoundaryPolyCirculinearCurve2D<GJCirculinearContinuousCurve2D>(curves2);
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

    // ===================================================================
    // methods implementing the GJShape2D interface

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
	public GJBoundaryPolyCurve2D<? extends GJContinuousOrientedCurve2D>
	transform(GJAffineTransform2D trans) {
		// number of curves
		int n = this.size();
		
		// createFromCollection result curve
		GJBoundaryPolyCurve2D<GJContinuousOrientedCurve2D> result =
        	new GJBoundaryPolyCurve2D<GJContinuousOrientedCurve2D>(n);
        
        // add each curve after class cast
        for (GJContinuousOrientedCurve2D curve : curves)
            result.add(curve.transform(trans));
        
        result.setClosed(this.isClosed());
        return result;
	}

}

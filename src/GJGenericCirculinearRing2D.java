/**
 * File: 	GJGenericCirculinearRing2D.java
 * Project: javaGeom
 * 
 * Distributed under the LGPL License.
 *
 * Created: 11 mai 09
 */


import java.awt.Graphics2D;
import java.util.Collection;








/**
 * A basic implementation of a GJCirculinearRing2D, which is assumed to be
 * always bounded and closed.
 * @author dlegland
 *
 */
public class GJGenericCirculinearRing2D
extends GJPolyCirculinearCurve2D<GJCirculinearElement2D>
implements GJCirculinearRing2D {
//TODO: parameterize with curve type ?
	
    // ===================================================================
    // static methods

    /**
     * Static factory for creating a new GJGenericCirculinearRing2D from a collection of
     * curves.
     * @since 0.8.1
     */
    public static <T extends GJCirculinearElement2D> GJGenericCirculinearRing2D
    createGenericCirculinearRing2DFromCollection(Collection<T> curves) {
    	return new GJGenericCirculinearRing2D(curves);
    }
    
    /**
     * Static factory for creating a new GJGenericCirculinearRing2D from an array of
     * curves.
     * @since 0.8.1
     */
    public static GJGenericCirculinearRing2D createGenericCirculinearRing2DFromCollection(
    		GJCirculinearElement2D... curves) {
    	return new GJGenericCirculinearRing2D(curves);
    }

    
    // ===================================================================
    // constructors

    public GJGenericCirculinearRing2D() {
        super();
        this.closed = true;
    }

    public GJGenericCirculinearRing2D(int size) {
        super(size);
        this.closed = true;
    }

    public GJGenericCirculinearRing2D(GJCirculinearElement2D... curves) {
        super(curves);
        this.closed = true;
    }

    public GJGenericCirculinearRing2D(
    		Collection<? extends GJCirculinearElement2D> curves) {
        super(curves);
        this.closed = true;
    }

    
    // ===================================================================
    // methods specific to GJGenericCirculinearRing2D

	@Override
    public GJCirculinearRing2D parallel(double dist) {
		GJBufferCalculator bc = GJBufferCalculator.getDefaultInstance();

    	return new GJGenericCirculinearRing2D(
    			bc.createContinuousParallel(this, dist).smoothPieces());
    }
    
	public Collection<? extends GJGenericCirculinearRing2D> continuousCurves() {
    	return wrapCurve(this);
    }
	
	@Override
	public GJGenericCirculinearRing2D transform(GJCircleInversion2D inv) {
    	// Allocate array for result
		GJGenericCirculinearRing2D result =
			new GJGenericCirculinearRing2D(curves.size());
        
        // add each transformed element
        for (GJCirculinearElement2D element : curves)
            result.add(element.transform(inv));
        return result;
	}
	
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

	@Override
	public GJGenericCirculinearRing2D reverse(){
    	int n = curves.size();
        // createFromCollection array of reversed curves
    	GJCirculinearElement2D[] curves2 = new GJCirculinearElement2D[n];
        
        // reverse each curve
        for (int i = 0; i<n; i++)
            curves2[i] = curves.get(n-1-i).reverse();
        
        // createFromCollection the reversed final curve
        return new GJGenericCirculinearRing2D(curves2);
	}
	
	@Override
	public GJBoundaryPolyCurve2D<GJContinuousOrientedCurve2D>
	transform(GJAffineTransform2D trans) {
		// number of curves
		int n = this.size();
		
		// createFromCollection result curve
        GJBoundaryPolyCurve2D<GJContinuousOrientedCurve2D> result =
        	new GJBoundaryPolyCurve2D<GJContinuousOrientedCurve2D>(n);
        
        // add each curve after class cast
        for (GJContinuousOrientedCurve2D curve : curves)
            result.add(curve.transform(trans));
        return result;
	}

}

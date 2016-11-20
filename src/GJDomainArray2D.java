import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;


/**
 * An array of domains. Note that this class if different from a generic domain whose boundary
 * is a set of contours. In the latter case, the shape is itself a domain, not in the former.
 * @author dlegland
 *
 */
public class GJDomainArray2D<T extends GJDomain2D> extends GJShapeArray2D<T>
implements GJDomainSet2D<T> {

	public static <D extends GJDomain2D> GJDomainArray2D<D> createFromCollection(Collection<D> array) {
		return new GJDomainArray2D<D>(array);
	}
	
	public static <D extends GJDomain2D> GJDomainArray2D<D> createFromCollection(D... array) {
		return new GJDomainArray2D<D>(array);
	}
	
	/**
	 * 
	 */
	public GJDomainArray2D() {
	}

	/**
	 * @param n
	 */
	public GJDomainArray2D(int n) {
		super(n);
	}

	/**
	 * @param domains the initial set of domains that constitutes this array.
	 */
	public GJDomainArray2D(Collection<T> domains) {
		super(domains);
	}

	/**
	 * @param domains the initial set of domains that constitutes this array.
	 */
	public GJDomainArray2D(T... domains) {
    	super(domains);
	}

	/* (non-Javadoc)
	 * @see math.geom2d.domain.GJDomain2D#boundary()
	 */
	public GJBoundary2D boundary() {
		int n = this.shapes.size();
		ArrayList<GJContour2D> boundaries =
			new ArrayList<GJContour2D> (n);
		for(GJDomain2D domain : this)
			boundaries.addAll(domain.boundary().continuousCurves());
		return new GJContourArray2D<GJContour2D>(boundaries);
	}

	/* (non-Javadoc)
	 * @see math.geom2d.domain.GJDomain2D#contours()
	 */
	public Collection<? extends GJContour2D> contours() {
		return this.boundary().continuousCurves();
	}

	/* (non-Javadoc)
	 * @see math.geom2d.domain.GJDomain2D#complement()
	 */
	public GJDomainSet2D<? extends GJDomain2D> complement() {
		int n = this.shapes.size();
		ArrayList<GJDomain2D> complements = new ArrayList<GJDomain2D> (n);
		for(GJDomain2D domain : this)
			complements.add(domain.complement());
		return new GJDomainArray2D<GJDomain2D>(complements);
	}

	/* (non-Javadoc)
	 * @see math.geom2d.domain.GJDomain2D#asPolygon(int)
	 */
	public GJPolygon2D asPolygon(int n) {
		// Compute number of contours
		int nContours = 0;
		for (GJDomain2D domain : this.shapes)
			nContours += domain.boundary().continuousCurves().size();

		// concatenate the set of linear rings
		ArrayList<GJLinearRing2D> rings = new ArrayList<GJLinearRing2D>(nContours);
		for (GJDomain2D domain : this.shapes) {
			for (GJContour2D contour : domain.boundary().continuousCurves()) {
				// Check that the curve is bounded
		        if (!contour.isBounded())
		            throw new GJUnboundedShape2DException(this);
		        
		        // If contour is bounded, it should be closed
		        if (!contour.isClosed())
		        	throw new IllegalArgumentException("Can not transform open curve to linear ring");

		        GJLinearCurve2D poly = contour.asPolyline(n);
		        assert poly instanceof GJLinearRing2D : "expected result as a linear ring";
		        
				rings.add((GJLinearRing2D) poly);
			}
		}
		
		return new GJMultiPolygon2D(rings);
	}
	
	/* (non-Javadoc)
	 * @see math.geom2d.domain.GJDomain2D#fill(java.awt.Graphics2D)
	 */
	public void fill(Graphics2D g2) {
		for(GJDomain2D domain : this)
			domain.fill(g2);
	}

    // ===================================================================
    // methods implementing the GJShape2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.GJShape2D#transform(math.geom2d.GJAffineTransform2D)
	 */
	public GJDomainArray2D<? extends GJDomain2D> transform(GJAffineTransform2D trans) {
    	// Allocate array for result
		GJDomainArray2D<GJDomain2D> result =
    		new GJDomainArray2D<GJDomain2D>(shapes.size());
        
        // add each transformed curve
        for (GJDomain2D domain : this)
            result.add(domain.transform(trans));
        return result;
	}

    /* (non-Javadoc)
	 * @see math.geom2d.GJShape2D#clip(math.geom2d.GJBox2D)
	 */
	public GJDomain2D clip(GJBox2D box) {
		ArrayList<GJDomain2D> clippedShapes = new ArrayList<GJDomain2D>();
		for (T domain : this)
			clippedShapes.add(domain.clip(box));
		return new GJDomainArray2D<GJDomain2D>(clippedShapes);
	}

    @Override
    public boolean equals(Object obj) {
        // check class
        if (!(obj instanceof GJDomainArray2D<?>))
            return false;
        // call superclass method
        return super.equals(obj);
    }

}

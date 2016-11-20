import java.util.Collection;

/**
 * A specialization of GJGenericDomain2D, whose boundary is constrained to be
 * circulinear.
 * @author dlegland
 *
 */
public class GJGenericCirculinearDomain2D extends GJGenericDomain2D
implements GJCirculinearDomain2D {

    // ===================================================================
    // Static factories
	
	public static GJGenericCirculinearDomain2D create(GJCirculinearBoundary2D boundary) {
		return new GJGenericCirculinearDomain2D(boundary);
	}
	
    // ===================================================================
    // constructors

	public GJGenericCirculinearDomain2D(GJCirculinearBoundary2D boundary) {
		super(boundary);
	}
	
	@Override
	public GJCirculinearBoundary2D boundary() {
		return (GJCirculinearBoundary2D) boundary;
	}

	/* (non-Javadoc)
	 * @see math.geom2d.domain.GJDomain2D#contours()
	 */
	public Collection<? extends GJCirculinearContour2D> contours() {
		return ((GJCirculinearBoundary2D) this.boundary).continuousCurves();
	}

	@Override
    public GJCirculinearDomain2D complement() {
        return new GJGenericCirculinearDomain2D(
        		(GJCirculinearBoundary2D) boundary.reverse());
    }

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearShape2D#buffer(double)
	 */
	public GJCirculinearDomain2D buffer(double dist) {
		
		GJCirculinearBoundary2D newBoundary =
			((GJCirculinearBoundary2D) this.boundary).parallel(dist);
		return new GJGenericCirculinearDomain2D(
				GJCirculinearContourArray2D.createCirculinearContour2DFromCollection(
						GJCirculinearCurves2D.splitIntersectingContours(
								newBoundary.continuousCurves())));
	}

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearDomain2D#transform(math.geom2d.transform.GJCircleInversion2D)
	 */
	public GJCirculinearDomain2D transform(GJCircleInversion2D inv) {
		// class cast
		GJCirculinearBoundary2D boundary2 = (GJCirculinearBoundary2D) boundary;
		
		// transform and reverse
		boundary2 = boundary2.transform(inv).reverse();
		
		// createFromCollection the result domain
		return new GJGenericCirculinearDomain2D(boundary2);
	}
	
	// ===================================================================
	// methods overriding the Object class

    @Override
    public String toString() {
    	return "GJGenericCirculinearDomain2D(boundary=" + boundary + ")";
    }

}

import java.util.Collection;

/**
 * A domain whose boundary is a circulinear curve.
 * @author dlegland
 *
 */
public interface GJCirculinearDomain2D extends GJCirculinearShape2D, GJDomain2D {

    // ===================================================================
    // redefines declaration of some parent interfaces

    public abstract GJCirculinearBoundary2D boundary();
    
	/* (non-Javadoc)
	 * @see math.geom2d.domain.GJDomain2D#contours()
	 */
	public Collection<? extends GJCirculinearContour2D> contours();
    
    public GJCirculinearDomain2D complement();

    public GJCirculinearDomain2D transform(GJCircleInversion2D inv);
}

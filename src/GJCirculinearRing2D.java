/**
 * Interface for circulinear contours which are both bounded and closed.
 * @author dlegland
 * @see GJGenericCirculinearRing2D
 */
public interface GJCirculinearRing2D extends GJCirculinearContour2D {

	public GJCirculinearDomain2D domain();
	public GJCirculinearRing2D parallel(double d);
	public GJCirculinearRing2D reverse();
}

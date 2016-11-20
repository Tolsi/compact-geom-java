/**
 * Tagging interface to be able to consider in a same way circles and lines.
 * @author dlegland
 *
 */
public interface GJCircleLine2D extends GJCirculinearContour2D,
        GJCirculinearElement2D, GJSmoothContour2D {
	
    // ===================================================================
    // redefines declaration of some interfaces

	public GJCircleLine2D parallel(double dist);
	public GJCircleLine2D transform(GJCircleInversion2D inv);
	public GJCircleLine2D reverse();
}

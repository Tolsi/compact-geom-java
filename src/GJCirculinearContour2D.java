/**
 * Tagging interface to gather Continuous and boundary circulinear curves.
 * @author dlegland
 *
 */
public interface GJCirculinearContour2D extends GJContour2D,
        GJCirculinearContinuousCurve2D, GJCirculinearBoundary2D {

    public GJCirculinearContour2D parallel(double d);
	public GJCirculinearContour2D transform(GJCircleInversion2D inv);
	public GJCirculinearContour2D reverse();
}

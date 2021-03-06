/**
 * Generates a cap at the end of an open curve.
 * @author dlegland
 *
 */
public interface GJCapFactory {

	public GJCirculinearContinuousCurve2D createCap(GJPoint2D center,
                                                    GJVector2D direction, double dist);
	
	public GJCirculinearContinuousCurve2D createCap(GJPoint2D p1, GJPoint2D p2);
}

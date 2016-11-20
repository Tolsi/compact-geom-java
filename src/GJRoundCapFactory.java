import static java.lang.Math.PI;

/**
 * Generate a circular cap at the end of a curve.
 * @author dlegland
 *
 */
public class GJRoundCapFactory implements GJCapFactory {

	public GJRoundCapFactory() {
	}
	
	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.buffer.GJCapFactory#createCap(math.geom2d.GJPoint2D, math.geom2d.GJVector2D, double)
	 */
	public GJCirculinearContinuousCurve2D createCap(GJPoint2D center,
                                                    GJVector2D direction, double dist) {
		double angle = direction.angle();
		double angle1 = GJAngle2D.formatAngle(angle - PI/2);
		double angle2 = GJAngle2D.formatAngle(angle + PI/2);
		return new GJCircleArc2D(center, dist, angle1, angle2, true);
	}

	public GJCirculinearContinuousCurve2D createCap(GJPoint2D p1, GJPoint2D p2) {
		GJPoint2D center = GJPoint2D.midPoint(p1, p2);
		double radius = p1.distance(p2)/2;
		
		double angle1 = GJAngle2D.horizontalAngle(center, p1);
		double angle2 = GJAngle2D.horizontalAngle(center, p2);
		return new GJCircleArc2D(center, radius, angle1, angle2, true);
	}

}

/**
 * @author dlegland
 *
 */
public class GJSquareCapFactory implements GJCapFactory {

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.buffer.GJCapFactory#createCap(math.geom2d.GJPoint2D, math.geom2d.GJVector2D, double)
	 */
	public GJCirculinearContinuousCurve2D createCap(GJPoint2D center,
                                                    GJVector2D direction, double dist) {
		double theta = direction.angle();
		return createCap(center, theta, dist);
	}

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.buffer.GJCapFactory#createCap(math.geom2d.GJPoint2D, math.geom2d.GJPoint2D)
	 */
	public GJCirculinearContinuousCurve2D createCap(GJPoint2D p1, GJPoint2D p2) {
		GJPoint2D center = GJPoint2D.midPoint(p1, p2);
		double dist = GJPoint2D.distance(p1, p2)/2;
		double theta = GJAngle2D.horizontalAngle(p1, p2) - Math.PI/2;
		return createCap(center, theta, dist);
	}

	/**
	 * Portion of code shared by the two public methods.
	 */
	private GJPolyline2D createCap(GJPoint2D center, double theta, double dist) {
		GJPoint2D p1 = GJPoint2D.createPolar(center, dist, theta-Math.PI/2);
		GJPoint2D p4 = GJPoint2D.createPolar(center, dist, theta+Math.PI/2);
		GJPoint2D p2 = GJPoint2D.createPolar(p1, dist, theta);
		GJPoint2D p3 = GJPoint2D.createPolar(p4, dist, theta);
		return new GJPolyline2D(new GJPoint2D[]{p1, p2, p3, p4});
	}
}

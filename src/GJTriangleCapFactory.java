/**
 * File: 	GJTriangleCapFactory.java
 * Project: javageom-buffer
 * 
 * Distributed under the LGPL License.
 *
 * Created: 5 janv. 2011
 */









/**
 * @author dlegland
 *
 */
public class GJTriangleCapFactory implements GJCapFactory {

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.buffer.GJCapFactory#createCap(math.geom2d.GJPoint2D, math.geom2d.GJVector2D, double)
	 */
	public GJCirculinearContinuousCurve2D createCap(GJPoint2D center,
													GJVector2D direction, double dist) {
		double theta = direction.angle();
		GJPoint2D p1 = GJPoint2D.createPolar(center, dist, theta-Math.PI/2);
		GJPoint2D p2 = GJPoint2D.createPolar(center, dist, theta);
		GJPoint2D p3 = GJPoint2D.createPolar(center, dist, theta+Math.PI/2);
		return new GJPolyline2D(new GJPoint2D[]{p1, p2, p3});
	}

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.buffer.GJCapFactory#createCap(math.geom2d.GJPoint2D, math.geom2d.GJPoint2D)
	 */
	public GJCirculinearContinuousCurve2D createCap(GJPoint2D p1, GJPoint2D p2) {
		GJPoint2D mid = GJPoint2D.midPoint(p1, p2);
		double rho = GJPoint2D.distance(p1, p2)/2;
		double theta = GJAngle2D.horizontalAngle(p1, p2) - Math.PI/2;
		GJPoint2D pt = GJPoint2D.createPolar(mid, rho, theta);
		return new GJPolyline2D(new GJPoint2D[]{p1, pt, p2});
	}

}

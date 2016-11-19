/**
 * File: 	GJMiterJoinFactory.java
 * Project: javageom
 * 
 * Distributed under the LGPL License.
 *
 * Created: 5 janv. 2011
 */


import static java.lang.Math.PI;








/**
 * @author dlegland
 *
 */
public class GJMiterJoinFactory implements GJJoinFactory {

	private double minDenom = 1e-100;
	
	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.buffer.GJJoinFactory#createJoin(math.geom2d.circulinear.GJCirculinearElement2D, math.geom2d.circulinear.GJCirculinearElement2D, double)
	 */
	public GJCirculinearContinuousCurve2D createJoin(GJCirculinearElement2D curve1,
													 GJCirculinearElement2D curve2, double dist) {
		
		// extremity of each curve
		GJPoint2D pc1 = curve1.lastPoint();
		GJPoint2D pc2 = curve2.firstPoint();
		
		// Compute tangent angle of each curve 
		GJVector2D vect1 = curve1.tangent(curve1.t1());
		GJVector2D vect2 = curve2.tangent(curve2.t0());
		double theta1 = vect1.angle();
		double theta2 = vect2.angle();
//		System.out.println(Math.toDegrees(theta1) + " " + Math.toDegrees(theta2));
		
//		// compute center point
		GJPoint2D center = GJPoint2D.midPoint(pc1, pc2);
		
		// Extremities of parallels
		GJPoint2D p1 = GJPoint2D.createPolar(center, dist, theta1 - PI / 2);
		GJPoint2D p2 = GJPoint2D.createPolar(center, dist, theta2 - PI / 2);
		
		double dtheta = GJAngle2D.formatAngle(theta2 - theta1);
		if (dtheta > PI)
			dtheta = dtheta - 2*PI;
		
		double denom = Math.cos(dtheta / 2);
		
		denom = Math.max(denom, this.minDenom);
		
		double hypot = dist / denom;
		double angle;
		if (dtheta > 0 ^ dist < 0) {
			// Creates a right-angle corner between the two parallels
			angle = theta1 - Math.PI / 2 + dtheta / 2;
			GJPoint2D pt = GJPoint2D.createPolar(center, hypot, angle);
			return new GJPolyline2D(new GJPoint2D[]{p1, pt, p2});
			
		} else {
			// return a direct connection between extremities
			return new GJPolyline2D(new GJPoint2D[]{pc1, pc2});
		}
			
	}
}

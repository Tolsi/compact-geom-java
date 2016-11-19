/**
 * File: 	GJButtCapFactory.java
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
public class GJButtCapFactory implements GJCapFactory {

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.buffer.GJCapFactory#createCap(math.geom2d.GJPoint2D, math.geom2d.GJVector2D, double)
	 */
	public GJCirculinearContinuousCurve2D createCap(GJPoint2D center,
                                                    GJVector2D direction, double dist) {
		double theta = direction.angle();
		GJPoint2D p1 = GJPoint2D.createPolar(center, dist/2, theta-Math.PI/2);
		GJPoint2D p2 = GJPoint2D.createPolar(center, dist/2, theta+Math.PI/2);
		return new GJLineSegment2D(p1, p2);
	}

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.buffer.GJCapFactory#createCap(math.geom2d.GJPoint2D, math.geom2d.GJPoint2D)
	 */
	public GJCirculinearContinuousCurve2D createCap(GJPoint2D p1, GJPoint2D p2) {
		return new GJLineSegment2D(p1, p2);
	}

}

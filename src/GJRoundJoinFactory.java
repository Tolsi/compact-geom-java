/**
 * File: 	CircularJoinFactory.java
 * Project: javageom-buffer
 * 
 * Distributed under the LGPL License.
 *
 * Created: 4 janv. 2011
 */


import static java.lang.Math.PI;


/**
 * @author dlegland
 *
 */
public class GJRoundJoinFactory implements GJJoinFactory {

	/**
	 * Creates a join between the parallels of two curves at the specified
	 * distance.
	 * The first point of curve2 is assumed to be the last point of curve1.
	 */
	public GJCirculinearContinuousCurve2D createJoin(
			GJCirculinearElement2D curve1,
			GJCirculinearElement2D curve2, double dist) {
		
		// center of circle arc
		GJPoint2D center = curve2.firstPoint();
		GJCurves2D.JunctionType junctionType =
			GJCurves2D.getJunctionType(curve1, curve2);
		
		// compute tangents to each portion
		GJVector2D direction1 = curve1.tangent(curve1.t1());
		GJVector2D direction2 = curve2.tangent(curve2.t0());

		// angle of each edge
		double angle1 = direction1.angle();
		double angle2 = direction2.angle();
				
		// Special cases of direct join between the two parallels
		if ((dist > 0 && junctionType == GJCurves2D.JunctionType.REENTRANT) || (dist <= 0 && junctionType == GJCurves2D.JunctionType.SALIENT)) {
			GJPoint2D p1 = GJPoint2D.createPolar(center, dist, angle1 - PI/2);
			GJPoint2D p2 = GJPoint2D.createPolar(center, dist, angle2 - PI/2);
			return new GJLineSegment2D(p1, p2);
		}
		
		// compute angles
		double startAngle, endAngle;
		if (dist > 0) {
			startAngle = angle1 - PI/2;
			endAngle = angle2 - PI/2;
		} else {
			startAngle = angle1 + PI/2;
			endAngle = angle2 + PI/2;
		}
		
		// format angles to stay between 0 and 2*PI
		startAngle = GJAngle2D.formatAngle(startAngle);
		endAngle = GJAngle2D.formatAngle(endAngle);
		
		// If the angle difference is too small, we consider the two curves
		// touch at their extremities
		if (junctionType == GJCurves2D.JunctionType.FLAT)
			return new GJCircleArc2D(center, Math.abs(dist), startAngle, 0);
		
		// otherwise add a circle arc to the polycurve
		return new GJCircleArc2D(
				center, Math.abs(dist), startAngle, endAngle, dist > 0);
	}
}

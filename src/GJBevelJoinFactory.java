/**
 * File: 	GJBevelJoinFactory.java
 * Project: javageom
 * 
 * Distributed under the LGPL License.
 *
 * Created: 5 janv. 2011
 */







/**
 * @author dlegland
 *
 */
public class GJBevelJoinFactory implements GJJoinFactory {

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.buffer.GJJoinFactory#createJoin(math.geom2d.circulinear.GJCirculinearElement2D, math.geom2d.circulinear.GJCirculinearElement2D, double)
	 */
	public GJLineSegment2D createJoin(GJCirculinearElement2D curve1,
									  GJCirculinearElement2D curve2, double dist) {
		GJPoint2D p1 = curve1.lastPoint();
		GJPoint2D p2 = curve2.firstPoint();
		return new GJLineSegment2D(p1, p2);
	}
}

/**
 * File: 	GJJoinFactory.java
 * Project: javageom-buffer
 * 
 * Distributed under the LGPL License.
 *
 * Created: 4 janv. 2011
 */






/**
 * Generate a join between two consecutive parallel curves.
 * @author dlegland
 *
 */
public interface GJJoinFactory {

	/**
	 * Creates a join between the parallels of two curves at the specified
	 * distance.
	 * The first point of curve2 is assumed to be the last point of curve1.
	 */
	public GJCirculinearContinuousCurve2D createJoin(
			GJCirculinearElement2D previous,
			GJCirculinearElement2D next, double dist);
}

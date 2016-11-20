/**
 * CirculinearShape comprises shapes which can be described from lines (or
 * linear shapes) and circles (or circle arcs). They allow several operations:
 * the computation of buffer (set of points located less than a given
 * distance), and transformation using a circle inversion.
 * @author dlegland
 *
 */
public interface GJCirculinearShape2D extends GJShape2D {

	/**
	 * Computes the buffer of the shape, formed by the set of points located
	 * at a distance from the shape that is lower or equal to d.
	 * @param dist the maximal distance between a point of the buffer and the
	 * 		shape
	 * @return the buffer of the shape
	 */
	public GJCirculinearDomain2D buffer(double dist);
	
	/**
	 * Transforms the shape by a circle inversion. The result is still an
	 * instance a GJCirculinearShape2D.
	 * @param inv the circle inversion
	 * @return the transformed shape
	 */
	public GJCirculinearShape2D transform(GJCircleInversion2D inv);
}

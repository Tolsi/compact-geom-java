/**
 * Grouping interface for all objects operating on Euclidean plane. This
 * includes shapes, boxes, transforms, vectors...
 * @author dlegland
 *
 */
public interface GJGeometricObject2D {

	/**
	 * Checks if the two objects are similar up to a given threshold value.
	 * This method can be used to compare the results of geometric
	 * computations, that introduce errors due to numerical computations.
	 *
	 * @param obj the object to compare
	 * @param eps a threshold value, for example the minimal coordinate difference
	 * @return true if both object have the same value up to the threshold
	 */
	public boolean almostEquals(GJGeometricObject2D obj, double eps);
}

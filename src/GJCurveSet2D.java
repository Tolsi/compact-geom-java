import java.util.Collection;


/**
 * <p>
 * A parameterized set of curves. A curve cannot be included twice in a
 * GJCurveSet2D.
 * </p>
 * 
 * @author Legland
 */
public interface GJCurveSet2D<T extends GJCurve2D>
extends GJCurve2D, GJShapeSet2D<T> {

    /**
     * Checks if the curve set contains the given curve.
     */
    public boolean contains(T curve) ;
  
    /**
     * Returns the collection of curves
     * 
     * @return the inner collection of curves
     */
	public Collection<T> curves() ;

    /**
     * Returns the inner curve corresponding to the given index.
     * 
     * @param index index of the curve
     * @return the i-th inner curve
     * @since 0.6.3
     */
	public T get(int index) ;

    /**
     * Returns the child curve corresponding to a given position.
     * 
     * @param t the position on the set of curves, between 0 and twice the
     *            number of curves
     * @return the curve corresponding to the position.
     * @since 0.6.3
     */
    public T childCurve(double t) ;

    /**
     * Returns the first curve of the collection if it exists, null otherwise.
     * 
     * @return the first curve of the collection
     */
    public T firstCurve() ;

    /**
     * Returns the last curve of the collection if it exists, null otherwise.
     * 
     * @return the last curve of the collection
     */
    public T lastCurve();
   
    /**
     * Converts the position on the curve set, which is comprised between 0 and
     * 2*Nc-1 with Nc being the number of curves, to the position on the curve
     * which contains the position. The result is comprised between the t0 and
     * the t1 of the child curve.
     * 
     * @see #globalPosition(int, double)
     * @see #curveIndex(double)
     * @param t the position on the curve set
     * @return the position on the subcurve
     */
    public double localPosition(double t);

    /**
     * Converts a position on a curve (between t0 and t1 of the curve) to the
     * position on the curve set (between 0 and 2*Nc-1).
     * 
     * @see #localPosition(double)
     * @see #curveIndex(double)
     * @param i the index of the curve to consider
     * @param t the position on the curve
     * @return the position on the curve set, between 0 and 2*Nc-1
     */
    public double globalPosition(int i, double t);

    /**
     * Returns the index of the curve corresponding to a given position.
     * 
     * @param t the position on the set of curves, between 0 and twice the
     *            number of curves minus 1
     * @return the index of the curve which contains position t
     */
    public int curveIndex(double t);

    
    // ===================================================================
    // add some class casts
    
    /**
     * Transforms each curve in the set and returns a new instance of 
     * GJCurveSet2D.
     */
    public GJCurveSet2D<? extends GJCurve2D> transform(GJAffineTransform2D trans);
}

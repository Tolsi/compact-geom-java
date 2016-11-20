import java.util.Collection;


/**
 * A set of points. All points within the set are instances of GJPoint2D.
 * The most direct implementation of GJPointSet2D is GJPointArray2D.
 * @author dlegland
 *
 */
public interface GJPointSet2D extends GJPointShape2D, GJShapeSet2D<GJPoint2D> {

    /**
     * Adds a new point to the point set. If point is not an instance of
     * GJPoint2D, a GJPoint2D with same location is added instead of point.
     * 
     * @param point the initial point in the set
     */
    public boolean add(GJPoint2D point);

    /**
     * Add a series of points
     * 
     * @param points an array of points
     */
    public void addAll(Collection<? extends GJPoint2D> points);

    /**
     * Returns the collection of points contained in this set.
     * 
     * @return the collection of points
     */
    public Collection<GJPoint2D> points();

    /**
     * Returns the number of points in the set.
     * 
     * @return the number of points
     */
    public int size();
    
    /**
     * Transforms the point set by returning a new point set containing each 
     * transformed point.
     */
    public abstract GJPointSet2D transform(GJAffineTransform2D trans);
    
    /**
     * Returns a new point set containing only points located within the box.
     */
    public abstract GJPointSet2D clip(GJBox2D box);
}

/**
 * File: 	GJPointShape2D.java
 * Project: javaGeom
 * 
 * Distributed under the LGPL License.
 *
 * Created: 6 f�vr. 09
 */


import java.util.Collection;







/**
 * Interface for shapes composed of a finite set of points. Single points
 * should also implements this interface. Implementations of this interface
 * can contains duplicate points.
 * @author dlegland
 *
 */
public interface GJPointShape2D extends GJCirculinearShape2D, Iterable<GJPoint2D> {

    /**
     * Returns the points in the shape as a collection.
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
     * Transforms the point shape by an affine transform. 
     * The result is an instance of GJPointShape2D.
     */
    public abstract GJPointShape2D transform(GJAffineTransform2D trans);

    /**
     * When a GJPointShape2D is clipped, the result is still a GJPointShape2D.
     */
    public abstract GJPointShape2D clip(GJBox2D box);
}

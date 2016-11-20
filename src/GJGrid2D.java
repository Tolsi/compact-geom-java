import java.util.Collection;


/**
 * Defines a grid for snapping mouse pointer. The main purpose of a grid is to
 * find the closest vertex to a given point. It also provides methods for
 * accessing the collection of vertices and edges visible in a GJBox2D.
 * 
 * @author dlegland
 */
public interface GJGrid2D {

	public GJPoint2D getOrigin();
	
    public GJPointSet2D getVertices(GJBox2D box);

    public Collection<GJLineSegment2D> getEdges(GJBox2D box);

    public GJPoint2D getClosestVertex(GJPoint2D point);
}

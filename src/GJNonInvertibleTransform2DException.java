/**
 * Exception thrown when trying to compute an inverse transform of a transform
 * that does not allows this feature.
 * @author dlegland
 */
public class GJNonInvertibleTransform2DException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    protected GJTransform2D transform;
    
    public GJNonInvertibleTransform2DException() {
    	this.transform = null;
    }
    
    public GJNonInvertibleTransform2DException(GJTransform2D transform) {
    	this.transform = transform;
    }
    
    public GJTransform2D getTransform() {
    	return transform;
    }
}

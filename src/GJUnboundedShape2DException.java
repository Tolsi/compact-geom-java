/**
 * Exception thrown when an unbounded shape is involved in an operation
 * that assumes a bounded shape. 
 * @author dlegland
 */
public class GJUnboundedShape2DException extends RuntimeException {

	private GJShape2D shape;
	
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public GJUnboundedShape2DException(GJShape2D shape) {
    	this.shape = shape;
    }

    public GJShape2D getShape() {
    	return shape;
    }
}

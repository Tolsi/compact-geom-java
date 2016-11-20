/**
 * Exception thrown when an unbounded box  is involved in an operation
 * that assumes a bounded box. 
 * @see GJBox2D
 * @author dlegland
 */
public class GJUnboundedBox2DException extends RuntimeException {

	private GJBox2D box;
	
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    
    public GJUnboundedBox2DException(GJBox2D box) {
    	this.box = box;
    }

    public GJBox2D getBox() {
    	return box;
    }
}

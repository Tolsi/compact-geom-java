/**
 * File: 	GJDegeneratedLine2DException.java
 * Project: javaGeom
 * 
 * Distributed under the LGPL License.
 *
 * Created: 19 ao�t 2010
 */



/**
 * A degenerated line, whose direction vector is undefined, had been
 * encountered.
 * This kind of exception can occur during polygon or polylines algorithms,
 * when polygons have multiple vertices. 
 * @author dlegland
 * @since 0.9.0
 */
public class GJDegeneratedLine2DException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected GJLinearShape2D line;
	
	/**
	 * @param msg the error message
	 * @param line the degenerated line
	 */
	public GJDegeneratedLine2DException(String msg, GJLinearShape2D line) {
		super(msg);
		this.line = line;
	}
	
	/**
	 * @param line the degenerated line
	 */
	public GJDegeneratedLine2DException(GJLinearShape2D line) {
		super();
		this.line = line;
	}

	public GJLinearShape2D getLine() {
		return line;
	}
}

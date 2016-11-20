/**
 * Exception thrown when the assumption of non colinearity is not respected.
 * Methods are provided by retrieving the three incriminated points.
 * @author dlegland
 *
 */
public class GJColinearPoints2DException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected GJPoint2D p1;
	protected GJPoint2D p2;
	protected GJPoint2D p3;
	
	public GJColinearPoints2DException(GJPoint2D p1, GJPoint2D p2, GJPoint2D p3) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
	}
	
	public GJPoint2D getP1() {
		return p1;
	}
	
	public GJPoint2D getP2() {
		return p2;
	}
	
	public GJPoint2D getP3() {
		return p3;
	}
}

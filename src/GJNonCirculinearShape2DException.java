/**
 * @author dlegland
 *
 */
public class GJNonCirculinearShape2DException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Object object;
	
	public GJNonCirculinearShape2DException(Object obj) {
		this.object = obj;
	}
	
	public Object getObject() {
		return object;
	}
}

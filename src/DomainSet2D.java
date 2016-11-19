/**
 * File: 	DomainSet2D.java
 * Project: javaGeom
 * 
 * Distributed under the LGPL License.
 *
 * Created: 17 ao�t 10
 */





/**
 * General interface for a set of domains, that is itself a domain.
 * @author dlegland
 *
 */
public interface DomainSet2D<T extends Domain2D> 
extends ShapeSet2D<T>, Domain2D {

}

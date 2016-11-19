/* file : ContinuousBoundary2D.java
 * 
 * Project : geometry
 *
 * ===========================================
 * 
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or (at
 * your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY, without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. if not, write to :
 * The Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 * 
 * Created on 25 d�c. 2006
 *
 */








/**
 * Defines a part of the boundary of a planar domain. A ContinuousBoundary2D is
 * a continuous, oriented and non self-intersecting curve.
 * 
 * @author dlegland
 */
public interface GJContinuousOrientedCurve2D extends GJContinuousCurve2D,
        GJOrientedCurve2D {

    public abstract GJContinuousOrientedCurve2D reverse();

    public abstract GJContinuousOrientedCurve2D subCurve(double t0, double t1);

    public abstract GJContinuousOrientedCurve2D transform(GJAffineTransform2D trans);

    public abstract GJCurveSet2D<? extends GJContinuousOrientedCurve2D> clip(
            GJBox2D box);
}

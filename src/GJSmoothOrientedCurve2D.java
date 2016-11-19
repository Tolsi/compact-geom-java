/* File SmoothOrientedCurve2D.java 
 *
 * Project : javaGeom
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
 * Created 24 janv. 08
 */








/**
 * Interface for smooth and oriented curves. The aim of this interface is mainly
 * to specify refinement of method declarations.
 */
public interface GJSmoothOrientedCurve2D extends GJSmoothCurve2D,
        GJContinuousOrientedCurve2D {

    public abstract GJSmoothOrientedCurve2D reverse();

    public abstract GJSmoothOrientedCurve2D subCurve(double t0, double t1);

    public abstract GJCurveSet2D<? extends GJSmoothOrientedCurve2D> clip(GJBox2D box);

    public abstract GJSmoothOrientedCurve2D transform(GJAffineTransform2D trans);
}

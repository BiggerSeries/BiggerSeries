/*
 * The MIT License
 *
 * Copyright (c) 2017-2020 JOML
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.roguelogix.phosphophyllite.repack.org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Represents a 2D circle using single-precision floating-point numbers.
 * 
 * @author Kai Burjack
 */
public class Circlef implements Externalizable {

    /**
     * The x coordiante of the circle's center.
     */
    public float x;
    
    /**
     * The y coordiante of the circle's center.
     */
    public float y;
    
    /**
     * The radius of the circle.
     */
    public float r;

    /**
     * Create a new {@link Circlef} with center position <code>(0, 0, 0)</code> and radius <code>0</code>.
     */
    public Circlef() {
    }

    /**
     * Create a new {@link Circlef} as a copy of the given <code>source</code>.
     * 
     * @param source
     *          the {@link Circlef} to copy from
     */
    public Circlef(Circlef source) {
        this.x = source.x;
        this.y = source.y;
        this.r = source.r;
    }

    /**
     * Create a new {@link Circlef} with center position <code>(x, y)</code> and radius <code>r</code>.
     * 
     * @param x
     *          the x coordinate of the circle's center
     * @param y
     *          the y coordinate of the circle's center
     * @param r
     *          the radius of the circle
     */
    public Circlef(float x, float y, float r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    /**
     * Translate <code>this</code> by the given vector <code>xy</code>.
     * 
     * @param xy
     *          the vector to translate by
     * @return this
     */
    public Circlef translate(Vector2fc xy) {
        return translate(xy.x(), xy.y(), this);
    }

    /**
     * Translate <code>this</code> by the given vector <code>xy</code> and store the result in <code>dest</code>.
     * 
     * @param xy
     *          the vector to translate by
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Circlef translate(Vector2fc xy, Circlef dest) {
        return translate(xy.x(), xy.y(), dest);
    }

    /**
     * Translate <code>this</code> by the vector <code>(x, y)</code>.
     * 
     * @param x
     *          the x coordinate to translate by
     * @param y
     *          the y coordinate to translate by
     * @return this
     */
    public Circlef translate(float x, float y) {
        return translate(x, y, this);
    }

    /**
     * Translate <code>this</code> by the vector <code>(x, y)</code> and store the result in <code>dest</code>.
     * 
     * @param x
     *          the x coordinate to translate by
     * @param y
     *          the y coordinate to translate by
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Circlef translate(float x, float y, Circlef dest) {
        dest.x = this.x + x;
        dest.y = this.y + y;
        return dest;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(r);
        result = prime * result + Float.floatToIntBits(x);
        result = prime * result + Float.floatToIntBits(y);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Circlef other = (Circlef) obj;
        if (Float.floatToIntBits(r) != Float.floatToIntBits(other.r))
            return false;
        if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
            return false;
        if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
            return false;
        return true;
    }

    /**
     * Return a string representation of this circle.
     * <p>
     * This method creates a new {@link DecimalFormat} on every invocation with the format string "<code>0.000E0;-</code>".
     * 
     * @return the string representation
     */
    public String toString() {
        return Runtime.formatNumbers(toString(Options.NUMBER_FORMAT));
    }

    /**
     * Return a string representation of this circle by formatting the vector components with the given {@link NumberFormat}.
     * 
     * @param formatter
     *          the {@link NumberFormat} used to format the components with
     * @return the string representation
     */
    public String toString(NumberFormat formatter) {
        return "(" + Runtime.format(x, formatter) + " " + Runtime.format(y, formatter) + " " + Runtime.format(r, formatter) + ")";
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeFloat(x);
        out.writeFloat(y);
        out.writeFloat(r);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        x = in.readFloat();
        y = in.readFloat();
        r = in.readFloat();
    }

}

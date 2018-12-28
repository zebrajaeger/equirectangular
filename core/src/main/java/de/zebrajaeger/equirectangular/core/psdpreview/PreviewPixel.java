package de.zebrajaeger.equirectangular.core.psdpreview;

/*-
 * #%L
 * de.zebrajaeger:equirectangular
 * %%
 * Copyright (C) 2016 - 2018 Lars Brandt
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

/**
 * this pixel implementation keeps the R,G,B parts (no alpha channel supported)
 * <p>
 * @author Lars Brandt on 14.05.2016.
 */
public class PreviewPixel {
    private PixelSection r = new PixelSection();
    private PixelSection g = new PixelSection();
    private PixelSection b = new PixelSection();

    public void addToR(int value) {
        r.addValue(value);
    }

    public void addToG(int value) {
        g.addValue(value);
    }

    public void addToB(int value) {
        b.addValue(value);
    }

    public PixelSection getR() {
        return r;
    }

    public PixelSection getG() {
        return g;
    }

    public PixelSection getB() {
        return b;
    }
}

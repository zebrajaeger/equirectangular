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
 * A color channel of a Pixel i.e. the Red-Channel
 * Accumulates the values and the count how often a value is added so we can later read the value of sum/count
 * <p>
 * @author Lars Brandt on 14.05.2016.
 */
public class PixelSection {
    private int value = 0;
    private int count = 0;

    public void addValue(int value) {
        this.value += value;
        ++count;
    }

    public int getValue() {
        return value;
    }

    public int getCount() {
        return count;
    }

    public int getNormalizedIntValue() {
        if (count == 0) return 0;
        else return value / count;
    }
}

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
 * @author Lars Brandt on 14.05.2016.
 */
public class ScaledPreviewData extends PreviewData {

    private int sourceWidth;
    private int sourceHeight;
    private double xFactor;
    private double yFactor;

    public ScaledPreviewData(int sourceWidth, int sourceHeight, int width, int height) {
        super(width, height);
        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
        xFactor = sourceWidth;
        xFactor /= width;
        yFactor = sourceHeight;
        yFactor /= height;
    }

    public int getSourceWidth() {
        return sourceWidth;
    }

    public int getSourceHeight() {
        return sourceHeight;
    }

    public void addToR(int sourceX, int sourceY, int value) {
        get((int) (((float) sourceX) / xFactor), (int) (((float) sourceY) / yFactor)).addToR(value);
    }

    public void addToG(int sourceX, int sourceY, int value) {
        get((int) (((float) sourceX) / xFactor), (int) (((float) sourceY) / yFactor)).addToG(value);
    }

    public void addToB(int sourceX, int sourceY, int value) {
        get((int) (((float) sourceX) / xFactor), (int) (((float) sourceY) / yFactor)).addToB(value);
    }
}

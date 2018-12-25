package de.zebrajaeger.panosnippet;

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


import de.zebrajaeger.common.FileUtils;
import de.zebrajaeger.psdimage.ReadablePsdImage;
import de.zebrajaeger.psdimage.autopano.GPanoData;

import java.io.File;
import java.io.IOException;

/**
 * a generator that creates a file with a view snippet for krpano view control
 * <p>
 * @author Lars Brandt on 15.05.2016.
 */
public class PanoSnippetGenerator {
    private static String CRLF = "\n";
    private static String INDENT = "    ";

    private File sourceFile;
    private File targetFile;

    public PanoSnippetGenerator(File sourceFile, File targetFile) {
        this.sourceFile = sourceFile;
        this.targetFile = targetFile;
    }

    public PanoSnippetGenerator(File sourceFile) {
        this.sourceFile = sourceFile;
        this.targetFile = FileUtils.replaceDotAndExtension(sourceFile, "_snippet.txt");
        this.targetFile = FileUtils.normalizeName(this.targetFile);
    }

    public ViewRange process() throws IOException {
        ReadablePsdImage source = new ReadablePsdImage(sourceFile);
        source.open();
        source.readHeader();
        source.close();

        GPanoData panoData = source.getGPanoData();

        // Width calculation
        int fullWidth = panoData.getFullPanoWidthPixels();
        double fovWidth = source.getWidth();
        fovWidth /= fullWidth;
        double fovLeft = fovWidth / 2;
        double fovRight = -(fovWidth - fovLeft);
        int marginLeft = (fullWidth - source.getWidth()) / 2;
        int marginRight = fullWidth - marginLeft - source.getWidth();

        // HeigthCalculatio
        int fullHeigth = fullWidth / 2;
        double fovHeight = source.getHeight();
        fovHeight /= fullHeigth;
        double fovHeightOffset = panoData.getCroppedAreaTopPixels();
        fovHeightOffset /= panoData.getFullPanoHeightPixels();
        double fovTop = ((double) 0.5) - fovHeightOffset;
        double fovBottom = -(fovHeight - fovTop);
        int marginTop = (int) (fullHeigth * fovHeightOffset);
        //int marginTop = (fullHeigth - source.getHeight()) / 2;
        int marginBottom = fullHeigth - source.getHeight() - marginTop;

        ViewRange viewRange = new ViewRange(fovLeft, fovRight, fovTop, fovBottom);
        String snippet = createSnippet(viewRange);

        FileUtils.storeInFile(targetFile, snippet);

        System.out.println(snippet);
        return viewRange;
    }

    protected String createSnippet(ViewRange range) {
        final StringBuilder sb = new StringBuilder();
        sb.append("<krpano version=\"1.18\" showerrors=\"false\">").append(CRLF);
        sb.append(CRLF);
        sb.append("<view").append(CRLF);
        sb.append(makeArg("limitview", "range"));

        sb.append(makeArg("hlookatmin", Double.toString(range.getFovLeft() * 360)));
        sb.append(makeArg("hlookatmax", Double.toString(range.getFovRight() * 360)));

        // TODO why it has to be negative? something is wrong but result is ok
        sb.append(makeArg("vlookatmin", Double.toString(-range.getFovTop() * 180)));
        sb.append(makeArg("vlookatmax", Double.toString(-range.getFovBottom() * 180)));

        sb.append(makeArg("hlookat", "0"));
        sb.append(makeArg("vlookat", "0"));
        sb.append(makeArg("maxpixelzoom", "2.0"));
        sb.append(makeArg("fovmax", "150"));
        sb.append("/>");
        return sb.toString();
    }

    private String makeArg(String name, String arg) {
        final StringBuilder sb = new StringBuilder();
        sb.append(INDENT);
        sb.append(name);
        sb.append("=\"");
        sb.append(arg);
        sb.append("\"");
        sb.append(CRLF);

        return sb.toString();
    }
}

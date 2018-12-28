package de.zebrajaeger.equirectangular.core.panosnippet;

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

import com.drew.imaging.ImageProcessingException;
import de.zebrajaeger.equirectangular.core.ViewCalculator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * a generator that creates a file with a view snippet for krpano view control
 * <p>
 *
 * @author Lars Brandt on 15.05.2016.
 */
public class PanoSnippetGenerator {
    private static String CRLF = "\n";
    private static String INDENT = "    ";

    private File sourceImage;
    private ViewCalculator viewData;
    private String snippet;
    private File targetFile;

    private PanoSnippetGenerator(File sourceImage, ViewCalculator viewData) {
        this.sourceImage = sourceImage;
        this.viewData = viewData;
    }

    public static PanoSnippetGenerator of(File sourceImage) throws IOException, ImageProcessingException {
        return new PanoSnippetGenerator(sourceImage, ViewCalculator.of(sourceImage));
    }

    public static PanoSnippetGenerator of(File sourceImage, ViewCalculator viewData) {
        return new PanoSnippetGenerator(sourceImage, viewData);
    }

    public PanoSnippetGenerator renderSippet() {
        snippet = generateSnippet(viewData);
        return this;
    }

    public PanoSnippetGenerator storeInFile() throws IOException {
        String targetFileName = FilenameUtils.removeExtension(sourceImage.getName() + "_snippet.txt");
        targetFile = new File(sourceImage.getParentFile(), targetFileName);
        FileUtils.write(targetFile, snippet, StandardCharsets.UTF_8);
        return this;
    }

    public PanoSnippetGenerator storeInFile(File targetFile) throws IOException {
        FileUtils.write(targetFile, snippet, StandardCharsets.UTF_8);
        return this;
    }

    public String getSnippet() {
        return snippet;
    }

    public File getTargetFile() {
        return targetFile;
    }

    protected String generateSnippet(ViewCalculator viewData) {
        final StringBuilder sb = new StringBuilder();
        sb.append("<krpano version=\"1.18\" showerrors=\"false\">").append(CRLF);
        sb.append(CRLF);
        sb.append("<view").append(CRLF);
        sb.append(makeArg("limitview", "range"));

        sb.append(makeArg("hlookatmin", Double.toString(viewData.getFovX1() )));
        sb.append(makeArg("hlookatmax", Double.toString(viewData.getFovX2() )));

        // TODO why it has to be negative? something is wrong but result is ok
        sb.append(makeArg("vlookatmin", Double.toString(-viewData.getFovY1() )));
        sb.append(makeArg("vlookatmax", Double.toString(-viewData.getFovY2() )));

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

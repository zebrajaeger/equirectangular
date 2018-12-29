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
import de.zebrajaeger.equirectangular.core.common.NoDataException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * a generator that creates a file with a view snippet for krpano view control
 * <p>
 *
 * @author Lars Brandt on 15.05.2016.
 */
public class PanoSnippetGenerator {
    private static String CRLF = "\n";
    private static String INDENT = "    ";

    private Optional<String> snippet = Optional.empty();

    private PanoSnippetGenerator() {
    }

    public static PanoSnippetGenerator of() {
        return new PanoSnippetGenerator();
    }

    public PanoSnippetGenerator renderSnippet(File sourceImage) throws NoDataException, IOException, ImageProcessingException {
        return renderSnippet(ViewCalculator.of(sourceImage));

    }

    public PanoSnippetGenerator renderSnippet(ViewCalculator viewData) throws NoDataException {
        return renderSnippet(viewData.createPanoView());
    }

    public PanoSnippetGenerator renderSnippet(Optional<ViewCalculator.PanoView> panoView) throws NoDataException {
        snippet = generateSnippet(panoView);
        return this;
    }

    public PanoSnippetGenerator storeInFile(File targetFile) throws IOException, NoDataException {
        if (snippet.isPresent()) {
            FileUtils.write(targetFile, snippet.get(), StandardCharsets.UTF_8);
        } else {
            throw new NoDataException();
        }
        return this;
    }

    public Optional<String> getSnippet() {
        return snippet;
    }

    private Optional<String> generateSnippet(Optional<ViewCalculator.PanoView> panoView) throws NoDataException {
        return panoView.map(v -> {
            final StringBuilder sb = new StringBuilder();
            sb.append("<krpano version=\"1.18\" showerrors=\"false\">").append(CRLF);
            sb.append(CRLF);
            sb.append("<view").append(CRLF);
            sb.append(makeArg("limitview", "range"));

            sb.append(makeArg("hlookatmin", Double.toString(180d - v.getFovX1())));
            sb.append(makeArg("hlookatmax", Double.toString(180d - v.getFovX2())));

            // TODO why it has to be negative? something is wrong but result is ok
            sb.append(makeArg("vlookatmin", Double.toString(90d - v.getFovY1())));
            sb.append(makeArg("vlookatmax", Double.toString(90d - v.getFovY2())));

            sb.append(makeArg("hlookat", "0"));
            sb.append(makeArg("vlookat", "0"));
            sb.append(makeArg("maxpixelzoom", "2.0"));
            sb.append(makeArg("fovmax", "150"));
            sb.append("/>");
            return Optional.of(sb.toString());
        }).orElseThrow(() -> new NoDataException("No PanoView available"));
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

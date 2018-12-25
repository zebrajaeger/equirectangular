package de.zebrajaeger.common;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lars Brandt on 22.05.2016.
 */
public class PanoNameUtils {
    public static String extractFirstImageName(String name) {
        Pattern pattern = Pattern.compile("result-\\(([\\w\\d_]+)-.*");
        Matcher matcher = pattern.matcher(name);
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        String name = "result-(IMG_0958-IMG_0979-22)-{d_S-143_14x19_61(9_12)}-{p_IMG_0958_IMG_0979-22_(2009-08-09)}_equirectangular";
        name = extractFirstImageName(name);
        System.out.println(name);
    }
}

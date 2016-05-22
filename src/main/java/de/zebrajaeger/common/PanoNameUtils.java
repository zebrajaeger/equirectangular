package de.zebrajaeger.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lars on 22.05.2016.
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

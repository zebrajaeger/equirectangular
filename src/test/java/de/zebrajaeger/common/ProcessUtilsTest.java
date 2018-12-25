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

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;

/**
 * @author Lars Brandt on 22.05.2016.
 */
public class ProcessUtilsTest {
    @Test
    public void testFirstLine() {
        String line = "Abbildname                     PID Sitzungsname       Sitz.-Nr. Speichernutzung";
        Matcher matcher = ProcessUtils.PROCESS_LINE_PATTERN.matcher(line);
        Assert.assertFalse(matcher.matches());
    }

    @Test
    public void testSecondLine() {
        String line = "========================= ======== ================ =========== ===============";
        Matcher matcher = ProcessUtils.PROCESS_LINE_PATTERN.matcher(line);
        Assert.assertFalse(matcher.matches());
    }

    @Test
    public void testSystemIdleProcessLine() {
        // "(.+)\\s+(\\d+)\\s+(\\w+)\\s+(\\d+)\\s+([\\d\\.]+)\\s+K"
        String line = "System Idle Process              0 Services                   0            24 K";
        Matcher matcher = ProcessUtils.PROCESS_LINE_PATTERN.matcher(line);
        Assert.assertTrue(matcher.matches());
        Assert.assertEquals("System Idle Process", matcher.group(1));
        Assert.assertEquals("0", matcher.group(2));
        Assert.assertEquals("Services", matcher.group(3));
        Assert.assertEquals("0", matcher.group(4));
        Assert.assertEquals("24", matcher.group(5));
    }

    @Test
    public void testGoogleCrashHandlerLine() {
        String line = "GoogleCrashHandler64.exe      3052 Services                   0           968 K";
        Matcher matcher = ProcessUtils.PROCESS_LINE_PATTERN.matcher(line);
        Assert.assertTrue(matcher.matches());
        Assert.assertEquals("GoogleCrashHandler64.exe", matcher.group(1));
        Assert.assertEquals("3052", matcher.group(2));
        Assert.assertEquals("Services", matcher.group(3));
        Assert.assertEquals("0", matcher.group(4));
        Assert.assertEquals("968", matcher.group(5));
    }

}

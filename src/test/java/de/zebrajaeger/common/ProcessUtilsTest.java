package de.zebrajaeger.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;

/**
 * Created by lars on 22.05.2016.
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
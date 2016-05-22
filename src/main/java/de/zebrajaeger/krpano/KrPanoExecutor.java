package de.zebrajaeger.krpano;

import de.zebrajaeger.common.ProcessUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by lars on 21.05.2016.
 */
public class KrPanoExecutor {
    private File krpanoPath;
    private File krpanoConfigFile;

    public KrPanoExecutor(File krpanoPath, File krpanoConfigFile) {
        this.krpanoPath = krpanoPath;
        this.krpanoConfigFile = krpanoConfigFile;
    }

    public void processImage(File path) throws IOException, InterruptedException {

        String cmd[] = new String[]{
                "cmd", "/c", "start",
                krpanoPath.toString(),
                "makepano",
                krpanoConfigFile.toString(),
                path.toString()
        };
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.directory(krpanoPath.getParentFile());
        pb = pb.redirectErrorStream(true);
        Process p = pb.start();

        p.waitFor();

        for (; ; ) {
            ProcessUtils.ProcessLine krpanotools = ProcessUtils.tasklist().findByName("krpanotools", false);
            if (krpanotools == null) {
                break;
            } else {
                Thread.sleep(1000);
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        //File krPano = new File("C:\\portableapps\\krpano-1.18.4\\!MAKE PANO (MULTIRES) droplet.bat");
        File krPano = new File("C:\\portableapps\\krpano-1.18.4\\krpanotools64.exe");
        File krpanoConfigFile = new File("C:\\portableapps\\krpano-1.18.4\\templates\\multires.config");
        File img = new File("R:\\TEMP\\test_equirectangular.psd");

        KrPanoExecutor krPanoExec = new KrPanoExecutor(krPano, krpanoConfigFile);
        krPanoExec.processImage(img);
    }
}

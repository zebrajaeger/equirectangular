package de.zebrajaeger.equirectangular.core.krpano;

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

import de.zebrajaeger.equirectangular.core.common.ProcessUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Due to the strange implementation of console output, this is only a starter that waits until
 * the process name does not appera in the system process list
 *
 * @author Lars Brandt on 21.05.2016.
 */
public class KrPanoExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(KrPanoExecutor.class);

    private File krPanoExe;
    private File krPanoConfig;

//    private boolean deleteIfPanoFolderExists = false;

    private long renderTimeout = 2 * 3600; // 2h
    private Type type = Type.SPHERE;
    private Double hFov = 360d;
    private Double vOffset;

    private Integer jpegquality = 82;
    private String jpegsubsamp = "422";
    private Boolean jpegoptimize = false;

    private String tilePath = "tiles/[c/]l%Al[_c]_%Av_%Ah.jpg";
    private String previewPath = "tiles/preview.jpg";
    private String xmlPath = "pano.xml";
    private String htmlPath = "index.html";

    private boolean addTestingServer = true;

    public static KrPanoExecutor of(Config config) {
        return of(config.getKrPanoExe(), config.getKrPanoConfig());
    }

    public static KrPanoExecutor of(File krPanoExe, File krPanoConfig) {
        return new KrPanoExecutor(krPanoExe, krPanoConfig);
    }

    private KrPanoExecutor(File krPanoExe, File krPanoConfig) {
        this.krPanoExe = krPanoExe;
        this.krPanoConfig = krPanoConfig;
    }

    public KrPanoExecutor renderTimeout(long renderTimeout) {
        this.renderTimeout = renderTimeout;
        return this;
    }

//    public KrPanoExecutor deleteIfPanoFolderExists(boolean deleteIfPanoFolderExists) {
//        this.deleteIfPanoFolderExists = deleteIfPanoFolderExists;
//        return this;
//    }

    public KrPanoExecutor hFov(Double hFov) {
        this.hFov = hFov;
        return this;
    }

    public KrPanoExecutor vOffset(Double vOffset) {
        this.vOffset = vOffset;
        return this;
    }

    public KrPanoExecutor type(Type type) {
        this.type = type;
        return this;
    }

    public KrPanoExecutor tilePath(String tilePath) {
        this.tilePath = tilePath;
        return this;
    }

    public KrPanoExecutor previewPath(String previewPath) {
        this.previewPath = previewPath;
        return this;
    }

    public KrPanoExecutor xmlPath(String xmlPath) {
        this.xmlPath = xmlPath;
        return this;
    }

    public KrPanoExecutor htmlPath(String htmlPath) {
        this.htmlPath = htmlPath;
        return this;
    }

    public KrPanoExecutor jpegquality(Integer jpegquality) {
        this.jpegquality = jpegquality;
        return this;
    }

    public KrPanoExecutor jpegsubsamp(String jpegsubsamp) {
        this.jpegsubsamp = jpegsubsamp;
        return this;
    }

    public KrPanoExecutor jpegoptimize(Boolean jpegoptimize) {
        this.jpegoptimize = jpegoptimize;
        return this;
    }

    public KrPanoExecutor addTestingServer(boolean addTestingServer) {
        this.addTestingServer = addTestingServer;
        return this;
    }

    public KrPanoExecutor renderImage(File imageFile, File targetFolder) throws IOException, InterruptedException {
        renderImage_(imageFile, targetFolder);
        return this;
    }

    private void renderImage_(File imageFile, File targetFolder) throws IOException, InterruptedException {
        if (!SystemUtils.IS_OS_WINDOWS) {
            throw new RuntimeException("Only Windows is supported");
        }

//        if(targetFolder.exists()){
//            if(deleteIfPanoFolderExists) {
//                LOG.info("pano directory already exist. Delete: '{}'", targetFolder.getAbsolutePath());
//                FileUtils.deleteDirectory(targetFolder);
//            }else{
//                LOG.info("pano directory already exist. Skip krpano execution: '{}'", targetFolder.getAbsolutePath());
//                return;
//            }
//        }
        targetFolder.mkdirs();

        List<String> cmdList = new LinkedList(Arrays.asList(new String[]{
                "cmd", "/c", "start",
                krPanoExe.getAbsolutePath(),
                "makepano",
                krPanoConfig.getAbsolutePath(),
                imageFile.getAbsolutePath(),
                "-html=true",
                "-html5=true",
                "-flash=false",
                "-filterbasename=false"
        }));

        //String panoDir = (directoryName != null) ? directoryName : FilenameUtils.removeExtension(imageFile.getName());
        String panoDir = targetFolder.getAbsolutePath() + "/";

        if (hFov != null) {
            cmdList.add("-hfov=" + hFov);
        }

        if (vOffset != null) {
            cmdList.add("-voffset=" + vOffset);
        }

        if (type != null) {
            cmdList.add("-panotype=" + type.getKrPanoCommandLineName());
        }

        if (jpegquality != null) {
            cmdList.add("-jpegquality=" + jpegquality);
        }

        if (jpegsubsamp != null) {
            cmdList.add("-jpegsubsamp=" + jpegsubsamp);
        }

        if (jpegoptimize != null) {
            cmdList.add("-jpegoptimize=" + jpegoptimize);
        }
        if (addTestingServer) {
            cmdList.add("-htmltemplate_additional_file=html/tour_testingserver.exe");
            cmdList.add("-htmltemplate_additional_file=html/tour_testingserver_macos+x");
        }

        if (tilePath != null) {
            cmdList.add("-tilepath=" + panoDir + tilePath);
        }

        if (previewPath != null) {
            cmdList.add("-previewpath=" + panoDir + previewPath);
        }

        if (xmlPath != null) {
            cmdList.add("-xmlpath=" + panoDir + xmlPath);
        }

        if (htmlPath != null) {
            cmdList.add("-htmlpath=" + panoDir + htmlPath);
        }

        LOG.info("exec: '{}'", cmdList.stream().map(part -> "\"" + part + "\"").collect(Collectors.joining(" ")));

        ProcessBuilder pb = new ProcessBuilder(cmdList);
        pb.directory(krPanoExe.getParentFile());
        pb.redirectErrorStream(true);
        Process p = pb.start();

        p.waitFor();

        // wait max 10 s for spawned process
        boolean startTimeout = true;
        for (int t = 0; t < 10; ++t) {
            ProcessUtils.ProcessLine krpanotools = ProcessUtils.tasklist().findByName("krpanotools", false);
            if (krpanotools != null) {
                LOG.info("KrPano process has been startetd");
                startTimeout = false;
                break;
            } else {
                Thread.sleep(1000);
            }
        }
        if (startTimeout) {
            LOG.warn("Timeout: Start KrPano process");
        }

        // wait until process has been gone
        boolean stopTimeout = true;
        for (long t = 0; t < renderTimeout; ++t) {
            ProcessUtils.ProcessLine krpanotools = ProcessUtils.tasklist().findByName("krpanotools", false);
            if (krpanotools == null) {
                LOG.info("KrPano process has stopped");
                stopTimeout = false;
                break;
            } else {
                Thread.sleep(1000);
            }
        }

        if (stopTimeout) {
            LOG.warn("Timeout: Stop KrPano process");
        }
    }

    public enum Type {
        AUTODETECT("autodetect "),
        FLAT("flat"),
        SPHERE("sphere"),
        CYLINDER("cylinder"),
        PARTIAL_SPHERE("partialsphere"),
        PARTIAL_CYLINDER("partialcylinder ");

        Type(String krPanoCommandLineName) {
            this.krPanoCommandLineName = krPanoCommandLineName;
        }

        private String krPanoCommandLineName;

        public String getKrPanoCommandLineName() {
            return krPanoCommandLineName;
        }
    }
}

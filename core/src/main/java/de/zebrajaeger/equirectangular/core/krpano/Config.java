package de.zebrajaeger.equirectangular.core.krpano;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private File krPanoExe;
    private File krPanoConfig;

    private Config() {
    }

    public static Config of(File propertiesFile) throws IOException {
        Properties props = new Properties();
        try (InputStream is = new FileInputStream(propertiesFile)) {
            props.load(is);
        }
        return of(new File(props.getProperty("krpano.exe")), new File(props.getProperty("krpano.config")));
    }

    public static Config of(File krPanoExe, File krPanoConfig) {
        Config result = new Config();
        result.krPanoExe = krPanoExe;
        result.krPanoConfig = krPanoConfig;
        return result;
    }

    public File getKrPanoExe() {
        return krPanoExe;
    }

    public File getKrPanoConfig() {
        return krPanoConfig;
    }
}

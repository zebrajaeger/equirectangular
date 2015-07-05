package de.zebrajaeger.equirectangular.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

public class ZJLogUtil {

  /**
   * change the loglevel of log4j2 for the root logger directly
   * 
   * @param level the new loglevel
   */
  public static void changeLogLevel(Level level) {
    final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    final Configuration config = ctx.getConfiguration();
    final LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
    loggerConfig.setLevel(level);
    ctx.updateLoggers();
  }
}

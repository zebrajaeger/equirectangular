package de.zebrajaeger.equirectangular;

import java.io.File;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;

/**
 * Wrapper for command line argument parser
 * 
 * @author Lars Brandt
 */
public class CLIArgs {
  public final static String OPT_HELP = "h";
  public final static String OPT_HELP_LONG = "help";

  public final static String OPT_TARGET_FILE = "o";
  public final static String OPT_TARGET_FILE_LONG = "out";

  public final static String OPT_DELETE_IF_EXISTS = "D";
  public final static String OPT_DELETE_IF_EXISTS_LONG = "delete";

  public final static String OPT_DRY_RUN = "r";
  public final static String OPT_DRY_RUN_LONG = "dry-run";

  public final static String OPT_TARGET_WIDTH = "w";
  public final static String OPT_TARGET_WIDTH_LONG = "target-width";

  public final static String OPT_TARGET_OFFSET_Y = "y";
  public final static String OPT_TARGET_OFFSET_Y_LONG = "offset-y";

  public final static String OPT_LOG_LEVEL = "l";
  public final static String OPT_LOG_LEVEL_LONG = "log-level";

  public final static String TARGET_FILE_POSTFIX = "_full";

  // public static enum LEVEL {
  // TRACE, DEBUG, INFO, WARN, ERROR
  // }

  private CommandLine line = null;
  private Options options;

  /**
   * Width of target image
   */
  private Double w;
  /**
   * Y-Offset (from middle) of source image in target image
   */
  private Double y = 0.0;

  /**
   * true means no file operation (except read) is performed
   */
  private boolean dryRun = false;

  /**
   * if target-file exists, delete otherwise create a non existing filename
   */
  private boolean deleteIfExists = false;

  /**
   * source image
   */
  private File source;

  /**
   * target image
   */
  private File target;
  private Level level;

  /**
   * only package visibility cause we use a builder to create it
   */
  protected CLIArgs() {}

  /**
   * Parses and validates the args against options
   * 
   * @param args command line args
   * @return true for execute application
   * @throws ParseException
   */
  protected boolean parse(String[] args) throws ParseException {
    final CommandLineParser parser = new DefaultParser();

    options = new Options();
    options.addOption(OPT_HELP, OPT_HELP_LONG, false, "show this help");

    options.addOption(OPT_TARGET_FILE, OPT_TARGET_FILE_LONG, true,
        "the target file. Without this option a file with a new filename is generated in the source-directory");
    options.addOption(OPT_DELETE_IF_EXISTS, OPT_DELETE_IF_EXISTS_LONG, false, "delete target file if exists");
    options.addOption(OPT_DRY_RUN, OPT_DRY_RUN_LONG, false, "make a dry run: no changing file calls will made");

    options.addOption(OPT_TARGET_WIDTH, OPT_TARGET_WIDTH_LONG, true, "the width of target-image in percent");
    options.addOption(OPT_TARGET_OFFSET_Y, OPT_TARGET_OFFSET_Y_LONG, true, "the y difference from middle in percent");

    options.addOption(OPT_LOG_LEVEL, OPT_LOG_LEVEL_LONG, true,
        "the log-level. One of TRACE, DEBUG, INFO(default), WARNING, ERROR");

    line = parser.parse(options, args);

    // LEVEL
    if (line.hasOption(OPT_LOG_LEVEL)) {
      try {
        level = Level.valueOf(line.getOptionValue(OPT_LOG_LEVEL));
      } catch (final IllegalArgumentException e) {
        final String msg =
            String.format("Level must be one of [TRACE|DEBUG|INFO|WARN|ERROR] but is '%s'",
                line.getOptionValue(OPT_LOG_LEVEL));
        throw new ParseException(msg);
      }
    }

    // DRY RUN
    if (line.hasOption(OPT_DRY_RUN)) {
      dryRun = true;
    }

    // DELETE IF EXIST
    if (line.hasOption(OPT_DELETE_IF_EXISTS)) {
      deleteIfExists = true;
    }

    // WIDTH
    if (line.hasOption(OPT_TARGET_WIDTH)) {
      this.w = parseDouble(OPT_TARGET_WIDTH);
    }

    // Y-OFFSET
    if (line.hasOption(OPT_TARGET_OFFSET_Y)) {
      this.y = parseDouble(OPT_TARGET_OFFSET_Y);
    }

    if (line.hasOption(OPT_TARGET_OFFSET_Y) && (w == null)) {
      final String msg = String.format("With the target-y-offset-option, the target-width-option is mandatory");
      throw new ParseException(msg);
    }

    // SOURCE-FILE
    final List<String> x = line.getArgList();

    if (x.isEmpty()) {
      throw new ParseException("a source file is needed");
    }

    if (x.size() > 1) {
      throw new ParseException("only one file please");
    } else {
      final File f = new File(x.get(0));
      if (!f.exists()) {
        final String msg = String.format("The file '%s' could not be found");
        throw new ParseException(msg);
      } else {
        source = f;
      }
    }

    // TARGE-FILE
    if (!line.hasOption(OPT_TARGET_FILE)) {
      chooseTargetFile();
    } else {
      target = new File(line.getOptionValue(OPT_TARGET_FILE));
    }
    return true;
  }

  /**
   * if no target file is given, compute one that not exists from input file name
   * 
   * @throws ParseException
   */
  protected void chooseTargetFile() throws ParseException {
    final String filename = source.getName();
    final int pos = filename.lastIndexOf('.');
    String name = filename;
    String ext = "";
    if (pos != -1) {
      ext = filename.substring(pos);
      name = filename.substring(0, pos);
    }

    for (int nr = -1; (target == null) || target.exists(); nr++) {
      final String n =
          (nr == -1) ? String.format("%s%s%s", name, TARGET_FILE_POSTFIX, ext) : String.format("%s%s_%04d%s", name,
              TARGET_FILE_POSTFIX, nr, ext);
      target = new File(source.getParent(), n);
    }
  }

  /**
   * Get an int-value-otion
   * 
   * @param name option name
   * @throws ParseException if value could not be parsed
   */
  protected Integer parseInt(char name) throws ParseException {
    final String val = line.getOptionValue(name);
    try {
      return Integer.parseInt(val);
    } catch (final NumberFormatException e) {
      final String msg =
          String.format("Argument '%s' must be a integer, but is '%s' and could not converted", name, val);
      throw new ParseException(msg);
    }
  }

  /**
   * Get an double-value-otion
   * 
   * @param oPT_TARGET_WIDTH2 option name
   * @throws ParseException if value could not be parsed
   */
  protected Double parseDouble(String name) throws ParseException {
    final String val = line.getOptionValue(name);
    try {
      return Double.parseDouble(val);
    } catch (final NumberFormatException e) {
      final String msg =
          String.format("Argument '%s' must be a double, but is '%s' and could not converted", name, val);
      throw new ParseException(msg);
    }
  }

  /**
   * print help to console
   */
  public void printHelp() {
    final HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("equirectangular", options);
  }


  public Double getW() {
    return this.w;
  }

  public Double getY() {
    return this.y;
  }

  public boolean isDryRun() {
    return this.dryRun;
  }

  public boolean isDeleteIfExists() {
    return this.deleteIfExists;
  }

  public File getSource() {
    return this.source;
  }

  public File getTarget() {
    return this.target;
  }

  public Level getLevel() {
    return this.level;
  }

  /**
   * the 'Factory' to create a instance
   * 
   * @author Lars Brandt
   */
  public static class Builder {
    public static CLIArgs build(String[] args) throws ParseException {
      final CLIArgs cliArgs = new CLIArgs();
      cliArgs.parse(args);
      return cliArgs;
    }
  }
}

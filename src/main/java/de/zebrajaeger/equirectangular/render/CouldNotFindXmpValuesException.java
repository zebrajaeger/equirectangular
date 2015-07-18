package de.zebrajaeger.equirectangular.render;

/**
 * Created by lars on 11.07.2015.
 */
public class CouldNotFindXmpValuesException extends Exception{

  public CouldNotFindXmpValuesException() {
  }

  public CouldNotFindXmpValuesException(String message) {
    super(message);
  }

  public CouldNotFindXmpValuesException(String message, Throwable cause) {
    super(message, cause);
  }

  public CouldNotFindXmpValuesException(Throwable cause) {
    super(cause);
  }

  public CouldNotFindXmpValuesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}

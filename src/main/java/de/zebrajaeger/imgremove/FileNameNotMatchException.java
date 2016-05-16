package de.zebrajaeger.imgremove;

/**
 * Created by lars on 05.05.2016.
 */
public class FileNameNotMatchException extends IllegalArgumentException {
    public FileNameNotMatchException() {
    }

    public FileNameNotMatchException(String s) {
        super(s);
    }

    public FileNameNotMatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileNameNotMatchException(Throwable cause) {
        super(cause);
    }
}

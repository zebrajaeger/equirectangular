package de.zebrajaeger.equirectangular.util;

public class ZJArrayUtils {
  public static byte[] copy(byte[] source) {
    if (source == null) {
      return null;
    }
    final byte[] result = new byte[source.length];
    System.arraycopy(source, 0, result, 0, source.length);
    return result;
  }
}

package de.zebrajaeger.equirectangular;

public class TestByteConverzation {
  public static void main(String[] args) {
    final int i = 127;
    final byte b = (byte) i;

    byte x = b;
    for (int n = 0; n < 8; ++n) {
      System.out.print(x & 1);
      x >>= 1;
    }

    final int j = b & 0xff;
    // System.out.println(j);

  }
}

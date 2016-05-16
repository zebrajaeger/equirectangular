package de.zebrajaeger.equirectangular.psd.readers;

import de.zebrajaeger.psdimage.DecoratedOutputStream;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by lars on 13.05.2016.
 */
public class TestDOS {

    @Test
    public void testByte() throws IOException {
        File f = new File("C:/temp/a-byte.bin");
        DecoratedOutputStream os = new DecoratedOutputStream(new FileOutputStream(f));
        os.writeByte(0x01);
        os.writeByte(0x80);
        os.writeByte(0xff);
    }

    @Test
    public void testUShort() throws IOException {
        File f = new File("C:/temp/a-ushort.bin");
        DecoratedOutputStream os = new DecoratedOutputStream(new FileOutputStream(f));
        os.writeUnsignedShort(0x01);
        os.writeUnsignedShort(0x80);
        os.writeUnsignedShort(0xff);
        os.writeUnsignedShort(0x0100);
        os.writeUnsignedShort(0xffff);
    }
}

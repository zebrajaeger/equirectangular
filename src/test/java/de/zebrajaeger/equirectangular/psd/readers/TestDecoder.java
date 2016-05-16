package de.zebrajaeger.equirectangular.psd.readers;

/**
 * Created by lars on 07.05.2016.
 */
public class TestDecoder {
/*    private File file;
    private FileInputStream is = null;

    public TestDecoder(File file) {
        this.file = file;
    }

    public void open() throws FileNotFoundException {
        if (is == null) {
            is = new FileInputStream(file);
        } else {
            throw new IllegalStateException("already opened");
        }
    }

    public void close() throws IOException {
        if (is == null) {
            throw new IllegalStateException("already closes");
        } else {
            try {
                is.close();
            } finally {
                is = null;
            }
        }
    }

    private byte[] readBytes(int length) throws IOException {
        byte[] buf = new byte[length];
        int count = is.read(buf);
        if (count < length) {
            byte[] res = new byte[count];
            System.arraycopy(buf, 0, res, 0, count);
            return res;
        } else {
            return buf;
        }
    }

    private int readByte() throws IOException {
        return is.read();
    }

    private int readShort() throws IOException {
        int i1 = is.read();
        int i2 = is.read();
        int res = ((0xff & i1) << 8) | i2;

        return res;
    }

    private long readInt() throws IOException {
        return ((((((
                (0x0ff & is.read()) << 8)
                | 0xff & is.read()) << 8)
                | 0xff & is.read()) << 8)
                | 0xff & is.read());
    }

    private long[] readInts(int length) throws IOException {
        long[] buf = new long[length];
        for (int i = 0; i < length; ++i) {
            buf[i] = (int) readInt();
        }
        return buf;
    }


    private long readLong() throws IOException {
        return ((((((((((((((
                (0x0ff & is.read()) << 8)
                | 0xff & is.read()) << 8)
                | 0xff & is.read()) << 8)
                | 0xff & is.read()) << 8)
                | 0xff & is.read()) << 8)
                | 0xff & is.read()) << 8)
                | 0xff & is.read()) << 8)
                | 0xff & is.read());
    }

    private String readString(int length) throws IOException {
        return new String(readBytes(length));
    }

    private long skipBytes(long length) throws IOException {
        return is.skip(length);
    }


    private boolean isPSB() {
        return version == 2;
    }

    private String id;
    private int version;
    private byte[] reserved;
    private int channels;
    private long h;
    private long w;
    private int depth;
    private int colorMode;
    private long colorDataSize;
    private long imgResourceSize;
    private long layerMaskSize;
    private int compression;
    private long[] compressionLineSizes;

    public void readHeader() throws IOException {
        id = readString(4);
        version = readShort();
        reserved = readBytes(6);
        channels = readShort();
        h = readInt();
        w = readInt();
        depth = readShort();
        colorMode = readShort();

        colorDataSize = readInt();
        skipBytes(colorDataSize);

        imgResourceSize = readInt();
        skipBytes(imgResourceSize);

        if (isPSB()) {
            layerMaskSize = readLong();
        } else {
            layerMaskSize = readInt();
        }

        compression = readShort();
    }

    public void readContent() throws IOException {
        if (compression == 1) {
            // RLE
            compressionLineSizes = readInts((int) (channels * h));
            PackBitsDecoder dec = new PackBitsDecoder();
            ByteBuffer buffer = ByteBuffer.allocate((int) w);
            int lines = (int) (h * channels);
            for (int i = 0; i < lines; ++i) {
                System.out.println("READ " + i + "/" + lines + "(" + is.available() + ")");
                buffer.clear();
                dec.decode(is, buffer);
            }
            System.out.println("DONE " + "(" + is.available() + ")");
        }
    }*/
/*
    public static void main(String[] args) throws IOException {
        String path = "R:\\!pano_neu2\\(1E6A0219-1E6A0236-18)-{d=S-114.37x19.74(-0.76)}-{p=TimmerdorferStrand1}_0000.psb";
        PsdImage img = new PsdImage(new File(path));
        img.open();
        img.readHeader();
        LineReader lineReader = img.getLineReader();
        ByteBuffer buffer = ByteBuffer.allocate(img.getWidth());

        int l = img.getHeigth() * img.getChannels();
        for(int i=0; i<l; ++i){
            lineReader.readLine(buffer);
            System.out.println(img.getInputStream().available());
        }

        img.close();
        //TestDecoder dec = new TestDecoder(new File(path));
    }
    */
}

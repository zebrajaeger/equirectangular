package de.zebrajaeger.psdimage;

import de.zebrajaeger.psdimage.autopano.GPanoData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * the resource block with panoramix data from autopano
 * <p>
 * Created by lars on 08.05.2016.
 */
public class ResourceBlock {
    private static Logger LOG = LogManager.getLogger(ResourceBlock.class);
    private transient byte[] signature = {'8', 'B', 'I', 'M'};
    private int uid;
    private PascalString name;
    private long size;
    private transient byte[] data;
    private Object decodedData = null;

    public long read(DecoratedInputStream is) throws IOException {
        long res = 0;

        this.signature = new byte[4];
        is.read(this.signature);
        res += 4;

        this.uid = is.readShort();
        res += 2;

        name = new PascalString();
        res += name.read(is);

        size = is.readInt();
        res += 4;

        data = new byte[(int) size];
        is.read(data);
        res += data.length;

        decodeData();
        return res;
    }

    protected void decodeData() {
        if (uid == 1058) {
            // EXIF
            // decodedData = new String(data, StandardCharsets.US_ASCII);
        } else if (uid == 1060) {
            // XMP see http://www.w3.org/RDF/Validator/
            decodedData = new String(data, StandardCharsets.US_ASCII);
            try {
                decodedData = GPanoData.Builder.buildFrombytes(data);
            } catch (final SAXException | IOException | ParserConfigurationException e) {
                LOG.error("could not parse XMP-FileUtils Data", e);
            }
        }
    }

    public Object getDecodedData() {
        return decodedData;
    }
}

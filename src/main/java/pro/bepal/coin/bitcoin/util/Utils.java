package pro.bepal.coin.bitcoin.util;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import pro.bepal.util.ByteUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Utils {
    /** The string that prefixes all text messages signed using Bitcoin keys. */
    public static final String BITCOIN_SIGNED_MESSAGE_HEADER = "Bitcoin Signed Message:\n";
    public static final byte[] BITCOIN_SIGNED_MESSAGE_HEADER_BYTES = BITCOIN_SIGNED_MESSAGE_HEADER.getBytes(Charsets.UTF_8);

    public static final Joiner SPACE_JOINER = Joiner.on(" ");

    /**
     * <p>Given a textual message, returns a byte buffer formatted as follows:</p>
     *
     * <tt><p>[24] "Bitcoin Signed Message:\n" [message.length as a varint] message</p></tt>
     */
    public static byte[] formatMessageForSigning(String message) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(BITCOIN_SIGNED_MESSAGE_HEADER_BYTES.length);
            bos.write(BITCOIN_SIGNED_MESSAGE_HEADER_BYTES);
            byte[] messageBytes = message.getBytes(Charsets.UTF_8);
            bos.write(ByteUtil.ivarToByte(messageBytes.length));
            bos.write(messageBytes);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);  // Cannot happen.
        }
    }
}

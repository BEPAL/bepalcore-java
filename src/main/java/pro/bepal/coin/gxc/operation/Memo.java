package pro.bepal.coin.gxc.operation;

import com.google.common.primitives.Bytes;
import org.spongycastle.util.encoders.Hex;
import org.json.me.JSONObject;
import pro.bepal.categories.ByteArrayData;
import pro.bepal.categories.SHAHash;
import pro.bepal.coin.gxc.Address;
import pro.bepal.coin.gxc.GXCSerializable;
import pro.bepal.core.gxc.GXCECKey;
import pro.bepal.crypto.AES;
import pro.bepal.util.BigIntUtil;

import java.math.BigInteger;
import java.util.Arrays;

public class Memo implements GXCSerializable {
    public static final String KEY_FROM = "from";
    public static final String KEY_TO = "to";
    public static final String KEY_NONCE = "nonce";
    public static final String KEY_MESSAGE = "message";

    public Address from;
    public Address to;
    public byte[] nonce;
    private byte[] enMessage;
    private byte[] message;

    /**
     * Empty Constructor
     */
    public Memo() {
        this.from = null;
        this.to = null;
        this.message = null;
    }

    public Memo(JSONObject json) {
        fromJson(json);
    }

    /**
     * Constructor used for private memos.
     *
     * @param from:  Address of sender
     * @param to:    Address of recipient.
     * @param nonce: Nonce used in the encryption.
     */
    public Memo(Address from, Address to, byte[] nonce) {
        this.from = from;
        this.to = to;
        this.nonce = nonce;
    }

    public Memo(GXCECKey from, GXCECKey to, byte[] nonce) {
        this.from = new Address(from);
        this.to = new Address(to);
        this.nonce = nonce;
    }

    @Override
    public byte[] toByte() {
        if (this.from == null || this.to == null) {
            return new byte[]{(byte) 0};
        } else if (this.enMessage == null) {
            return ByteArrayData.concat(new byte[]{1},
                    new byte[]{0},
                    new byte[]{0},
                    new byte[]{0},
                    new byte[]{(byte) this.message.length},
                    this.message);
        } else {
            return ByteArrayData.concat(new byte[]{1},
                    from.getKey().getPublicKey(),
                    to.getKey().getPublicKey(),
                    ByteArrayData.reverseBytes(nonce),
                    new byte[]{(byte) this.enMessage.length},
                    this.enMessage);
        }
    }

    private void fromJson(JSONObject obj) {
        try {
            from = new Address(obj.getString(KEY_FROM));
            to = new Address(obj.getString(KEY_TO));
            nonce = BigIntUtil.bigIntegerToBytesLE(new BigInteger(obj.getString(KEY_NONCE)), 8);
            enMessage = Hex.decode(obj.getString(KEY_MESSAGE));
        } catch (Exception ignored) {
        }
    }

    @Override
    public Object toJson() {
        JSONObject obj = new JSONObject();
        if (this.enMessage == null) {
            // Public memo
            obj.put(KEY_FROM, this.from.toString());
            obj.put(KEY_TO, this.to.toString());
            obj.put(KEY_NONCE, BigIntUtil.bytesToBigIntegerLE(nonce).toString());
            obj.put(KEY_MESSAGE, Hex.toHexString(this.message));
        } else {
            obj.put(KEY_FROM, this.from.toString());
            obj.put(KEY_TO, this.to.toString());
            obj.put(KEY_NONCE, BigIntUtil.bytesToBigIntegerLE(nonce).toString());
            obj.put(KEY_MESSAGE, Hex.toHexString(this.enMessage));
        }
        return obj;
    }

    public byte[] encryptMessage(GXCECKey key, byte[] message) {
        this.message = message;
        if (key == null) {
            return message;
        }
        enMessage = encryptMessage(key, to.getKey(), nonce, message);
        return enMessage;
    }

    public byte[] decryptMessage(GXCECKey key) {
        if (key == null) {
            return message;
        }
        message = decryptMessage(key, from.getKey(), nonce, enMessage);
        return message;
    }

    private static byte[] encryptMessage(GXCECKey senderKey, GXCECKey recipientKey, byte[] nonce, byte[] message) {
        // Getting nonce bytes
        String stringNonce = BigIntUtil.bytesToBigIntegerLE(nonce).toString();
        byte[] nonceBytes = stringNonce.getBytes();

        // Getting shared secret
        byte[] secret = recipientKey.getPubKeyPoint().multiply(senderKey.getPrivKey()).normalize().getXCoord().getEncoded();

        // SHA-512 of shared secret
        byte[] ss = SHAHash.sha2512(secret);

        byte[] seed = ByteArrayData.concat(nonceBytes, Hex.toHexString(ss).getBytes());

        // Calculating checksum
        byte[] sha256Msg = SHAHash.sha2256(message);
        byte[] checksum = ByteArrayData.copyOfRange(sha256Msg, 0, 4);

        // Concatenating checksum + message bytes
        byte[] msgFinal = Bytes.concat(checksum, message);

        // Applying encryption
        return encryptAES(msgFinal, seed);
    }

    private static byte[] decryptMessage(GXCECKey recipientKey, GXCECKey senderKey, byte[] nonce, byte[] message) {
        // Getting nonce bytes
        String stringNonce = BigIntUtil.bytesToBigIntegerLE(nonce).toString();
        byte[] nonceBytes = stringNonce.getBytes();

        // Getting shared secret
        byte[] secret = senderKey.getPubKeyPoint().multiply(recipientKey.getPrivKey()).normalize().getXCoord().getEncoded();

        // SHA-512 of shared secret
        byte[] ss = SHAHash.sha2512(secret);

        byte[] seed = Bytes.concat(nonceBytes, Hex.toHexString(ss).getBytes());

        // Applying decryption
        byte[] temp = decryptAES(message, seed);
        byte[] checksum = Arrays.copyOfRange(temp, 0, 4);
        byte[] decrypted = Arrays.copyOfRange(temp, 4, temp.length);
        byte[] checksumConfirmation = Arrays.copyOfRange(SHAHash.sha2256(decrypted), 0, 4);
        if (!Arrays.equals(checksum, checksumConfirmation)) {
            return null;
        }
        return decrypted;
    }

    private static byte[] encryptAES(byte[] input, byte[] key) {
        byte[] result = SHAHash.sha2512(key);
        byte[] ivBytes = ByteArrayData.copyOfRange(result, 32, 16);
        byte[] sksBytes = ByteArrayData.copyOfRange(result, 0, 32);
        return AES.encryptCBC(input, sksBytes, ivBytes);
    }

    private static byte[] decryptAES(byte[] input, byte[] key) {
        byte[] result = SHAHash.sha2512(key);
        byte[] ivBytes = ByteArrayData.copyOfRange(result, 32, 16);
        byte[] sksBytes = ByteArrayData.copyOfRange(result, 0, 32);
        return AES.decryptCBC(input, sksBytes, ivBytes);
    }
}

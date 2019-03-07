package pro.bepal.coin.gxc;

import pro.bepal.categories.Base58;
import pro.bepal.categories.ByteArrayData;
import pro.bepal.categories.SHAHash;
import pro.bepal.core.gxc.GXCECKey;
import pro.bepal.util.ByteUtil;

public class Address {

    public final static String PREFIX = "GXC";

    private GXCECKey key;

    public Address(GXCECKey key) {
        this.key = key;
    }

    public Address(String pubKey) {
        this.key = GXCECKey.fromPublicKey(pubKey);
    }

    @Override
    public String toString() {
        return toAddress(key.getPublicKey());
    }

    public GXCECKey getKey() {
        return key;
    }

    public static String toAddress(byte[] pubKey) {
        byte[] pubhash = ByteArrayData.copyOfRange(SHAHash.ripemd160(pubKey), 0, 4);
        byte[] data = ByteArrayData.concat(pubKey, pubhash);
        return PREFIX + Base58.encode(data);
    }

    public static byte[] toPubKey(String base58Data) {
        String prefix = PREFIX;
        byte[] data = Base58.decode(base58Data.substring(prefix.length()));
        byte[] pub = ByteArrayData.copyOfRange(data, 0, 33);
        byte[] checksum = ByteArrayData.copyOfRange(data, 33, 4);
        byte[] r160 = SHAHash.ripemd160(pub);
        long checksumByCal = ByteUtil.bytesToInt(r160, 0);
        long checksumFromData = ByteUtil.bytesToInt(checksum, 0);
        if (checksumByCal != checksumFromData) {
            throw new IllegalArgumentException("Invalid format, checksum mismatch");
        }
        return pub;
    }
}

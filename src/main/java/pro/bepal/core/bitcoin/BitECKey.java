package pro.bepal.core.bitcoin;

import com.google.common.primitives.UnsignedBytes;
import pro.bepal.categories.Base58;
import pro.bepal.categories.ByteArrayData;
import pro.bepal.categories.SHAHash;
import pro.bepal.core.ECKey;
import pro.bepal.util.ErrorTool;

import java.util.Comparator;

public class BitECKey extends SecpECKey<BitECKey> {

    public byte[] getPubKeyHash() {
        return SHAHash.ripemd160(SHAHash.sha2256(getPublicKey()));
    }

    /**
     * Compares pub key bytes using {@link UnsignedBytes#lexicographicalComparator()}
     */
    public static final Comparator PUBKEY_COMPARATOR = new Comparator() {
        private Comparator<byte[]> comparator = UnsignedBytes.lexicographicalComparator();

        @Override
        public int compare(Object k1, Object k2) {
            return comparator.compare(((ECKey) k1).getPublicKey(), ((ECKey) k2).getPublicKey());
        }
    };

    public static BitECKey fromPrivateKey(byte[] key) {
        return new BitECKey().initWithPrvKey(key);
    }

    public static BitECKey fromPublicKey(byte[] key) {
        return new BitECKey().initWithPubKey(key);
    }

    public String getPrivateKeyAsWiF(int version) {
        byte[] bversion;
        if (version <= 255) {
            bversion = new byte[]{(byte) version};
        } else {
            bversion = new byte[]{(byte) (version >> 8 & 0xFF), (byte) (version & 0xFF)};
        }
        ErrorTool.checkArgument(privateKey.length == 32, "私钥长度错误");
        ByteArrayData data = new ByteArrayData();
        data.putBytes(bversion);
        data.putBytes(privateKey);
        data.appendByte(1);//表示公钥被压缩
        data.putBytes(SHAHash.hash2256Twice(data.toBytes()), 4);
        return Base58.encode(data.toBytes());
    }
}

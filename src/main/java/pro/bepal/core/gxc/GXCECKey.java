package pro.bepal.core.gxc;

import org.spongycastle.math.ec.ECPoint;
import pro.bepal.categories.Base58;
import pro.bepal.categories.ByteArrayData;
import pro.bepal.categories.SHAHash;
import pro.bepal.coin.gxc.Address;
import pro.bepal.core.bitcoin.SecpECKey;
import pro.bepal.util.ByteUtil;

import java.math.BigInteger;

public class GXCECKey extends SecpECKey<GXCECKey> {

    public static GXCECKey fromPrivateKey(byte[] key) {
        return new GXCECKey().initWithPrvKey(key);
    }

    public static GXCECKey fromPublicKey(byte[] key) {
        return new GXCECKey().initWithPubKey(key);
    }

    public static GXCECKey fromPublicKey(String base58Data) {
        return fromPublicKey(Address.toPubKey(base58Data));
    }

    public static GXCECKey fromPrivateKey(String key) {
        return new GXCECKey().initWithPrvKey(ByteArrayData.copyOfRange(Base58.decode(key), 1, 32));
    }

    public String toPubblicKeyString() {
        return Address.toAddress(publicKey);
    }

    public String toWif() {
        ByteArrayData data = new ByteArrayData();
        data.appendByte(0x80);
        data.putBytes(privateKey);
        data.putBytes(ByteArrayData.copyOfRange(SHAHash.hash2256Twice(data.toBytes()), 0, 4));
        return Base58.encode(data.toBytes());
    }

    public ECPoint getPubKeyPoint() {
        return CURVE.getCurve().decodePoint(publicKey);
    }

    public BigInteger getPrivKey() {
        return new BigInteger(1, privateKey);
    }
}

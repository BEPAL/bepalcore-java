package pro.bepal.core.gxc;

import pro.bepal.core.DeterministicKey;
import pro.bepal.core.ECKey;
import pro.bepal.core.bitcoin.SecpDeterministicKey;

public class GXCDeterministicKey extends SecpDeterministicKey<GXCDeterministicKey, GXCECKey> {

    public static GXCDeterministicKey createMasterPrivateKey(byte[] seed) {
        return new GXCDeterministicKey().initWithSeed(seed);
    }

    public static GXCDeterministicKey fromXPrivateKey(byte[] xprvkey) {
        return new GXCDeterministicKey().initWithPrvKey(xprvkey);
    }

    public static GXCDeterministicKey fromXPublicKey(byte[] xpubkey) {
        return new GXCDeterministicKey().initWithPubKey(xpubkey);
    }

    @Override
    protected ECKey getECKey() {
        return new GXCECKey().initWithKey(privateKey, publicKey);
    }

    @Override
    protected DeterministicKey getDeterministicKey(byte[] prvKey, byte[] pubKey, byte[] code) {
        return new GXCDeterministicKey().initWithPrv(prvKey, pubKey, code);
    }
}

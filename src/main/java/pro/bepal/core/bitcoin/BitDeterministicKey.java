package pro.bepal.core.bitcoin;

import pro.bepal.core.DeterministicKey;
import pro.bepal.core.ECKey;

/**
 * @author 10254
 */
public class BitDeterministicKey extends SecpDeterministicKey<BitDeterministicKey, BitECKey> {

    public static BitDeterministicKey createMasterPrivateKey(byte[] seed) {
        return new BitDeterministicKey().initWithSeed(seed);
    }

    public static BitDeterministicKey fromXPrivateKey(byte[] xprvkey) {
        return new BitDeterministicKey().initWithPrvKey(xprvkey);
    }

    public static BitDeterministicKey fromXPublicKey(byte[] xpubkey) {
        return new BitDeterministicKey().initWithPubKey(xpubkey);
    }

    @Override
    protected ECKey getECKey() {
        return new BitECKey().initWithKey(privateKey, publicKey);
    }

    @Override
    protected DeterministicKey getDeterministicKey(byte[] prvKey, byte[] pubKey, byte[] code) {
        return new BitDeterministicKey().initWithPrv(prvKey, pubKey, code);
    }
}

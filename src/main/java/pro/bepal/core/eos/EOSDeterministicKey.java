package pro.bepal.core.eos;

import pro.bepal.core.DeterministicKey;
import pro.bepal.core.ECKey;
import pro.bepal.core.bitcoin.SecpDeterministicKey;

public class EOSDeterministicKey extends SecpDeterministicKey<EOSDeterministicKey, EOSECKey> {

    public static EOSDeterministicKey createMasterPrivateKey(byte[] seed) {
        return new EOSDeterministicKey().initWithSeed(seed);
    }

    public static EOSDeterministicKey fromXPrivateKey(byte[] xprvkey) {
        return new EOSDeterministicKey().initWithPrvKey(xprvkey);
    }

    public static EOSDeterministicKey fromXPublicKey(byte[] xpubkey) {
        return new EOSDeterministicKey().initWithPubKey(xpubkey);
    }

    @Override
    protected ECKey getECKey() {
        return new EOSECKey().initWithKey(privateKey, publicKey);
    }

    @Override
    protected DeterministicKey getDeterministicKey(byte[] prvKey, byte[] pubKey, byte[] code) {
        return new EOSDeterministicKey().initWithPrv(prvKey, pubKey, code);
    }
}

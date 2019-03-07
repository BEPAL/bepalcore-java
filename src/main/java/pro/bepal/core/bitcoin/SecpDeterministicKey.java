package pro.bepal.core.bitcoin;

import pro.bepal.categories.ByteArrayData;
import pro.bepal.categories.SHAHash;
import pro.bepal.core.ChildNumber;
import pro.bepal.core.DeterministicKey;
import pro.bepal.core.ECKey;
import pro.bepal.util.BigIntUtil;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.math.ec.FixedPointCombMultiplier;
import pro.bepal.util.ErrorTool;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author 10254
 */
public abstract class SecpDeterministicKey<T, K> extends DeterministicKey<T, K> {

    @Override
    public T initWithSeed(byte[] seed) {
        byte[] masterPrvKey = SHAHash.hmac512("Bitcoin seed".getBytes(), seed);
        initWithPrv(ByteArrayData.copyOfRange(masterPrvKey, 0, 32), null, ByteArrayData.copyOfRange(masterPrvKey, 32, 32));
        return (T) this;
    }

    @Override
    protected T initWithPrv(byte[] prvKey, byte[] pubKey, byte[] code) {
        super.initWithPrv(prvKey, pubKey, code);
        if (privateKey != null && publicKey == null) {
            BigInteger privKey = new BigInteger(1, privateKey);
            if (privKey.bitLength() > BitECKey.CURVE.getN().bitLength()) {
                privKey = privKey.mod(BitECKey.CURVE.getN());
            }
            ECPoint ecPoint = new FixedPointCombMultiplier().multiply(BitECKey.CURVE.getG(), privKey);
            publicKey = ecPoint.getEncoded(true);
        }
        return (T) this;
    }

    @Override
    protected DeterministicKey privChild(ChildNumber childNumber) {
        ByteBuffer data = ByteBuffer.allocate(37);
        if (childNumber.hardened) {
            data.put((byte) 0);
            data.put(privateKey);
        } else {
            data.put(publicKey);
        }
        data.put(childNumber.getPath());
        byte[] i = SHAHash.hmac512(chainCode, data.array());
        ErrorTool.checkState(i.length == 64, i.length);
        byte[] il = Arrays.copyOfRange(i, 0, 32);
        byte[] chainCode = Arrays.copyOfRange(i, 32, 64);
        BigInteger ilInt = new BigInteger(1, il);
        ErrorTool.assertLessThanN(BitECKey.CURVE.getN(), ilInt, "Illegal derived key: I_L >= n");
        final BigInteger priv = new BigInteger(1, privateKey);
        BigInteger ki = priv.add(ilInt).mod(BitECKey.CURVE.getN());
        ErrorTool.assertNonZero(ki, "Illegal derived key: derived private key equals 0.");
        return getDeterministicKey(BigIntUtil.bigIntegerToBytesLE(ki, 32), null, chainCode);
    }

    @Override
    protected DeterministicKey pubChild(ChildNumber childNumber) {
        ErrorTool.checkArgument(!childNumber.hardened, "Can't use private derivation with public keys only.");
        ByteBuffer data = ByteBuffer.allocate(37);
        data.put(publicKey);
        data.put(childNumber.getPath());
        byte[] i = SHAHash.hmac512(chainCode, data.array());
        ErrorTool.checkState(i.length == 64, i.length);
        byte[] il = Arrays.copyOfRange(i, 0, 32);
        byte[] chainCode = Arrays.copyOfRange(i, 32, 64);
        BigInteger ilInt = new BigInteger(1, il);
        ErrorTool.assertLessThanN(BitECKey.CURVE.getN(), ilInt, "Illegal derived key: I_L >= n");

        ECPoint Ki = publicPointFromPrivate(ilInt).add(BitECKey.CURVE.getCurve().decodePoint(publicKey));
        return getDeterministicKey(null, Ki.getEncoded(true), chainCode);
    }

    /**
     * Returns public key point from the given private key. To convert a byte array into a BigInteger, use <tt>
     * new BigInteger(1, bytes);</tt>
     */
    private static ECPoint publicPointFromPrivate(BigInteger privKey) {
        /*
         * TODO: FixedPointCombMultiplier currently doesn't support scalars longer than the group order,
         * but that could change in future versions.
         */
        if (privKey.bitLength() > BitECKey.CURVE.getN().bitLength()) {
            privKey = privKey.mod(BitECKey.CURVE.getN());
        }
        return new FixedPointCombMultiplier().multiply(BitECKey.CURVE.getG(), privKey);
    }

    @Override
    public K toECKey() {
        return (K) getECKey();
    }

    @Override
    public int[] getKeyLength() {
        return new int[]{32, 33, 32};
    }

    protected abstract ECKey getECKey();

    protected abstract DeterministicKey getDeterministicKey(byte[] prvKey, byte[] pubKey, byte[] code);
}

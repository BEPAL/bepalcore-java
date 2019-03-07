package pro.bepal.core.bitcoin;

import pro.bepal.core.ECKey;
import pro.bepal.core.ECSign;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.asn1.x9.X9IntegerConverter;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.ec.CustomNamedCurves;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.crypto.signers.ECDSASigner;
import org.spongycastle.crypto.signers.HMacDSAKCalculator;
import org.spongycastle.math.ec.ECAlgorithms;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.math.ec.FixedPointCombMultiplier;
import org.spongycastle.math.ec.custom.sec.SecP256K1Curve;
import pro.bepal.util.ErrorTool;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * @author 10254
 */
public class SecpECKey<T> extends ECKey<T> {

    // The parameters of the secp256k1 curve that Bitcoin uses.
    private static final X9ECParameters CURVE_PARAMS = CustomNamedCurves.getByName("secp256k1");

    /**
     * The parameters of the secp256k1 curve that Bitcoin uses.
     */
    public static ECDomainParameters CURVE;
    protected static BigInteger HALF_CURVE_ORDER;

    static {
        CURVE = new ECDomainParameters(CURVE_PARAMS.getCurve(), CURVE_PARAMS.getG(), CURVE_PARAMS.getN(), CURVE_PARAMS.getH());
        HALF_CURVE_ORDER = CURVE.getN().shiftRight(1);
    }

    @Override
    public T initWithKey(byte[] prvKey, byte[] pubKey) {
        privateKey = prvKey;
        publicKey = pubKey;
        if (prvKey != null && pubKey == null) {
            BigInteger privKey = new BigInteger(1, privateKey);
            if (privKey.bitLength() > CURVE.getN().bitLength()) {
                privKey = privKey.mod(CURVE.getN());
            }
            ECPoint ecPoint = new FixedPointCombMultiplier().multiply(CURVE.getG(), privKey);
            publicKey = ecPoint.getEncoded(true);
        }
        int[] len = getKeyLength();
        ErrorTool.checkArgument(privateKey == null || privateKey.length == len[0], "privateKey error");
        ErrorTool.checkArgument(publicKey == null || publicKey.length == len[1], "publicKey error");
        return (T) this;
    }

    @Override
    public ECSign sign(byte[] hash) {
        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
        ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(new BigInteger(1, privateKey), CURVE);
        signer.init(true, privKey);

        BigInteger[] components = signer.generateSignature(hash);
        if (components[1].compareTo(HALF_CURVE_ORDER) > 0) {
            // The order of the curve is the number of valid points that exist on that curve. If S is in the upper
            // half of the number of valid points, then bring it back to the lower half. Otherwise, imagine that
            //    N = 10
            //    s = 8, so (-8 % 10 == 2) thus both (r, 8) and (r, 2) are valid solutions.
            //    10 - 8 == 2, giving us always the latter solution, which is canonical.
            components[1] = CURVE.getN().subtract(components[1]);
        }

        ECSign signature = new ECSign(components);
        for (int i = 0; i < 4; i++) {
            byte[] k = recoverPublicKey((byte) i, hash, signature);
            if (Arrays.equals(publicKey, k)) {
                signature.V = (byte) i;
                break;
            }
        }
        if (signature.V == -1) {
            throw new RuntimeException("Could not construct a recoverable key. This should never happen.");
        }
        return signature;
    }

    @Override
    public boolean verify(byte[] hash, ECSign sig) {
        ECDSASigner signer = new ECDSASigner();
        ECPublicKeyParameters params = new ECPublicKeyParameters(CURVE.getCurve().decodePoint(publicKey), CURVE);
        signer.init(false, params);
        try {
            return signer.verifySignature(hash, sig.getRBigInt(), sig.getSBigInt());
        } catch (NullPointerException e) {
            // Bouncy Castle contains a bug that can cause NPEs given specially crafted signatures. Those signatures
            // are inherently invalid/attack sigs so we just fail them here rather than crash the thread.
            return false;
        }
    }

    @Override
    public int[] getKeyLength() {
        return new int[]{32, 33};
    }

    public static byte[] recoverPublicKey(byte[] hash, ECSign sig) {
        return recoverPublicKey(sig.V, hash, sig);
    }

    protected static byte[] recoverPublicKey(byte recId, byte[] hash, ECSign sig) {
        ErrorTool.checkArgument(recId >= 0, "recId must be positive");
        ErrorTool.checkArgument(sig.getRBigInt().signum() >= 0, "r must be positive");
        ErrorTool.checkArgument(sig.getSBigInt().signum() >= 0, "s must be positive");
        ErrorTool.checkNotNull(hash);
        // 1.0 For j from 0 to h   (h == recId here and the loop is outside this function)
        //   1.1 Let x = r + jn
        BigInteger n = CURVE.getN();  // Curve order.
        BigInteger i = BigInteger.valueOf((long) recId / 2);
        BigInteger x = sig.getRBigInt().add(i.multiply(n));
        //   1.2. Convert the integer x to an octet string X of length mlen using the conversion routine
        //        specified in Section 2.3.7, where mlen = ⌈(log2 p)/8⌉ or mlen = ⌈m/8⌉.
        //   1.3. Convert the octet string (16 set binary digits)||X to an elliptic curve point R using the
        //        conversion routine specified in Section 2.3.4. If this conversion routine outputs “invalid”, then
        //        do another iteration of Step 1.
        //
        // More concisely, what these points mean is to use X as a compressed public key.
        BigInteger prime = SecP256K1Curve.q;
        if (x.compareTo(prime) >= 0) {
            // Cannot have point co-ordinates larger than this as everything takes place modulo Q.
            return null;
        }
        // Compressed keys require you to know an extra bit of data about the y-coord as there are two possibilities.
        // So it's encoded in the recId.
        ECPoint R = decompressKey(x, (recId & 1) == 1);
        //   1.4. If nR != point at infinity, then do another iteration of Step 1 (callers responsibility).
        if (!R.multiply(n).isInfinity()) {
            return null;
        }
        //   1.5. Compute e from M using Steps 2 and 3 of ECDSA signature verification.
        BigInteger e = new BigInteger(1, hash);
        //   1.6. For k from 1 to 2 do the following.   (loop is outside this function via iterating recId)
        //   1.6.1. Compute a candidate public key as:
        //               Q = mi(r) * (sR - eG)
        //
        // Where mi(x) is the modular multiplicative inverse. We transform this into the following:
        //               Q = (mi(r) * s ** R) + (mi(r) * -e ** G)
        // Where -e is the modular additive inverse of e, that is z such that z + e = 0 (mod n). In the above equation
        // ** is point multiplication and + is point addition (the EC group operator).
        //
        // We can find the additive inverse by subtracting e from zero then taking the mod. For example the additive
        // inverse of 3 modulo 11 is 8 because 3 + 8 mod 11 = 0, and -3 mod 11 = 8.
        BigInteger eInv = BigInteger.ZERO.subtract(e).mod(n);
        BigInteger rInv = sig.getRBigInt().modInverse(n);
        BigInteger srInv = rInv.multiply(sig.getSBigInt()).mod(n);
        BigInteger eInvrInv = rInv.multiply(eInv).mod(n);
        ECPoint q = ECAlgorithms.sumOfTwoMultiplies(CURVE.getG(), eInvrInv, R, srInv);
        return q.getEncoded(true);
    }

    /**
     * Decompress a compressed public key (x co-ord and low-bit of y-coord).
     */
    public static ECPoint decompressKey(BigInteger xBN, boolean yBit) {
        X9IntegerConverter x9 = new X9IntegerConverter();
        byte[] compEnc = x9.integerToBytes(xBN, 1 + x9.getByteLength(CURVE.getCurve()));
        compEnc[0] = (byte) (yBit ? 0x03 : 0x02);
        return CURVE.getCurve().decodePoint(compEnc);
    }
}

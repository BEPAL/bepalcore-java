/*
 * Copyright (c) 2018-2019, BEPAL
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of California, Berkeley nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package pro.bepal.core.eos;

import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.signers.ECDSASigner;
import org.spongycastle.crypto.signers.RandomDSAKCalculator;
import pro.bepal.categories.Base58;
import pro.bepal.categories.ByteArrayData;
import pro.bepal.categories.SHAHash;
import pro.bepal.coin.eos.Address;
import pro.bepal.core.ECSign;
import pro.bepal.core.bitcoin.BitECKey;
import pro.bepal.core.bitcoin.SecpECKey;
import pro.bepal.util.ErrorTool;
import org.spongycastle.math.ec.ECAlgorithms;
import org.spongycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

public class EOSECKey extends SecpECKey<EOSECKey> {

    private SecureRandom secureRandom = new SecureRandom();

    private static BigInteger CURVE_Q = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F", 16);

    private static BigInteger getQ() {
        return CURVE_Q;
    }

    public EOSECKey() {
    }

    public EOSECKey(byte[] priKey) {
        initWithPrvKey(priKey);
    }

    public static EOSECKey fromPrivateKey(byte[] key) {
        return new EOSECKey().initWithPrvKey(key);
    }

    public static EOSECKey fromPublicKey(byte[] key) {
        return new EOSECKey().initWithPubKey(key);
    }

    public static EOSECKey fromPublicKey(String base58Data) {
        return fromPublicKey(Address.toPubKey(base58Data));
    }

    public static EOSECKey fromPrivateKey(String key) {
        return new EOSECKey().initWithPrvKey(ByteArrayData.copyOfRange(Base58.decode(key), 1, 32));
    }

    @Override
    public ECSign sign(byte[] hash) {
        ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(new BigInteger(1, privateKey), CURVE);
        RandomDSAKCalculator calculator = new RandomDSAKCalculator();
        ECDomainParameters ec = privKey.getParameters();
        calculator.init(ec.getN(), secureRandom);
        ECDSASigner signer = new ECDSASigner(calculator);
        signer.init(true, privKey);
        BigInteger[] components;
        do {
            components = signer.generateSignature(hash);
            if (components[1].compareTo(HALF_CURVE_ORDER) > 0) {
                // The order of the curve is the number of valid points that exist on that curve. If S is in the upper
                // half of the number of valid points, then bring it back to the lower half. Otherwise, imagine that
                //    N = 10
                //    s = 8, so (-8 % 10 == 2) thus both (r, 8) and (r, 2) are valid solutions.
                //    10 - 8 == 2, giving us always the latter solution, which is canonical.
                components[1] = CURVE.getN().subtract(components[1]);
            }
        } while (components[0].toByteArray().length != 32 || components[1].toByteArray().length != 32);

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
        if (!isCanonical(sig.encoding(true))) {
            System.out.println("sign is_canonical");
            return false;
        }
        return super.verify(hash, sig);
    }

    private boolean oldVerify(byte[] hash, ECSign sig) {
        if (!isCanonical(sig.encoding(true))) {
            System.out.println("sign is_canonical");
            return false;
        }
        byte recId = sig.V;
        ErrorTool.checkArgument(recId >= 0, "recId must be positive");
        ErrorTool.checkArgument(sig.getRBigInt().compareTo(BigInteger.ZERO) >= 0, "r must be positive");
        ErrorTool.checkArgument(sig.getSBigInt().compareTo(BigInteger.ZERO) >= 0, "s must be positive");
        ErrorTool.checkNotNull(hash);
        // 1.0 For j from 0 to h (h == recId here and the loop is outside this
        // function)
        // 1.1 Let x = r + jn

        BigInteger n = CURVE.getN();//Secp256k1Param.n; // EcCurve order.
        BigInteger i = BigInteger.valueOf((long) recId / 2);
        BigInteger x = sig.getRBigInt().add(i.multiply(n));
        // 1.2. Convert the integer x to an octet string X of length mlen using
        // the conversion routine
        // specified in Section 2.3.7, where mlen = ⌈(log2 p)/8⌉ or mlen =
        // ⌈m/8⌉.
        // 1.3. Convert the octet string (16 set binary digits)||X to an elliptic
        // curve point R using the
        // conversion routine specified in Section 2.3.4. If this conversion
        // routine outputs "invalid", then
        // do another iteration of Step 1.
        //
        // More concisely, what these points mean is to use X as a compressed
        // public key.

        BigInteger prime = CURVE_Q; // Bouncy Castle is not consistent about
        // the letter it uses for the prime.
        if (x.compareTo(prime) >= 0) {
            // Cannot have point co-ordinates larger than this as everything takes
            // place modulo Q.
            return false;
        }
        // Compressed keys require you to know an extra bit of data about the
        // y-coord as there are two possibilities.
        // So it's encoded in the recId.
        ECPoint R = BitECKey.decompressKey(x, (recId & 1) == 1);
        // 1.4. If nR != point at infinity, then do another iteration of Step 1
        // (callers responsibility).
        if (!R.multiply(n).isInfinity()) {
            return false;
        }
        // 1.5. Compute e from M using Steps 2 and 3 of ECDSA signature
        // verification.
        BigInteger e = new BigInteger(1, hash);
        // 1.6. For k from 1 to 2 do the following. (loop is outside this function
        // via iterating recId)
        // 1.6.1. Compute a candidate public key as:
        // Q = mi(r) * (sR - eG)
        //
        // Where mi(x) is the modular multiplicative inverse. We transform this
        // into the following:
        // Q = (mi(r) * s ** R) + (mi(r) * -e ** G)
        // Where -e is the modular additive inverse of e, that is z such that z +
        // e = 0 (mod n). In the above equation
        // ** is point multiplication and + is point addition (the EC group
        // operator).
        //
        // We can find the additive inverse by subtracting e from zero then taking
        // the mod. For example the additive
        // inverse of 3 modulo 11 is 8 because 3 + 8 mod 11 = 0, and -3 mod 11 =
        // 8.
        BigInteger eInv = BigInteger.ZERO.subtract(e).mod(n);
        BigInteger rInv = sig.getRBigInt().modInverse(n);
        BigInteger srInv = rInv.multiply(sig.getSBigInt()).mod(n);
        BigInteger eInvrInv = rInv.multiply(eInv).mod(n);
        ECPoint q = ECAlgorithms.sumOfTwoMultiplies(CURVE.getG(), eInvrInv, R, srInv); //Secp256k1Param.G, eInvrInv, R, srInv);
        return Arrays.equals(publicKey, q.getEncoded(true));
    }

    public String toPubblicKeyString() {
        return Address.toAddress(publicKey);
    }

    public String toWif() {
        byte[] rawPrivKey = privateKey;
        byte[] resultWIFBytes = new byte[1 + 32 + 4];
        resultWIFBytes[0] = (byte) 0x80;
        System.arraycopy(rawPrivKey, rawPrivKey.length > 32 ? 1 : 0, resultWIFBytes, 1, 32);
        byte[] hash = SHAHash.hash2256Twice(ByteArrayData.copyOfRange(resultWIFBytes, 0, 33));
        System.arraycopy(hash, 0, resultWIFBytes, 33, 4);
        return Base58.encode(resultWIFBytes);
    }

    private boolean isCanonical(byte[] data) {
        return (data[1] & 0x80) == 0
                && !(data[1] == 0 && (data[2] & 0x80) == 0)
                && (data[33] & 0x80) == 0
                && !(data[33] == 0 && (data[34] & 0x80) == 0);
    }
}

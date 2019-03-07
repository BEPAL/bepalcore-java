package pro.bepal.categories;

import org.spongycastle.crypto.digests.KeccakDigest;
import org.spongycastle.crypto.digests.RIPEMD160Digest;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.digests.SHA512Digest;
import org.spongycastle.crypto.macs.HMac;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.jcajce.provider.digest.SHA3;

import java.security.MessageDigest;

/**
 * 常见的hash运算
 */
public class SHAHash {

    public static byte[] hmac512(byte[] key, byte[] data) {
        SHA512Digest digest = new SHA512Digest();
        HMac hMac = new HMac(digest);
        hMac.init(new KeyParameter(key));
        hMac.reset();
        hMac.update(data, 0, data.length);
        byte[] out = new byte[64];
        hMac.doFinal(out, 0);
        return out;
    }

    public static byte[] hmac256(byte[] key, byte[] data) {
        SHA256Digest digest = new SHA256Digest();
        HMac hMac = new HMac(digest);
        hMac.init(new KeyParameter(key));
        hMac.reset();
        hMac.update(data, 0, data.length);
        byte[] out = new byte[32];
        hMac.doFinal(out, 0);
        return out;
    }

    public static byte[] sha1(byte[] data) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA");
            sha.update(data);
            return sha.digest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] ripemd160(byte[] data) {
        RIPEMD160Digest digest = new RIPEMD160Digest();
        digest.update(data, 0, data.length);
        byte[] out = new byte[20];
        digest.doFinal(out, 0);
        return out;
    }

    public static byte[] keccak256(byte[] data) {
        KeccakDigest digest = new  KeccakDigest(256);
        digest.update(data, 0, data.length);
        byte[] out = new byte[32];
        digest.doFinal(out, 0);
        return out;
    }

    public static byte[] keccak512(byte[] data) {
        KeccakDigest digest = new  KeccakDigest(512);
        digest.update(data, 0, data.length);
        byte[] out = new byte[64];
        digest.doFinal(out, 0);
        return out;
    }

    public static byte[] sha3512(byte[] data) {
        try {
            MessageDigest digest = new SHA3.Digest512();
            digest.update(data, 0, data.length);
            return digest.digest();
        } catch (Exception e) {
            throw new RuntimeException(e);  // Can't happen.
        }
    }

    public static byte[] sha3256(byte[] data) {
        try {
            MessageDigest digest = new SHA3.Digest256();
            digest.update(data, 0, data.length);
            return digest.digest();
        } catch (Exception e) {
            throw new RuntimeException(e);  // Can't happen.
        }
    }

    public static byte[] sha2512(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.update(data, 0, data.length);
            return digest.digest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] sha2256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(data, 0, data.length);
            return digest.digest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static MessageDigest newDigest2256() throws Exception {
        return MessageDigest.getInstance("SHA-256");
    }

    public static byte[] md5(byte[] data) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(data);
            return md5.digest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] hash2256Twice(byte[] data) {
        return SHAHash.sha2256(SHAHash.sha2256(data));
    }

    public static byte[] sha256hash160(byte[] data) {
        data = SHAHash.sha2256(data);
        return SHAHash.ripemd160(data);
    }
}

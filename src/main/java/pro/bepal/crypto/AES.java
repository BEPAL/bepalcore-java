package pro.bepal.crypto;

import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by 10254 on 2017-08-02.
 */
public class AES {

    /**
     * AES 加密
     */
    public static byte[] encrypt(byte[] content, byte[] key) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            key = Arrays.copyOf(key, 16);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * AES CBC 加密
     */
    public static byte[] encryptCBC(byte[] content, byte[] sksBytes, byte[] ivBytes) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(sksBytes, "AES");
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * AES 解密
     */
    public static byte[] decrypt(byte[] content, byte[] key) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            key = Arrays.copyOf(key, 16);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * AES CBC 解密
     */
    public static byte[] decryptCBC(byte[] content, byte[] sksBytes, byte[] ivBytes) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(sksBytes, "AES");
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * AES 加密
     */
    public static byte[] encryptRandom(byte[] content, byte[] key) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(key));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec skey = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * AES 解密
     */
    public static byte[] decryptRandom(byte[] content, byte[] key) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(key));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec skey = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skey);
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}

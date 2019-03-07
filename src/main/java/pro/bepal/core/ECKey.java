package pro.bepal.core;

import org.spongycastle.util.encoders.Hex;

import java.text.MessageFormat;

/**
 * 单私钥或公钥对象
 * @param <T> 子类型
 */
public abstract class ECKey<T> {
    /**
     * 私钥
     */
    protected byte[] privateKey;
    /**
     * 公钥 为空且私钥不为空可以推导出公钥
     */
    protected byte[] publicKey;

    public ECKey() {
    }

    /**
     * 初始化对象
     * @param prvKey 私钥
     * @param pubKey 公钥
     */
    public ECKey(byte[] prvKey, byte[] pubKey) {
        initWithKey(prvKey, pubKey);
    }

    public T initWithPrvKey(byte[] prvKey) {
        initWithKey(prvKey, null);
        return (T) this;
    }

    public T initWithPubKey(byte[] pubKey) {
        initWithKey(null, pubKey);
        return (T) this;
    }

    public abstract T initWithKey(byte[] prvKey, byte[] pubKey);

    public byte[] getPublicKey() {
        return publicKey;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    public String getPublicKeyAsHex() {
        return Hex.toHexString(getPublicKey());
    }

    public String getPrivateKeyAsHex() {
        return Hex.toHexString(getPrivateKey());
    }

    public boolean hasPrivateKey() {
        return privateKey != null;
    }

    /**
     * 签名数据
     */
    public abstract ECSign sign(byte[] hash);

    public String signAsHex(byte[] hash) {
        return sign(hash).toHex();
    }

    /**
     * 验证签名
     */
    public abstract boolean verify(byte[] hash, ECSign sig);

    public abstract int[] getKeyLength();

    @Override
    public String toString() {
        return MessageFormat.format("\nprivateKey:{0} \npublicKey:{1}", getPrivateKeyAsHex(), getPublicKeyAsHex());
    }
}

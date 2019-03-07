package pro.bepal.core;

import pro.bepal.util.ByteUtil;

import java.text.MessageFormat;
import java.util.List;

/**
 * 推导公钥时路径的编号
 */
public class ChildNumber {
    private int intPath;
    public boolean hardened;

    public ChildNumber(int path) {
        this(path, false);
    }

    public ChildNumber(int path, boolean hardened) {
        intPath = path;
        this.hardened = hardened;
    }

    /**
     * 获取标准公钥使用
     */
    public long getKeyPath() {
        long temp = intPath;
        if (hardened) {
            temp += 0x80000000;
        }
        return temp;
    }

    /**
     * SECP256k1家族的路径
     */
    public byte[] getPath() {
        long temp = intPath;
        if (hardened) {
            temp += 0x80000000;
        }
        return ByteUtil.intToBytesLE(temp);
    }

    /**
     * Nem的路径
     */
    public byte[] getPathNem() {
        long temp = intPath;
        return ByteUtil.intToBytes(temp);
    }

    /**
     * BTM的路径
     */
    public byte[] getPathBtm() {
        return ByteUtil.longToBytes(intPath);
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0} {1}", intPath, hardened ? "H" : "");
    }
}

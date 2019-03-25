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
package pro.bepal.core;

import pro.bepal.categories.Base58;
import pro.bepal.categories.ByteArrayData;
import org.spongycastle.util.encoders.Hex;
import pro.bepal.categories.SHAHash;
import pro.bepal.util.ErrorTool;

import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 主私钥或者主公钥对象
 *
 * @param <T> 子类对象类型
 * @param <K> ECKey对象类型
 */
public abstract class DeterministicKey<T, K> {
    /**
     * 私钥
     */
    protected byte[] privateKey;
    /**
     * 公钥 若私钥公钥为空自动推导出公钥
     */
    protected byte[] publicKey;
    /**
     * Code
     */
    protected byte[] chainCode;
    /**
     * 深度 第几层的意思
     */
    private int depth = 0;
    /**
     * 0 if this key is root node of key hierarchy
     */
    private int parentFingerprint;
    /**
     * 路径
     */
    private long keyPath = 0;
    /**
     * 存储推导过的数据 可以减少重复推导以提高速度
     */
    private HashMap<Long, DeterministicKey> keyHashMap = new HashMap<>();

    public DeterministicKey() {

    }

    /**
     * 初始化对象
     *
     * @param prv  私钥
     * @param pub  公钥
     * @param code Code
     */
    public DeterministicKey(byte[] prv, byte[] pub, byte[] code) {
        initWithPrv(prv, pub, code);
    }

    /**
     * 主私钥初始化
     */
    public T initWithPrvKey(byte[] xpri) {
        byte[] prvkey = ByteArrayData.copyOfRange(xpri, 0 + 4, 33);
        byte[] code = ByteArrayData.copyOfRange(xpri, 33 + 4, 33);
        initWithPrv(prvkey, null, code);
        return (T) this;
    }

    /**
     * 主公钥初始化
     */
    public T initWithPubKey(byte[] xpub) {
        byte[] pubkey = ByteArrayData.copyOfRange(xpub, 0 + 4, 33);
        byte[] code = ByteArrayData.copyOfRange(xpub, 33 + 4, 33);
        initWithPrv(null, pubkey, code);
        return (T) this;
    }

    public T initWithStandardKey(byte[] key, int xprvhead) {
        ErrorTool.checkArgument(key != null && key.length >= 78, "XKey Error");
        ByteArrayData data = new ByteArrayData(key);
        int head = data.readIntLE();
        depth = data.readByte();
        parentFingerprint = data.readIntLE();
        keyPath = data.readIntLE();
        byte[] code = data.readData(32);
        if (head == xprvhead) {
            initWithPrv(data.readData(33), null, code);
        } else {
            initWithPrv(null, data.readData(33), code);
        }
        return (T) this;
    }

    /**
     * 根据种子初始化
     */
    public abstract T initWithSeed(byte[] seed);

    /**
     * 初始化对象并判断数据长度是否符合要求
     */
    protected T initWithPrv(byte[] prvKey, byte[] pubKey, byte[] code) {
        if (prvKey != null && prvKey[0] == 0 && prvKey.length > 32) {
            prvKey = ByteArrayData.copyOfRange(prvKey, 1, 32);
        }
        if (pubKey != null && pubKey[0] == 0 && pubKey.length > 32) {
            pubKey = ByteArrayData.copyOfRange(pubKey, 1, 32);
        }
        if (code != null && code[0] == 0 && code.length > 32) {
            code = ByteArrayData.copyOfRange(code, 1, 32);
        }
        privateKey = prvKey;
        publicKey = pubKey;
        chainCode = code;
        int[] len = getKeyLength();
        ErrorTool.checkArgument(privateKey == null || privateKey.length == len[0], "privateKey error");
        ErrorTool.checkArgument(publicKey == null || publicKey.length == len[1], "publicKey error");
        ErrorTool.checkArgument(chainCode != null && chainCode.length == len[2], "chainCode error");
        return (T) this;
    }

    public String toXPrv(int prefix) {
        return Hex.toHexString(toXPrivive(prefix));
    }

    public String toXPub(int prefix) {
        return Hex.toHexString(toXPublic(prefix));
    }

    /**
     * 转自定义主私钥
     */
    public byte[] toXPrivive(int prefix) {
        ByteArrayData data = new ByteArrayData();
        data.appendIntLE(prefix);
        if (privateKey.length == 32) {
            data.appendByte((byte) 0);
        }
        data.putBytes(privateKey);
        if (chainCode.length == 32) {
            data.appendByte((byte) 0);
        }
        data.putBytes(chainCode);
        return data.toBytes();
    }

    /**
     * 转自定义主公钥
     */
    public byte[] toXPublic(int prefix) {
        ByteArrayData data = new ByteArrayData();
        data.appendIntLE(prefix);
        if (publicKey.length == 32) {
            data.appendByte((byte) 0);
        }
        data.putBytes(publicKey);
        if (chainCode.length == 32) {
            data.appendByte((byte) 0);
        }
        data.putBytes(chainCode);
        return data.toBytes();
    }

    /**
     * 转标准主私钥
     *
     * @param prefix 私钥头
     */
    public byte[] toStandardXPrivate(int prefix) {
        ByteArrayData data = new ByteArrayData();
        data.appendIntLE(prefix);
        data.appendByte(depth);
        data.appendIntLE(parentFingerprint);
        data.appendIntLE(keyPath);
        data.putBytes(chainCode);
        if (privateKey.length == 32) {
            data.appendByte(0);
        }
        data.putBytes(privateKey);
        return data.toBytes();
    }

    /**
     * 转标准主公钥
     *
     * @param prefix 公钥头
     */
    public byte[] toStandardXPublic(int prefix) {
        ByteArrayData data = new ByteArrayData();
        data.appendIntLE(prefix);
        data.appendByte(depth);
        data.appendIntLE(parentFingerprint);
        data.appendIntLE(keyPath);
        data.putBytes(chainCode);
        if (publicKey.length == 32) {
            data.appendByte(0);
        }
        data.putBytes(publicKey);
        return data.toBytes();
    }

    public String toStandardXPrv(int prefix) {
        ByteArrayData data = new ByteArrayData();
        data.putBytes(toStandardXPrivate(prefix));
        data.putBytes(SHAHash.hash2256Twice(data.toBytes()), 4);
        return Base58.encode(data.toBytes());
    }

    public String toStandardXPub(int prefix) {
        ByteArrayData data = new ByteArrayData();
        data.putBytes(toStandardXPublic(prefix));
        data.putBytes(SHAHash.hash2256Twice(data.toBytes()), 4);
        return Base58.encode(data.toBytes());
    }

    public int getDepth() {
        return depth;
    }

    public int getParentFingerprint() {
        return parentFingerprint;
    }

    public boolean hasPrivateKey() {
        return privateKey != null;
    }

    public byte[] getPrivKeyBytes33() {
        return privateKey.length == 33 ? privateKey : ByteArrayData.concat(new byte[]{0}, privateKey);
    }

    public byte[] getPrivKey() {
        return privateKey;
    }

    /**
     * 推导子私钥或子公钥
     */
    public T derive(List<ChildNumber> childNumbers) {
        DeterministicKey temp = this;
        for (ChildNumber childNumber : childNumbers) {
            DeterministicKey temp1 = temp;
            long keypath = childNumber.getKeyPath();
            if (temp1.keyHashMap.containsKey(keypath)) {
                temp = (DeterministicKey) temp1.keyHashMap.get(keypath);
                continue;
            }
            if (temp.hasPrivateKey()) {
                temp = temp.privChild(childNumber);
            } else {
                temp = temp.pubChild(childNumber);
            }
            temp1.keyHashMap.put(keypath, temp);
            temp.depth = temp1.depth + 1;
            temp.keyPath = keypath;
            temp.parentFingerprint = temp1.getFingerprint();
        }
        return (T) temp;
    }

    /**
     * Returns the first 32 bits of the result of Identifier
     */
    private int getFingerprint() {
        return ByteBuffer.wrap(Arrays.copyOfRange(SHAHash.sha256hash160(publicKey), 0, 4)).getInt();
    }

    /**
     * 推子私钥
     */
    protected abstract DeterministicKey privChild(ChildNumber childNumber);

    /**
     * 推子公钥
     */
    protected abstract DeterministicKey pubChild(ChildNumber childNumber);

    public abstract K toECKey();

    public abstract int[] getKeyLength();

    @Override
    public String toString() {
        return MessageFormat.format("\nprivateKey:{0} \npublicKey:{1} \nchainCode:{2}",
                Hex.toHexString(privateKey),
                Hex.toHexString(publicKey),
                Hex.toHexString(chainCode));
    }

    public long getKeyPath() {
        return keyPath;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public byte[] getChainCode() {
        return chainCode;
    }
}

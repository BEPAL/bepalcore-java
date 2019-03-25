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

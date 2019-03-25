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
package pro.bepal.core.bitcoin;

import com.google.common.primitives.UnsignedBytes;
import pro.bepal.categories.Base58;
import pro.bepal.categories.ByteArrayData;
import pro.bepal.categories.SHAHash;
import pro.bepal.core.ECKey;
import pro.bepal.util.ErrorTool;

import java.util.Comparator;

public class BitECKey extends SecpECKey<BitECKey> {

    public byte[] getPubKeyHash() {
        return SHAHash.ripemd160(SHAHash.sha2256(getPublicKey()));
    }

    /**
     * Compares pub key bytes using {@link UnsignedBytes#lexicographicalComparator()}
     */
    public static final Comparator PUBKEY_COMPARATOR = new Comparator() {
        private Comparator<byte[]> comparator = UnsignedBytes.lexicographicalComparator();

        @Override
        public int compare(Object k1, Object k2) {
            return comparator.compare(((ECKey) k1).getPublicKey(), ((ECKey) k2).getPublicKey());
        }
    };

    public static BitECKey fromPrivateKey(byte[] key) {
        return new BitECKey().initWithPrvKey(key);
    }

    public static BitECKey fromPublicKey(byte[] key) {
        return new BitECKey().initWithPubKey(key);
    }

    public String getPrivateKeyAsWiF(int version) {
        byte[] bversion;
        if (version <= 255) {
            bversion = new byte[]{(byte) version};
        } else {
            bversion = new byte[]{(byte) (version >> 8 & 0xFF), (byte) (version & 0xFF)};
        }
        ErrorTool.checkArgument(privateKey.length == 32, "私钥长度错误");
        ByteArrayData data = new ByteArrayData();
        data.putBytes(bversion);
        data.putBytes(privateKey);
        data.appendByte(1);//表示公钥被压缩
        data.putBytes(SHAHash.hash2256Twice(data.toBytes()), 4);
        return Base58.encode(data.toBytes());
    }
}

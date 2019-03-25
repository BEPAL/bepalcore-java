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
package pro.bepal.coin.eos;

import pro.bepal.categories.Base58;
import pro.bepal.categories.SHAHash;
import pro.bepal.core.eos.EOSECKey;
import pro.bepal.util.ByteUtil;

import java.util.Arrays;

public class Address {

    public final static String PREFIX = "EOS";

    private EOSECKey key;

    public Address(EOSECKey key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return toAddress(key.getPublicKey());
    }

    public static String toAddress(byte[] pubKey) {
        byte[] postfixBytes = new byte[0];
        byte[] mData = pubKey;
        byte[] toDigest = new byte[mData.length + postfixBytes.length];
        System.arraycopy(mData, 0, toDigest, 0, mData.length);
        byte[] digest = SHAHash.ripemd160(toDigest);
        byte[] result = new byte[4 + mData.length];
        System.arraycopy(mData, 0, result, 0, mData.length);
        System.arraycopy(digest, 0, result, mData.length, 4);
        return PREFIX + Base58.encode(result);
    }

    public static byte[] toPubKey(String base58Data) {
        String prefix = PREFIX;
        byte[] prefixBytes = new byte[0];
        byte[] data = Base58.decode(base58Data.substring(prefix.length()));
        byte[] toHashData = new byte[data.length - 4 + prefixBytes.length];
        System.arraycopy(data, 0, toHashData, 0, data.length - 4); // key data
        System.arraycopy(prefixBytes, 0, toHashData, data.length - 4, prefixBytes.length);
        byte[] r160 = SHAHash.ripemd160(toHashData);
        long checksumByCal = ByteUtil.bytesToInt(r160, 0);
        long checksumFromData = ByteUtil.bytesToInt(data, data.length - 4);
        if (checksumByCal != checksumFromData) {
            throw new IllegalArgumentException("Invalid format, checksum mismatch");
        }
        return Arrays.copyOfRange(data, 0, data.length - 4);
    }
}

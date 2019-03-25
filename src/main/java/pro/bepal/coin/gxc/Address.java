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
package pro.bepal.coin.gxc;

import pro.bepal.categories.Base58;
import pro.bepal.categories.ByteArrayData;
import pro.bepal.categories.SHAHash;
import pro.bepal.core.gxc.GXCECKey;
import pro.bepal.util.ByteUtil;

public class Address {

    public final static String PREFIX = "GXC";

    private GXCECKey key;

    public Address(GXCECKey key) {
        this.key = key;
    }

    public Address(String pubKey) {
        this.key = GXCECKey.fromPublicKey(pubKey);
    }

    @Override
    public String toString() {
        return toAddress(key.getPublicKey());
    }

    public GXCECKey getKey() {
        return key;
    }

    public static String toAddress(byte[] pubKey) {
        byte[] pubhash = ByteArrayData.copyOfRange(SHAHash.ripemd160(pubKey), 0, 4);
        byte[] data = ByteArrayData.concat(pubKey, pubhash);
        return PREFIX + Base58.encode(data);
    }

    public static byte[] toPubKey(String base58Data) {
        String prefix = PREFIX;
        byte[] data = Base58.decode(base58Data.substring(prefix.length()));
        byte[] pub = ByteArrayData.copyOfRange(data, 0, 33);
        byte[] checksum = ByteArrayData.copyOfRange(data, 33, 4);
        byte[] r160 = SHAHash.ripemd160(pub);
        long checksumByCal = ByteUtil.bytesToInt(r160, 0);
        long checksumFromData = ByteUtil.bytesToInt(checksum, 0);
        if (checksumByCal != checksumFromData) {
            throw new IllegalArgumentException("Invalid format, checksum mismatch");
        }
        return pub;
    }
}

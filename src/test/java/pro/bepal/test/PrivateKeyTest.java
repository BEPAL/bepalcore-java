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
package pro.bepal.test;

import com.google.common.collect.Lists;
import org.junit.BeforeClass;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;
import pro.bepal.categories.Base58;
import pro.bepal.categories.SHAHash;
import pro.bepal.core.ChildNumber;
import pro.bepal.core.DeterministicKey;
import pro.bepal.core.MnemonicCode;
import pro.bepal.core.eos.EOSDeterministicKey;
import pro.bepal.core.eos.EOSECKey;
import pro.bepal.core.gxc.GXCDeterministicKey;
import pro.bepal.core.gxc.GXCECKey;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class PrivateKeyTest {

    public static byte[] seed;
    public static String message = "test";

    @BeforeClass
    public static void testStart() {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 12; ++i) {
            data.add("Bepal");
        }

        seed = MnemonicCode.toSeed(data, "");
        System.out.println("Seed : [" + Hex.toHexString(seed) + "]");
    }

    @Test
    public void testEos() {
        EOSDeterministicKey rootKey = EOSDeterministicKey.createMasterPrivateKey(seed);

        /// build key chain
        // 44'/194'/0'/0/0
        List<ChildNumber> path = Lists.newArrayList(new ChildNumber(44, true),
                new ChildNumber(194, true),
                new ChildNumber(0, true),
                new ChildNumber(0, false),
                new ChildNumber(0));
        // 44'/194'/0'
        List<ChildNumber> path1 = Lists.newArrayList(new ChildNumber(44, true),
                new ChildNumber(194, true),
                new ChildNumber(0, true));
        // 0/0
        List<ChildNumber> path2 = Lists.newArrayList(new ChildNumber(0, false),
                new ChildNumber(0, false));


        // this's standard bip 44
        String xpub = rootKey.derive(path1).toStandardXPub(0x043587CF);
        EOSDeterministicKey rootxpub = new EOSDeterministicKey().initWithStandardKey(Base58.decode(xpub), 0x04358394);

        // ec key
        EOSECKey privateKey = rootKey.derive(path).toECKey();
        EOSECKey publicKey2 = rootxpub.derive(path2).toECKey();

        // sign message
        assertArrayEquals(privateKey.getPublicKey(), publicKey2.getPublicKey());
        byte[] msg = SHAHash.sha2256(message.getBytes());
        assertTrue(publicKey2.verify(msg, privateKey.sign(msg)));

        // private key
        // 0e3c85f023ee52312d97132c8f84ea386baa3f918322d3f8003d956925a40f03
        // 5HvZEPkiAur49Tb4k9nYvagAYEmobsiFtmegzSeuWHWke2JCL9K
        System.out.println("EOS prvkey: [" + privateKey.toWif() + "]");
        // address
        // EOS5eyq829Bi8Cmg99WGvVPNVWqq2Rc5kYRXxtxjTP2RAisom1GHa
        System.out.println("EOS address: [" + publicKey2.toPubblicKeyString() + "]");
    }

    @Test
    public void testGXC() {
        GXCDeterministicKey rootKey = GXCDeterministicKey.createMasterPrivateKey(seed);

        /// build key chain
        // 44'/2303'/0'/0/0
        List<ChildNumber> path = Lists.newArrayList(new ChildNumber(44, true),
                new ChildNumber(2303, true),
                new ChildNumber(0, true),
                new ChildNumber(0, false),
                new ChildNumber(0));
        // 44'/2303'/0'
        List<ChildNumber> path1 = Lists.newArrayList(new ChildNumber(44, true),
                new ChildNumber(2303, true),
                new ChildNumber(0, true));
        // 0/0
        List<ChildNumber> path2 = Lists.newArrayList(new ChildNumber(0, false),
                new ChildNumber(0, false));


        // this's standard bip 44
        String xpub = rootKey.derive(path1).toStandardXPub(0x043587CF);
        GXCDeterministicKey rootxpub = new GXCDeterministicKey().initWithStandardKey(Base58.decode(xpub), 0x04358394);

        // ec key
        GXCECKey privateKey = rootKey.derive(path).toECKey();
        GXCECKey publicKey2 = rootxpub.derive(path2).toECKey();

        // sign message
        assertArrayEquals(privateKey.getPublicKey(), publicKey2.getPublicKey());
        byte[] msg = SHAHash.sha2256(message.getBytes());
        assertTrue(publicKey2.verify(msg, privateKey.sign(msg)));

        // private key
        // 9e407fa0f213976887c2008f0731cce9922b9d2d16ace22b7f707cea60a77c2d
        // 5K1yv2ghXNEGPzuSRYxY4hTYsjoftSSFSKgbaqwZ68RvnyoBgYK
        System.out.println("GXC prvkey: [" + privateKey.toWif() + "]");
        // address
        // GXC86frDL7Qw5hj6TV1qoCfoBbnQNXHnHxAX45VAMRN8VrMp8egr1
        System.out.println("GXC address: [" + publicKey2.toPubblicKeyString() + "]");
    }
}

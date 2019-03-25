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
package pro.bepal.core.eos;

import pro.bepal.core.DeterministicKey;
import pro.bepal.core.ECKey;
import pro.bepal.core.bitcoin.SecpDeterministicKey;

public class EOSDeterministicKey extends SecpDeterministicKey<EOSDeterministicKey, EOSECKey> {

    public static EOSDeterministicKey createMasterPrivateKey(byte[] seed) {
        return new EOSDeterministicKey().initWithSeed(seed);
    }

    public static EOSDeterministicKey fromXPrivateKey(byte[] xprvkey) {
        return new EOSDeterministicKey().initWithPrvKey(xprvkey);
    }

    public static EOSDeterministicKey fromXPublicKey(byte[] xpubkey) {
        return new EOSDeterministicKey().initWithPubKey(xpubkey);
    }

    @Override
    protected ECKey getECKey() {
        return new EOSECKey().initWithKey(privateKey, publicKey);
    }

    @Override
    protected DeterministicKey getDeterministicKey(byte[] prvKey, byte[] pubKey, byte[] code) {
        return new EOSDeterministicKey().initWithPrv(prvKey, pubKey, code);
    }
}

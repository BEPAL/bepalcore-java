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
package pro.bepal.coin.eos.tx.message.newaccount;

import pro.bepal.categories.ByteArrayData;
import pro.bepal.util.ByteUtil;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Authority {
    /**
     * 签名有效所需权重
     */
    public long threshold;
    /**
     * 公钥及其权重
     */
    public List<KeyPermissionWeight> keys = new ArrayList<>();
    /**
     * 可以默认不添加
     */
    public List<AccountPermissionWeight> accounts = new ArrayList<>();
    /**
     * 可以默认不添加
     */
    public List<WaitWeight> waits = new ArrayList<>();

    public Authority() {
        threshold = 1;
    }

    public byte[] toByte() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write(ByteUtil.intToBytes(threshold));
            stream.write(ByteUtil.uvarToBytes(keys.size()));
            for (KeyPermissionWeight key : keys) {
                stream.write(key.toByte());
            }
            stream.write(ByteUtil.uvarToBytes(accounts.size()));
            for (AccountPermissionWeight account : accounts) {
                stream.write(account.toByte());
            }
            stream.write(ByteUtil.uvarToBytes(waits.size()));
            for (WaitWeight wait : waits) {
                stream.write(wait.toByte());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream.toByteArray();
    }

    public void parse(ByteArrayData data) {
        threshold = data.readIntByLong();
        long count = data.readUVarInt();
        for (int i = 0; i < count; i++) {
            KeyPermissionWeight weight = new KeyPermissionWeight();
            weight.parse(data);
            keys.add(weight);
        }
        count = data.readUVarInt();
        for (int i = 0; i < count; i++) {
            AccountPermissionWeight weight = new AccountPermissionWeight();
            weight.parse(data);
            accounts.add(weight);
        }
        count = data.readUVarInt();
        for (int i = 0; i < count; i++) {
            WaitWeight wait = new WaitWeight();
            wait.parse(data);
            waits.add(wait);
        }
    }

    public void addKey(KeyPermissionWeight key) {
        keys.add(key);
    }

    public void addAccount(AccountPermissionWeight account) {
        accounts.add(account);
    }
}

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
package pro.bepal.coin.gxc.operation;

import org.json.me.JSONArray;
import org.json.me.JSONObject;
import pro.bepal.categories.ByteArrayData;
import pro.bepal.coin.gxc.GXCSerializable;

import java.util.ArrayList;
import java.util.List;

public class Authority implements GXCSerializable {

    public static final String KEY_ACCOUNT_AUTHS = "account_auths";
    public static final String KEY_KEY_AUTHS = "key_auths";
    public static final String KEY_WEIGHT_THRESHOLD = "weight_threshold";
    public static final String KEY_ADDRESS_AUTHS = "address_auths";

    public long weightThreshold;
    public List<AccountAuth> accountAuths;
    private List<KeyAuth> keyAuths;
    private JSONArray addressAuths;

    public Authority() {
        weightThreshold = 1;
        accountAuths = new ArrayList<>();
        keyAuths = new ArrayList<>();
        addressAuths = new JSONArray();
    }

    public Authority(String key) {
        this();
        KeyAuth auth = new KeyAuth(key);
        keyAuths.add(auth);
    }

    public void addKey(String key) {
        KeyAuth auth = new KeyAuth(key);
        keyAuths.add(auth);
    }

    @Override
    public byte[] toByte() {
        ByteArrayData data = new ByteArrayData();
        if (accountAuths.size() + keyAuths.size() > 0) {
            data.appendInt(weightThreshold);
            data.appendByte(accountAuths.size());
            for (AccountAuth account : accountAuths) {
                data.putBytes(account.account.toByte());
                data.appendShort(account.weight);
            }
            data.appendByte(keyAuths.size());
            for (KeyAuth key : keyAuths) {
                data.putBytes(key.key.getPublicKey());
                data.appendShort(key.weight);
            }
            data.appendByte(addressAuths.length());
        }
        return data.toBytes();
    }

    @Override
    public Object toJson() {
        JSONObject obj = new JSONObject();
        obj.put(KEY_WEIGHT_THRESHOLD, weightThreshold);
        JSONArray array = new JSONArray();
        for (AccountAuth accountAuth : accountAuths) {
            array.put(accountAuth.toJson());
        }
        obj.put(KEY_ACCOUNT_AUTHS, array);
        array = new JSONArray();
        for (KeyAuth keyAuth : keyAuths) {
            array.put(keyAuth.toJson());
        }
        obj.put(KEY_KEY_AUTHS, array);
        obj.put(KEY_ADDRESS_AUTHS, addressAuths);
        return obj;
    }

    public void fromJson(JSONObject json) {
        try {
            weightThreshold = json.getLong(KEY_WEIGHT_THRESHOLD);
            JSONArray array = json.getJSONArray(KEY_ACCOUNT_AUTHS);
            for (int i = 0; i < array.length(); i++) {
                accountAuths.add(new AccountAuth(array.getJSONArray(i)));
            }
            array = json.getJSONArray(KEY_KEY_AUTHS);
            for (int i = 0; i < array.length(); i++) {
                keyAuths.add(new KeyAuth(array.getJSONArray(i)));
            }
            addressAuths = json.getJSONArray(KEY_ADDRESS_AUTHS);
        } catch (Exception ignored) {
        }
    }
}

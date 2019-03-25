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
import pro.bepal.coin.gxc.GXCSerializable;
import pro.bepal.coin.gxc.UserAccount;
import pro.bepal.core.gxc.GXCECKey;

public class AccountAuth implements GXCSerializable {

    public UserAccount account;

    public int weight;

    public AccountAuth() {
    }

    public AccountAuth(String accountId, int weight) {
        this.weight = weight;
        account = new UserAccount(accountId);
    }

    public AccountAuth(JSONArray json) {
        fromJson(json);
    }

    @Override
    public byte[] toByte() {
        return new byte[0];
    }

    @Override
    public Object toJson() {
        JSONArray array = new JSONArray();
        array.put(account.toString());
        array.put(weight);
        return array;
    }

    public void fromJson(JSONArray array) {
        try {
            account = new UserAccount(array.getString(0));
            weight = array.getInt(1);
        } catch (Exception ignored) {
        }
    }
}

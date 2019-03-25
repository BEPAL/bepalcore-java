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

import org.json.me.JSONObject;
import pro.bepal.categories.ByteArrayData;
import pro.bepal.coin.gxc.UserAccount;

public class AccountUpdateOperation extends BaseOperation {

    public static final String KEY_ACCOUNT = "account";
    public static final String KEY_OWNER = "owner";
    public static final String KEY_ACTIVE = "active";
    public static final String KEY_NEW_OPTIONS = "new_options";

    public UserAccount account;
    public Optional<Authority> owner;
    public Optional<Authority> active;
    public Optional<AccountOptions> newOptions;

    public AccountUpdateOperation() {
        super(OperationType.ACCOUNT_UPDATE_OPERATION);
        owner = new Optional<>();
        active = new Optional<>();
        newOptions = new Optional<>();
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put(KEY_ACCOUNT, account.getId());
        if (owner.isSet()) {
            json.put(KEY_OWNER, owner.toJson());
        }
        if (active.isSet()) {
            json.put(KEY_ACTIVE, active.toJson());
        }
        if (newOptions.isSet()) {
            json.put(KEY_NEW_OPTIONS, newOptions.toJson());
        }
        return json;
    }

    @Override
    public void fromJson(JSONObject obj) {
        super.fromJson(obj);
        try {
            account = new UserAccount(obj.getString(KEY_ACCOUNT));
            if (obj.has(KEY_OWNER)) {
                Authority authority = new Authority();
                authority.fromJson(obj.getJSONObject(KEY_OWNER));
                owner = new Optional<>(authority);
            }
            if (obj.has(KEY_ACTIVE)) {
                Authority authority = new Authority();
                authority.fromJson(obj.getJSONObject(KEY_ACTIVE));
                active = new Optional<>(authority);
            }
            if (obj.has(KEY_OWNER)) {
                AccountOptions options = new AccountOptions();
                options.fromJson(obj.getJSONObject(KEY_OWNER));
                newOptions = new Optional<>(options);
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public byte[] toByte() {
        ByteArrayData data = new ByteArrayData();
        data.putBytes(fee.toByte());
        data.putBytes(account.toByte());
        data.putBytes(owner.toByte());
        data.putBytes(active.toByte());
        data.putBytes(newOptions.toByte());
        data.putBytes(extensions.toByte());
        return data.toBytes();
    }
}

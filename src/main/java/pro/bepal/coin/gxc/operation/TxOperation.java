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
import pro.bepal.coin.gxc.AssetAmount;
import pro.bepal.coin.gxc.UserAccount;

public class TxOperation extends BaseOperation {

    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_FROM = "from";
    public static final String KEY_TO = "to";
    public static final String KEY_MEMO = "memo";

    public UserAccount from;

    public UserAccount to;

    public AssetAmount amount;

    public Memo memo;

    public TxOperation() {
        super(OperationType.TRANSFER_OPERATION);
    }

    @Override
    public byte[] toByte() {
        ByteArrayData data = new ByteArrayData();
        data.putBytes(fee.toByte());
        data.putBytes(from.toByte());
        data.putBytes(to.toByte());
        data.putBytes(amount.toByte());
        if (memo != null) {
            data.putBytes(memo.toByte());
        } else {
            data.putBytes(new byte[]{0});
        }
        data.putBytes(extensions.toByte());
        return data.toBytes();
    }

    @Override
    public void fromJson(JSONObject obj) {
        try {
            super.fromJson(obj);
            from = new UserAccount(obj.getString(KEY_FROM));
            to = new UserAccount(obj.getString(KEY_TO));
            amount = new AssetAmount(obj.getJSONObject(KEY_AMOUNT));
            if (obj.has(KEY_MEMO)) {
                memo = new Memo(obj.getJSONObject(KEY_MEMO));
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put(KEY_FROM, from.getId());
        json.put(KEY_TO, to.getId());
        json.put(KEY_AMOUNT, amount.toJson());
        if (memo != null) {
            json.put(KEY_MEMO, memo.toJson());
        }
        return json;
    }
}

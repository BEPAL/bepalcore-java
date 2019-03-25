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
import pro.bepal.coin.gxc.AssetAmount;
import pro.bepal.coin.gxc.Extensions;
import pro.bepal.coin.gxc.GXCSerializable;

public abstract class BaseOperation implements GXCSerializable {
    public static final String KEY_FEE = "fee";
    public static final String KEY_EXTENSIONS = "extensions";

    public AssetAmount fee;

    protected int opType;

    protected Extensions extensions;

    public BaseOperation(int type) {
        opType = type;
        extensions = new Extensions();
    }

    @Override
    public abstract byte[] toByte();

    public void fromJson(JSONObject obj) {
        try {
            fee = new AssetAmount(obj.getJSONObject(KEY_FEE));
            extensions = new Extensions(obj.getJSONArray(KEY_EXTENSIONS));
        } catch (Exception ignored) {
        }
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put(KEY_FEE, fee.toJson());
        json.put(KEY_EXTENSIONS, extensions.toJson());
        return json;
    }

    public JSONArray toJsonArray() {
        JSONArray array = new JSONArray();
        array.put(opType);
        array.put(toJson());
        return array;
    }

    public byte[] toOpByte() {
        ByteArrayData data = new ByteArrayData();
        data.appendByte(opType);
        data.putBytes(toByte());
        return data.toBytes();
    }

    public static BaseOperation fromJson(JSONArray array) {
        BaseOperation operation = null;
        try {
            int type = array.getInt(0);
            if (type == OperationType.TRANSFER_OPERATION) {
                operation = new TxOperation();
                operation.fromJson(array.getJSONObject(1));
            } else if (type == OperationType.ACCOUNT_CREATE_OPERATION) {
                operation = new AccountCreateOperation();
                operation.fromJson(array.getJSONObject(1));
            }
        } catch (Exception ex) {
        }
        return operation;
    }
}

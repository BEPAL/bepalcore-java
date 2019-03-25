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

import org.json.me.JSONObject;
import pro.bepal.categories.ByteArrayData;
import pro.bepal.util.ByteUtil;

public class AssetAmount implements GXCSerializable {
    /**
     * Constants used in the JSON serialization procedure.
     */
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_ASSET_ID = "asset_id";

    /**
     * 金额
     */
    public long amount;

    /**
     * 资产
     */
    public Asset asset;

    public AssetAmount(JSONObject json) {
        fromJson(json);
    }

    public AssetAmount(long amount, String asset) {
        this.amount = amount;
        this.asset = new Asset(asset);
    }

    @Override
    public byte[] toByte() {
        ByteArrayData data = new ByteArrayData();
        data.appendLong(amount);
        data.appendUVarInt(asset.instance);
        return data.toBytes();
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put(KEY_AMOUNT, amount);
        json.put(KEY_ASSET_ID, asset.getId());
        return json;
    }

    private void fromJson(JSONObject json) {
        try {
            amount = json.getLong(KEY_AMOUNT);
            asset = new Asset(json.getString(KEY_ASSET_ID));
        } catch (Exception ignored) {
        }
    }
}

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
package pro.bepal.coin.eos.abi;

import pro.bepal.coin.eos.tx.AccountName;
import org.json.me.JSONArray;
import org.json.me.JSONObject;
import pro.bepal.categories.ByteArrayData;

import java.util.ArrayList;
import java.util.List;

public class TableDef implements BaseAbi {
    /**
     * the name of the table
     */
    public AccountName name;
    /**
     * the kind of index, i64, i128i128, etc
     */
    public String indexType;
    /**
     * names for the keys defined by key_types
     */
    private List<String> keyNames = new ArrayList<>();
    /**
     * the type of key parameters
     */
    private List<String> keyTypes = new ArrayList<>();
    /**
     * type of binary data stored in this table
     */
    public String type;

    @Override
    public void parse(JSONObject data) throws Exception {
        name = new AccountName(data.getString("name"));
        type = data.getString("type");
        indexType = data.getString("index_type");
        JSONArray keyNames1 = data.getJSONArray("key_names");
        for (int i = 0; i < keyNames1.length(); i++) {
            keyNames.add(keyNames1.getString(i));
        }
        JSONArray keyTypes1 = data.has("keyTypes") ? data.getJSONArray("keyTypes") : new JSONArray();
        for (int i = 0; i < keyTypes1.length(); i++) {
            keyTypes.add(keyTypes1.getString(i));
        }
    }

    @Override
    public byte[] toData() {
        ByteArrayData stream = new ByteArrayData();
        stream.putBytes(name.accountData);
        stream.appendStringByUVar(indexType);
        stream.appendUVarInt(keyNames.size());
        for (String keyName : keyNames) {
            stream.appendStringByUVar(keyName);
        }
        stream.appendUVarInt(keyTypes.size());
        for (String keyType : keyTypes) {
            stream.appendStringByUVar(keyType);
        }
        stream.appendStringByUVar(type);
        return stream.toBytes();
    }
}

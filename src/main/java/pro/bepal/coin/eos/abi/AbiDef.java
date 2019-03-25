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

import org.json.me.JSONArray;
import org.json.me.JSONObject;
import pro.bepal.categories.ByteArrayData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

public class AbiDef implements BaseAbi {

    public String version;
    public List<TypeDef> types = new ArrayList<>();
    public List<StructDef> structs = new ArrayList<>();
    public List<ActionDef> actions = new ArrayList<>();
    public List<TableDef> tables = new ArrayList<>();
    public List<ClausePair> ricardianClauses = new ArrayList<>();
    public List<ErrorMessage> errorMessages = new ArrayList<>();
    public TreeMap<Integer, String> extensionsType = new TreeMap<>(new DataComparator());

    public AbiDef() {
        version = "eosio::abi/1.0";
    }

    @Override
    public void parse(JSONObject data) {
        try {
            version = data.getString("version");
            JSONArray types = data.getJSONArray("types");
            for (int i = 0; i < types.length(); i++) {
                TypeDef temp = new TypeDef();
                temp.parse(types.getJSONObject(i));
                this.types.add(temp);
            }
            JSONArray structs = data.getJSONArray("structs");
            for (int i = 0; i < structs.length(); i++) {
                StructDef temp = new StructDef();
                temp.parse(structs.getJSONObject(i));
                this.structs.add(temp);
            }
            JSONArray actions = data.getJSONArray("actions");
            for (int i = 0; i < actions.length(); i++) {
                ActionDef temp = new ActionDef();
                temp.parse(actions.getJSONObject(i));
                this.actions.add(temp);
            }
            JSONArray tables = data.getJSONArray("tables");
            for (int i = 0; i < tables.length(); i++) {
                TableDef temp = new TableDef();
                temp.parse(tables.getJSONObject(i));
                this.tables.add(temp);
            }
            JSONArray ricardianClauses = data.has("ricardian_clauses") ? data.getJSONArray("ricardian_clauses") : new JSONArray();
            for (int i = 0; i < ricardianClauses.length(); i++) {
                ClausePair temp = new ClausePair();
                temp.parse(ricardianClauses.getJSONObject(i));
                this.ricardianClauses.add(temp);
            }
//            JSONArray abi_extensions = data.getJSONArray("abi_extensions");
//            for (int i = 0; i < abi_extensions.length(); i++) {
//                JSONObject temp = abi_extensions.getJSONObject(i);
//                extensionsType.put(temp.getInt("key"),temp.getString("value"));
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public byte[] toData() {
        ByteArrayData stream = new ByteArrayData();
        stream.appendStringByUVar(version);
        stream.appendUVarInt(types.size());
        for (TypeDef type : types) {
            stream.putBytes(type.toData());
        }
        stream.appendUVarInt(structs.size());
        for (StructDef struct : structs) {
            stream.putBytes(struct.toData());
        }
        stream.appendUVarInt(actions.size());
        for (ActionDef action : actions) {
            stream.putBytes(action.toData());
        }
        stream.appendUVarInt(tables.size());
        for (TableDef table : tables) {
            stream.putBytes(table.toData());
        }
        stream.appendUVarInt(ricardianClauses.size());
        for (ClausePair ricardianclause : ricardianClauses) {
            stream.putBytes(ricardianclause.toData());
        }
        stream.appendUVarInt(errorMessages.size());
        stream.appendUVarInt(extensionsType.size());
        return stream.toBytes();
    }

    public class DataComparator implements Comparator {

        @Override
        public int compare(Object s, Object t1) {
            return ((Integer) s).compareTo((Integer) t1);
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }
    }
}

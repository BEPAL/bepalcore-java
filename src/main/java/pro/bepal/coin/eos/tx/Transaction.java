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
package pro.bepal.coin.eos.tx;

import pro.bepal.categories.Base58;
import pro.bepal.categories.ByteArrayData;
import pro.bepal.categories.SHAHash;
import pro.bepal.coin.eos.tx.message.newaccount.NewAccountMessageData;
import pro.bepal.coin.eos.tx.message.TxMessageData;
import pro.bepal.core.ECSign;
import pro.bepal.core.eos.EOSECKey;
import pro.bepal.util.ByteUtil;
import org.spongycastle.util.encoders.Hex;
import org.json.me.JSONArray;
import org.json.me.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

public class Transaction {

    public byte[] chainID;
    public long expiration;
    public int blockNum;//uint16
    public long blockPrefix;//uint32
    public int netUsageWords;//unsigned_int
    public int kcpuUsage;//uint8
    public int delaySec;//unsigned_int

    public List<Action> contextFreeActions = new ArrayList<>();
    public List<Action> actions = new ArrayList<>();
    public TreeMap<Integer, String> extensionsType = new TreeMap<>(new DataComparator());//uint16_t,vector<char>
    public List<byte[]> signature = new ArrayList<>();

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

    public static boolean getSign(long x, long y) {
        if (x > 0 && y > 0) {
            return x < y;
        }
        if (x < 0 && y < 0) {
            return x < y;
        }
        return x > y;
    }

    public static void sortAccountName(List<AccountName> accountNames) {
        for (int i = accountNames.size() - 1; i > 0; --i) {
            for (int j = 0; j < i; ++j) {
                if (accountNames.get(j + 1).accountValue.compareTo(accountNames.get(j).accountValue) < 0) {
                    AccountName temp = accountNames.get(j);
                    accountNames.set(j, accountNames.get(j + 1));
                    accountNames.set(j + 1, temp);
                }
            }
        }
    }

    public byte[] toByte() {
        ByteArrayData stream = new ByteArrayData();
        try {
            stream.putBytes(ByteUtil.intToBytes(expiration));
            stream.putBytes(ByteUtil.shortToBytes(blockNum));
            stream.putBytes(ByteUtil.intToBytes(blockPrefix));
            stream.putBytes(ByteUtil.uvarToBytes(netUsageWords));
            stream.appendByte((byte) kcpuUsage);
            stream.putBytes(ByteUtil.uvarToBytes(delaySec));

            stream.putBytes(ByteUtil.uvarToBytes(contextFreeActions.size()));
            for (int i = 0; i < contextFreeActions.size(); i++) {
                stream.putBytes(contextFreeActions.get(i).toByte());
            }
            stream.putBytes(ByteUtil.uvarToBytes(actions.size()));
            for (int i = 0; i < actions.size(); i++) {
                stream.putBytes(actions.get(i).toByte());
            }

            stream.putBytes(ByteUtil.uvarToBytes(extensionsType.size()));
            for (Integer key : extensionsType.keySet()) {
                int name = key;
                String value = extensionsType.get(key);
                stream.putBytes(ByteUtil.shortToBytes(name));
                stream.appendStringByUVar(value);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream.toBytes();
    }

    public void parse(byte[] dataa) {
        ByteArrayData data = new ByteArrayData(dataa);
        expiration = data.readIntByLong();
        blockNum = data.readShort();
        blockPrefix = data.readIntByLong();
        netUsageWords = (int) data.readUVarInt();
        kcpuUsage = data.readByte();
        delaySec = (int) data.readUVarInt();

        long count = data.readUVarInt();
        for (int i = 0; i < count; i++) {
            Action action = new Action();
            action.parse(data);
            contextFreeActions.add(action);
        }
        count = data.readUVarInt();
        for (int i = 0; i < count; i++) {
            Action action = new Action();
            action.parse(data);
            actions.add(action);
        }
        count = data.readUVarInt();
        for (int i = 0; i < count; i++) {
            int name = data.readShort();
            String value = data.readStringByUVar();
            extensionsType.put(name, value);
        }
    }

    public byte[] toSignData() {
        return toSignData(new byte[0]);
    }

    public byte[] toSignData(byte[] cfd) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            if (chainID == null) {
                return null;
            }
            //pack chain_id
            stream.write(chainID);
            stream.write(toByte());
            //cfd.size = 0;
            if (cfd.length > 0) {
                stream.write(SHAHash.sha2256(cfd));
            } else {
                stream.write(Hex.decode("0000000000000000000000000000000000000000000000000000000000000000"));
            }
        } catch (Exception ignored) {
        }
        return stream.toByteArray();
    }

    public byte[] getSignHash() {
        return SHAHash.sha2256(toSignData());
    }

    public byte[] getCreatePubKey() {
        NewAccountMessageData messageData = (NewAccountMessageData) actions.get(0).data;
        ByteArrayData stream = new ByteArrayData();
        stream.putBytes(messageData.name.accountData);
        stream.appendDataByUVar(messageData.active.keys.get(0).pubKey.data);
        return stream.toBytes();
    }

    public JSONObject toJson() {
        JSONArray signature = new JSONArray();
        if (this.signature.size() != 0) {
            for (byte[] aSignature : this.signature) {
                signature.put(toEOSSignature(aSignature));
            }
        }
        JSONObject endjson = new JSONObject();
        endjson.put("compression", "none");
        endjson.put("signatures", signature);
        endjson.put("packed_trx", Hex.toHexString(toByte()));
        return endjson;
    }

    public static String toEOSSignature(byte[] data) {
        try {
            ByteArrayOutputStream temp = new ByteArrayOutputStream();
            temp.write(data, 0, data.length);
            temp.write("K1".getBytes(StandardCharsets.UTF_8));

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            stream.write(data, 0, data.length);
            stream.write(SHAHash.ripemd160(temp.toByteArray()), 0, 4);
            return "SIG_K1_" + Base58.encode(stream.toByteArray());
        } catch (Exception ignored) {
        }
        return "";
    }

    public static byte[] fromEOSSignature(String data) {
        try {
            return Base58.decode(data.substring(7));
        } catch (Exception ignored) {
        }
        return new byte[0];
    }

    public String getActionType() {
        return actions.get(0).name.accountName;
    }

    public byte[] getTokenAccount() {
        return actions.get(0).account.accountData;
    }

    public byte[] getSendTo() {
        TxMessageData messageData = (TxMessageData) actions.get(0).data;
        return messageData.to.accountData;
    }

    public long getSendAmount() {
        TxMessageData messageData = (TxMessageData) actions.get(0).data;
        return messageData.amount.amount;
    }

    public byte[] getMessageData() {
        return actions.get(0).data.toByte();
    }

    public byte[] getTxID() {
        return SHAHash.sha2256(toByte());
    }

    public boolean verifyTx(String pubKey) {
        System.out.println(Hex.toHexString(getSignHash()));
        return EOSECKey.fromPublicKey(pubKey).verify(getSignHash(), ECSign.fromData(this.signature.get(0)));
    }
}
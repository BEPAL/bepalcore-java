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
import org.spongycastle.util.encoders.Hex;
import pro.bepal.categories.ByteArrayData;
import pro.bepal.coin.eos.tx.AccountName;
import pro.bepal.coin.gxc.AssetAmount;
import pro.bepal.coin.gxc.UserAccount;

public class ContractCallOperation extends BaseOperation {
    public static final String KEY_ACCOUNT = "account";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_CONTRACT_ID = "contract_id";
    public static final String KEY_METHOD_NAME = "method_name";
    public static final String KEY_DATA = "data";

    /**
     * 执行账户
     */
    public UserAccount account;

    /**
     * 执行的金额
     */
    public Optional<AssetAmount> amount;

    /**
     * 执行的合约
     */
    public UserAccount contractAccount;

    /**
     * 执行的合约方法名
     */
    public String methodName;

    /**
     * 执行的合约参数
     */
    public byte[] data;

    public ContractCallOperation() {
        super(OperationType.CONTRACT_CALL_OPERATION);
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put(KEY_ACCOUNT, account.getId());
        json.put(KEY_AMOUNT, amount.toJson());
        json.put(KEY_CONTRACT_ID, contractAccount.getId());
        json.put(KEY_METHOD_NAME, methodName);
        json.put(KEY_DATA, Hex.toHexString(data));
        return json;
    }

    @Override
    public void fromJson(JSONObject obj) {
        super.fromJson(obj);
        try {
            account = new UserAccount(obj.getString(KEY_ACCOUNT));
            amount = new Optional<>(new AssetAmount(obj.getJSONObject(KEY_AMOUNT)));
            contractAccount = new UserAccount(obj.getString(KEY_CONTRACT_ID));
            methodName = obj.getString(KEY_METHOD_NAME);
            data = Hex.decode(obj.getString(KEY_DATA));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public byte[] toByte() {
        ByteArrayData data = new ByteArrayData();
        data.putBytes(fee.toByte());
        data.putBytes(account.toByte());
        data.putBytes(contractAccount.toByte());
        data.putBytes(amount.toByte());
        data.putBytes(AccountName.getData(methodName));
        data.appendData(this.data);
        data.putBytes(extensions.toByte());
        return data.toBytes();
    }
}

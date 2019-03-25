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
import pro.bepal.coin.gxc.UserAccount;
import pro.bepal.util.ByteUtil;
import pro.bepal.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

public class ProposalCreateOperation extends BaseOperation {
    public static final String KEY_FEE_PAYING_ACCOUNT = "fee_paying_account";
    public static final String KEY_EXPIRATION_TIME = "expiration_time";
    public static final String KEY_PROPOSED_OPS = "proposed_ops";
    public static final String KEY_OP = "op";

    public UserAccount feePayingAccount;

    public long expirationTime;

    public List<BaseOperation> proposedOps = new ArrayList<>();

    public ProposalCreateOperation() {
        super(OperationType.PROPOSAL_CREATE_OPERATION);
        expirationTime = System.currentTimeMillis() + 24 * 60 * 60 * 1000;
    }

    @Override
    public byte[] toByte() {
        ByteArrayData data = new ByteArrayData();
        data.putBytes(fee.toByte());
        data.putBytes(feePayingAccount.toByte());
        data.putBytes(ByteUtil.intToBytes(expirationTime));
        data.appendByte(proposedOps.size());
        for (BaseOperation proposedOp : proposedOps) {
            data.putBytes(proposedOp.toOpByte());
        }
        data.putBytes(extensions.toByte());
        return data.toBytes();
    }

    public void addOperation(BaseOperation operation) {
        proposedOps.add(operation);
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put(KEY_FEE_PAYING_ACCOUNT, feePayingAccount.getId());
        json.put(KEY_EXPIRATION_TIME, TimeUtil.timeStampToUtc(expirationTime));

        JSONArray array = new JSONArray();
        for (int i = 0; i < proposedOps.size(); i++) {
            JSONObject obj = new JSONObject();
            obj.put(KEY_OP, proposedOps.get(i).toJsonArray());
            array.put(obj);
        }
        json.put(KEY_PROPOSED_OPS, array);
        return json;
    }
}

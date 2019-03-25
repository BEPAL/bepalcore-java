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

import pro.bepal.categories.ByteArrayData;
import pro.bepal.coin.eos.tx.message.*;
import pro.bepal.coin.eos.tx.message.newaccount.NewAccountMessageData;
import pro.bepal.util.ByteUtil;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Action {
    /**
     * 合约账户名
     */
    public AccountName account;
    /**
     * 合约方法
     */
    public AccountName name;
    /**
     * 一般是签名私钥的权限
     */
    public List<AccountPermission> authorization = new ArrayList<>();//permission_level
    public MessageData data;

    public byte[] toByte() {
        ByteArrayData stream = new ByteArrayData();
        try {
            stream.putBytes(account.accountData);
            stream.putBytes(name.accountData);

            stream.putBytes(ByteUtil.uvarToBytes(authorization.size()));
            for (int i = 0; i < authorization.size(); i++) {
                stream.putBytes(authorization.get(i).toByte());
            }
            byte[] data = this.data.toByte();
            stream.putBytes(ByteUtil.uvarToBytes(data.length));
            stream.putBytes(data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream.toBytes();
    }

    public void parse(ByteArrayData data) {
        account = new AccountName(data.readData(8));
        name = new AccountName(data.readData(8));
        long count = data.readUVarInt();
        for (int i = 0; i < count; i++) {
            AccountPermission permission = new AccountPermission();
            permission.parse(data);
            authorization.add(permission);
        }
        if ("transfer".equals(name.accountName)) {
            this.data = new TxMessageData();
        } else if ("newaccount".equals(name.accountName)) {
            this.data = new NewAccountMessageData();
        } else if ("buyram".equals(name.accountName)) {
            this.data = new BuyRamMessageData();
        } else if ("sellram".equals(name.accountName)) {
            this.data = new SellRamMessageData();
        } else if ("delegatebw".equals(name.accountName)) {
            this.data = new DelegatebwMessageData();
        } else if ("undelegatebw".equals(name.accountName)) {
            this.data = new UnDelegatebwMessageData();
        } else if ("voteproducer".equals(name.accountName)) {
            this.data = new VoteProducerMessageData();
        } else if ("regproxy".equals(name.accountName)) {
            this.data = new RegProxyMessageData();
        } else if ("claimrewards".equals(name.accountName)) {
            this.data = new ClaimrewardsMessageData();
        } else if ("setcode".equals(name.accountName)) {
            this.data = new SetCodeMessageData();
        } else if ("setabi".equals(name.accountName)) {
            this.data = new SetAbiMessageData();
        } else if ("create".equals(name.accountName)) {
            this.data = new CreateMessageData();
        } else if ("issue".equals(name.accountName)) {
            this.data = new IssueMessageData();
        } else if ("updateauth".equals(name.accountName)) {
            this.data = new UpdateAuthMessageData();
        } else if ("deleteauth".equals(name.accountName)) {
            this.data = new DeleteAuthMessageData();
        } else if ("linkauth".equals(name.accountName)) {
            this.data = new LinkAuthMessageData();
        } else if ("unlinkauth".equals(name.accountName)) {
            this.data = new UnLinkAuthMessageData();
        } else {
            this.data = new ByteMessageData();
        }

        this.data.parse(data.readDataByUVar());
    }
}
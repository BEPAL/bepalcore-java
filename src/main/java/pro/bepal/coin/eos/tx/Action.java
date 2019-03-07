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
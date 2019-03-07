package pro.bepal.coin.eos.tx.message;

import pro.bepal.categories.ByteArrayData;
import pro.bepal.coin.eos.tx.AccountName;

public class IssueMessageData implements MessageData {

    /**
     * 发行到哪个人的代币
     */
    public AccountName to;
    /**
     * 金额
     */
    public Asset quantity;
    /**
     * 备注
     */
    public String memo;

    public IssueMessageData() {
        memo = "issue";
    }

    @Override
    public byte[] toByte() {
        ByteArrayData stream = new ByteArrayData();
        try {
            stream.putBytes(to.accountData);
            quantity.toByte(stream);
            stream.appendStringByUVar(memo);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream.toBytes();
    }

    @Override
    public void parse(byte[] dataa) {
        ByteArrayData data = new ByteArrayData(dataa);
        to = new AccountName(data.readData(8));
        quantity = new Asset();
        quantity.parse(data);
        memo = data.readStringByUVar();
    }
}

package pro.bepal.coin.eos.tx.message;

import pro.bepal.coin.eos.tx.AccountName;
import pro.bepal.categories.ByteArrayData;
import pro.bepal.util.ByteUtil;

public class TxMessageData implements MessageData {
    /**
     * 发送人
     */
    public AccountName from;
    /**
     * 接收人
     */
    public AccountName to;
    /**
     * 发送金额
     */
    public Asset amount;
    /**
     * 发送的备注
     */
    public byte[] data;

    @Override
    public byte[] toByte() {
        ByteArrayData stream = new ByteArrayData();
        try {
            stream.putBytes(from.accountData);
            stream.putBytes(to.accountData);
            amount.toByte(stream);
            if (data != null) {
                stream.putBytes(ByteUtil.uvarToBytes(data.length));
                stream.putBytes(data);
            } else {
                stream.appendByte((byte) 0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream.toBytes();
    }

    @Override
    public void parse(byte[] dataa) {
        ByteArrayData data = new ByteArrayData(dataa);
        from = new AccountName(data.readData(8));
        to = new AccountName(data.readData(8));
        amount = Asset.toAsset(data);
        if (data.hasData()) {
            this.data = data.readDataByUVar();
        } else {
            this.data = new byte[0];
        }
    }
}

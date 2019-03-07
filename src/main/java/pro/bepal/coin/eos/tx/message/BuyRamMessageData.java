package pro.bepal.coin.eos.tx.message;

import pro.bepal.categories.ByteArrayData;
import pro.bepal.coin.eos.tx.AccountName;

public class BuyRamMessageData implements MessageData {

    /**
     * 购买人
     */
    public AccountName payer;
    /**
     * 接收人
     */
    public AccountName receiver;
    /**
     * 购买量
     */
    public Asset quant;

    @Override
    public byte[] toByte() {
        ByteArrayData stream = new ByteArrayData();
        try {
            stream.putBytes(payer.accountData);
            stream.putBytes(receiver.accountData);
            quant.toByte(stream);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream.toBytes();
    }

    @Override
    public void parse(byte[] dataa) {
        ByteArrayData data = new ByteArrayData(dataa);
        payer = new AccountName(data.readData(8));
        receiver = new AccountName(data.readData(8));
        quant = Asset.toAsset(data);
    }
}

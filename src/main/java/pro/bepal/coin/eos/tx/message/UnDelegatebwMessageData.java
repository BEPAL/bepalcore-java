package pro.bepal.coin.eos.tx.message;

import pro.bepal.categories.ByteArrayData;
import pro.bepal.coin.eos.tx.AccountName;

public class UnDelegatebwMessageData implements MessageData {

    public AccountName from;
    public AccountName receiver;
    public Asset stakeNetQuantity;
    public Asset stakeCpuQuantity;

    @Override
    public byte[] toByte() {
        ByteArrayData stream = new ByteArrayData();
        try {
            stream.putBytes(from.accountData);
            stream.putBytes(receiver.accountData);
            stakeNetQuantity.toByte(stream);
            stakeCpuQuantity.toByte(stream);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream.toBytes();
    }

    @Override
    public void parse(byte[] dataa) {
        ByteArrayData data = new ByteArrayData(dataa);
        from = new AccountName(data.readData(8));
        receiver = new AccountName(data.readData(8));
        stakeNetQuantity = Asset.toAsset(data);
        stakeCpuQuantity = Asset.toAsset(data);
    }
}

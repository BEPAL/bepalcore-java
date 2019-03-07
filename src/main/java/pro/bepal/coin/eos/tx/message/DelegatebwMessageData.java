package pro.bepal.coin.eos.tx.message;

import pro.bepal.coin.eos.tx.AccountName;
import pro.bepal.util.ByteUtil;
import pro.bepal.categories.ByteArrayData;

public class DelegatebwMessageData implements MessageData {

    /**
     * 质押人
     */
    public AccountName from;
    /**
     * 获得人
     */
    public AccountName receiver;
    /**
     * 质押获得的Net
     */
    public Asset stakeNetQuantity;
    /**
     * 质押获得的Cpu
     */
    public Asset stakeCpuQuantity;
    /**
     * 是否送给对方 应填写1 否则难以反质押
     */
    public long transfer;

    @Override
    public byte[] toByte() {
        ByteArrayData stream = new ByteArrayData();
        try {
            stream.putBytes(from.accountData);
            stream.putBytes(receiver.accountData);
            stakeNetQuantity.toByte(stream);
            stakeCpuQuantity.toByte(stream);
            stream.putBytes(ByteUtil.uvarToBytes(transfer));
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
        transfer = data.readUVarInt();
    }
}

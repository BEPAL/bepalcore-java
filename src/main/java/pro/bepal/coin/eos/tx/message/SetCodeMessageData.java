package pro.bepal.coin.eos.tx.message;

import pro.bepal.coin.eos.tx.AccountName;
import pro.bepal.categories.ByteArrayData;

public class SetCodeMessageData implements MessageData {

    /**
     * 设置到的账户
     */
    public AccountName account;
    /**
     * 默认为0
     */
    public long vmType;
    /**
     * 默认为0
     */
    public long vmVersion;
    /**
     * 设置的编译内容
     */
    public byte[] code;

    @Override
    public byte[] toByte() {
        ByteArrayData stream = new ByteArrayData();
        try {
            stream.putBytes(account.accountData);
            stream.appendUVarInt(vmType);
            stream.appendUVarInt(vmVersion);
            stream.appendDataByUVar(code);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream.toBytes();
    }

    @Override
    public void parse(byte[] dataa) {
        ByteArrayData data = new ByteArrayData(dataa);
        account = new AccountName(data.readData(8));
        vmType = data.readUVarInt();
        vmVersion = data.readUVarInt();
        if (data.hasData()) {
            code = data.readDataByUVar();
        } else {
            code = new byte[0];
        }
    }
}

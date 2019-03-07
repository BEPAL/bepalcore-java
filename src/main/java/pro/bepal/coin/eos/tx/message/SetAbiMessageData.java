package pro.bepal.coin.eos.tx.message;

import pro.bepal.categories.ByteArrayData;
import pro.bepal.coin.eos.tx.AccountName;

public class SetAbiMessageData implements MessageData{

    /**
     * 设置ABI到的账户
     */
    public AccountName account;
    /**
     * ABI内容
     */
    public byte[] abi;

    @Override
    public byte[] toByte() {
        ByteArrayData stream = new ByteArrayData();
        try {
            stream.putBytes(account.accountData);
            stream.appendDataByUVar(abi);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream.toBytes();
    }

    @Override
    public void parse(byte[] dataa) {
        ByteArrayData data = new ByteArrayData(dataa);
        account = new AccountName(data.readData(8));
        if (data.hasData()) {
            abi = data.readDataByUVar();
        } else {
            abi = new byte[0];
        }
    }
}

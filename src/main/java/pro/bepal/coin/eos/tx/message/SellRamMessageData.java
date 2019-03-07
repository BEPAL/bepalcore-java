package pro.bepal.coin.eos.tx.message;

import pro.bepal.coin.eos.tx.AccountName;
import pro.bepal.util.ByteUtil;
import pro.bepal.categories.ByteArrayData;

import java.io.ByteArrayOutputStream;

public class SellRamMessageData implements MessageData {

    public AccountName account;

    /**
     * 单位是字节
     */
    public long bytes;

    @Override
    public byte[] toByte() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write(account.accountData);
            stream.write(ByteUtil.longToBytes(bytes));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream.toByteArray();
    }

    @Override
    public void parse(byte[] dataa) {
        ByteArrayData data = new ByteArrayData(dataa);
        account = new AccountName(data.readData(8));
        bytes = data.readLong();
    }
}

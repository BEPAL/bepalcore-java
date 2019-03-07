package pro.bepal.coin.eos.tx.message.newaccount;

import pro.bepal.categories.ByteArrayData;
import pro.bepal.coin.eos.tx.AccountName;
import pro.bepal.coin.eos.tx.message.MessageData;

import java.io.ByteArrayOutputStream;

public class NewAccountMessageData implements MessageData {

    /**
     * 创建人
     */
    public AccountName creator;
    /**
     * 被创建人
     */
    public AccountName name;
    /**
     * Owner权限的公钥
     */
    public Authority owner;
    /**
     * Active权限的公钥
     */
    public Authority active;

    @Override
    public byte[] toByte() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write(creator.accountData);
            stream.write(name.accountData);
            stream.write(owner.toByte());
            stream.write(active.toByte());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream.toByteArray();
    }

    @Override
    public void parse(byte[] dataa) {
        ByteArrayData data = new ByteArrayData(dataa);
        creator = new AccountName(data.readData(8));
        name = new AccountName(data.readData(8));
        owner = new Authority();
        owner.parse(data);
        active = new Authority();
        active.parse(data);
    }
}

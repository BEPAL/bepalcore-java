package pro.bepal.coin.eos.tx.message;

import pro.bepal.categories.ByteArrayData;
import pro.bepal.coin.eos.tx.AccountName;

/**
 * 创建代币
 */
public class CreateMessageData implements MessageData {

    /**
     * 发行的代币所有权
     */
    public AccountName issuer;

    /**
     * 发行的总量
     */
    public Asset maximumSupply;

    @Override
    public byte[] toByte() {
        ByteArrayData stream = new ByteArrayData();
        try {
            stream.putBytes(issuer.accountData);
            maximumSupply.toByte(stream);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream.toBytes();
    }

    @Override
    public void parse(byte[] dataa) {
        ByteArrayData data = new ByteArrayData(dataa);
        issuer = new AccountName(data.readData(8));
        maximumSupply = new Asset();
        maximumSupply.parse(data);
    }
}

package pro.bepal.coin.eos.tx.message;

import pro.bepal.categories.ByteArrayData;
import pro.bepal.coin.eos.tx.AccountName;

import java.io.ByteArrayOutputStream;

public class ClaimrewardsMessageData implements MessageData {

    /**
     * 领取奖励时获得奖励的人
     */
    public AccountName owner;

    @Override
    public byte[] toByte() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write(owner.accountData);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream.toByteArray();
    }

    @Override
    public void parse(byte[] data) {
        ByteArrayData data1 = new ByteArrayData(data);
        owner = new AccountName(data1.readData(8));
    }
}

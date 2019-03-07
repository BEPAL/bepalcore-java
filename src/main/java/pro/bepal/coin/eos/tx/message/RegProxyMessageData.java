package pro.bepal.coin.eos.tx.message;

import pro.bepal.coin.eos.tx.AccountName;
import pro.bepal.categories.ByteArrayData;

import java.io.ByteArrayOutputStream;

public class RegProxyMessageData implements MessageData {

    public AccountName proxy;

    /**
     * 是否注册代理
     */
    public boolean isProxy;

    @Override
    public byte[] toByte() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write(proxy.accountData);
            stream.write(isProxy ? 1 : 0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream.toByteArray();
    }

    @Override
    public void parse(byte[] dataa) {
        ByteArrayData data = new ByteArrayData(dataa);
        proxy = new AccountName(data.readData(8));
        isProxy = data.readByte() == 1;
    }
}

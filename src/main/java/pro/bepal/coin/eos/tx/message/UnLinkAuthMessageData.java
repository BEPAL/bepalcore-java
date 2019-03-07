package pro.bepal.coin.eos.tx.message;

import pro.bepal.coin.eos.tx.AccountName;
import pro.bepal.categories.ByteArrayData;

import java.io.ByteArrayOutputStream;

public class UnLinkAuthMessageData implements MessageData {

    /**
     * 取消授权的账户
     */
    public AccountName account;
    /**
     * 执行合约的地址
     */
    public AccountName code;
    /**
     * 执行合约的方法
     */
    public AccountName type;

    @Override
    public byte[] toByte() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write(account.accountData);
            stream.write(code.accountData);
            stream.write(type.accountData);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream.toByteArray();
    }

    @Override
    public void parse(byte[] dataa) {
        ByteArrayData data = new ByteArrayData(dataa);
        account = new AccountName(data.readData(8));
        code = new AccountName(data.readData(8));
        type = new AccountName(data.readData(8));
    }
}

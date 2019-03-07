package pro.bepal.coin.eos.tx.message;

import pro.bepal.coin.eos.tx.AccountName;
import pro.bepal.categories.ByteArrayData;

import java.io.ByteArrayOutputStream;

public class LinkAuthMessageData implements MessageData {

    public AccountName account;
    /**
     * 执行合约的地址
     */
    public AccountName code;
    /**
     * 执行合约的方法
     */
    public AccountName type;
    /**
     * 赋值给的权限的子账户名称
     */
    public AccountName requirement;

    @Override
    public byte[] toByte() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write(account.accountData);
            stream.write(code.accountData);
            stream.write(type.accountData);
            stream.write(requirement.accountData);
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
        requirement = new AccountName(data.readData(8));
    }
}

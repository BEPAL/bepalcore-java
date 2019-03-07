package pro.bepal.coin.eos.tx.message;

import pro.bepal.coin.eos.tx.AccountName;
import pro.bepal.categories.ByteArrayData;

import java.io.ByteArrayOutputStream;

public class DeleteAuthMessageData implements MessageData {

    /**
     * 账户名
     */
    public AccountName account;
    /**
     * 被删除的权限
     */
    public AccountName permission;

    @Override
    public byte[] toByte() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write(account.accountData);
            stream.write(permission.accountData);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream.toByteArray();
    }

    @Override
    public void parse(byte[] dataa) {
        ByteArrayData data = new ByteArrayData(dataa);
        account = new AccountName(data.readData(8));
        permission = new AccountName(data.readData(8));
    }
}

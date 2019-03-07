package pro.bepal.coin.eos.tx.message;

import pro.bepal.categories.ByteArrayData;
import pro.bepal.coin.eos.tx.AccountName;
import pro.bepal.coin.eos.tx.message.newaccount.Authority;

import java.io.ByteArrayOutputStream;

/**
 * 添加或修改和账户的权限
 */
public class UpdateAuthMessageData implements MessageData  {


    public AccountName account;
    /**
     * 权限名称
     */
    public AccountName permission;
    /**
     * 权限来源 active owner
     */
    public AccountName parent;
    /**
     * 公钥信息
     */
    public Authority auth;

    @Override
    public byte[] toByte() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write(account.accountData);
            stream.write(permission.accountData);
            stream.write(parent.accountData);
            stream.write(auth.toByte());
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
        parent = new AccountName(data.readData(8));
        auth = new Authority();
        auth.parse(data);
    }
}

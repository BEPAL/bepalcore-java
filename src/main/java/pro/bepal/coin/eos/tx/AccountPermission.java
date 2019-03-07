package pro.bepal.coin.eos.tx;

import pro.bepal.categories.ByteArrayData;

import java.io.ByteArrayOutputStream;

/**
 * 一般用于交易中当前签名私钥在账户中的权限
 */
public class AccountPermission {
    /**
     * 账户名
     */
    public AccountName account;
    /**
     * 权限
     */
    public AccountName permission;

    public AccountPermission() {

    }

    public AccountPermission(String account, String permission) {
        this.account = new AccountName(account);
        this.permission = new AccountName(permission);
    }

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

    public void parse(ByteArrayData data) {
        account = new AccountName(data.readData(8));
        this.permission = new AccountName(data.readData(8));
    }
}

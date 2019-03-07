package pro.bepal.coin.eos.tx.message.newaccount;

import pro.bepal.categories.ByteArrayData;
import pro.bepal.coin.eos.tx.AccountPermission;

public class AccountPermissionWeight {

    public AccountPermission permission;
    public int weight;

    public AccountPermissionWeight() {
        weight = 1;
    }

    public AccountPermissionWeight(String account, String permission) {
        this();
        this.permission = new AccountPermission(account, permission);
    }

    public byte[] toByte() {
        ByteArrayData stream = new ByteArrayData();
        try {
            stream.putBytes(permission.toByte());
            stream.appendShort(weight);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream.toBytes();
    }

    public void parse(ByteArrayData data) {
        permission = new AccountPermission();
        permission.parse(data);
        weight = data.readShort();
    }
}

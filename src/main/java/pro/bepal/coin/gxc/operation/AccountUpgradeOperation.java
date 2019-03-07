package pro.bepal.coin.gxc.operation;

import org.json.me.JSONObject;
import pro.bepal.categories.ByteArrayData;
import pro.bepal.coin.gxc.UserAccount;

public class AccountUpgradeOperation extends BaseOperation {

    public static final String ACCOUNT_TO_UPGRADE = "account_to_upgrade";
    public static final String UPGRADE_TO_LIFETIME_MEMBER = "upgrade_to_lifetime_member";

    public UserAccount account;

    public boolean toLifeTimeMember = true;

    public AccountUpgradeOperation() {
        super(OperationType.ACCOUNT_UPGRADE_OPERATION);
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put(ACCOUNT_TO_UPGRADE, account.getId());
        json.put(UPGRADE_TO_LIFETIME_MEMBER, toLifeTimeMember);
        return json;
    }

    @Override
    public void fromJson(JSONObject obj) {
        super.fromJson(obj);
        try {
            account = new UserAccount(obj.getString(ACCOUNT_TO_UPGRADE));
            toLifeTimeMember = obj.getBoolean(UPGRADE_TO_LIFETIME_MEMBER);
        } catch (Exception ignored) {
        }
    }

    @Override
    public byte[] toByte() {
        ByteArrayData data = new ByteArrayData();
        data.putBytes(fee.toByte());
        data.putBytes(account.toByte());
        data.appendByte(toLifeTimeMember ? 1 : 0);
        data.putBytes(extensions.toByte());
        return data.toBytes();
    }
}

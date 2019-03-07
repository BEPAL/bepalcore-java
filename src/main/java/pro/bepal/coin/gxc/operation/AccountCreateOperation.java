package pro.bepal.coin.gxc.operation;

import org.json.me.JSONObject;
import pro.bepal.categories.ByteArrayData;
import pro.bepal.coin.gxc.UserAccount;

public class AccountCreateOperation extends BaseOperation {

    public static final String KEY_REGISTRAR = "registrar";
    public static final String KEY_REFERRER = "referrer";
    public static final String KEY_REFERRER_PERCENT = "referrer_percent";
    public static final String KEY_NAME = "name";
    public static final String KEY_OWNER = "owner";
    public static final String KEY_ACTIVE = "active";
    public static final String KEY_OPTIONS = "options";

    public UserAccount registrar;

    public UserAccount referrer;

    private short referrerPercent = 0;

    public String name;

    public Authority owner;

    public Authority active;

    public AccountOptions options;

    public AccountCreateOperation() {
        super(OperationType.ACCOUNT_CREATE_OPERATION);
    }

    @Override
    public byte[] toByte() {
        ByteArrayData data = new ByteArrayData();
        data.putBytes(fee.toByte());
        data.putBytes(registrar.toByte());
        data.putBytes(referrer.toByte());
        data.appendShort(referrerPercent);
        data.appendString(name);
        data.putBytes(owner.toByte());
        data.putBytes(active.toByte());
        data.putBytes(options.toByte());
        data.putBytes(extensions.toByte());
        return data.toBytes();
    }

    @Override
    public void fromJson(JSONObject obj) {
        try {
            registrar = new UserAccount(obj.getString(KEY_REGISTRAR));

        } catch (Exception ex) {
        }
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put(KEY_REGISTRAR, registrar.getId());
        json.put(KEY_REFERRER, referrer.getId());
        json.put(KEY_REFERRER_PERCENT, referrerPercent);
        json.put(KEY_NAME, name);
        json.put(KEY_OWNER, owner.toJson());
        json.put(KEY_ACTIVE, active.toJson());
        json.put(KEY_OPTIONS, options.toJson());
        return json;
    }
}

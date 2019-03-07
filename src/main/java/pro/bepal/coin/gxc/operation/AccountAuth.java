package pro.bepal.coin.gxc.operation;

import org.json.me.JSONArray;
import org.json.me.JSONObject;
import pro.bepal.coin.gxc.GXCSerializable;
import pro.bepal.coin.gxc.UserAccount;
import pro.bepal.core.gxc.GXCECKey;

public class AccountAuth implements GXCSerializable {

    public UserAccount account;

    public int weight;

    public AccountAuth() {
    }

    public AccountAuth(String accountId, int weight) {
        this.weight = weight;
        account = new UserAccount(accountId);
    }

    public AccountAuth(JSONArray json) {
        fromJson(json);
    }

    @Override
    public byte[] toByte() {
        return new byte[0];
    }

    @Override
    public Object toJson() {
        JSONArray array = new JSONArray();
        array.put(account.toString());
        array.put(weight);
        return array;
    }

    public void fromJson(JSONArray array) {
        try {
            account = new UserAccount(array.getString(0));
            weight = array.getInt(1);
        } catch (Exception ignored) {
        }
    }
}

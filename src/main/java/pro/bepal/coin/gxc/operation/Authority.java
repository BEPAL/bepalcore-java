package pro.bepal.coin.gxc.operation;

import org.json.me.JSONArray;
import org.json.me.JSONObject;
import pro.bepal.categories.ByteArrayData;
import pro.bepal.coin.gxc.GXCSerializable;

import java.util.ArrayList;
import java.util.List;

public class Authority implements GXCSerializable {

    public static final String KEY_ACCOUNT_AUTHS = "account_auths";
    public static final String KEY_KEY_AUTHS = "key_auths";
    public static final String KEY_WEIGHT_THRESHOLD = "weight_threshold";
    public static final String KEY_ADDRESS_AUTHS = "address_auths";

    public long weightThreshold;
    public List<AccountAuth> accountAuths;
    private List<KeyAuth> keyAuths;
    private JSONArray addressAuths;

    public Authority() {
        weightThreshold = 1;
        accountAuths = new ArrayList<>();
        keyAuths = new ArrayList<>();
        addressAuths = new JSONArray();
    }

    public Authority(String key) {
        this();
        KeyAuth auth = new KeyAuth(key);
        keyAuths.add(auth);
    }

    public void addKey(String key) {
        KeyAuth auth = new KeyAuth(key);
        keyAuths.add(auth);
    }

    @Override
    public byte[] toByte() {
        ByteArrayData data = new ByteArrayData();
        if (accountAuths.size() + keyAuths.size() > 0) {
            data.appendInt(weightThreshold);
            data.appendByte(accountAuths.size());
            for (AccountAuth account : accountAuths) {
                data.putBytes(account.account.toByte());
                data.appendShort(account.weight);
            }
            data.appendByte(keyAuths.size());
            for (KeyAuth key : keyAuths) {
                data.putBytes(key.key.getPublicKey());
                data.appendShort(key.weight);
            }
            data.appendByte(addressAuths.length());
        }
        return data.toBytes();
    }

    @Override
    public Object toJson() {
        JSONObject obj = new JSONObject();
        obj.put(KEY_WEIGHT_THRESHOLD, weightThreshold);
        JSONArray array = new JSONArray();
        for (AccountAuth accountAuth : accountAuths) {
            array.put(accountAuth.toJson());
        }
        obj.put(KEY_ACCOUNT_AUTHS, array);
        array = new JSONArray();
        for (KeyAuth keyAuth : keyAuths) {
            array.put(keyAuth.toJson());
        }
        obj.put(KEY_KEY_AUTHS, array);
        obj.put(KEY_ADDRESS_AUTHS, addressAuths);
        return obj;
    }

    public void fromJson(JSONObject json) {
        try {
            weightThreshold = json.getLong(KEY_WEIGHT_THRESHOLD);
            JSONArray array = json.getJSONArray(KEY_ACCOUNT_AUTHS);
            for (int i = 0; i < array.length(); i++) {
                accountAuths.add(new AccountAuth(array.getJSONArray(i)));
            }
            array = json.getJSONArray(KEY_KEY_AUTHS);
            for (int i = 0; i < array.length(); i++) {
                keyAuths.add(new KeyAuth(array.getJSONArray(i)));
            }
            addressAuths = json.getJSONArray(KEY_ADDRESS_AUTHS);
        } catch (Exception ignored) {
        }
    }
}

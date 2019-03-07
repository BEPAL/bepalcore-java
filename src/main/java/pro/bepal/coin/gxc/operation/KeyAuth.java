package pro.bepal.coin.gxc.operation;

import org.json.me.JSONArray;
import pro.bepal.coin.gxc.GXCSerializable;
import pro.bepal.core.gxc.GXCECKey;

public class KeyAuth implements GXCSerializable {

    public GXCECKey key;

    public int weight;

    public KeyAuth() {
        weight = 1;
    }

    public KeyAuth(String key) {
        this();
        this.key = GXCECKey.fromPublicKey(key);
    }

    public KeyAuth(JSONArray json) {
        fromJson(json);
    }

    @Override
    public byte[] toByte() {
        return new byte[0];
    }

    @Override
    public Object toJson() {
        JSONArray array = new JSONArray();
        array.put(key.toPubblicKeyString());
        array.put(weight);
        return array;
    }

    public void fromJson(JSONArray json) {
        try {
            key = GXCECKey.fromPublicKey(json.getString(0));
            weight = json.getInt(1);
        } catch (Exception ignored) {
        }
    }
}

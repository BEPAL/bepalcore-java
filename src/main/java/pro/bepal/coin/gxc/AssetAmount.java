package pro.bepal.coin.gxc;

import org.json.me.JSONObject;
import pro.bepal.categories.ByteArrayData;
import pro.bepal.util.ByteUtil;

public class AssetAmount implements GXCSerializable {
    /**
     * Constants used in the JSON serialization procedure.
     */
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_ASSET_ID = "asset_id";

    /**
     * 金额
     */
    public long amount;

    /**
     * 资产
     */
    public Asset asset;

    public AssetAmount(JSONObject json) {
        fromJson(json);
    }

    public AssetAmount(long amount, String asset) {
        this.amount = amount;
        this.asset = new Asset(asset);
    }

    @Override
    public byte[] toByte() {
        ByteArrayData data = new ByteArrayData();
        data.appendLong(amount);
        data.appendUVarInt(asset.instance);
        return data.toBytes();
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put(KEY_AMOUNT, amount);
        json.put(KEY_ASSET_ID, asset.getId());
        return json;
    }

    private void fromJson(JSONObject json) {
        try {
            amount = json.getLong(KEY_AMOUNT);
            asset = new Asset(json.getString(KEY_ASSET_ID));
        } catch (Exception ignored) {
        }
    }
}

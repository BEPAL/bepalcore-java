package pro.bepal.coin.gxc.operation;

import org.json.me.JSONArray;
import org.json.me.JSONObject;
import pro.bepal.categories.ByteArrayData;
import pro.bepal.coin.gxc.AssetAmount;
import pro.bepal.coin.gxc.Extensions;
import pro.bepal.coin.gxc.GXCSerializable;

public abstract class BaseOperation implements GXCSerializable {
    public static final String KEY_FEE = "fee";
    public static final String KEY_EXTENSIONS = "extensions";

    public AssetAmount fee;

    protected int opType;

    protected Extensions extensions;

    public BaseOperation(int type) {
        opType = type;
        extensions = new Extensions();
    }

    @Override
    public abstract byte[] toByte();

    public void fromJson(JSONObject obj) {
        try {
            fee = new AssetAmount(obj.getJSONObject(KEY_FEE));
            extensions = new Extensions(obj.getJSONArray(KEY_EXTENSIONS));
        } catch (Exception ignored) {
        }
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put(KEY_FEE, fee.toJson());
        json.put(KEY_EXTENSIONS, extensions.toJson());
        return json;
    }

    public JSONArray toJsonArray() {
        JSONArray array = new JSONArray();
        array.put(opType);
        array.put(toJson());
        return array;
    }

    public byte[] toOpByte() {
        ByteArrayData data = new ByteArrayData();
        data.appendByte(opType);
        data.putBytes(toByte());
        return data.toBytes();
    }

    public static BaseOperation fromJson(JSONArray array) {
        BaseOperation operation = null;
        try {
            int type = array.getInt(0);
            if (type == OperationType.TRANSFER_OPERATION) {
                operation = new TxOperation();
                operation.fromJson(array.getJSONObject(1));
            } else if (type == OperationType.ACCOUNT_CREATE_OPERATION) {
                operation = new AccountCreateOperation();
                operation.fromJson(array.getJSONObject(1));
            }
        } catch (Exception ex) {
        }
        return operation;
    }
}

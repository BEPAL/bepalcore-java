package pro.bepal.coin.gxc.operation;

import org.json.me.JSONObject;
import pro.bepal.categories.ByteArrayData;
import pro.bepal.coin.gxc.AssetAmount;
import pro.bepal.coin.gxc.UserAccount;

public class TxOperation extends BaseOperation {

    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_FROM = "from";
    public static final String KEY_TO = "to";
    public static final String KEY_MEMO = "memo";

    public UserAccount from;

    public UserAccount to;

    public AssetAmount amount;

    public Memo memo;

    public TxOperation() {
        super(OperationType.TRANSFER_OPERATION);
    }

    @Override
    public byte[] toByte() {
        ByteArrayData data = new ByteArrayData();
        data.putBytes(fee.toByte());
        data.putBytes(from.toByte());
        data.putBytes(to.toByte());
        data.putBytes(amount.toByte());
        if (memo != null) {
            data.putBytes(memo.toByte());
        } else {
            data.putBytes(new byte[]{0});
        }
        data.putBytes(extensions.toByte());
        return data.toBytes();
    }

    @Override
    public void fromJson(JSONObject obj) {
        try {
            super.fromJson(obj);
            from = new UserAccount(obj.getString(KEY_FROM));
            to = new UserAccount(obj.getString(KEY_TO));
            amount = new AssetAmount(obj.getJSONObject(KEY_AMOUNT));
            if (obj.has(KEY_MEMO)) {
                memo = new Memo(obj.getJSONObject(KEY_MEMO));
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put(KEY_FROM, from.getId());
        json.put(KEY_TO, to.getId());
        json.put(KEY_AMOUNT, amount.toJson());
        if (memo != null) {
            json.put(KEY_MEMO, memo.toJson());
        }
        return json;
    }
}

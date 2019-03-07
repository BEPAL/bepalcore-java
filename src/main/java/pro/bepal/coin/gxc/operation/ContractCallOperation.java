package pro.bepal.coin.gxc.operation;

import org.json.me.JSONObject;
import org.spongycastle.util.encoders.Hex;
import pro.bepal.categories.ByteArrayData;
import pro.bepal.coin.eos.tx.AccountName;
import pro.bepal.coin.gxc.AssetAmount;
import pro.bepal.coin.gxc.UserAccount;

public class ContractCallOperation extends BaseOperation {
    public static final String KEY_ACCOUNT = "account";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_CONTRACT_ID = "contract_id";
    public static final String KEY_METHOD_NAME = "method_name";
    public static final String KEY_DATA = "data";

    /**
     * 执行账户
     */
    public UserAccount account;

    /**
     * 执行的金额
     */
    public Optional<AssetAmount> amount;

    /**
     * 执行的合约
     */
    public UserAccount contractAccount;

    /**
     * 执行的合约方法名
     */
    public String methodName;

    /**
     * 执行的合约参数
     */
    public byte[] data;

    public ContractCallOperation() {
        super(OperationType.CONTRACT_CALL_OPERATION);
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put(KEY_ACCOUNT, account.getId());
        json.put(KEY_AMOUNT, amount.toJson());
        json.put(KEY_CONTRACT_ID, contractAccount.getId());
        json.put(KEY_METHOD_NAME, methodName);
        json.put(KEY_DATA, Hex.toHexString(data));
        return json;
    }

    @Override
    public void fromJson(JSONObject obj) {
        super.fromJson(obj);
        try {
            account = new UserAccount(obj.getString(KEY_ACCOUNT));
            amount = new Optional<>(new AssetAmount(obj.getJSONObject(KEY_AMOUNT)));
            contractAccount = new UserAccount(obj.getString(KEY_CONTRACT_ID));
            methodName = obj.getString(KEY_METHOD_NAME);
            data = Hex.decode(obj.getString(KEY_DATA));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public byte[] toByte() {
        ByteArrayData data = new ByteArrayData();
        data.putBytes(fee.toByte());
        data.putBytes(account.toByte());
        data.putBytes(contractAccount.toByte());
        data.putBytes(amount.toByte());
        data.putBytes(AccountName.getData(methodName));
        data.appendData(this.data);
        data.putBytes(extensions.toByte());
        return data.toBytes();
    }
}

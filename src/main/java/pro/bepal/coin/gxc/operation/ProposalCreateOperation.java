package pro.bepal.coin.gxc.operation;

import org.json.me.JSONArray;
import org.json.me.JSONObject;
import pro.bepal.categories.ByteArrayData;
import pro.bepal.coin.gxc.UserAccount;
import pro.bepal.util.ByteUtil;
import pro.bepal.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

public class ProposalCreateOperation extends BaseOperation {
    public static final String KEY_FEE_PAYING_ACCOUNT = "fee_paying_account";
    public static final String KEY_EXPIRATION_TIME = "expiration_time";
    public static final String KEY_PROPOSED_OPS = "proposed_ops";
    public static final String KEY_OP = "op";

    public UserAccount feePayingAccount;

    public long expirationTime;

    public List<BaseOperation> proposedOps = new ArrayList<>();

    public ProposalCreateOperation() {
        super(OperationType.PROPOSAL_CREATE_OPERATION);
        expirationTime = System.currentTimeMillis() + 24 * 60 * 60 * 1000;
    }

    @Override
    public byte[] toByte() {
        ByteArrayData data = new ByteArrayData();
        data.putBytes(fee.toByte());
        data.putBytes(feePayingAccount.toByte());
        data.putBytes(ByteUtil.intToBytes(expirationTime));
        data.appendByte(proposedOps.size());
        for (BaseOperation proposedOp : proposedOps) {
            data.putBytes(proposedOp.toOpByte());
        }
        data.putBytes(extensions.toByte());
        return data.toBytes();
    }

    public void addOperation(BaseOperation operation) {
        proposedOps.add(operation);
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put(KEY_FEE_PAYING_ACCOUNT, feePayingAccount.getId());
        json.put(KEY_EXPIRATION_TIME, TimeUtil.timeStampToUtc(expirationTime));

        JSONArray array = new JSONArray();
        for (int i = 0; i < proposedOps.size(); i++) {
            JSONObject obj = new JSONObject();
            obj.put(KEY_OP, proposedOps.get(i).toJsonArray());
            array.put(obj);
        }
        json.put(KEY_PROPOSED_OPS, array);
        return json;
    }
}

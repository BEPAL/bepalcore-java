package pro.bepal.coin.gxc;

import org.spongycastle.util.encoders.Hex;
import org.json.me.JSONArray;
import org.json.me.JSONObject;
import pro.bepal.categories.ByteArrayData;
import pro.bepal.categories.SHAHash;
import pro.bepal.coin.gxc.operation.BaseOperation;
import pro.bepal.core.gxc.GXCECKey;
import pro.bepal.util.TimeUtil;

import java.util.*;

public class Transaction {
    /* Default expiration time */
    public static final int DEFAULT_EXPIRATION_TIME = 30;

    /* Constant field names used for serialization/deserialization purposes */
    public static final String KEY_EXPIRATION = "expiration";
    public static final String KEY_SIGNATURES = "signatures";
    public static final String KEY_OPERATIONS = "operations";
    public static final String KEY_EXTENSIONS = "extensions";
    public static final String KEY_REF_BLOCK_NUM = "ref_block_num";
    public static final String KEY_REF_BLOCK_PREFIX = "ref_block_prefix";

    public byte[] chainID;
    public long expiration;
    public int blockNum;//uint16
    public long blockPrefix;//uint32

    public List<BaseOperation> operations = new ArrayList<>();

    protected Extensions extensions = new Extensions();

    public List<byte[]> signature = new ArrayList<>();

    private byte[] toBaseByte() {
        ByteArrayData data = new ByteArrayData();
        data.appendShort(blockNum);
        data.appendInt(blockPrefix);
        data.appendInt(expiration);
        data.appendByte(operations.size());
        for (BaseOperation operation : operations) {
            data.putBytes(operation.toOpByte());
        }
        data.putBytes(extensions.toByte());
        return data.toBytes();
    }

    public byte[] toByte() {
        ByteArrayData data = new ByteArrayData();
        data.putBytes(chainID);
        data.putBytes(toBaseByte());
        return data.toBytes();
    }

    public byte[] getSignHash() {
        return SHAHash.sha2256(toByte());
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put(KEY_REF_BLOCK_NUM, blockNum);
        json.put(KEY_REF_BLOCK_PREFIX, blockPrefix);
        json.put(KEY_EXPIRATION, TimeUtil.timeStampToUtc(expiration * 1000));

        JSONArray operationsArray = new JSONArray();
        for (BaseOperation operation : operations) {
            operationsArray.put(operation.toJsonArray());
        }
        // Adding operations
        json.put(KEY_OPERATIONS, operationsArray);

        JSONArray signature = new JSONArray();
        for (byte[] aSignature : this.signature) {
            signature.put(Hex.toHexString(aSignature));
        }
        json.put(KEY_SIGNATURES, signature);
        // Adding extensions
        json.put(KEY_EXTENSIONS, extensions.toJson());
        return json;
    }

    public void fromJson(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            blockNum = obj.getInt(KEY_REF_BLOCK_NUM);
            blockPrefix = obj.getLong(KEY_REF_BLOCK_PREFIX);
            expiration =TimeUtil.utcToTimeStamp(obj.getString(KEY_EXPIRATION));
            extensions = new Extensions(obj.getJSONArray(KEY_EXTENSIONS));
            JSONArray array = obj.getJSONArray(KEY_SIGNATURES);
            for (int i = 0; i < array.length(); i++) {
                signature.add(Hex.decode(array.getString(i)));
            }
        } catch (Exception ignored) {
        }
    }

    public byte[] sign(GXCECKey key) {
        byte[] sigData = null;
        while (true) {
            byte[] hash = getSignHash();
            sigData = key.sign(hash).encoding(true);
            if (((sigData[1] & 0x80) != 0) || (sigData[1] == 0) || ((sigData[2] & 0x80) != 0) || ((sigData[33] & 0x80) != 0) || (sigData[33] == 0) || ((sigData[34] & 0x80) != 0)) {
                expiration++;
            } else {
                break;
            }
        }
        signature.add(sigData);
        return sigData;
    }

    public byte[] getTxID() {
        return ByteArrayData.copyOfRange(SHAHash.sha2256(toBaseByte()), 0, 20);
    }
}

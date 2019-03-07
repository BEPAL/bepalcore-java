package pro.bepal.coin.eos.abi;

import pro.bepal.coin.eos.tx.AccountName;
import org.json.me.JSONArray;
import org.json.me.JSONObject;
import pro.bepal.categories.ByteArrayData;

import java.util.ArrayList;
import java.util.List;

public class TableDef implements BaseAbi {
    /**
     * the name of the table
     */
    public AccountName name;
    /**
     * the kind of index, i64, i128i128, etc
     */
    public String indexType;
    /**
     * names for the keys defined by key_types
     */
    private List<String> keyNames = new ArrayList<>();
    /**
     * the type of key parameters
     */
    private List<String> keyTypes = new ArrayList<>();
    /**
     * type of binary data stored in this table
     */
    public String type;

    @Override
    public void parse(JSONObject data) throws Exception {
        name = new AccountName(data.getString("name"));
        type = data.getString("type");
        indexType = data.getString("index_type");
        JSONArray keyNames1 = data.getJSONArray("key_names");
        for (int i = 0; i < keyNames1.length(); i++) {
            keyNames.add(keyNames1.getString(i));
        }
        JSONArray keyTypes1 = data.has("keyTypes") ? data.getJSONArray("keyTypes") : new JSONArray();
        for (int i = 0; i < keyTypes1.length(); i++) {
            keyTypes.add(keyTypes1.getString(i));
        }
    }

    @Override
    public byte[] toData() {
        ByteArrayData stream = new ByteArrayData();
        stream.putBytes(name.accountData);
        stream.appendStringByUVar(indexType);
        stream.appendUVarInt(keyNames.size());
        for (String keyName : keyNames) {
            stream.appendStringByUVar(keyName);
        }
        stream.appendUVarInt(keyTypes.size());
        for (String keyType : keyTypes) {
            stream.appendStringByUVar(keyType);
        }
        stream.appendStringByUVar(type);
        return stream.toBytes();
    }
}

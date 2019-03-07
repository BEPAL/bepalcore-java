package pro.bepal.coin.eos.abi;

import org.json.me.JSONObject;
import pro.bepal.categories.ByteArrayData;

public class TypeDef implements BaseAbi {

    public String newTypeName;

    public String type;

    @Override
    public void parse(JSONObject data) throws Exception {
        newTypeName = data.getString("new_type_name");
        type = data.getString("type");
    }

    @Override
    public byte[] toData() {
        ByteArrayData stream = new ByteArrayData();
        stream.appendStringByUVar(newTypeName);
        stream.appendStringByUVar(type);
        return stream.toBytes();
    }
}

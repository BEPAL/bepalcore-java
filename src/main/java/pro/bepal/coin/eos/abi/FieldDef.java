package pro.bepal.coin.eos.abi;

import org.json.me.JSONObject;
import pro.bepal.categories.ByteArrayData;

public class FieldDef implements BaseAbi {

    public String name;

    public String type;

    @Override
    public void parse(JSONObject data) throws Exception {
        name = data.getString("name");
        type = data.getString("type");
    }

    @Override
    public byte[] toData() {
        ByteArrayData stream = new ByteArrayData();
        stream.appendStringByUVar(name);
        stream.appendStringByUVar(type);
        return stream.toBytes();
    }
}

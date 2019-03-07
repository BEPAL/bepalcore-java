package pro.bepal.coin.eos.abi;

import org.json.me.JSONArray;
import org.json.me.JSONObject;
import pro.bepal.categories.ByteArrayData;

import java.util.ArrayList;
import java.util.List;

public class StructDef implements BaseAbi {

    public String name;

    public String base;

    public List<FieldDef> fields = new ArrayList<>();

    @Override
    public void parse(JSONObject data) throws Exception {
        name = data.getString("name");
        base = data.getString("base");
        JSONArray fields = data.getJSONArray("fields");
        for (int i = 0; i < fields.length(); i++) {
            FieldDef temp = new FieldDef();
            temp.parse(fields.getJSONObject(i));
            this.fields.add(temp);
        }
    }

    @Override
    public byte[] toData() {
        ByteArrayData stream = new ByteArrayData();
        stream.appendStringByUVar(name);
        stream.appendStringByUVar(base);
        stream.appendUVarInt(fields.size());
        for (FieldDef field : fields) {
            stream.appendData(field.toData());
        }
        return stream.toBytes();
    }
}

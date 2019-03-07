package pro.bepal.coin.eos.abi;

import pro.bepal.coin.eos.tx.AccountName;
import org.json.me.JSONObject;
import pro.bepal.categories.ByteArrayData;

public class ActionDef implements BaseAbi {

    public AccountName name;

    public String type;

    public String ricardianContract;

    @Override
    public void parse(JSONObject data) throws Exception {
        name = new AccountName(data.getString("name"));
        type = data.getString("type");
        ricardianContract = data.getString("ricardian_contract");
    }

    @Override
    public byte[] toData() {
        ByteArrayData stream = new ByteArrayData();
        stream.putBytes(name.accountData);
        stream.appendStringByUVar(type);
        stream.appendStringByUVar(ricardianContract);
        return stream.toBytes();
    }
}

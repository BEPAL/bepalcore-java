package pro.bepal.coin.eos.abi;

import org.json.me.JSONObject;

public class ClausePair implements BaseAbi {

    public String id;

    public String body;

    @Override
    public void parse(JSONObject data) {

    }

    @Override
    public byte[] toData() {
        return new byte[0];
    }
}

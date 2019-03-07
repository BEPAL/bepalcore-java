package pro.bepal.coin.eos.abi;

import org.json.me.JSONObject;

public interface BaseAbi {
    void parse(JSONObject data) throws Exception;

    byte[] toData();
}

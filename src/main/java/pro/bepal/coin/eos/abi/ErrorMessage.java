package pro.bepal.coin.eos.abi;

import org.json.me.JSONObject;

public class ErrorMessage implements BaseAbi {

    public long errorCode;

    public String errorMsg;

    @Override
    public void parse(JSONObject data) {

    }

    @Override
    public byte[] toData() {
        return new byte[0];
    }
}

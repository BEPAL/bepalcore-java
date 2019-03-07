package pro.bepal.coin.gxc;

import org.json.me.JSONArray;

import java.util.ArrayList;

public class Extensions {
    public static final String KEY_EXTENSIONS = "extensions";

    private JSONArray extensions;

    public Extensions() {
        extensions = new JSONArray();
    }

    public Extensions(JSONArray json) {
        fromJson(json);
    }

    public JSONArray toJson() {
        return extensions;
    }

    private void fromJson(JSONArray json) {
        extensions = json;
    }

    public byte[] toByte() {
        return new byte[1];
    }

    public int size() {
        return extensions.length();
    }
}

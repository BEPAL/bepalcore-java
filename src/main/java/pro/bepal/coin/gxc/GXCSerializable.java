package pro.bepal.coin.gxc;

import org.json.me.JSONObject;

public interface GXCSerializable {

    byte[] toByte();

    Object toJson();
}

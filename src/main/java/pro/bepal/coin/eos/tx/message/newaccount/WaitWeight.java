package pro.bepal.coin.eos.tx.message.newaccount;

import pro.bepal.categories.ByteArrayData;
import pro.bepal.util.ByteUtil;

import java.io.ByteArrayOutputStream;

public class WaitWeight {
    public long waitSec;//uint32_t
    public int weight;//uint16_t

    public byte[] toByte() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write(ByteUtil.intToBytes(waitSec));
            stream.write(ByteUtil.shortToBytes(weight));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream.toByteArray();
    }

    public void parse(ByteArrayData data) {
        waitSec = data.readInt();
        weight = data.readShort();
    }
}

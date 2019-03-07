package pro.bepal.coin.gxc.operation;

import pro.bepal.categories.ByteArrayData;
import pro.bepal.coin.gxc.GXCSerializable;

public class Vote {

    private int type;
    private int instance;

    public Vote(String vote){
        String[] parts = vote.split(":");
        assert(parts.length == 2);
        this.type = Integer.valueOf(parts[0]);
        this.instance = Integer.valueOf(parts[1]);
    }

    public Vote(int type, int instance){
        this.type = type;
        this.instance = instance;
    }

    public byte[] toByte() {
        ByteArrayData data = new ByteArrayData();
        data.appendByte(type);
        data.appendShort(this.instance);
        data.appendByte(0);
        return data.toBytes();
//        return new byte[] { (byte) this.instance, (byte) this.type };
    }

    @Override
    public String toString() {
        return String.format("%d:%d", this.type, this.instance);
    }
}

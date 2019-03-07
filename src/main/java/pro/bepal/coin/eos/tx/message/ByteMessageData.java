package pro.bepal.coin.eos.tx.message;

public class ByteMessageData implements MessageData {

    public byte[] data;

    public ByteMessageData() {

    }

    public ByteMessageData(byte[] data) {
        this.data = data;
    }

    @Override
    public byte[] toByte() {
        return data;
    }

    @Override
    public void parse(byte[] data) {
        this.data = data;
    }
}

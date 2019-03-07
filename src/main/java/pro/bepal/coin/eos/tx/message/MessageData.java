package pro.bepal.coin.eos.tx.message;

public interface MessageData {
    byte[] toByte();

    void parse(byte[] data);
}

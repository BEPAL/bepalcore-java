package pro.bepal.coin.eos.tx.message.newaccount;

import pro.bepal.categories.ByteArrayData;
import pro.bepal.util.ByteUtil;

import java.io.ByteArrayOutputStream;

public class KeyPermissionWeight {
    public PublicKey pubKey;//33 byte
    public int weight;

    public KeyPermissionWeight() {
        weight = 1;
    }

    public KeyPermissionWeight(byte[] pubKey) {
        this();
        this.pubKey = new PublicKey(pubKey);
    }

    public byte[] toByte() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write(pubKey.toByte());
            stream.write(ByteUtil.shortToBytes(weight));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream.toByteArray();
    }

    public void parse(ByteArrayData data) {
        pubKey = new PublicKey();
        pubKey.parse(data);
        weight = data.readShort();
    }

    public class PublicKey {
        public int type;
        public byte[] data;

        public PublicKey() {

        }

        public PublicKey(byte[] data) {
            type = 0;
            this.data = data;
        }

        public byte[] toByte() {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            try {
                stream.write(ByteUtil.uvarToBytes(type));
                stream.write(data);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream.toByteArray();
        }

        public void parse(ByteArrayData data) {
            type = (int) data.readUVarInt();
            this.data = data.readData(33);
        }
    }
}

package pro.bepal.coin.eos.tx.message.newaccount;

import pro.bepal.categories.ByteArrayData;
import pro.bepal.util.ByteUtil;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Authority {
    /**
     * 签名有效所需权重
     */
    public long threshold;
    /**
     * 公钥及其权重
     */
    public List<KeyPermissionWeight> keys = new ArrayList<>();
    /**
     * 可以默认不添加
     */
    public List<AccountPermissionWeight> accounts = new ArrayList<>();
    /**
     * 可以默认不添加
     */
    public List<WaitWeight> waits = new ArrayList<>();

    public Authority() {
        threshold = 1;
    }

    public byte[] toByte() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write(ByteUtil.intToBytes(threshold));
            stream.write(ByteUtil.uvarToBytes(keys.size()));
            for (KeyPermissionWeight key : keys) {
                stream.write(key.toByte());
            }
            stream.write(ByteUtil.uvarToBytes(accounts.size()));
            for (AccountPermissionWeight account : accounts) {
                stream.write(account.toByte());
            }
            stream.write(ByteUtil.uvarToBytes(waits.size()));
            for (WaitWeight wait : waits) {
                stream.write(wait.toByte());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream.toByteArray();
    }

    public void parse(ByteArrayData data) {
        threshold = data.readIntByLong();
        long count = data.readUVarInt();
        for (int i = 0; i < count; i++) {
            KeyPermissionWeight weight = new KeyPermissionWeight();
            weight.parse(data);
            keys.add(weight);
        }
        count = data.readUVarInt();
        for (int i = 0; i < count; i++) {
            AccountPermissionWeight weight = new AccountPermissionWeight();
            weight.parse(data);
            accounts.add(weight);
        }
        count = data.readUVarInt();
        for (int i = 0; i < count; i++) {
            WaitWeight wait = new WaitWeight();
            wait.parse(data);
            waits.add(wait);
        }
    }

    public void addKey(KeyPermissionWeight key) {
        keys.add(key);
    }

    public void addAccount(AccountPermissionWeight account) {
        accounts.add(account);
    }
}

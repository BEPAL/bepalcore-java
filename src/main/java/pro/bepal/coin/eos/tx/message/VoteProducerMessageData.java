package pro.bepal.coin.eos.tx.message;

import pro.bepal.categories.ByteArrayData;
import pro.bepal.coin.eos.tx.AccountName;
import pro.bepal.coin.eos.tx.Transaction;
import pro.bepal.util.ByteUtil;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class VoteProducerMessageData implements MessageData {

    public AccountName voter;
    /**
     * 如果为空则不是代理
     */
    public AccountName proxy;
    /**
     * 投票列表需要排序 代码中自动排序
     */
    public List<AccountName> producers = new ArrayList<>();

    @Override
    public byte[] toByte() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write(voter.accountData);
            stream.write(proxy.accountData);
            stream.write(ByteUtil.uvarToBytes(producers.size()));
            Transaction.sortAccountName(producers);
            for (AccountName producer : producers) {
                stream.write(producer.accountData);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream.toByteArray();
    }

    @Override
    public void parse(byte[] dataa) {
        ByteArrayData data = new ByteArrayData(dataa);
        voter = new AccountName(data.readData(8));
        proxy = new AccountName(data.readData(8));
        int count = (int) data.readUVarInt();
        for (int i = 0; i < count; i++) {
            AccountName temp = new AccountName(data.readData(8));
            producers.add(temp);
        }
    }
}

package pro.bepal.test;

import org.junit.BeforeClass;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;
import pro.bepal.categories.SHAHash;
import pro.bepal.coin.gxc.AssetAmount;
import pro.bepal.coin.gxc.Transaction;
import pro.bepal.coin.gxc.UserAccount;
import pro.bepal.coin.gxc.operation.*;
import pro.bepal.core.ECSign;
import pro.bepal.core.gxc.GXCECKey;
import pro.bepal.util.TimeUtil;

import java.security.SecureRandom;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GXCTest {

    public static GXCECKey ecKey;
    public static byte[] chainID;

    @BeforeClass
    public static void testStart() {
        ecKey = GXCECKey.fromPrivateKey("5K1yv2ghXNEGPzuSRYxY4hTYsjoftSSFSKgbaqwZ68RvnyoBgYK");
        chainID = Hex.decode("c2af30ef9340ff81fd61654295e98a1ff04b23189748f86727d0b26b40bb0ff4");
    }

    @Test
    public void testTx() {
        int block_num = 0;
        int ref_block_prefix = 0;

        Transaction tx = new Transaction();
        tx.chainID = chainID;
        tx.blockNum = block_num;
        tx.blockPrefix = ref_block_prefix;
        tx.expiration = System.currentTimeMillis() / 1000 + 60 * 10;

        String sendfrom = "1.2.1207";
        String sendto = "1.2.695";
        String runasset = "1.3.1";

        TxOperation txop = new TxOperation();
        txop.amount = new AssetAmount(10000, runasset);
        txop.fee = new AssetAmount(2000, runasset);
        txop.from = new UserAccount(sendfrom);
        txop.to = new UserAccount(sendto);

        GXCECKey key1 = GXCECKey.fromPublicKey("GXC6m2KavJGPwxRTrqZTXd8ZspVEajoy8CDLWe5qEhJsbKPoLkxiT");
        SecureRandom random = new SecureRandom();
        txop.memo = new Memo(ecKey, key1, random.generateSeed(8));
        txop.memo.encryptMessage(ecKey, "123456".getBytes());

        tx.operations.add(txop);

        tx.sign(ecKey);

        try {
            System.out.println(tx.toJson().toString(4));
        } catch (Exception ex) {
            fail();
        }

        assertTrue(ecKey.verify(tx.getSignHash(), ECSign.fromData(tx.signature.get(0))));
    }

    @Test
    public void testRegAccount() {
        int block_num = 0;
        int ref_block_prefix = 0;

        Transaction tx = new Transaction();
        tx.chainID = chainID;
        tx.blockNum = block_num;
        tx.blockPrefix = ref_block_prefix;
        tx.expiration = System.currentTimeMillis() / 1000 + 60 * 10;

        //注册人必须是终身会员
        String registrar = "1.2.1207";
        String regname = "bepal123456789";
        String runasset = "1.3.1";

        GXCECKey regkey = GXCECKey.fromPrivateKey(SHAHash.sha2256("bepal123456789".getBytes()));

        AccountCreateOperation operation = new AccountCreateOperation();
        operation.fee = new AssetAmount(1000, runasset);
        operation.registrar = new UserAccount(registrar);
        operation.referrer = new UserAccount(registrar);
        operation.name = regname;
        operation.owner = new Authority(regkey.toPubblicKeyString());
        operation.active = new Authority(regkey.toPubblicKeyString());
        operation.options = new AccountOptions(regkey.toPubblicKeyString());
        tx.operations.add(operation);

        tx.sign(ecKey);

        try {
            System.out.println(tx.toJson().toString(4));
        } catch (Exception ex) {
            fail();
        }

        assertTrue(ecKey.verify(tx.getSignHash(), ECSign.fromData(tx.signature.get(0))));
    }

    @Test
    public void testUpdateAccount() {
        //更改账户中含投票
        int block_num = 0;
        int ref_block_prefix = 0;

        Transaction tx = new Transaction();
        tx.chainID = chainID;
        tx.blockNum = block_num;
        tx.blockPrefix = ref_block_prefix;
        tx.expiration = System.currentTimeMillis() / 1000 + 60 * 10;

        String runuser = "1.2.1207";
        String runasset = "1.3.1";

        GXCECKey updatekey = GXCECKey.fromPrivateKey(SHAHash.sha2256("bepal123456789".getBytes()));

        AccountUpdateOperation operation1 = new AccountUpdateOperation();
        operation1.fee = new AssetAmount(100, runasset);
        operation1.account = new UserAccount(runuser);
        Authority authority1 = new Authority();
        authority1.addKey(updatekey.toPubblicKeyString());
        authority1.addKey(updatekey.toPubblicKeyString());
        operation1.active = new Optional<>(authority1);
        tx.operations.add(operation1);

        tx.sign(ecKey);

        try {
            System.out.println(tx.toJson().toString(4));
        } catch (Exception ex) {
            fail();
        }

        assertTrue(ecKey.verify(tx.getSignHash(), ECSign.fromData(tx.signature.get(0))));
    }

    @Test
    public void testToLifeTimeMember() {
        int block_num = 0;
        int ref_block_prefix = 0;

        Transaction tx = new Transaction();
        tx.chainID = chainID;
        tx.blockNum = block_num;
        tx.blockPrefix = ref_block_prefix;
        tx.expiration = System.currentTimeMillis() / 1000 + 60 * 10;

        String runuser = "1.2.1207";
        String runasset = "1.3.1";

        AccountUpgradeOperation operation = new AccountUpgradeOperation();
        operation.fee = new AssetAmount(5000000, runasset);
        operation.account = new UserAccount(runuser);
        operation.toLifeTimeMember = true;
        tx.operations.add(operation);

        tx.sign(ecKey);

        try {
            System.out.println(tx.toJson().toString(4));
        } catch (Exception ex) {
            fail();
        }

        assertTrue(ecKey.verify(tx.getSignHash(), ECSign.fromData(tx.signature.get(0))));
    }

    @Test
    public void testProposalCreate() {
        int block_num = 0;
        int ref_block_prefix = 0;

        Transaction tx = new Transaction();
        tx.chainID = chainID;
        tx.blockNum = block_num;
        tx.blockPrefix = ref_block_prefix;
        tx.expiration = System.currentTimeMillis() / 1000 + 60 * 10;

        String proposalcreate = "1.2.1207";
        String runasset = "1.3.1";

        ProposalCreateOperation operation = new ProposalCreateOperation();
        operation.fee = new AssetAmount(100, runasset);
        operation.feePayingAccount = new UserAccount(proposalcreate);
        operation.expirationTime = TimeUtil.utcToTimeStamp("2019-01-12T12:50:52");

        TxOperation txop = new TxOperation();
        txop.amount = new AssetAmount(100000, "1.3.1");
        txop.fee = new AssetAmount(1000, "1.3.1");
        txop.from = new UserAccount(proposalcreate);
        txop.to = new UserAccount("1.2.1298");
        operation.addOperation(txop);

        tx.operations.add(operation);

        tx.sign(ecKey);

        try {
            System.out.println(tx.toJson().toString(4));
        } catch (Exception ex) {
            fail();
        }

        assertTrue(ecKey.verify(tx.getSignHash(), ECSign.fromData(tx.signature.get(0))));
    }
}

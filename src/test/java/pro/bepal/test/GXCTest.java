/*
 * Copyright (c) 2018-2019, BEPAL
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of California, Berkeley nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
import pro.bepal.util.ByteUtil;
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

        // TestNet Chain Id
        chainID = Hex.decode("c2af30ef9340ff81fd61654295e98a1ff04b23189748f86727d0b26b40bb0ff4");
        // MainNet Chain Id
        //chainID = Hex.decode("4f7d07969c446f8342033acb3ab2ae5044cbe0fde93db02de75bd17fa8fd84b8");
        // Get chain id rpc interface;
        // curl --data '{"jsonrpc": "2.0","method": "call","params": [0, "get_chain_id", []],"id": 1}' https://node1.gxb.io/rpc
        // docs url : https://docs.gxchain.org/zh/guide/apis.html#get-chain-id
    }

    @Test
    public void testTx() {
        // blockId = head_block_id
        // curl -X POST  https://node1.gxb.io/rpc -d '{"jsonrpc": "2.0","method": "call","params": [0, "get_dynamic_global_properties", []],"id": 1}'
        // docs url: https://docs.gxchain.org/zh/guide/apis.html#get-dynamic-global-properties
        String blockId = "0117fc711ba70bbcf1d8a77e60761a458a2205c6";
        int block_num = ByteUtil.bytesToShortLE(Hex.decode(blockId.substring(4, 8)), 0);
        long ref_block_prefix = ByteUtil.bytesToIntByLong(Hex.decode(blockId.substring(8, 16)), 0);

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
        // 手续费可通过rpc接口获取;
        // 文档链接：https://docs.gxchain.org/zh/guide/apis.html#get-required-fees
        txop.fee = new AssetAmount(2000, runasset);
        txop.from = new UserAccount(sendfrom);
        txop.to = new UserAccount(sendto);

        GXCECKey key1 = GXCECKey.fromPublicKey("GXC7wLzcYtJWVGLUamgzdhoAaRsQYNf9yN2JVKqNM4VHjBnfWgoS9");
        SecureRandom random = new SecureRandom();
        txop.memo = new Memo(ecKey, key1, random.generateSeed(8));
        txop.memo.encryptMessage(ecKey, "123456".getBytes());

        tx.operations.add(txop);

        tx.sign(ecKey);

        try {
            // 广播一笔交易：https://docs.gxchain.org/zh/guide/apis.html#broadcast-transaction
            System.out.println(tx.toJson().toString(4));
        } catch (Exception ex) {
            fail();
        }

        assertTrue(ecKey.verify(tx.getSignHash(), ECSign.fromData(tx.signature.get(0))));
    }

    @Test
    public void testRegAccount() {
        // blockId = head_block_id
        // curl -X POST  https://node1.gxb.io/rpc -d '{"jsonrpc": "2.0","method": "call","params": [0, "get_dynamic_global_properties", []],"id": 1}'
        // docs url: https://docs.gxchain.org/zh/guide/apis.html#get-dynamic-global-properties
        String blockId = "0117fc711ba70bbcf1d8a77e60761a458a2205c6";
        int block_num = ByteUtil.bytesToShortLE(Hex.decode(blockId.substring(4, 8)), 0);
        long ref_block_prefix = ByteUtil.bytesToIntByLong(Hex.decode(blockId.substring(8, 16)), 0);

        Transaction tx = new Transaction();
        tx.chainID = chainID;
        tx.blockNum = block_num;
        tx.blockPrefix = ref_block_prefix;
        tx.expiration = System.currentTimeMillis() / 1000 + 60 * 10;

        //注册人必须是终身会员
        String registrar = "1.2.955603";
        String regname = "bepal-test";
        String runasset = "1.3.1";

        GXCECKey regkey = GXCECKey.fromPublicKey("GXC7wLzcYtJWVGLUamgzdhoAaRsQYNf9yN2JVKqNM4VHjBnfWgoS9");

        AccountCreateOperation operation = new AccountCreateOperation();
        // 手续费可通过rpc接口获取;
        // 文档链接：https://docs.gxchain.org/zh/guide/apis.html#get-required-fees
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
        // blockId = head_block_id
        // curl -X POST  https://node1.gxb.io/rpc -d '{"jsonrpc": "2.0","method": "call","params": [0, "get_dynamic_global_properties", []],"id": 1}'
        // docs url: https://docs.gxchain.org/zh/guide/apis.html#get-dynamic-global-properties
        String blockId = "0117fc711ba70bbcf1d8a77e60761a458a2205c6";
        int block_num = ByteUtil.bytesToShortLE(Hex.decode(blockId.substring(4, 8)), 0);
        long ref_block_prefix = ByteUtil.bytesToIntByLong(Hex.decode(blockId.substring(8, 16)), 0);

        //更改账户中含投票
        Transaction tx = new Transaction();
        tx.chainID = chainID;
        tx.blockNum = block_num;
        tx.blockPrefix = ref_block_prefix;
        tx.expiration = System.currentTimeMillis() / 1000 + 60 * 10;

        String runuser = "1.2.1207";
        String runasset = "1.3.1";

        GXCECKey updatekey = GXCECKey.fromPublicKey("GXC7wLzcYtJWVGLUamgzdhoAaRsQYNf9yN2JVKqNM4VHjBnfWgoS9");

        AccountUpdateOperation operation1 = new AccountUpdateOperation();
        // 手续费可通过rpc接口获取;
        // 文档链接：https://docs.gxchain.org/zh/guide/apis.html#get-required-fees
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
        // blockId = head_block_id
        // curl -X POST  https://node1.gxb.io/rpc -d '{"jsonrpc": "2.0","method": "call","params": [0, "get_dynamic_global_properties", []],"id": 1}'
        // docs url: https://docs.gxchain.org/zh/guide/apis.html#get-dynamic-global-properties
        String blockId = "0117fc711ba70bbcf1d8a77e60761a458a2205c6";
        int block_num = ByteUtil.bytesToShortLE(Hex.decode(blockId.substring(4, 8)), 0);
        long ref_block_prefix = ByteUtil.bytesToIntByLong(Hex.decode(blockId.substring(8, 16)), 0);

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
        // blockId = head_block_id
        // curl -X POST  https://node1.gxb.io/rpc -d '{"jsonrpc": "2.0","method": "call","params": [0, "get_dynamic_global_properties", []],"id": 1}'
        // docs url: https://docs.gxchain.org/zh/guide/apis.html#get-dynamic-global-properties
        String blockId = "0117fc711ba70bbcf1d8a77e60761a458a2205c6";
        int block_num = ByteUtil.bytesToShortLE(Hex.decode(blockId.substring(4, 8)), 0);
        long ref_block_prefix = ByteUtil.bytesToIntByLong(Hex.decode(blockId.substring(8, 16)), 0);

        Transaction tx = new Transaction();
        tx.chainID = chainID;
        tx.blockNum = block_num;
        tx.blockPrefix = ref_block_prefix;
        tx.expiration = System.currentTimeMillis() / 1000 + 60 * 10;

        String proposalcreate = "1.2.1207";
        String runasset = "1.3.1";

        ProposalCreateOperation operation = new ProposalCreateOperation();
        // 手续费可通过rpc接口获取;
        // 文档链接：https://docs.gxchain.org/zh/guide/apis.html#get-required-fees
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

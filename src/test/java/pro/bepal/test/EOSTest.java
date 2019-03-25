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

import org.json.me.JSONException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;
import pro.bepal.coin.eos.tx.AccountName;
import pro.bepal.coin.eos.tx.AccountPermission;
import pro.bepal.coin.eos.tx.Action;
import pro.bepal.coin.eos.tx.Transaction;
import pro.bepal.coin.eos.tx.message.*;
import pro.bepal.core.ECSign;
import pro.bepal.core.eos.EOSECKey;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class EOSTest {

    public static EOSECKey ecKey;
    public static byte[] chainID;

    @BeforeClass
    public static void testStart() {
        ecKey =  EOSECKey.fromPrivateKey("5KQD4VwQknSMtb1pWSmhQYRfyH9KDXraPbbmHtYTfL6vMnJPK1s");
        // rpc service localhost:8080/v1/chain/get_info  -> chain_id
        chainID = Hex.decode("aca376f206b8fc25a6ed44dbdc66547c36c6c33e3a119ffbeaef943642f0e906");
    }

    /// EOSIO transaction content packaging use cases
    @Test
    public void testTx() {
        int block_num = 0;// The most recent irreversible block # last_irreversible_block_num
        int ref_block_prefix = 0; // encode last_irreversible_block_id # last_irreversible_block_id[8-16]
        // ref https://github.com/EOSIO/eos/blob/master/libraries/chain/transaction.cpp set_reference_block function

        /// generate transaction
        // [1] make block base info
        Transaction transaction = new Transaction();
        transaction.chainID = chainID;
        transaction.blockNum = block_num;
        transaction.blockPrefix = ref_block_prefix;
        transaction.netUsageWords = 0;
        transaction.kcpuUsage = 0;
        transaction.delaySec = 0;
        transaction.expiration = System.currentTimeMillis() / 1000 + 60 * 60;

        String sendfrom = "bepal1";
        String sendto = "bepal2";
        String runtoken = "eosio.token";
        String tokenfun = "transfer";

        // [2] determine the type of transaction
        Action message = new Action();
        message.account = new AccountName(runtoken);
        message.name = new AccountName(tokenfun);
        message.authorization.add(new AccountPermission(sendfrom, "active"));
        transaction.actions.add(message);

        // [3] content of the action of the account party
        TxMessageData mdata = new TxMessageData();
        mdata.from = new AccountName(sendfrom);
        mdata.to = new AccountName(sendto);
        mdata.amount = new Asset("1.0000 EOS");
        message.data = mdata;

        // [4] sign action
        transaction.signature.add(ecKey.sign(transaction.getSignHash()).toData());

        // [5] print broadcast data
        try {
            System.out.println(transaction.toJson().toString(4));
        } catch (Exception ex) {
            fail();
        }

        // [6] check sign
        assertTrue(ecKey.verify(transaction.getSignHash(), ECSign.fromData(transaction.signature.get(0))));
    }

    @Test
    public void testBuyRam() {
        int block_num = 0;// The most recent irreversible block # last_irreversible_block_num
        int ref_block_prefix = 0; // encode last_irreversible_block_id # last_irreversible_block_id[8-16]
        // ref https://github.com/EOSIO/eos/blob/master/libraries/chain/transaction.cpp set_reference_block function


        // [1] make block base info
        Transaction transaction = new Transaction();
        transaction.chainID = chainID;
        transaction.blockNum = block_num;
        transaction.blockPrefix = ref_block_prefix;
        transaction.netUsageWords = 0;
        transaction.kcpuUsage = 0;
        transaction.delaySec = 0;
        transaction.expiration = System.currentTimeMillis() / 1000 + 60 * 60;

        String sendfrom = "bepal1";
        String sendto = "bepal1";
        String runtoken = "eosio";
        String tokenfun = "buyram";//569164

        // [2] determine the type of transaction
        Action message = new Action();
        message.account = new AccountName(runtoken);
        message.name = new AccountName(tokenfun);
        message.authorization.add(new AccountPermission(sendfrom, "active"));
        transaction.actions.add(message);

        // [3] content of the action of the account party
        BuyRamMessageData mdata = new BuyRamMessageData();
        mdata.payer = new AccountName(sendfrom);
        mdata.receiver = new AccountName(sendto);
        mdata.quant = new Asset("1.0000 EOS");
        message.data = mdata;

        // [4] sign action
        transaction.signature.add(ecKey.sign(transaction.getSignHash()).toData());

        // [5] print broadcast data
        try {
            System.out.println(transaction.toJson().toString(4));
        } catch (Exception ex) {
            assertTrue(false);
        }

        // [6] check sign
        assertTrue(ecKey.verify(transaction.getSignHash(), ECSign.fromData(transaction.signature.get(0))));
    }

    @Test
    public void testSellRam() {
        int block_num = 0;// The most recent irreversible block # last_irreversible_block_num
        int ref_block_prefix = 0; // encode last_irreversible_block_id # last_irreversible_block_id[8-16]
        // ref https://github.com/EOSIO/eos/blob/master/libraries/chain/transaction.cpp set_reference_block function


        // [1] make block base info
        Transaction transaction = new Transaction();
        transaction.chainID = chainID;
        transaction.blockNum = block_num;
        transaction.blockPrefix = ref_block_prefix;
        transaction.netUsageWords = 0;
        transaction.kcpuUsage = 0;
        transaction.delaySec = 0;
        transaction.expiration = System.currentTimeMillis() / 1000 + 60 * 60;

        String sendfrom = "bepal1";
        String runtoken = "eosio";
        String tokenfun = "sellram";//570351

        // [2] determine the type of transaction
        Action message = new Action();
        message.account = new AccountName(runtoken);
        message.name = new AccountName(tokenfun);
        message.authorization.add(new AccountPermission(sendfrom, "active"));
        transaction.actions.add(message);

        // [3] content of the action of the account party
        SellRamMessageData mdata = new SellRamMessageData();
        mdata.account = new AccountName(sendfrom);
        mdata.bytes = 1024;
        message.data = mdata;

        // [4] sign action
        transaction.signature.add(ecKey.sign(transaction.getSignHash()).toData());

        // [5] print broadcast data
        try {
            System.out.println(transaction.toJson().toString(4));
        } catch (Exception ex) {
            assertTrue(false);
        }

        // [6] check sign
        assertTrue(ecKey.verify(transaction.getSignHash(), ECSign.fromData(transaction.signature.get(0))));
    }

    @Test
    public void testDelegatebw() {
        int block_num = 0;// The most recent irreversible block # last_irreversible_block_num
        int ref_block_prefix = 0; // encode last_irreversible_block_id # last_irreversible_block_id[8-16]
        // ref https://github.com/EOSIO/eos/blob/master/libraries/chain/transaction.cpp set_reference_block function


        // [1] make block base info
        Transaction transaction = new Transaction();
        transaction.chainID = chainID;
        transaction.blockNum = block_num;
        transaction.blockPrefix = ref_block_prefix;
        transaction.netUsageWords = 0;
        transaction.kcpuUsage = 0;
        transaction.delaySec = 0;
        transaction.expiration = System.currentTimeMillis() / 1000 + 60 * 60;

        String sendfrom = "bepal1";
        String runtoken = "eosio";
        String tokenfun = "delegatebw";//570916

        // [2] determine the type of transaction
        Action message = new Action();
        message.account = new AccountName(runtoken);
        message.name = new AccountName(tokenfun);
        message.authorization.add(new AccountPermission(sendfrom, "active"));
        transaction.actions.add(message);

        // [3] content of the action of the account party
        DelegatebwMessageData mdata = new DelegatebwMessageData();
        mdata.from = new AccountName(sendfrom);
        mdata.receiver = new AccountName(sendfrom);
        mdata.stakeCpuQuantity = new Asset("1.0000 EOS");
        mdata.stakeNetQuantity = new Asset("1.0000 EOS");
        // @notes: 0: the authorizer cannot undelegatebw.
        //         1: the authorizer can undelegatebw.
        //         It is suggested to fill in 0
        mdata.transfer = 0;
        message.data = mdata;

        // [4] sign action
        transaction.signature.add(ecKey.sign(transaction.getSignHash()).toData());

        // [5] print broadcast data
        try {
            System.out.println(transaction.toJson().toString(4));
        } catch (Exception ex) {
            assertTrue(false);
        }

        // [6] check sign
        assertTrue(ecKey.verify(transaction.getSignHash(), ECSign.fromData(transaction.signature.get(0))));
    }

    @Test
    public void testUnDelegatebw() {
        int block_num = 0;// The most recent irreversible block # last_irreversible_block_num
        int ref_block_prefix = 0; // encode last_irreversible_block_id # last_irreversible_block_id[8-16]
        // ref https://github.com/EOSIO/eos/blob/master/libraries/chain/transaction.cpp set_reference_block function


        // [1] make block base info
        Transaction transaction = new Transaction();
        transaction.chainID = chainID;
        transaction.blockNum = block_num;
        transaction.blockPrefix = ref_block_prefix;
        transaction.netUsageWords = 0;
        transaction.kcpuUsage = 0;
        transaction.delaySec = 0;
        transaction.expiration = System.currentTimeMillis() / 1000 + 60 * 60;

        String sendfrom = "bepal1";
        String runtoken = "eosio";
        String tokenfun = "undelegatebw";//571165

        // [2] determine the type of transaction
        Action message = new Action();
        message.account = new AccountName(runtoken);
        message.name = new AccountName(tokenfun);
        message.authorization.add(new AccountPermission(sendfrom, "active"));
        transaction.actions.add(message);

        // [3] content of the action of the account party
        UnDelegatebwMessageData mdata = new UnDelegatebwMessageData();
        mdata.from = new AccountName(sendfrom);
        mdata.receiver = new AccountName(sendfrom);
        mdata.stakeCpuQuantity = new Asset("1.0000 EOS");
        mdata.stakeNetQuantity = new Asset("1.0000 EOS");
        message.data = mdata;

        // [4] sign action
        transaction.signature.add(ecKey.sign(transaction.getSignHash()).toData());

        // [5] print broadcast data
        try {
            System.out.println(transaction.toJson().toString(4));
        } catch (Exception ex) {
            assertTrue(false);
        }

        // [6] check sign
        assertTrue(ecKey.verify(transaction.getSignHash(), ECSign.fromData(transaction.signature.get(0))));
    }

    @Test
    public void testRegProxy() {
        int block_num = 0;// The most recent irreversible block # last_irreversible_block_num
        int ref_block_prefix = 0; // encode last_irreversible_block_id # last_irreversible_block_id[8-16]
        // ref https://github.com/EOSIO/eos/blob/master/libraries/chain/transaction.cpp set_reference_block function


        // [1] make block base info
        Transaction transaction = new Transaction();
        transaction.chainID = chainID;
        transaction.blockNum = block_num;
        transaction.blockPrefix = ref_block_prefix;
        transaction.netUsageWords = 0;
        transaction.kcpuUsage = 0;
        transaction.delaySec = 0;
        transaction.expiration = System.currentTimeMillis() / 1000 + 60 * 60;

        String sendfrom = "bepal1";
        String runtoken = "eosio";
        String tokenfun = "regproxy";//571581

        // [2] determine the type of transaction
        Action message = new Action();
        message.account = new AccountName(runtoken);
        message.name = new AccountName(tokenfun);
        message.authorization.add(new AccountPermission(sendfrom, "active"));
        transaction.actions.add(message);

        // [3] content of the action of the account party
        RegProxyMessageData mdata = new RegProxyMessageData();
        mdata.proxy = new AccountName(sendfrom);
        // set proxy  `isProxy = true`
        // off set proxy  `isProxy = false`
        mdata.isProxy = true;
        message.data = mdata;

        // [4] sign action
        transaction.signature.add(ecKey.sign(transaction.getSignHash()).toData());

        // [5] print broadcast data
        try {
            System.out.println(transaction.toJson().toString(4));
        } catch (Exception ex) {
            assertTrue(false);
        }

        // [6] check sign
        assertTrue(ecKey.verify(transaction.getSignHash(), ECSign.fromData(transaction.signature.get(0))));
    }

    @Test
    public void testVote() {
        int block_num = 0;// The most recent irreversible block # last_irreversible_block_num
        int ref_block_prefix = 0; // encode last_irreversible_block_id # last_irreversible_block_id[8-16]
        // ref https://github.com/EOSIO/eos/blob/master/libraries/chain/transaction.cpp set_reference_block function


        // [1] make block base info
        Transaction transaction = new Transaction();
        transaction.chainID = chainID;
        transaction.blockNum = block_num;
        transaction.blockPrefix = ref_block_prefix;
        transaction.netUsageWords = 0;
        transaction.kcpuUsage = 0;
        transaction.delaySec = 0;
        transaction.expiration = System.currentTimeMillis() / 1000 + 60 * 60;

        String sendfrom = "bepal1";
        String sendto = "bepal2";
        // If the voting is conducted on behalf of others,
        // please fill in the account name of the agent here.
        // If the voting is conducted on an individual,  proxy = ""
        String proxy = "bepal3";
        String runtoken = "eosio";
        String tokenfun = "voteproducer";//571581

        // [2] determine the type of transaction
        Action message = new Action();
        message.account = new AccountName(runtoken);
        message.name = new AccountName(tokenfun);
        message.authorization.add(new AccountPermission(sendfrom, "active"));
        transaction.actions.add(message);

        // [3] content of the action of the account party
        VoteProducerMessageData mdata = new VoteProducerMessageData();
        mdata.voter = new AccountName(sendfrom);
        mdata.proxy = new AccountName(proxy);
        mdata.producers.add(new AccountName(sendto));
        message.data = mdata;

        // [4] sign action
        transaction.signature.add(ecKey.sign(transaction.getSignHash()).toData());

        // [5] print broadcast data
        try {
            System.out.println(transaction.toJson().toString(4));
        } catch (Exception ex) {
            assertTrue(false);
        }

        // [6] check sign
        assertTrue(ecKey.verify(transaction.getSignHash(), ECSign.fromData(transaction.signature.get(0))));
    }
}

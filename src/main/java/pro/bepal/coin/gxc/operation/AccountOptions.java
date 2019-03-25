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
package pro.bepal.coin.gxc.operation;

import com.google.common.primitives.Bytes;
import org.json.me.JSONArray;
import org.json.me.JSONObject;
import pro.bepal.categories.ByteArrayData;
import pro.bepal.coin.gxc.Extensions;
import pro.bepal.coin.gxc.GXCSerializable;
import pro.bepal.coin.gxc.UserAccount;
import pro.bepal.core.gxc.GXCECKey;

import java.util.ArrayList;
import java.util.List;

public class AccountOptions implements GXCSerializable {

    public static final String KEY_MEMO_KEY = "memo_key";
    public static final String KEY_NUM_COMMITTEE = "num_committee";
    public static final String KEY_NUM_WITNESS = "num_witness";
    public static final String KEY_VOTES = "votes";
    public static final String KEY_VOTING_ACCOUNT = "voting_account";
    public static final String KEY_EXTENSIONS = Extensions.KEY_EXTENSIONS;

    public GXCECKey memoKey;
    public UserAccount votingAccount;
    /**
     * 投票的数量
     */
    public int numWitness;
    public int numComittee;
    public List<Vote> votes;
    private Extensions extensions;

    public AccountOptions() {
        votingAccount = new UserAccount(UserAccount.PROXY_TO_SELF);
        this.votes = new ArrayList<>();
        this.extensions = new Extensions();
        this.numWitness = 0;
        this.numComittee = 0;
    }

    public AccountOptions(GXCECKey memoKey) {
        this();
        this.memoKey = memoKey;
    }

    public AccountOptions(String memoKey) {
        this();
        this.memoKey = GXCECKey.fromPublicKey(memoKey);
    }

    public void addVote(String name) {
        votes.add(new Vote(name));
    }

    @Override
    public byte[] toByte() {
        ByteArrayData data = new ByteArrayData();
        if (memoKey != null) {
            data.putBytes(memoKey.getPublicKey());
            data.putBytes(votingAccount.toByte());
            data.appendShort(numWitness);
            data.appendShort(numComittee);
            data.appendByte(votes.size());
            for (Vote vote : votes) {
                data.putBytes(vote.toByte());
            }
            data.putBytes(extensions.toByte());
        } else {
            data.appendByte(0);
        }
        return data.toBytes();
    }

    @Override
    public Object toJson() {
        JSONObject options = new JSONObject();
        options.put(KEY_MEMO_KEY, memoKey.toPubblicKeyString());
        options.put(KEY_NUM_COMMITTEE, numComittee);
        options.put(KEY_NUM_WITNESS, numWitness);
        options.put(KEY_VOTING_ACCOUNT, votingAccount.getId());
        JSONArray votesArray = new JSONArray();
        for (Vote vote : votes) {
            votesArray.put(vote.toString());
        }
        options.put(KEY_VOTES, votesArray);
        options.put(KEY_EXTENSIONS, extensions.toJson());
        return options;
    }

    public void fromJson(JSONObject obj) {
        try {
            memoKey = GXCECKey.fromPublicKey(obj.getString(KEY_MEMO_KEY));
            numComittee = obj.getInt(KEY_NUM_COMMITTEE);
            numWitness = obj.getInt(KEY_NUM_WITNESS);
            votingAccount = new UserAccount(obj.getString(KEY_VOTING_ACCOUNT));
            JSONArray votesArray = obj.getJSONArray(KEY_VOTES);
            for (int i = 0; i < votesArray.length(); i++) {
                votes.add(new Vote(votesArray.getString(i)));
            }
            extensions = new Extensions(obj.getJSONArray(KEY_EXTENSIONS));
        } catch (Exception ignored) {
        }
    }
}

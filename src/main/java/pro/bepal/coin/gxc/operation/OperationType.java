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

public class OperationType {
    //https://github.com/gxchain/gxb-core/blob/61bdcb3fec8f0ef4d21c656d7c299cf1d19cede1/libraries/chain/include/graphene/chain/protocol/operations.hpp
    public static final int TRANSFER_OPERATION = 0;//转账
    public static final int ACCOUNT_CREATE_OPERATION = 5;//创建账户
    public static final int ACCOUNT_UPDATE_OPERATION = 6;//更新账户
    public static final int ACCOUNT_UPGRADE_OPERATION = 8;//升级账户
    public static final int ACCOUNT_TRANSFER_OPERATION = 9;//更新账户

    public static final int ASSET_CREATE_OPERATION = 10;//创建资产

    public static final int WITNESS_CREATE_OPERATION = 20;

    public static final int PROPOSAL_CREATE_OPERATION = 22;
    public static final int PROPOSAL_UPDATE_OPERATION = 23;

    public static final int COMMITTEE_MEMBER_CREATE_OPERATION = 29;

    public static final int DIY_OPERATION = 35;//自定义操作
    public static final int PROXY_TRANSFER_OPERATION = 73;//代理转账
    public static final int CONTRACT_DEPLOY_OPERATION = 74;//合约部署
    public static final int CONTRACT_CALL_OPERATION = 75;//调用合约
    public static final int CONTRACT_UPDATE_OPERATION = 76;//更新合约
}

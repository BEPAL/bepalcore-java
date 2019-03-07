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

package pro.bepal.coin.gxc;

import pro.bepal.util.ByteUtil;

public class UserAccount {
    public static final String PROXY_TO_SELF = "1.2.5";

    protected String id;

    protected int space;
    protected int type;
    protected long instance;

    /**
     * Constructor that expects a user account in the string representation.
     * That is in the 1.2.x format.
     * @param id: The string representing the user account.
     */
    public UserAccount(String id) {
        this.id = id;
        String[] parts = id.split("\\.");
        if(parts.length == 3){
            this.space = Integer.parseInt(parts[0]);
            this.type = Integer.parseInt(parts[1]);
            this.instance = Long.parseLong(parts[2]);
        }
    }

    public String getId() {
        return id;
    }

    public byte[] toByte() {
        return ByteUtil.uvarToBytes(this.instance);
    }

    /**
     *
     * @return: A String containing the full object apiId in the form {space}.{type}.{instance}
     */
    public String getObjectId(){
        return String.format("%d.%d.%d", space, type, instance);
    }

    @Override
    public String toString() {
        return id;
    }
}

package pro.bepal.coin.gxc;

public class Asset {

    protected String id;

    protected int space;
    protected int type;
    protected long instance;

    /**
     * Simple constructor
     *
     * @param id
     */
    public Asset(String id) {
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
}

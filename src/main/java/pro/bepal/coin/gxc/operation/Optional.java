package pro.bepal.coin.gxc.operation;

import pro.bepal.categories.ByteArrayData;
import pro.bepal.coin.gxc.GXCSerializable;

public class Optional<T extends GXCSerializable> implements GXCSerializable{

    private T optionalField;

    public Optional(){
        optionalField = null;
    }

    public Optional(T field){
        optionalField = field;
    }

    @Override
    public byte[] toByte() {
        if(optionalField == null) {
            return new byte[] { (byte) 0 };
        } else {
            ByteArrayData data = new ByteArrayData();
            data.appendByte(1);
            data.putBytes(optionalField.toByte());
            return data.toBytes();
        }
    }

    public boolean isSet(){
        return this.optionalField != null;
    }

    @Override
    public Object toJson() {
        return optionalField.toJson();
    }
}

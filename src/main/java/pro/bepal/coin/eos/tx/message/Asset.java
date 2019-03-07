package pro.bepal.coin.eos.tx.message;

import pro.bepal.categories.ByteArrayData;
import pro.bepal.util.ByteUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * EOS资产
 */
public class Asset {

    /**
     * 金额
     */
    public long amount;
    /**
     * 小数点个数
     */
    public int decimal;
    /**
     * 单位
     */
    public String unit;

    public Asset() {

    }

    /**
     * 初始化
     *
     * @param value 小数点后面的数字为小数点个数
     */
    public Asset(String value) {
        String[] arr = value.split(" ");
        int index = arr[0].indexOf(".");
        decimal = arr[0].length() - index - 1;
        amount = (long) (Double.parseDouble(arr[0]) * Math.pow(10, decimal));
        unit = arr[1];
    }

    public Asset(long amount, int decimal, String unit) {
        this.amount = amount;
        this.decimal = decimal;
        this.unit = unit;
    }

    public void toByte(ByteArrayData stream) {
        stream.putBytes(ByteUtil.longToBytes(amount));
        stream.appendByte((byte) decimal);
        stream.putBytes(getStringToData(unit));
    }

    public void parse(ByteArrayData data) {
        amount = data.readLong();
        decimal = data.readByte();
        unit = new String(data.readData(7)).trim();
    }

    public byte[] getStringToData(String str) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            byte[] data = str.getBytes(StandardCharsets.UTF_8);
            stream.write(data, 0, data.length >= 7 ? 7 : data.length);
            for (int i = data.length; i < 7; i++) {
                stream.write(0);
            }
        } catch (Exception ex) {
        }
        return stream.toByteArray();
    }

    @Override
    public String toString() {
        double value = amount / Math.pow(10, decimal);
        return String.format("%." + decimal + "f %s", value, unit);
    }

    public static Asset toAsset(ByteArrayData data) {
        Asset asset = new Asset();
        asset.parse(data);
        return asset;
    }

    public double getValue() {
        return amount / Math.pow(10, decimal);
    }
}

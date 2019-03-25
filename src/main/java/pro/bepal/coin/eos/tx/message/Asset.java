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

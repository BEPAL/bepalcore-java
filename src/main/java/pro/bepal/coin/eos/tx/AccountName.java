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
package pro.bepal.coin.eos.tx;

import org.spongycastle.util.encoders.Hex;
import pro.bepal.categories.ByteArrayData;
import pro.bepal.util.ByteUtil;

import java.math.BigInteger;

/**
 * 为uint64类型长度为13
 */
public class AccountName {

    static char[] charmap = ".12345abcdefghijklmnopqrstuvwxyz".toCharArray();

    public byte[] accountData;
    public String accountName;
    public BigInteger accountValue;

    public AccountName(String name) {
        accountName = name;
        accountData = accountNameToHex(name);
        accountValue = new BigInteger(Hex.toHexString(ByteArrayData.reverseBytes(accountData)), 16);
    }

    public AccountName(byte[] name) {
        if (name.length < 8) {
            name = new byte[8];
        }
        accountName = hexToAccountName(name);
        accountData = name;
        accountValue = new BigInteger(Hex.toHexString(ByteArrayData.reverseBytes(accountData)), 16);
    }

    public byte[] accountNameToHex(String name) {
        //去掉尾部的点
        int len = name.length();
        long value = 0;
        for (int i = 0; i <= 12; ++i) {
            long c = 0;
            if (i < len && i <= 12) {
                c = charIndexOf(charmap, name.charAt(i));
            }
            if (i < 12) {
                c &= 0x1f;
                c <<= 64 - 5 * (i + 1);
            } else {
                c &= 0x0f;
            }
            value |= c;
        }
        return ByteUtil.longToBytes(value);
    }

    public String hexToAccountName(byte[] hex) {
        long tmp = ByteUtil.bytesToLong(hex, 0);
        char[] str = new char[13];
        for (int i = 0; i <= 12; ++i) {
            char c = charmap[(int) (tmp & (i == 0 ? 0x0f : 0x1f))];
            str[12 - i] = c;
            tmp >>= (i == 0 ? 4 : 5);
        }
        int count = 0;
        for (int i = 12; i >= 0; i--) {
            if (str[i] != 46) {
                break;
            }
            count = i;
        }
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < count; i++) {
            name.append(str[i]);
        }
        return name.toString();
    }

    public static int charIndexOf(char[] map, char data) {
        for (int i = 0; i < map.length; i++) {
            if (map[i] == data) {
                return i;
            }
        }
        return 0;
    }

    public static int charIndexOfNoZero(char[] map, char data) {
        for (int i = 0; i < map.length; i++) {
            if (map[i] == data) {
                return i;
            }
        }
        return -1;
    }

    public static byte[] getData(String name) {
        return new AccountName(name).accountData;
    }

    public static BigInteger getValue(String name) {
        return new AccountName(name).accountValue;
    }

    public static boolean isAccountName(String name) {
        char[] ch = name.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            if (charIndexOfNoZero(charmap, ch[i]) < 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return accountName;
    }
}

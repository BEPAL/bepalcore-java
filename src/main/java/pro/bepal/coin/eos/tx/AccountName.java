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

package pro.bepal.util;

import com.google.common.io.BaseEncoding;
import pro.bepal.categories.Base58;
import pro.bepal.categories.ByteArrayData;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by 10254 on 2017-08-15.
 * 数值类型转字节流
 */
public class ByteUtil {

    public static byte[] ivarToByte(long value) {
        byte[] bytes;
        switch (sizeOfIvar(value)) {
            case 1:
                return new byte[]{(byte) value};
            case 3:
                return new byte[]{(byte) 253, (byte) (value), (byte) (value >> 8)};
            case 5:
                bytes = new byte[5];
                byte[] ibyte = intToBytes(value);
                bytes[0] = (byte) 254;
                bytes[1] = ibyte[0];
                bytes[2] = ibyte[1];
                bytes[3] = ibyte[2];
                bytes[4] = ibyte[3];
                return bytes;
            default:
                bytes = new byte[9];
                byte[] lbyte = longToBytes(value);
                bytes[0] = (byte) 255;
                bytes[1] = lbyte[0];
                bytes[2] = lbyte[1];
                bytes[3] = lbyte[2];
                bytes[4] = lbyte[3];
                bytes[5] = lbyte[4];
                bytes[6] = lbyte[5];
                bytes[7] = lbyte[6];
                bytes[8] = lbyte[7];
                return bytes;
        }
    }

    public static long byteToIvar(byte[] data, int offset) {
        long value = 0;
        int first = 0xFF & data[offset];
        if (first < 253) {
            value = first;
        } else if (first == 253) {
            value = (0xFF & data[offset + 1]) | ((0xFF & data[offset + 2]) << 8);
        } else if (first == 254) {
            value = bytesToIntByLong(data, offset + 1);
        } else {
            value = bytesToLong(data, offset + 1);
        }
        return value;
    }

    public static int sizeOfIvar(long value) {
        if (value < 0) {
            return 9;
        }
        if (value < 253) {
            return 1;
        }
        if (value <= 0xFFFFL) {
            return 3;
        }
        if (value <= 0xFFFFFFFFL) {
            return 5;
        }
        return 9;
    }

    public static byte[] shortToBytes(int value) {
        byte[] src = new byte[2];
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    public static int bytesToShort(byte[] data, int offset) {
        return (data[offset] & 0xFF)
                | ((data[offset + 1] & 0xFF) << 8);
    }

    public static byte[] shortToBytesLE(int value) {
        byte[] src = new byte[2];
        src[0] = (byte) ((value >> 8) & 0xFF);
        src[1] = (byte) (value & 0xFF);
        return src;
    }

    public static int bytesToShortLE(byte[] data, int offset) {
        return (data[offset + 1] & 0xFF)
                | ((data[offset] & 0xFF) << 8);
    }

    public static byte[] intToBytes(long value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * 小位在尾部
     *
     * @param value
     * @return
     */
    public static byte[] intToBytesLE(long value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value >> 24) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }

    public static int bytesToInt(byte[] data, int offset) {
        return (data[offset] & 0xFF)
                | ((data[offset + 1] & 0xFF) << 8)
                | ((data[offset + 2] & 0xFF) << 16)
                | ((data[offset + 3] & 0xFF) << 24);
    }

    /**
     * 小位在尾部
     */
    public static int bytesToIntLE(byte[] data, int offset) {
        return (data[offset + 3] & 0xFF)
                | ((data[offset + 2] & 0xFF) << 8)
                | ((data[offset + 1] & 0xFF) << 16)
                | ((data[offset] & 0xFF) << 24);
    }

    public static long bytesToIntByLong(byte[] data, int offset) {
        return (long) (data[offset] & 0xFF)
                | ((long) (data[offset + 1] & 0xFF) << 8)
                | ((long) (data[offset + 2] & 0xFF) << 16)
                | ((long) (data[offset + 3] & 0xFF) << 24);
    }

    public static byte[] longToBytes(long value) {
        byte[] src = new byte[8];
        src[7] = (byte) ((value >> 56) & 0xFF);
        src[6] = (byte) ((value >> 48) & 0xFF);
        src[5] = (byte) ((value >> 40) & 0xFF);
        src[4] = (byte) ((value >> 32) & 0xFF);
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    public static byte[] longToBytesLE(long value) {
        byte[] src = new byte[8];
        src[0] = (byte) ((value >> 56) & 0xFF);
        src[1] = (byte) ((value >> 48) & 0xFF);
        src[2] = (byte) ((value >> 40) & 0xFF);
        src[3] = (byte) ((value >> 32) & 0xFF);
        src[4] = (byte) ((value >> 24) & 0xFF);
        src[5] = (byte) ((value >> 16) & 0xFF);
        src[6] = (byte) ((value >> 8) & 0xFF);
        src[7] = (byte) (value & 0xFF);
        return src;
    }

    public static long bytesToLong(byte[] data, int offset) {
        return ((long) data[offset] & 0xFF)
                | ((long) (data[offset + 1] & 0xFF) << 8)
                | ((long) (data[offset + 2] & 0xFF) << 16)
                | ((long) (data[offset + 3] & 0xFF) << 24)
                | ((long) (data[offset + 4] & 0xFF) << 32)
                | ((long) (data[offset + 5] & 0xFF) << 40)
                | ((long) (data[offset + 6] & 0xFF) << 48)
                | ((long) (data[offset + 7] & 0xFF) << 56);
    }

    public static long bytesToUVar(byte[] data, int offset) {
        long x = 0;
        int s = 0;
        for (int i = offset; true; i++) {
            byte b = data[i];
            if (b >= 0) {
                if (i > 9 + offset || (i == 9 + offset && b > 1)) {
                    return x;
                }
                return x | (long) (b) << s;
            }
            x |= (long) (b & 0x7f) << s;
            s += 7;
        }
//        long v = 0;
//        int b = 0;
//        int by = 0;
//        do {
//            b = data[offset];
//            v |= ((long) b & 0x7f) << by;
//            by += 7;
//            offset++;
//        } while ((b & 0x80) != 0);
//        return v;
    }

    public static byte[] uvarToBytes(long value) {
        int i = 0;
        long x = value;
        byte[] buf = new byte[9];
        while (x >= 0x80) {
            buf[i] = (byte) ((byte) x | 0x80);
            x >>= 7;
            i++;
        }
        buf[i] = (byte) x;
        return Arrays.copyOfRange(buf, 0, i + 1);
    }

    public static int sizeOfUVar(long value) {
        return uvarToBytes(value).length;
    }

    /**
     * Converts a int value into a byte array.
     *
     * @param val - int value to convert
     * @return value with leading byte that are zeroes striped
     */
    public static byte[] intToBytesNoLeadZeroes(int val) {
        if (val == 0) {
            return ByteArrayData.EMPTY_BYTE_ARRAY;
        }
        int lenght = 0;
        int tmpVal = val;
        while (tmpVal != 0) {
            tmpVal = tmpVal >>> 8;
            ++lenght;
        }
        byte[] result = new byte[lenght];
        int index = result.length - 1;
        while (val != 0) {
            result[index] = (byte) (val & 0xFF);
            val = val >>> 8;
            index -= 1;
        }
        return result;
    }

    public static boolean isNullOrZeroArray(byte[] array) {
        return (array == null) || (array.length == 0);
    }

    public static boolean isSingleZero(byte[] array) {
        return (array.length == 1 && array[0] == 0);
    }

    /**
     * <p>Checks if an array of primitive bytes is empty or {@code null}.</p>
     *
     * @param array the array to test
     * @return {@code true} if the array is empty or {@code null}
     * @since 2.1
     */
    public static boolean isEmpty(final byte[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Attempts to parse the given string as arbitrary-length hex or base58 and then return the results, or null if
     * neither parse was successful.
     */
    public static byte[] parseAsHexOrBase58(String data) {
        try {
            return BaseEncoding.base16().lowerCase().decode(data);
        } catch (Exception e) {
            // Didn't decode as hex, try base58.
            try {
                return Base58.decodeChecked(data);
            } catch (Exception e1) {
                return null;
            }
        }
    }

    /**
     * Converts a long value into a byte array.
     *
     * @param val - long value to convert
     * @return decimal value with leading byte that are zeroes striped
     */
    public static byte[] longToBytesNoLeadZeroes(long val) {

        // todo: improve performance by while strip numbers until (long >> 8 == 0)
        if (val == 0) {
            return new byte[0];
        }
        byte[] data = ByteBuffer.allocate(8).putLong(val).array();
        return stripLeadingZeroes(data);
    }

    public static byte[] stripLeadingZeroes(byte[] data) {
        if (data == null) {
            return null;
        }

        final int firstNonZero = firstNonZeroByte(data);
        switch (firstNonZero) {
            case -1:
                return new byte[]{1};
            case 0:
                return data;

            default:
                byte[] result = new byte[data.length - firstNonZero];
                System.arraycopy(data, firstNonZero, result, 0, data.length - firstNonZero);
                return result;
        }
    }

    public static int firstNonZeroByte(byte[] data) {
        for (int i = 0; i < data.length; ++i) {
            if (data[i] != 0) {
                return i;
            }
        }
        return -1;
    }
}

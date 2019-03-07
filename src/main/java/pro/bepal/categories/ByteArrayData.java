package pro.bepal.categories;

import org.spongycastle.util.encoders.Hex;
import pro.bepal.util.ByteUtil;
import pro.bepal.util.BigIntUtil;
import pro.bepal.util.ErrorTool;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 字节数组的拼接和读取
 */
public class ByteArrayData {
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    public static final byte[] ZERO_BYTE_ARRAY = new byte[]{0};

    /**
     * 输出流
     */
    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    /**
     * 输入流
     */
    private byte[] inputStream = new byte[0];
    /**
     * 是否可以读取 输出流不能读取
     */
    private boolean isRead = false;
    /**
     * 当前读取位置
     */
    private int readIndex = 0;

    public ByteArrayData() {
        isRead = false;
    }

    public ByteArrayData(int length) {
        outputStream = new ByteArrayOutputStream(length);
    }

    public ByteArrayData(byte[] data) {
        this(data, 0);
    }

    public ByteArrayData(byte[] data, int index) {
        inputStream = data;
        isRead = true;
        readIndex = index;
    }

    public void setReadIndex(int index) {
        readIndex = index;
    }

    //添加数据到流中

    public void appendByte(int i) {
        appendNormalData(new byte[]{(byte) i});
    }

    public void appendShort(int i) {
        appendNormalData(ByteUtil.shortToBytes(i));
    }

    public void appendInt(long i) {
        appendNormalData(ByteUtil.intToBytes(i));
    }

    public void appendIntLE(long i) {
        appendNormalData(ByteUtil.intToBytesLE(i));
    }

    public void appendLong(long i) {
        appendNormalData(ByteUtil.longToBytes(i));
    }

    public void appendVarInt(long i) {
        appendNormalData(ByteUtil.ivarToByte(i));
    }

    public void appendUVarInt(long i) {
        appendNormalData(ByteUtil.uvarToBytes(i));
    }

    public void putBytes(byte[] data) {
        appendNormalData(data);
    }

    private void appendNormalData(byte[] data) {
        checkIsWrite();
        outputStream.write(data, 0, data.length);
    }

    public void appendData(byte[] data) {
        appendVarInt(data.length);
        appendNormalData(data);
    }

    public void putBytes(byte[] data, int length) {
        outputStream.write(data, 0, length);
    }

    public void putBytes(byte[] data, int offset, int length) {
        outputStream.write(data, offset, length);
    }

    public void appendString(String data) {
        try {
            appendData(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void appendDataByUVar(byte[] data) {
        appendUVarInt(data.length);
        appendNormalData(data);
    }

    public void appendStringByUVar(String data) {
        try {
            appendDataByUVar(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void appendBigInt(BigInteger data) {
        try {
            appendData(BigIntUtil.bigIntToBytes(data));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void appendBigIntLE8(BigInteger val) {
        byte[] bytes = val.toByteArray();
        if (bytes.length > 8) {
            throw new RuntimeException("Input too large to encode into a uint64");
        }
        bytes = reverseBytes(bytes);
        outputStream.write(bytes, 0, bytes.length);
        if (bytes.length < 8) {
            for (int i = 0; i < 8 - bytes.length; i++) {
                outputStream.write(0);
            }
        }
    }

    public void appendBigInt8(BigInteger val) {
        byte[] bytes = val.toByteArray();
        if (bytes.length > 8) {
            throw new RuntimeException("Input too large to encode into a uint64");
        }
        if (bytes.length < 8) {
            for (int i = 0; i < 8 - bytes.length; i++) {
                outputStream.write(0);
            }
        }
        outputStream.write(bytes, 0, bytes.length);
    }

    private void checkIsRead() {
        if (isRead) {
            return;
        }
        throw new RuntimeException("当前并非读取模式");
    }

    private void checkIsWrite() {
        if (!isRead) {
            return;
        }
        throw new RuntimeException("当前并非写入模式");
    }

    //读取数据
    public byte readByte() {
        checkIsRead();
        byte value = inputStream[readIndex];
        readIndex++;
        return value;
    }

    public int readShort() {
        checkIsRead();
        int value = ByteUtil.bytesToShort(inputStream, readIndex);
        readIndex += 2;
        return value;
    }

    public int readInt() {
        checkIsRead();
        int value = ByteUtil.bytesToInt(inputStream, readIndex);
        readIndex += 4;
        return value;
    }

    public int readIntLE() {
        checkIsRead();
        int value = ByteUtil.bytesToIntLE(inputStream, readIndex);
        readIndex += 4;
        return value;
    }

    public long readIntByLong() {
        checkIsRead();
        long value = ByteUtil.bytesToIntByLong(inputStream, readIndex);
        readIndex += 4;
        return value;
    }

    public long readLong() {
        checkIsRead();
        long value = ByteUtil.bytesToLong(inputStream, readIndex);
        readIndex += 8;
        return value;
    }

    public long readVarInt() {
        checkIsRead();
        long value = ByteUtil.byteToIvar(inputStream, readIndex);
        readIndex += ByteUtil.sizeOfIvar(value);
        return value;
    }

    public long readUVarInt() {
        checkIsRead();
        long value = ByteUtil.bytesToUVar(inputStream, readIndex);
        readIndex += ByteUtil.sizeOfUVar(value);
        return value;
    }

    public byte[] readData(int length) {
        checkIsRead();
        byte[] value = Arrays.copyOfRange(inputStream, readIndex, readIndex + length);
        readIndex += length;
        return value;
    }

    public byte[] readData() {
        int length = (int) readVarInt();
        return readData(length);
    }

    public String readString() {
        return new String(readData(), StandardCharsets.UTF_8);
    }

    public byte[] readDataByUVar() {
        int length = (int) readUVarInt();
        return readData(length);
    }

    public String readStringByUVar() {
        return new String(readDataByUVar(), StandardCharsets.UTF_8);
    }

    public BigInteger readBigInt() {
        return BigIntUtil.bytesToBigInt(readData());
    }

    public BigInteger readBigIntLE8() {
        byte[] data = readData(8);
        data = ByteArrayData.reverseBytes(data);
        return new BigInteger(1, data);
    }

    public BigInteger readBigInt8() {
        byte[] data = readData(8);
        return new BigInteger(1, data);
    }

    /**
     * 读取全部未读数据
     */
    public byte[] readToEndData() {
        return Arrays.copyOfRange(inputStream, readIndex, inputStream.length);
    }

    /**
     * 是否还有未读数据
     */
    public boolean hasData() {
        return inputStream.length >= readIndex + 1;
    }

    /**
     * 获取字节流
     */
    public byte[] toBytes() {
        if (isRead) {
            return inputStream;
        } else {
            return outputStream.toByteArray();
        }
    }

    public int getLength() {
        if (isRead) {
            return inputStream.length;
        } else {
            return outputStream.size();
        }
    }

    @Override
    public String toString() {
        return Hex.toHexString(toBytes());
    }

    /**
     * 合并字节数组
     */
    public static byte[] concat(final byte[]... arrays) {
        ByteArrayData data = new ByteArrayData();
        for (final byte[] array : arrays) {
            data.appendNormalData(array);
        }
        return data.toBytes();
    }

    /**
     * 截取字节数组
     *
     * @param data   内容
     * @param start  开始
     * @param length 长度
     */
    public static byte[] copyOfRange(byte[] data, int start, int length) {
        ErrorTool.checkArgument(data.length >= start + length, "长度不足");
        return Arrays.copyOfRange(data, start, start + length);
    }

    /**
     * Constant-time byte[] comparison. The constant time behavior eliminates side channel attacks.
     *
     * @param b An array.
     * @param c An array.
     * @return 1 if b and c are equal, 0 otherwise.
     */
    public static int isEqualConstantTime(final byte[] b, final byte[] c) { // ok
        int result = 0;
        result |= b.length - c.length;
        for (int i = 0; i < b.length; i++) {
            result |= b[i] ^ c[i];
        }

        return isEqualConstantTime(result, 0);
    }

    /**
     * Constant-time byte comparison. The constant time behavior eliminates side channel attacks.
     *
     * @param b One byte.
     * @param c Another byte.
     * @return 1 if b and c are equal, 0 otherwise.
     */
    public static int isEqualConstantTime(final int b, final int c) { // ok
        int result = 0;
        final int xor = b ^ c;
        for (int i = 0; i < 8; i++) {
            result |= xor >> i;
        }

        return (result ^ 0x01) & 0x01;
    }

    /**
     * Constant-time check if byte is negative. The constant time behavior eliminates side channel attacks.
     *
     * @param b The byte to check.
     * @return 1 if the byte is negative, 0 otherwise.
     */
    public static int isNegativeConstantTime(final int b) { // ok
        return (b >> 8) & 1;
    }

    public byte[] reverseBytes() {
        return reverseBytes(outputStream.toByteArray());
    }

    /**
     * Returns a copy of the given byte array in reverse order.
     */
    public static byte[] reverseBytes(byte[] bytes) {
        // We could use the XOR trick here but it's easier to understand if we don't. If we find this is really a
        // performance issue the matter can be revisited.
        byte[] buf = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            buf[i] = bytes[bytes.length - 1 - i];
        }
        return buf;
    }

    /**
     * Gets the i'th bit of a byte array.
     *
     * @param h The byte array.
     * @param i The bit index.
     * @return The value of the i'th bit in h
     */
    public static int getBit(final byte[] h, final int i) {
        return (h[i >> 3] >> (i & 7)) & 1;
    }
}

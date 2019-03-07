package pro.bepal.util;

import java.math.BigInteger;

public class ErrorTool {

    public static void checkArgument(boolean bo, String mess) {
        if (!bo) {
            throw new RuntimeException(mess);
        }
    }

    public static void checkNotNull(Object data) {
        if (data == null) {
            throw new RuntimeException("出现空对象");
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * @param expression   a boolean expression
     * @param errorMessage the exception message to use if the check fails; will be converted to a
     *                     string using {@link String#valueOf(Object)}
     * @throws IllegalStateException if {@code expression} is false
     */
    public static void checkState(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new IllegalStateException(String.valueOf(errorMessage));
        }
    }

    public static void assertNonZero(BigInteger integer, String errorMessage) {
        if (integer.equals(BigInteger.ZERO)) {
            throw new RuntimeException(errorMessage);
        }
    }

    public static void assertLessThanN(BigInteger n, BigInteger integer, String errorMessage) {
        if (integer.compareTo(n) > 0) {
            throw new RuntimeException(errorMessage);
        }
    }
}

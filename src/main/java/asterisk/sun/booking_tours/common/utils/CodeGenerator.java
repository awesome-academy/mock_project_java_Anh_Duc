package asterisk.sun.booking_tours.common.utils;

import java.util.UUID;
import java.util.function.Predicate;

public class CodeGenerator {
    private CodeGenerator() {
        // Private constructor to prevent instantiation
    }

    /**
     * Generate a unique booking code
     *
     * @param prefix        the prefix for the code (e.g., "BK")
     * @param length        the length of the random part
     * @param existsChecker a function to check if the code already exists
     * @return a unique code
     */
    public static String generateUniqueCode(String prefix, int length, Predicate<String> existsChecker) {
        String code;
        do {
            code = prefix + UUID.randomUUID().toString().substring(0, length).toUpperCase();
        } while (existsChecker.test(code));
        return code;
    }

    /**
     * Generate a booking code with default settings (prefix "BK", length 8)
     *
     * @param existsChecker a function to check if the code already exists
     * @return a unique booking code
     */
    public static String generateBookingCode(Predicate<String> existsChecker) {
        return generateUniqueCode("BK", 8, existsChecker);
    }

    /**
     * Generate a simple random code without uniqueness check
     *
     * @param prefix the prefix for the code
     * @param length the length of the random part
     * @return a random code
     */
    public static String generateCode(String prefix, int length) {
        return prefix + UUID.randomUUID().toString().substring(0, length).toUpperCase();
    }
}

package asterisk.sun.booking_tours.common.security;

import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.util.Base64;

/**
 * Utility class for generating cryptographic keys for JWT signing
 * Run this class to generate a new secret key when needed
 */
public class KeyGenerator {

    /**
     * Generate a secure secret key for HMAC-SHA512
     * Recommended for symmetric key signing
     *
     * @return Base64 encoded secret key
     */
    public static String generateSecretKey() {
        return generateHS512Key();
    }

    /**
     * Generate a secure HMAC-SHA512 secret key
     *
     * @return Base64 encoded secret key
     */
    public static String generateHS512Key() {
        SecretKey key = Jwts.SIG.HS512.key().build();
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static void main(String[] args) {
        System.out.println("=== JWT Key Generator ===\n");

        // Generate HMAC Secret Key (Recommended for most use cases)
        System.out.println("1. HMAC-SHA512 Secret Key (Symmetric):");
        String secretKey = generateHS512Key();
        System.out.println(secretKey);
        System.out.println("\nAdd this to your application.yml:");
        System.out.println("jwt:");
        System.out.println("  secret: " + secretKey);
        System.out.println();

        System.out.println("\n=== Generation Complete ===");
        System.out.println("Note: Keep your secret key secure and never commit it to version control!");
    }
}

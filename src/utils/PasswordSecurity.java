package src.utils;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordSecurity {
    // Code partially sourced from https://dev.to/awwsmm/how-to-encrypt-a-password-in-java-42dh

    private static final SecureRandom RAND = new SecureRandom();

    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 512;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";


    /**
     * Generates salt of provided length
     * @param length length of salt
     * @return Generated salt as a string
     */
    private static Optional<String> generateSalt(final int length) {

        // salt must be at least length 1
        if (length < 1) {
            System.err.println("error in generateSalt: length must be > 0");
            return Optional.empty();
        }

        // encode the salt into a byte array
        byte[] salt = new byte[length];
        RAND.nextBytes(salt);

        // convert the byte array to string
        return Optional.of(Base64.getEncoder().encodeToString(salt));
    }

    /**
     * Hashes a password with the given salt
     * @param password password to hash
     * @param salt salt to use when hashing password
     * @return Hashed password as a string
     */
    private static Optional<String> hashPassword(String password, String salt) {

        // convert password string to char array
        char[] chars = password.toCharArray();
        // convert byte string back to byte array
        byte[] bytes = salt.getBytes();

        // assign the password to a pseudonym with specified length and number of iterations for the hashing algorithm to run.
        // this variable still holds the plaintext password along with all other encryption information.
        PBEKeySpec spec = new PBEKeySpec(chars, bytes, ITERATIONS, KEY_LENGTH);

        // sets the char array to all \000 characters (null chars)
        Arrays.fill(chars, Character.MIN_VALUE);

        try {
            // get the hashing algorithm instance
            SecretKeyFactory fac = SecretKeyFactory.getInstance(ALGORITHM);
            // hash the password and convert the hash to a byte array
            byte[] securePassword = fac.generateSecret(spec).getEncoded();
            // convert the byte array to a string and return it
            return Optional.of(Base64.getEncoder().encodeToString(securePassword));

        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            System.err.println("Exception encountered in hashPassword()");
            return Optional.empty();

        } finally {
            // clear the spec so the plaintext password is not stored in memory
            spec.clearPassword();
        }
    }

    /**
     * Check if a given password matches a hashed password.
     * @param password password to check
     * @param key hashed key of password to check against
     * @param salt salt of password to check against
     * @return boolean indicating whether the password matched the hashed password or not
     */
    public static boolean verifyPassword (String password, String key, String salt) {
        Optional<String> optEncrypted = hashPassword(password, salt);
        return optEncrypted.map(s -> s.equals(key)).orElse(false);
    }

    /**
     * Method to hash and salt a password with predefined salt length.
     * @param password password to hash.
     * @return A SecurePair object with the hashed password and salt.
     */
    public static Optional<SecurePair> securePassword(String password) {
        Optional<String> saltOptional = PasswordSecurity.generateSalt(16);
        String salt;
        if (!saltOptional.isPresent()) {
            return Optional.empty();
        } else {
            salt = saltOptional.get();
        }
        Optional<String> hashOptional = PasswordSecurity.hashPassword(password, salt);
        String hash;
        if (!hashOptional.isPresent()) {
            return Optional.empty();
        } else {
            hash = hashOptional.get();
        }
        return Optional.of(new SecurePair(hash, salt));
    }

    /**
     * Class used to store a hash and salt pair for a single hashed password.
     */
    public static class SecurePair {
        private final String hash;
        private final String salt;

        private SecurePair(String hash, String salt) {
            this.hash = hash;
            this.salt = salt;
        }

        /**
         *
         * @return hashed version of the password stored
         */
        public String getHash() {
            return hash;
        }

        /**
         *
         * @return salt used on the hashed password
         */
        public String getSalt() {
            return salt;
        }
    }
}

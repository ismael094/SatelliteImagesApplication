package utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Util class for handling hashed strings
 */

public class Encryptor {
    /**
     * Hash a string using BCrypt library
     * @param password String to be hashed
     * @return hashed string
     */
    public static String hashString(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Checks if a string and a hashed one match
     * @param password not hashed string
     * @param hashed hashed string
     * @return true if both strings matches. If not, return false
     */
    public static boolean matchString(String password, String hashed){
        return BCrypt.checkpw(password, hashed);
    }
}

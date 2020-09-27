package utils;

import org.mindrot.jbcrypt.BCrypt;

public class Encryptor {
    public static String hashString(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean matchString(String password, String hashed){
        return BCrypt.checkpw(password, hashed);
    }
}

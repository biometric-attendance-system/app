package pl.projekt.service;
import org.mindrot.jbcrypt.BCrypt;

/**
 * @brief Class uses external library bcrypt in order to ensure safety of lecturer's password.
 */
public class AuthenticationService{

    /**
     * @brief Function encrypts password.
     *
     * @param password Given password.
     * @return Hashed password.
     */
    public static String passwordToHash(String password){
        if (password == null || password.isEmpty()) return null;
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * @brief Function checks if given password is the same as encrypted one.
     *
     * @param password Given password.
     * @param hashPassword Encrypted password.
     * @return True if passwords match false otherwise.
     */
    public static boolean checkPassword(String password, String hashPassword){
        if (password == null || password.isEmpty()) return false;
        if (hashPassword == null || hashPassword.isEmpty()) return false;
        return BCrypt.checkpw(password, hashPassword);
    }
}
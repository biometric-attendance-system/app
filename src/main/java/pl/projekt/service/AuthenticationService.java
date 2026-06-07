package pl.projekt.service;
import org.mindrot.jbcrypt.BCrypt;

/**
 * @brief Class uses external library bcrypt in order to ensure safety of lecturer's PIN.
 */
public class AuthenticationService{

    /**
     * @brief Function encrypts PIN.
     *
     * @param pin Given PIN.
     * @return Hashed PIN.
     */
    public static String pinToHash(String pin){
        if (pin == null || pin.isEmpty()) return null;
        return BCrypt.hashpw(pin, BCrypt.gensalt()); 
    }

    /**
     * @brief Function checks if given PIN is the same as encrypted one.
     *
     * @param pin Given PIN.
     * @param hashPin Encrypted PIN.
     * @return True if passwords match false otherwise.
     */
    public static boolean checkPin(String pin, String hashPin){
        if (pin == null || pin.isEmpty()) return false;
        if (hashPin == null || hashPin.isEmpty()) return false;
        return BCrypt.checkpw(pin, hashPin);
    }
}
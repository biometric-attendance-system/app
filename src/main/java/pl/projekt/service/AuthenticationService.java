package pl.projekt.service;
import org.mindrot.jbcrypt.BCrypt;

public class AuthenticationService{
    
    public static String pinToHash(String pin){
        if (pin == null || pin.isEmpty()) return null;
        return BCrypt.hashpw(pin, BCrypt.gensalt()); 
    }

    public static boolean checkPin(String pin, String hashPin){
        if (pin == null || pin.isEmpty()) return false;
        if (hashPin == null || hashPin.isEmpty()) return false;
        return BCrypt.checkpw(pin, hashPin);
    }
}
package pl.projekt.Service;

import pl.projekt.service.AuthenticationService;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationTest {

    @Test
    public void correctPasswordTest(){
        String password = "CorrectPassword1!";
        String hashed = AuthenticationService.passwordToHash(password);
        assertTrue(AuthenticationService.checkPassword(password,hashed));
    }

    @Test
    public void wrongPasswordTest(){
        String password = "CorrectPassword1!";
        String hashed = AuthenticationService.passwordToHash(password);
        assertFalse(AuthenticationService.checkPassword("FalsePassword1!",hashed));
    }

    @Test
    public void NullPasswordGivenTest(){
        String password = "CorrectPassword1!";
        String hashed = AuthenticationService.passwordToHash(password);
        assertFalse(AuthenticationService.checkPassword(null,hashed));
    }

    @Test
    public void NullPasswordHashingTest(){
        assertNull(AuthenticationService.passwordToHash(null));
    }
}

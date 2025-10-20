package learning.itstep.javaweb222.services.kdf;

import com.google.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import learning.itstep.javaweb222.services.hash.HashService;

/**
 * Password-based Key derivation function ver.1
 * by sec 5.1 RFC 2898 https://datatracker.ietf.org/doc/html/rfc2898#section-5.1
 * @author Lector
 */
public class PbKdf1Service implements KdfService {
    private final HashService hashService;
    private static final int ITERATION_COUNT = 3;
    private static final int DK_LENGTH = 32;

    @Inject
    public PbKdf1Service(HashService hashService) {
        this.hashService = hashService;
    }
    
    @Override
    public String dk(String password, String salt) {
        String t = hashService.digest(password + salt);
// System.out.println(password + salt + " " + t);
        for (int i = 1; i < ITERATION_COUNT; i++) {
            t = hashService.digest(t);            
        }
        return t.substring(0, DK_LENGTH);
    }

}
/*
Д.З. Створити сервіс, який буде видавати мітку часу у вигляді
числа, що відповідає кількості секунд від UNIX-часу (timestamp).
Впровадити у проєкт, вивести результат на сторінці.
До звіту додати скріншот.
*/
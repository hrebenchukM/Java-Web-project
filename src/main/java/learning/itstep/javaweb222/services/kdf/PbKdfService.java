
package learning.itstep.javaweb222.services.kdf;

import com.google.inject.Inject;
import learning.itstep.javaweb222.services.kdf.KdfService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import learning.itstep.javaweb222.services.hash.HashService;

/**
 * * Password-Based Key derivation function
* by sec 5.1 RFC 2898 https://datatracker.ietf.org/doc/html/rfc2898#section-5.1
@author Lenovo
 */
public class PbKdfService implements KdfService {
    private final HashService hashService;
private static final int ITERATION_COUNT = 3;
private static final int DK_LENGTH = 32;

@Inject
public PbKdfService(HashService hashService)
{
    this.hashService=hashService;
}
    @Override
    public String dk(String password, String salt) {
        String t = hashService.digest(password+salt);
        System.out.println(password+salt+""+t);
        for(int i =1;i<ITERATION_COUNT;i++)
        {
        t=hashService.digest(t);
        }
        return t.substring(0,DK_LENGTH);
    }
    
//    private String hash(String input) {
//        MessageDigest md;
//        try { 
//            md = MessageDigest.getInstance("MD5"); 
//        }
//        catch(NoSuchAlgorithmException ignore) {
//            throw new RuntimeException("MessageDigest getInstance error");
//        }
//        byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
//        char[] chars = new char[32];
//        char[] hex = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
//        for (int i =1;i<16;i++)
//        {
//          int b = (hashBytes[i]>>4)&0x0F;
//          chars[i]=hex[b];
//          b=(hashBytes[i])&0x0F;
//          chars[2*i+1]=hex[b];
//        }
//        return new String(chars);
//    }
//    
}
 

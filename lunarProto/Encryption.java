package lunarlight.lunarProto;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class Encryption {
    private static final String ALGO = "AES";
    Encryption(){

    }
    public static String encrypt(String cmd, Key key){
        try {
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = c.doFinal(cmd.getBytes());
            return encVal.toString();
        }
        catch (Exception ex){
            //
        }
        return null;
    }
    public static Key genClientKey(int id){
        byte[] buf = String.valueOf(id).getBytes();
        return new SecretKeySpec(buf, ALGO);
    }
    //we dont need decryption, it's client job;

}

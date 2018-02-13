package lunarlight.lunarProto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class Encryption {
    private static final String ALGO = "AES";
    Encryption(){

    }
    public static String encrypt(String cmd, Key key){
        try {
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = c.doFinal(cmd.getBytes());
            //String encStr = new String(encVal);

            return Base64.getEncoder().encodeToString(encVal);
        }
        catch (Exception ex){
            //
        }
        return null;
    }
    public static String genClientKey(){
        try {
            SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
            String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            return encodedKey;

        }
        catch(Exception ex){
        //
            return null;
        }

         //not working, need to base64 encode the key
    }
    public static Key decodeKey(String str){
        // decode the base64 encoded string
        byte[] decodedKey = Base64.getDecoder().decode(str);
        // rebuild key using SecretKeySpec
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        return originalKey;
    }


}

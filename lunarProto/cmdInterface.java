package lunarlight.lunarProto;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.net.Socket;

public class cmdInterface {
    private String data;
    private Socket conn;
    private String authData[] = new String[2];
    public cmdInterface(Socket conn){
        this.conn = conn;
        authData[0]="lunar";//login
        authData[1]="light";//passwd

        if(!(this.auth())){
            try {
                this.conn.close();

            }
            catch (Exception ex){
                // nothing
            }
        }
        else{

        }

    }
    private boolean auth(){
        try{
        InputStream in = conn.getInputStream();
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write("###Lunar Light authentication###\n" + "Login: "); //rn will accept plain text, later  upgrade to hash
        writer.flush();
        BufferedReader bufread = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        if(bufread.readLine().equals(authData[0])){ // works fine when connecting via putty telnet
            writer.write("Password: ");
            writer.flush();
            if(bufread.readLine().equals(authData[1])){
                writer.write("Login success\r\n");
                writer.flush();
                return true;
            }
            return false;

        }
        else {
            writer.write("Access denied\r\n");
            writer.flush();
            return false;
        }
        }
        catch (java.io.IOException ex){
            System.out.println(ex);
            return false;
        }

    }
    private String trimNewline(String str){
        str = str.replaceAll("(\\r|\\n)", "");
        return str;
    }

}

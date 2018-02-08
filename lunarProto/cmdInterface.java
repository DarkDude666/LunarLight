package lunarlight.lunarProto;

import lunarlight.ListenerService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.net.Socket;

public class cmdInterface {
    private String data;
    private Socket conn;
    private String authData[] = new String[2];
    private BufferedReader in;
    private OutputStreamWriter out;
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
            this.session();
        }

    }
    private void session(){
            try {
                String str = "";
                while(true){
                    out.write("Command: ");
                    out.flush();
                    str = in.readLine();
                    switch(str) {
                        case "shutdown":
                            this.shutdown();

                        case "exit":
                            this.conn.close();
                            return;

                        default :
                            out.write("Doing nothing\r\n");
                            out.flush();
                    }


                }

            }
            catch (Exception ex){
                //
            }
    }
    private boolean auth(){
        try{
        //InputStream in = conn.getInputStream();
        this.out = new OutputStreamWriter(conn.getOutputStream());
        out.write("###Lunar Light authentication###\n" + "Login: "); //rn will accept plain text, later  upgrade to hash
        out.flush();
        this.in = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        if(in.readLine().equals(authData[0])){ // works fine when connecting via putty telnet
            out.write("Password: ");
            out.flush();
            if(in.readLine().equals(authData[1])){
                out.write("Login success\r\n");
                out.flush();
                return true;
            }
            return false;

        }
        else {
            out.write("Access denied\r\n");
            out.flush();
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
    private void shutdown(){
        try {
            out.write("Shutting down...\r\n");
            out.flush();
            ListenerService.die();
            conn.close();
        }
        catch(Exception ex){}

    }

}

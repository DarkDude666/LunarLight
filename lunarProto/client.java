package lunarlight.lunarProto;

import lunarlight.database.lldb;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import java.security.Key;

public class client {
    private static String broadcastCmd = "";
    private static final int  CLSIZE = 10; // size of client's packet in chars
    private Socket conn;
    private BufferedReader in;
    private OutputStreamWriter out;
    public client(Socket conn){
        this.conn=conn;
        try {
            this.out = new OutputStreamWriter(conn.getOutputStream());
            this.in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            this.acquireData();
        }
        catch (Exception ex){
            //
        }
    }
    private void acquireData(){
        char[] data = new char[100]; //actually more than enough
        //String data ="";
        try {
            int bytesRead = in.read(data);
            if(data[0]==('R')){ //client can send only 'R' or int id, so we dont care what's after first item in arr
                String remoteAddr = conn.getInetAddress().toString().substring(1);
                System.out.println("Registering new client \""+remoteAddr+"\""); //just cutting the slash
                int id = lldb.registerClient(remoteAddr);
                if(id == -1){
                    try {
                        conn.close();
                        return;
                    }
                    catch (Exception ex){
                        return;
                        //
                    }
                }
                out.write(id+":"+Encryption.genClientKey());//then we tell him his UID and Encryption key encoded in base64
                out.flush();
                conn.close();
            }
            else if(bytesRead<=CLSIZE){//means client is registered, and sending us his unique ID
                //if the length is higher, then we again dont care and wont procedure that.
                String strId = new String(data);
                int id = Integer.parseInt(strId);
                String strKey = lldb.acquireKey(id);
                if(strKey==null){
                    conn.close();//ERROR!!! no such id
                    return;
                }
                Key key = Encryption.decodeKey(strKey);
                String encStr  = Encryption.encrypt(broadcastCmd, key );
                out.write(encStr);
                out.flush();
                conn.close();
            }
            else{
                conn.close();
            }


        }
        catch (Exception ex){

        }

    }
    public static void setBroadcastCmd(String cmd){
        broadcastCmd = cmd;
    }
    public static String getBroadcastCmd(){
        return broadcastCmd;
    }
}

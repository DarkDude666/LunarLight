package lunarlight.lunarProto;

import lunarlight.database.lldb;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import java.security.Key;
import java.util.Date;
import java.util.Timer;


public class client {
    private static String broadcastCmd = "";
    private static final int  CLSIZE = 10; // MAX size of client's packet
    private Socket conn;
    private BufferedReader in;
    private OutputStreamWriter out;
    public client(Socket conn){
        this.conn=conn;

        try {
            this.conn.setSoTimeout(10*1000); // timeout for client will be ten seconds, so we wont hang
            this.out = new OutputStreamWriter(conn.getOutputStream());
            this.in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            this.acquireData();
        }
        catch (Exception ex){
            //
        }
    }
    private void acquireData(){
        char[] data = new char[11]; //actually more than enough
        //String data ="";
        try {
            Date now = new Date();
            int bytesRead = in.read(data);
            if(data[0]==('R')){ //client can send only 'R' or int id, so we dont care what's after first item in arr
                String remoteAddr = conn.getInetAddress().toString().substring(1);
                System.out.println(now.toString()+" Registering new client \""+remoteAddr+"\""); //just cutting the slash
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
                out.write(id+":"+lldb.acquireKey(id));//then we tell him his UID and Encryption key encoded in base64
                out.flush();
                conn.close();
            }
            else if(bytesRead<=CLSIZE){//means client is registered, and sending us his unique ID
                //if the length is higher, then we again dont care and wont procedure that.
                String strId = new String(data);
                int id = Integer.parseInt(strId.trim());
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
            ex.printStackTrace();
            System.out.println(ex);
            System.out.println("Error while processing client");
        }

    }
    public static void setBroadcastCmd(String cmd){
        broadcastCmd = cmd;
    }
    public static String getBroadcastCmd(){
        return broadcastCmd;
    }
}

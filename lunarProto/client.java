package lunarlight.lunarProto;

import lunarlight.lldb;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class client {
    private static String broadcastCmd = "";
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
    public void acquireData(){
        try {
            String data = in.readLine();
            if(data.equals("R")){
                System.out.println("Registering new client \""+conn.getRemoteSocketAddress().toString()+"\"");
                lldb.registerClient(conn.getRemoteSocketAddress().toString()); //other calculations such as id, time and key
                //will be done in another class
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

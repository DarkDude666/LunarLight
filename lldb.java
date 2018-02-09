package lunarlight;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class lldb
{
    private static int LastId;
    private static Connection conn = null;
    lldb(){

    }

    public void connect(){
        Properties props = new Properties();
        props.put("user","root");
        props.put("pass","lunarlight");
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", props);
            this.testConn();
        }
        catch (Exception ex){
            //
            System.out.println("Couldnt connect");
        }
    }
    public boolean testConn(){
        try {
            if (conn.isValid(10))
                return true;
            else{
                return  false;
            }
        }catch (Exception ex){
            return false;
        }
    }
    public static void registerClient(String ip){
        //placeholder

    }
    private int updateId(){
        return ++LastId;
    }
}

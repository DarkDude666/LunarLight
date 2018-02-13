package lunarlight.database;

import com.mysql.fabric.jdbc.FabricMySQLDriver;
import lunarlight.lunarProto.Encryption;
import java.sql.*;
import java.util.Properties;

public class lldb //class with static methods, because we need it everywhere
{
    private static int LastId = 0;
    private static Connection conn = null;
    private static Statement statement = null;
    public lldb(){ }

    public void connect(){
        Properties props = new Properties();

        props.put("user","lunarlight"); //later will add config file
        props.put("password","lunarlight");
        props.put("useSSL","false");
        try {
            Driver jdbc = new FabricMySQLDriver();
            DriverManager.registerDriver(jdbc);
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/lldb", props);
            statement = conn.createStatement();
            //this.testConn();
            lldb.getLastId();

        }
        catch (Exception ex){
            //
            System.out.println("Couldnt connect to database");
            //System.out.println(ex);
        }
    }
    public boolean testConn(){
        try {
            if (!conn.isClosed())
                return true;
            else{
                return  false;
            }
        }catch (Exception ex){
            return false;
        }
    }
    private static void getLastId(){
        try {
            ResultSet res = statement.executeQuery("SELECT * FROM lldb.clients ORDER BY uid DESC LIMIT 1");
            if(res.next()) {
                LastId = res.getInt(1);
            }
        }
        catch (SQLException ex){
            //
        }
    }
    public static int registerClient(String ip){ // we return id
        //placeholder
        if(ip==null){return -1;}
        //

        ExecStatement("INSERT INTO lldb.clients " + "(EncKey, IpAddr) VALUES ('"+
                Encryption.genClientKey()+"','"+
                ip+"')");

        return lldb.updateId();

    }
    private static synchronized int updateId(){
        LastId = ++LastId;
        return LastId;
    }
    public static String acquireKey(int id){
        if(id<0){
            return null;
        }
        try{
        ResultSet res = statement.executeQuery("SELECT EncKey FROM lldb.clients WHERE UID ="+id);
            if(res.next()){
                return  res.getString(2);

            }
            else{
                return null;
            }
        }
        catch (SQLException ex){
            return null;
        }
    }
    public static boolean ExecStatement(String sql){ //write only
        try {
            statement.executeUpdate(sql);
            return true;
        }
        catch (Exception ex){
            return false;
        }

    }

    public static void close(){
        try {
            statement.close();
            conn.close();
        }
        catch (Exception ex){
            //
        }
    }
}

package lunarlight;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lunarlight.ServingThreads.cmdConnThread;
import lunarlight.ServingThreads.serveConnThread;
import lunarlight.database.lldb;

public class ListenerService {
    private int listenPort; // will listen for "bots"
    private int controlPort; // here we can setup our server at runtime
    private Socket conn;
    private ExecutorService threadPool;
    private int ThreadsCount = 2000;
    private String dbPort;
    private String dbUser;
    private String dbPass;
    private String dbHost;

    public static void die(){
        try{
            lldb.close(); //just to be sure, we are closing our database connection
            System.exit(-2);
        }
        catch (Exception ex){
            //
        }
    }
    ListenerService(String[] args){ // so these will be default ports
        listenPort = 3333;
        controlPort = 3330;
        parseArgs(args);

    }

    public void init(){
        try {
            //connecting to db
            lldb db = new lldb(dbPort,dbUser,dbPass, dbHost);
            db.connect();
            threadPool = Executors.newFixedThreadPool(ThreadsCount);
            ServerSocket ListenSock = new ServerSocket(this.listenPort);
            //ServerSocket controlSock = new ServerSocket(this.controlPort);
            cmdConnThread cmdConn = new cmdConnThread(new ServerSocket(this.controlPort));// we cant rly DDOS that
            cmdConn.start(); // first we execute server setup listener
            while (true) { // then for other clients
                this.conn = ListenSock.accept();
                execThread(this.conn);
            }

        }
        catch (java.io.IOException ex) {
            System.out.println(ex);
        }


    }
    private void execThread(Socket c) { //method executes thread for every connection, except command
        Callable<Void> task = new serveConnThread(c);
        threadPool.submit(task);
    }
    private void readConfFile(String file){
        //
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            Properties props = new Properties();
            props.load(reader);
            this.listenPort = Integer.parseInt(props.getProperty("CLIENTPORT","3333").trim());
            this.controlPort =Integer.parseInt(props.getProperty("COMMNDPORT","3330").trim());
            this.ThreadsCount = Integer.parseInt(props.getProperty("THREADS","2000").trim());
            this.dbPort = props.getProperty("DBPORT", "3306");
            this.dbUser = props.getProperty("DBUSER", "lunarlight");
            this.dbPass = props.getProperty("DBPASS","lunarlight");
            this.dbHost = props.getProperty("DBHOST","localhost");

        }
        catch (Exception ex){
            System.out.println("File processing error");
            die();
        }
    }
    private void parseArgs(String[] args){
        if(args.length==0){
            System.out.println("Wrong usage!\r\n"
            +"-i or --install to install service\r\n"
            +"-c <Path> to supply config file");
            die(); //at least we need config file
        }
        else{
            if (args[0].equals("--install") || args[0].equals("-i")){
                //placeholder, installation script not implemented yet
            }
            else if (args[0].equals("-c") && args.length==2){
                String path = args[1];
                readConfFile(path);
            }
            else{
                System.out.println("Incorrect or missing path to config");
                die(); //at least we need config file
            }

        }

    }


}

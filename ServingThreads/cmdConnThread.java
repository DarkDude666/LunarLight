package lunarlight.ServingThreads;

import lunarlight.lunarProto.cmdInterface;

import java.net.ServerSocket;
import java.net.Socket;

public class cmdConnThread extends Thread{
    private ServerSocket socket;
    private Socket setupClient;

    public cmdConnThread(ServerSocket srvSock){
        this.socket = srvSock;
    }

    @Override
    public void run(){
      try{
          while(true){
              setupClient = socket.accept();
              setupClient.setSoTimeout(60*1000);//timeout for setup socket will be one minute
              cmdInterface cmdint = new cmdInterface(setupClient);
          }
      }
      catch(java.io.IOException ex){
          System.out.println("Something bad happened with control port\n" + "Shutting down....");
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

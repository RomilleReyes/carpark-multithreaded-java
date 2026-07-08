import java.net.*;
import java.util.ArrayList;
import java.io.*;





public class CarParkServer {
  public static void main(String[] args) throws IOException {

	ServerSocket ActionServerSocket = null;
    boolean listening = true;
    String ActionServerName = "ActionServer";
    int ActionServerNumber = 4545;
    
    double SharedVariable = 0;
    ArrayList<String> queue = new ArrayList<String>();

    //Create the shared object in the global scope...
    
    SharedCarparkState ourSharedActionStateObject = new SharedCarparkState(SharedVariable, queue);
        
    // Make the server socket

    try {
      ActionServerSocket = new ServerSocket(ActionServerNumber);
    } catch (IOException e) {
      System.err.println("Could not start " + ActionServerName + " specified port.");
      System.exit(-1);
    }
    System.out.println(ActionServerName + " started");

    //Got to do this in the correct order with only four clients!  Can automate this...
    
    while (listening){
      new CarparkServerThread(ActionServerSocket.accept(), "EntranceClient1", ourSharedActionStateObject).start();
      new CarparkServerThread(ActionServerSocket.accept(), "EntranceClient2", ourSharedActionStateObject).start();
      new CarparkServerThread(ActionServerSocket.accept(), "ExitClient1", ourSharedActionStateObject).start();
      new CarparkServerThread(ActionServerSocket.accept(), "ExitClient2", ourSharedActionStateObject).start();
      System.out.println("New " + ActionServerName + " thread started.");
    }
    ActionServerSocket.close();
  }
}

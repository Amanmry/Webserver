import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    
    public void run() throws UnknownHostException{
        int port = 8010;
        InetAddress address =  InetAddress.getByName("localhost");
        try(Socket clientSocket = new Socket(address, port)) {
            PrintWriter toServer = new PrintWriter(clientSocket.getOutputStream());
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            toServer.println("Hello from Client");
            String line = fromServer.readLine();
            System.out.println("Response from Server is : " + line);
            toServer.close();
            fromServer.close();
        } 
        catch(Exception e) {
            System.out.println("Exception Occured : " + e.getMessage());
        }

    }

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.run();
        }
        catch(Exception e) {
            System.out.println("Exception Occured : " + e.getMessage());
        }
    }

}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    
    public void run() {
        int port = 8010;
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(10000);
            while(true) {
                System.out.println("Server is listening on Port " + port);
                Socket acceptedConnection = serverSocket.accept();
                System.out.println("Connection Accepted from Client : " + acceptedConnection.getRemoteSocketAddress());
                PrintWriter toClient = new PrintWriter(acceptedConnection.getOutputStream());
                BufferedReader fromClient = new BufferedReader(new InputStreamReader(acceptedConnection.getInputStream()));
                toClient.println("Hello from Server");
                toClient.close();
                fromClient.close();
                acceptedConnection.close();
            }
        }
        catch(IOException e) {
            System.out.println("IOException Occured : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.run();
        }
        catch(Exception e) {
            System.out.println("Exception : " + e.getMessage());
        }

    }

}

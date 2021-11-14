
/*
 * Build off of the Oracle exmaple for hosting a server that can create and maintain several web socket connections
 *
 */

import java.net.*;
import java.io.*;

public class MultiServer {

    // private static List<MultiServerThread> threads = new ArrayList<>();
    private static Socket clientA;
    private static Socket clientB;

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java MultiServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        System.out.println("Listeing on port 3000");
        while(true) {
            clientA = null;
            clientB = null;
            try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
                while (clientA == null || clientB == null) {
                    // MultiServerThread mst = new MultiServerThread(serverSocket.accept());
                    Socket connectedClient = serverSocket.accept();
                    if (clientA == null)
                        clientA = connectedClient;
                    else
                        clientB = connectedClient;
                }
    
                System.out.println("Got two clients!");
    
                // Generate starting positions
                StringBuffer clientAworkers = new StringBuffer();
                StringBuffer clientBworkers = new StringBuffer();
    
                for (int i = 0; i < 5; i++) {
                    int x = (int) (Math.random() * 100 + 150);
                    int y = (int) (Math.random() * 100 + 150);
                    clientAworkers.append(i + " " + x + " " + y + " ");
                    clientBworkers.append(i + " " + (x + 1400) + " " + (y + 1400) + " ");
                }
    
                MultiServerSimpleThread msstA = new MultiServerSimpleThread(clientA, clientB, 0, clientAworkers.toString(),
                        clientBworkers.toString());
                MultiServerSimpleThread msstB = new MultiServerSimpleThread(clientB, clientA, 1, clientBworkers.toString(),
                        clientAworkers.toString());
                msstA.start();
                msstB.start();
    
            } catch (IOException e) {
                System.err.println("Could not listen on port " + portNumber);
                System.exit(-1);
            }
        }
    }

}
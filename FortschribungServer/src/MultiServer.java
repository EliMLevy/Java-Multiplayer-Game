
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
        while (true) {
            clientA = null;
            clientB = null;
            try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
                while (clientA == null || clientB == null) {
                    // MultiServerThread mst = new MultiServerThread(serverSocket.accept());
                    Socket connectedClient = serverSocket.accept();
                    if (clientA == null) {
                        clientA = connectedClient;
                        System.out.println("Client A connected");
                    } else {
                        clientB = connectedClient;
                        System.out.println("Client B connected");

                    }
                }

                System.out.println("Got two clients!");

                GameState gameState = new GameState();

                for (int i = 0; i < 1; i++) {
                    double x = (Math.random() * (64 * 3 - 70) + 70);
                    double y = (Math.random() * (400 - 298) + 298);

                    // MathUtils.random(70, 64 * 3), MathUtils.random(298, 400)

                    gameState.addWorker('A', i, x, y, x, y);
                    gameState.addWorker('B', i, (x + 10), (y + 10), x, y);
                }

                MultiServerSimpleThread msstA = new MultiServerSimpleThread(clientA, clientB, 0, gameState);
                MultiServerSimpleThread msstB = new MultiServerSimpleThread(clientB, clientA, 1, gameState);
                msstA.start();
                msstB.start();

            } catch (IOException e) {
                System.err.println("Could not listen on port " + portNumber);
                System.exit(-1);
            }
        }
    }

}
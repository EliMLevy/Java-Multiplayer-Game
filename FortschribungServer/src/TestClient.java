
/*
 * Based off of the Oracle example for connecting to a Websocket and send and recieve data
 */

import java.io.*;
import java.net.*;

public class TestClient {
    public static void main(String[] args) throws IOException {

        if (args.length != 3) {
            System.err.println("Usage: java EchoClient <host name> <port number> <listening?>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        boolean listening;
        if(args[2].equals("1")) listening = true;
        else listening = false;

        try (Socket socket = new Socket(hostName, portNumber);
                PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader fromServerStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String fromServer;
            String fromUser;

            while ((fromServer = fromServerStream.readLine()) != null) {
                System.out.println("Server: " + fromServer);
                if (fromServer.equals("Bye."))
                    break;

                if(!listening) {
                    System.out.println("waiting for user input");
                    fromUser = stdIn.readLine();
                    if (fromUser != null) {
                        System.out.println("Client: " + fromUser);
                        toServer.println(fromUser);
                    }
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            // System.err.println(e.toString());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

/*
 * Copyright (c) 1995, 2013, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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

            new MultiServerSimpleThread(clientA, clientB).start();
            new MultiServerSimpleThread(clientB, clientA).start();


            // try (PrintWriter toClientA = new PrintWriter(clientA.getOutputStream(), true);
            //         BufferedReader fromClientA = new BufferedReader(new InputStreamReader(clientA.getInputStream()));

            //         PrintWriter toClientB = new PrintWriter(clientB.getOutputStream(), true);
            //         BufferedReader fromClientB = new BufferedReader(new InputStreamReader(clientB.getInputStream()));

            // ) {

            //     toClientA.println("The game will begin now...");
            //     toClientB.println("The game will begin now...");

            //     String clientAIncoming;
            //     String clientBIncoming;

            //     boolean clientAClosed = false;
            //     boolean clientBClosed = false;

            //     while (true) {
                    
            //         System.out.println("pre");
            //         clientAIncoming = fromClientA.readLine();
            //         System.out.println("limbo");
            //         clientBIncoming = fromClientB.readLine();
            //         System.out.println("post");

            //         if (clientAIncoming.equals("CLOSE"))
            //             clientA.close();
            //         if (clientBIncoming.equals("CLOSE"))
            //             clientB.close();
            //         if(clientAClosed && clientBClosed)
            //             break;
                    

            //         if(clientAIncoming.length() > 0) {
            //             System.out.println(clientAIncoming);
            //             toClientB.println(clientAIncoming);
            //         }

            //         if(clientBIncoming.length() > 0) {
            //             System.out.println(clientBIncoming);
            //             toClientA.println(clientBIncoming);
            //         }

            //     }
            // } catch (IOException e) {
            //     e.printStackTrace();
            // }

        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }


}
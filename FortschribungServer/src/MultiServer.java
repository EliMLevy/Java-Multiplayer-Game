
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;

public class MultiServer {

    // private static List<MultiServerThread> threads = new ArrayList<>();
    private static MultiServerThread clientA;
    private static MultiServerThread clientB;


    public static List<StringBuffer> outgoingData = new ArrayList<>();
    

    private static Map<MultiServerThread, MultiServerThread> crossRoads = new HashMap<>();

    private static int i = 0;

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java MultiServer <port number>");
            System.exit(1);
        }

        
        outgoingData.add(new StringBuffer());
        outgoingData.add(new StringBuffer());
        
        int portNumber = Integer.parseInt(args[0]);
        System.out.println("Listeing on port 3000");

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (clientA == null || clientB == null) {
                MultiServerThread mst = new MultiServerThread(serverSocket.accept());
                mst.dataIndex = i++;
                mst.start();
                if(clientA == null) clientA = mst;
                else clientB = mst;
            }

            System.out.println("Got two clients!");

            crossRoads.put(clientA, clientB);
            crossRoads.put(clientB, clientA);
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }

    public static void sendData(MultiServerThread from, String data) {
        System.out.println(from.id + ": " + data);
        MultiServerThread target = MultiServer.crossRoads.get(from);
        System.out.println(target.id + ": " + data);
        outgoingData.get(from.dataIndex + 1 % 2).append(data);
        outgoingData.get(from.dataIndex + 1 % 2).append("::");
        // target.send(data);
        // clientA.send(data);
        // clientB.send(data);

    }

}
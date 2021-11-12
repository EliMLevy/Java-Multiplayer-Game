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

public class MultiServerThread extends Thread {
    private Socket socket = null;

    public int id = (int)(Math.random() * 1000);

    private static StringBuffer incoming = new StringBuffer();

    public MultiServerThread(Socket socket) {
        super("MultiServerThread");
        this.socket = socket;
        System.out.println("Thread created!");
    }

    public void run() {

        try (PrintWriter toClient = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
            String inputLine;
            toClient.println("Welcome");

            boolean listening = true;
            while (listening) {
                inputLine = fromClient.readLine();
                if (inputLine.equals("CLOSE"))
                    break;
                if (inputLine.length() > 0) {
                    MultiServer.sendData(this, inputLine);
                }
                if (incoming.length() > 0) {
                    toClient.println(incoming.toString());
                    incoming = new StringBuffer();
                }
            }

            // while ((inputLine = fromClient.readLine()) != null) {
            //     // outputLine = kkp.processInput(inputLine);
            //     // out.println(outputLine);
            //     if (inputLine.equals("Bye"))
            //         break;
            //     if (incoming.length() > 0) {
            //         toClient.println(incoming.toString());
            //         incoming = new StringBuffer();
            //     }
            //     if (inputLine.length() > 0) {
            //         MultiServer.sendData(this, inputLine);
            //     }
            // }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String data) {
        incoming.append(data);
        incoming.append("::");
    }
}
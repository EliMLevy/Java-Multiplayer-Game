import java.net.*;
import java.io.*;

public class MultiServerSimpleThread extends Thread {

    private Socket src;
    private Socket dest;

    private final int id;

    private final GameState initialGameState;
    public boolean connected = true;

    public MultiServerSimpleThread(Socket src, Socket dest, int id, GameState gameState) {
        super("MultiServerThread");
        this.src = src;
        this.dest = dest;

        this.id = id;

        System.out.println("Thread created!");

        this.initialGameState = gameState;
    }

    public void run() {
        try (PrintWriter toDest = new PrintWriter(dest.getOutputStream(), true);
                BufferedReader fromSrc = new BufferedReader(new InputStreamReader(src.getInputStream()));) {

            String inputLine;

            if(id == 0) {
                toDest.println("ping");
            } else {
                toDest.println("pong");
            }

            // this.sendInitInfo(toDest);
            toDest.println(this.initialGameState.serialize());
     
            while ((inputLine = fromSrc.readLine()) != null) {
                if (inputLine.equals("CLOSE")) {
                    this.connected = false;
                    
                    break;

                }
                if (inputLine.length() > 0) {
                    // System.out.println(inputLine);
                    toDest.println(inputLine + "!" + this.initialGameState.checkForUpdates(this.id));
                }
            }
            this.src.close();
        } catch (SocketException e) {
            System.out.println("Client " + this.id + " disconnected...");
            this.connected = false;
        } catch (IOException e) {
            e.printStackTrace();
            this.connected = false;

        }
    }


    


}

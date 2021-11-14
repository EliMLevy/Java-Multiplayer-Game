import java.net.*;
import java.io.*;

public class MultiServerSimpleThread extends Thread  {

    private Socket src;
    private Socket dest;
    
    private int id;
    private String yourWorkers;
    private String enemyWorkers;


    public MultiServerSimpleThread(Socket src, Socket dest, int id, String yourWorkers, String enemyWorkers) {
        super("MultiServerThread");
        this.src = src;
        this.dest = dest;
        this.id = id;
        this.yourWorkers = yourWorkers;
        this.enemyWorkers = enemyWorkers;
        System.out.println("Thread created!");
    }


    public void run() {
        try (PrintWriter toDest = new PrintWriter(dest.getOutputStream(), true);
            BufferedReader fromSrc = new BufferedReader(new InputStreamReader(src.getInputStream()));) {

            String inputLine;
            toDest.println(this.id);
            toDest.println(this.yourWorkers);
            toDest.println(this.enemyWorkers);


            while ((inputLine = fromSrc.readLine()) != null) {
                System.out.println("looping");
                if (inputLine.equals("CLOSE"))
                    break;
                if (inputLine.length() > 0) {
                    toDest.println(inputLine);
                    System.out.println("From client: " + inputLine);
                }
            }
            this.src.close();
        } catch (SocketException e) {
            System.out.println("Client " + this.id + " disconnected...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}

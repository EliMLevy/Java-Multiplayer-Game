import java.net.*;
import java.io.*;

public class MultiServerSimpleThread extends Thread  {

    private Socket src;
    private Socket dest;
    private int id;

    public MultiServerSimpleThread(Socket src, Socket dest, int id) {
        super("MultiServerThread");
        this.src = src;
        this.dest = dest;
        this.id = id;
        System.out.println("Thread created!");
    }


    public void run() {
        try (PrintWriter toDest = new PrintWriter(dest.getOutputStream(), true);
            BufferedReader fromSrc = new BufferedReader(new InputStreamReader(src.getInputStream()));) {

            String inputLine;
            toDest.println(this.id);

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}

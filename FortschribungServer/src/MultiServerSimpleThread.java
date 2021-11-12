import java.net.*;
import java.io.*;

public class MultiServerSimpleThread extends Thread  {

    private Socket src;
    private Socket dest;

    public MultiServerSimpleThread(Socket src, Socket dest) {
        super("MultiServerThread");
        this.src = src;
        this.dest = dest;
        System.out.println("Thread created!");
    }


    public void run() {
        try (PrintWriter toDest = new PrintWriter(dest.getOutputStream(), true);
            BufferedReader fromSrc = new BufferedReader(new InputStreamReader(src.getInputStream()));) {

            String inputLine;
            toDest.println("The game will begin now...");

            while ((inputLine = fromSrc.readLine()) != null) {

                if (inputLine.equals("CLOSE"))
                    break;
                if (inputLine.length() > 0) {
                    toDest.println(inputLine);
                }
            }
            this.src.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}

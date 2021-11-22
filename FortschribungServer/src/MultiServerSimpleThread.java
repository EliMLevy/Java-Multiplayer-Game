import java.net.*;
import java.io.*;

public class MultiServerSimpleThread extends Thread {

    private Socket src;
    private Socket dest;

    private int id;
    private GameState gameState;

    private char client;
    private char otherClient;

    public MultiServerSimpleThread(Socket src, Socket dest, int id, GameState gs) {
        super("MultiServerThread");
        this.src = src;
        this.dest = dest;
        this.id = id;
        this.client = this.id == 0 ? 'A' : 'B';
        this.otherClient = this.id == 0 ? 'B' : 'A';

        this.gameState = gs;
        System.out.println("Thread created!");
    }

    public void run() {
        try (PrintWriter toDest = new PrintWriter(dest.getOutputStream(), true);
                BufferedReader fromSrc = new BufferedReader(new InputStreamReader(src.getInputStream()));) {

            String inputLine;
            toDest.println(this.id);
            toDest.println(this.gameState.mapRows + " " + this.gameState.mapCols + " " + this.gameState.serializeMap() + " " + this.gameState.serializeGameObjects());
            toDest.println(this.gameState.serialize());

            while ((inputLine = fromSrc.readLine()) != null) {
                if (inputLine.equals("CLOSE"))
                    break;
                if (inputLine.length() > 0) {
                    parseInput(this.gameState, inputLine);
                    String output = this.gameState.serialize(this.client);
                    toDest.println(output);
                }
            }
            this.src.close();
        } catch (SocketException e) {
            System.out.println("Client " + this.id + " disconnected...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parseInput(GameState gs, String input) {
        String[] parsedInput = input.split(" ");
        for (int i = 0; i < parsedInput.length; i ++) {
            switch (parsedInput[i]) {
            case "worker":
                gs.updateWorker(this.client, Integer.parseInt(parsedInput[i + 1]),
                        Double.parseDouble(parsedInput[i + 2]), Double.parseDouble(parsedInput[i + 3]));
                break;
            case "newWorker":
                gs.addWorker(this.client, Integer.parseInt(parsedInput[i + 1]), 
                    Double.parseDouble(parsedInput[i + 2]),
                        Double.parseDouble(parsedInput[i + 3]), 
                        Double.parseDouble(parsedInput[i + 4]),
                        Double.parseDouble(parsedInput[i + 5]));
            case "generate":
                gs.addToEvents(this.client, "generate", parsedInput[i + 1], parsedInput[i + 2]);
                break;
            case "weaponry":
                gs.addToEvents(this.client, "weaponry", parsedInput[i + 1], parsedInput[i + 2]);
                break;
            default:
                break;
            }
        }
    }

}

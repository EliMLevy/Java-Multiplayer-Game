import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class GameState {
    public Map<Integer, Worker> clientAWorkers = new HashMap<>();
    public Map<Integer, Worker> clientBWorkers = new HashMap<>();

    public Map<Integer, Bullet> clientABullets = new HashMap<>();
    public Map<Integer, Bullet> clientBBullets = new HashMap<>();

    public StringBuffer clientAEvents = new StringBuffer();
    public StringBuffer clientBEvents = new StringBuffer();

    public int mapRows = 20;
    public int mapCols = 20;
    public String[] gameMap = {  
     "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1",
     "1", "0", "0", "1", "1", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "1",
     "1", "0", "0", "0", "0", "0", "1", "0", "0", "1", "0", "1", "0", "1", "1", "1", "1", "1", "0", "1",
     "1", "1", "1", "1", "1", "0", "1", "1", "1", "1", "0", "1", "0", "1", "0", "0", "0", "1", "0", "1",
     "1", "1", "1", "1", "0", "0", "0", "1", "1", "0", "0", "1", "0", "0", "0", "1", "0", "1", "0", "1",
     "1", "0", "0", "1", "1", "1", "0", "0", "1", "0", "1", "1", "0", "1", "1", "1", "0", "1", "0", "1",
     "1", "0", "0", "0", "0", "1", "1", "0", "0", "0", "0", "0", "0", "0", "0", "1", "0", "1", "0", "1",
     "1", "0", "1", "1", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "1", "1", "1", "0", "1",
     "1", "0", "1", "0", "0", "1", "1", "0", "0", "0", "1", "0", "1", "0", "0", "0", "0", "0", "0", "1",
     "1", "0", "1", "1", "0", "0", "1", "0", "0", "0", "0", "0", "0", "0", "0", "1", "0", "1", "1", "1",
     "1", "0", "0", "1", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "1", "0", "0", "0", "1",
     "1", "1", "0", "1", "1", "0", "0", "1", "0", "1", "1", "1", "1", "1", "0", "1", "0", "1", "0", "1",
     "1", "1", "0", "0", "1", "1", "0", "1", "0", "1", "0", "0", "0", "1", "0", "0", "0", "1", "0", "1",
     "1", "1", "1", "0", "0", "1", "1", "1", "0", "1", "0", "0", "0", "1", "1", "1", "0", "1", "1", "1",
     "1", "1", "1", "1", "0", "0", "0", "0", "0", "1", "0", "0", "0", "1", "0", "0", "0", "0", "0", "1",
     "1", "0", "0", "0", "0", "1", "0", "1", "1", "1", "1", "0", "1", "1", "0", "0", "1", "1", "0", "1",
     "1", "0", "1", "1", "0", "1", "0", "0", "1", "0", "1", "0", "1", "0", "0", "1", "1", "0", "0", "1",
     "1", "0", "1", "1", "0", "1", "1", "1", "1", "0", "1", "0", "1", "0", "0", "1", "0", "0", "0", "1",
     "1", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "1",
     "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1" };

 
    private class Target {
        public float x, y;

        public Target(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    private class Worker {
        public int id;
        public double x, y;
        public Queue<Target> path;

        public Worker(int id, double x, double y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }

        public void setPos(double x, double y) {
            this.x = x;
            this.y = y;
        }


        public String toString() {
            return this.id + " " + this.x + " " + this.y;
        }
    }

    private class Bullet {
        public double x, y, vx, vy;

        public Bullet(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public GameState() {

    }

    public String serializeMap() {
        return String.join(" ", gameMap);
    }

    public String serializeGameObjects() {
        // gameObjects.add(new Generator(200, 350));
        // gameObjects.add(new Weaponry(300, 350));
        return "generator 0 200 350 weaponry 1 300 350";
    }

    public void addWorker(char client,int id, double x, double y, double targetX, double targetY) {
        if (client == 'A') {
            this.clientAWorkers.put(id, new Worker(id, x, y));
            this.clientAEvents.append("newWorker 0 " + id + " " + x + " " + y + " " + targetX + " " +  targetY);
        } else {
            this.clientBWorkers.put(id, new Worker(id, x, y));
            this.clientBEvents.append("newWorker 1 " + id + " " + x + " " + y + " " + targetX + " " +  targetY);

        }
    }

    public void updateWorker(char client, int id, double x, double y) {
        if (client == 'A') {
            if(this.clientAWorkers.containsKey(id)) {
                this.clientAWorkers.get(id).setPos(x, y);
            } else {
                // addWorker(client, x, y);
                System.out.println("WORKER NOT FOUND");

            }
        } else if (client == 'B') {
            if(this.clientBWorkers.containsKey(id)) {
                this.clientBWorkers.get(id).setPos(x, y);
            } else {
                System.out.println("WORKER NOT FOUND");
                // addWorker(client, x, y);
            }
        }
    }

    public String serialize(char client) {

        StringBuffer buffer = new StringBuffer();
        if (client == 'A') {
            for (Worker w : this.clientAWorkers.values()) {
                buffer.append("worker ");
                buffer.append(w.toString());
                buffer.append(" ");
            }
            for (Bullet b : this.clientABullets.values()) {
                buffer.append("bullet ");
                buffer.append(b.toString());
                buffer.append(" ");
            }
            if(this.clientAEvents.length() > 0) {
                buffer.append(this.clientAEvents.toString());
                this.clientAEvents.setLength(0);
            }
        } else {
            for (Worker w : this.clientBWorkers.values()) {
                buffer.append("worker ");
                buffer.append(w.toString());
                buffer.append(" ");
            }
            for (Bullet b : this.clientBBullets.values()) {
                buffer.append("bullet ");
                buffer.append(b.toString());
                buffer.append(" ");
            }
            if(this.clientBEvents.length() > 0) {
                buffer.append(this.clientBEvents.toString());
                this.clientBEvents.setLength(0);
            }


        }

        return buffer.toString();

    }

    public String serialize() {
        StringBuffer buffer = new StringBuffer();

        for (Worker w : this.clientAWorkers.values()) {
            buffer.append("0worker ");
            buffer.append(w.toString());
            buffer.append(" ");
        }
        for (Bullet b : this.clientABullets.values()) {
            buffer.append("0bullet ");
            buffer.append(b.toString());
            buffer.append(" ");
        }
        for (Worker w : this.clientBWorkers.values()) {
            buffer.append("1worker ");
            buffer.append(w.toString());
            buffer.append(" ");
        }
        for (Bullet b : this.clientBBullets.values()) {
            buffer.append("1bullet ");
            buffer.append(b.toString());
            buffer.append(" ");
        }
        return buffer.toString();

    }

    public void addToEvents(char client, String event, String workerid, String objid) {
        if(client == 'A') {
            this.clientAEvents.append(event + " " + workerid + " " + objid + " ");
        } else {
            this.clientBEvents.append(event + " " + workerid + " " + objid + " ");

        }
    }

}

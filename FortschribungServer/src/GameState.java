import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.HashSet;

public class GameState {
    public Map<Integer, Worker> clientAWorkers = new HashMap<>();
    public Map<Integer, Worker> clientBWorkers = new HashMap<>();

    public Map<Integer, Bullet> clientABullets = new HashMap<>();
    public Map<Integer, Bullet> clientBBullets = new HashMap<>();

    public enum Mod {
        NONE, GENERATING, WEAPONRIED
    };

    private class Target {
        public float x, y;

        public Target(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    private class Worker {
        public int id;
        public Double x, y;
        public Mod modifier = Mod.NONE;
        public Queue<Target> path;

        public Worker(int id, Double x, Double y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }

        public void setPos(Double x, Double y) {
            this.x = x;
            this.y = y;
        }

        public void setModifier(Mod modifier) {
            this.modifier = modifier;
        }

        public String toString() {
            return this.id + " " + this.x + " " + this.y + " " + this.modifier;
        }
    }

    private class Bullet {
        public Double x, y, vx, vy;

        public Bullet(Double x, Double y) {
            this.x = x;
            this.y = y;
        }
    }

    public GameState() {

    }

    public void addWorker(char client, int id, Double x, Double y) {
        Worker temp = new Worker(id, x, y);
        if (client == 'A')
            this.clientAWorkers.put(id, temp);
        else if (client == 'B')
            this.clientBWorkers.put(id, temp);
    }

    public void updateWorker(char client, int id, Double x, Double y) {
        if (client == 'A') {
            this.clientAWorkers.get(id).setPos(x, y);
        } else if (client == 'B') {
            this.clientBWorkers.get(id).setPos(x, y);
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

}

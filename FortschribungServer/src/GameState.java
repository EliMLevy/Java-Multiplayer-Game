import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameState {
    private final int mapRows = 20;
    private final int mapCols = 20;
    private final int[][] gameMap = { { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
    { 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
    { 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1 },
    { 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1 },
    { 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1 },
    { 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1 },
    { 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1 },
    { 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1 },
    { 1, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
    { 1, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1 },
    { 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1 },
    { 1, 1, 0, 1, 1, 0, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1 },
    { 1, 1, 0, 0, 1, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1 },
    { 1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1 },
    { 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1 },
    { 1, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1 },
    { 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1 },
    { 1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 1 },
    { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
    { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 } };


    private final List<String> gameObjects;
    private final List<String> workers;

    private long timeStarted;
    private int minorUpdates = 0;
    private int majorUpdates = 0;

    private long minorUpdateInterval = 15000;
    private long majorUpdateInterval = 20000;
    private boolean UpdateSentToA = true;
    private boolean UpdateSentToB = true;

    private String latestUpdate = null;

    public GameState() {
        this.gameObjects = new ArrayList<>();
        gameObjects.add("generator 854 0");
        gameObjects.add("generator 554 -128");
        gameObjects.add("orb 704 -64");

        this.workers = new ArrayList<>();
        workers.add("worker 0 100 300");
        workers.add("worker 1 1068 -608");

        timeStarted = System.currentTimeMillis();
    }

    public String serialize() {
        StringBuffer buffer = new StringBuffer();

        buffer.append(mapRows + " " + mapCols + " ");
        for(int i = 0; i < this.gameMap.length; i++) {
            for(int j = 0; j < this.gameMap[i].length; j++) {
                buffer.append(this.gameMap[i][j] + " ");
            }
        }
        buffer.append("!");
        for(String s : gameObjects) {
            buffer.append(s + "=");
        }
        buffer.append("!");
        for(String s : this.workers) {
            buffer.append(s + "=");
        }


        return buffer.toString();
    }

    public String checkForUpdates(int client) {
        long timeElapsed = System.currentTimeMillis() - this.timeStarted;

        // StringBuffer buffer = new StringBuffer();

        if(Math.floor(timeElapsed / this.minorUpdateInterval) > minorUpdates) {
            float[] pos = this.findRandomSpot(7 * 64, 96, 14 * 64, 96 - 4 * 64);
            this.latestUpdate = "gun " + pos[0] + " " + pos[1];
            // buffer.append("gun " + pos[0] + " " + pos[1]);
            this.UpdateSentToA = false;
            this.UpdateSentToB = false;
            minorUpdates++;
        }

        if(Math.floor(timeElapsed / this.majorUpdateInterval) > majorUpdates) {
            float[] pos = this.findRandomSpot(7 * 64, 96, 14 * 64, 96 - 4 * 64);
            this.latestUpdate = "orb " + pos[0] + " " + pos[1];
            // buffer.append("gun " + pos[0] + " " + pos[1]);
            this.UpdateSentToA = false;
            this.UpdateSentToB = false;
            majorUpdates++;
        }


        if(!this.UpdateSentToA && client == 0) {
            this.UpdateSentToA = true;
            System.out.println(this.latestUpdate);
            return this.latestUpdate;
        }

        if(!this.UpdateSentToB && client == 1) {
            this.UpdateSentToB = true;

            return this.latestUpdate;
        }

        return "";


        // if(buffer.length() > 0) {
        //     System.out.println(buffer.toString());
        // } else {
        //     // System.out.println(Math.floor(timeElapsed / this.minorUpdateInterval));

        // }
        // return buffer.toString();
    }


    public float[] findRandomSpot(float x1, float y1, float x2, float y2) {
        float x = (float)(Math.random() * (x2 - x1) + x1);
        float y = (float)(Math.random() * (y2 - y1) + y1);

        float[] result = {x, y};

        return result;

    }
}

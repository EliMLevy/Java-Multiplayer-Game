import java.awt.Graphics2D;
import java.awt.Color;

public class Generator extends GameObject {


    private long timeStarted = -1;
    private Worker operator;
    private GameClient client;

    private Vector2D rallyPoint;



    public Generator(int x, int y, int scl, int id, GameClient client) {
        super(x, y, scl, id);
        this.client = client;
        this.r = this.scl * 4;

        this.rallyPoint = new Vector2D(this.x - this.r * 2, this.y);
    }


    public void generate(Worker w) {
        this.occupied = true;
        this.operator = w;
        if(this.timeStarted == -1) 
            this.timeStarted = System.currentTimeMillis();
    }

    public void stop() {
        this.occupied = false;
        this.timeStarted = -1;
    }


    public void display(Graphics2D g2d, int offSetX, int offSetY) {
        double x = this.pos.x - offSetX;
        double y = this.pos.y - offSetY;
        if(!this.occupied && this.hovering) {
            g2d.setColor(new Color(100,255,100));
        } else {
            g2d.setColor(new Color(0,255,0));
        }
        g2d.fillOval((int)x - this.r / 2, (int)y - this.r / 2, this.r, this.r);
        if(this.occupied) {
            g2d.setColor(new Color(0,0,0));
            int angle = (int)GameClient.linearMap(System.currentTimeMillis() - this.timeStarted, 0, 10000, 0, 360);
            g2d.fillArc((int)x - this.r / 2, (int)y - this.r / 2, this.r, this.r , 0, angle);
            if(angle > 360) {
                this.timeStarted = System.currentTimeMillis();
                this.client.workerGenerated((int)this.pos.x, (int)this.pos.y, (int)this.rallyPoint.x, (int)this.rallyPoint.y, false);
            }
        }


    }
}

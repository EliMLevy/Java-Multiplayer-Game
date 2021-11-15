import java.awt.Graphics2D;
import java.awt.Color;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

public class Worker {

    public Vector2D pos;

    private Target target;
    private Queue<Target> targetQueue = new ArrayDeque<>();
    private Generator generating = null;

    private GameMap gm;

    private int scl = 1;

    public int r = this.scl * 30;

    private int speed = this.scl * 5;

    private boolean selected = false;

    private boolean enemy;

    public final int id;

    public Worker(int xOff, int yOff, int scl, GameMap gm, boolean enemy, int id) {
        this.gm = gm;

        this.pos = new Vector2D(xOff, yOff);

        this.scl = scl;

        this.enemy = enemy;

        this.id = id;
    }

    public void display(Graphics2D g, int offSetX, int offSetY) {
        int x = (int) this.pos.x - offSetX;
        int y = (int) this.pos.y - offSetY;

        if (this.enemy) {
            g.setColor(new Color(255, 0, 0));
        } else {
            g.setColor(new Color(0, 0, 255));
        }
        g.fillOval(x - this.r / 2, y - this.r / 2, this.r, this.r);

        if (this.selected) {
            g.setColor(new Color(100, 255, 100));
            g.fillOval(x - this.r / 2 + 2, y - this.r / 2 + 2, (int) (r * 0.8), (int) (r * 0.8));
        }

        if (!this.targetQueue.isEmpty() && !this.enemy) {
            Iterator<Target> iter = this.targetQueue.iterator();
            g.setColor(new Color(255, 255, 0));
            while (iter.hasNext()) {
                Target current = iter.next();
                g.fillOval(current.x - this.r / 4 + 2 - offSetX, current.y - this.r / 4 + 2 - offSetY, (int) (r * 0.5),
                        (int) (r * 0.5));
            }
        }

        if (this.target != null) {
            if (!this.enemy) {
                g.setColor(new Color(255, 255, 0));
                g.fillOval(this.target.x - this.r / 4 + 2 - offSetX, this.target.y - this.r / 4 + 2 - offSetY,
                        (int) (r * 0.5), (int) (r * 0.5));
            }

            Vector2D vec = Vector2D.sub(new Vector2D(this.target.x, this.target.y), this.pos);

            int buffer = 0;
            if(this.target instanceof Generator) buffer = this.r;
            double mag = Math.sqrt(Math.pow(vec.x, 2) + Math.pow(vec.y, 2)) - (buffer);

            vec.normalize();

            if (mag > this.speed) {
                if (gm.canMove(this.pos.x + vec.x * this.speed, this.pos.y + vec.y * this.speed)) {
                    this.pos.add(vec.mult(this.speed));
                }
            } else if (gm.canMove(this.target.x, this.target.y)) {

                this.pos.add(vec.mult((float)mag));

                if(this.target instanceof Generator) {
                    Generator gen = (Generator)this.target;
                    gen.generate(this);
                    this.generating = gen;
                } 
                if (this.targetQueue.isEmpty())
                    this.target = null;
                else
                    this.target = this.targetQueue.remove();

            }

        } else {
            if (!this.targetQueue.isEmpty())
                this.target = this.targetQueue.remove();

        }
    }

    public boolean toggleSelected() {
        this.selected = !this.selected;
        return this.selected;
    }

    public void setTarget(int x, int y) {
        if(this.generating != null) {
            this.generating.stop();
            this.generating = null;
        }
        this.target = new Target(x, y);
        this.targetQueue.clear();
    }

    public void addTargetToQueue(int x, int y) {
        if(this.generating != null) {
            this.generating.stop();
            this.generating = null;
        }
        this.targetQueue.add(new Target(x, y));
    }

    public void setTarget(GameObject obj) {
        if(this.generating != null) {
            this.generating.stop();
        }
        this.target = obj;
        this.targetQueue.clear();

    }

    public void addObjectToQueue(GameObject obj) {
        if(this.generating != null) {
            this.generating.stop();
            this.generating = null;
        }
        this.targetQueue.add(obj);
    }

    public double dist(float x1, float y1, float x2, float y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

}

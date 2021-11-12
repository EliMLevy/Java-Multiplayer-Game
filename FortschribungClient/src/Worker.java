import java.awt.Graphics2D;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

import javax.swing.*;



public class Worker {

    private int posX = 0;
    private int posY = 0;

    private int xOff = 0;
    private int yOff = 0;

    private Target target;
    private Queue<Target> targetQueue = new ArrayDeque<>();

    public int absolutePosX, absolutePosY;

    private GameMap gm;

    private int scl = 1;

    public int r = this.scl * 30;

    private int speed = this.scl * 20;

    private boolean selected = false;

    private boolean enemy;

    public final int id;

    private class Target {
        public int x;
        public int y;

        public Target(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public Worker(int xOff, int yOff, int scl, GameMap gm, boolean enemy, int id) {
        this.gm = gm;

        this.posX = xOff;
        this.posY = yOff;

        this.absolutePosX = this.posX + this.xOff;
        this.absolutePosY = this.posY + this.yOff;

        this.scl = scl;

        this.enemy = enemy;

        this.id = id;
    }

    public void display(Graphics2D g) {
        int x = this.posX + this.xOff;
        int y = this.posY + this.yOff;

        // int r = this.scl * 10;

        if(this.enemy) {
            g.setColor(new Color(255, 0, 0));
        } else {
            g.setColor(new Color(0,0,255));
        }
        g.fillOval(x - this.r / 2, y - this.r / 2, this.r, this.r);

        if (this.selected) {
            g.setColor(new Color(100, 255, 100));
            g.fillOval(x - this.r / 2 + 2, y - this.r / 2 + 2, (int) (r * 0.8), (int) (r * 0.8));
        }
        if (!this.targetQueue.isEmpty()) {
            Iterator<Target> iter = this.targetQueue.iterator();
            g.setColor(new Color(255, 255, 0));
            while (iter.hasNext()) {
                Target current = iter.next();
                g.fillOval(current.x - this.r / 4 + 2 + this.xOff, current.y - this.r / 4 + 2 + this.yOff,
                        (int) (r * 0.5), (int) (r * 0.5));
            }
        }

        if (this.target != null) {
            g.setColor(new Color(255, 255, 0));
            g.fillOval(this.target.x - this.r / 4 + 2 + this.xOff, this.target.y - this.r / 4 + 2 + this.yOff,
                    (int) (r * 0.5), (int) (r * 0.5));
            if (this.dist(this.target.x, this.target.y, this.posX, this.posY) < this.speed) {
                if (this.targetQueue.isEmpty())
                    this.target = null;
                else
                    this.target = this.targetQueue.remove();
            } else {
                if (this.target.x < this.posX && this.target.x < this.posX - this.speed
                        && gm.canMove(this.posX - this.speed, this.posY))
                    this.posX -= this.speed;
                if (this.target.x > this.posX && this.target.x > this.posX + this.speed
                        && gm.canMove(this.posX + this.speed, this.posY))
                    this.posX += this.speed;

                if (this.target.y < this.posY && this.target.y < this.posY - this.speed
                        && gm.canMove(this.posX, this.posY - this.speed))
                    this.posY -= this.speed;
                if (this.target.y > this.posY && this.target.y > this.posY + this.speed
                        && gm.canMove(this.posX, this.posY + this.speed))
                    this.posY += this.speed;
            }
        } else {
            if (!this.targetQueue.isEmpty())
                this.target = this.targetQueue.remove();

        }
    }

    public void move(int x, int y) {
        this.posX += x;
        this.posY += y;

        this.absolutePosX = this.posX + this.xOff;
        this.absolutePosY = this.posY + this.yOff;
    }

    public void moveCamera(int x, int y) {
        this.xOff += x;
        this.yOff += y;

        this.absolutePosX = this.posX + this.xOff;
        this.absolutePosY = this.posY + this.yOff;
    }

    public boolean toggleSelected() {
        this.selected = !this.selected;
        return this.selected;
    }

    public void setTarget(int x, int y) {
        this.target = new Target(x - this.xOff, y - this.yOff);
        this.targetQueue.clear();
    }

    public void addTargetToQueue(int x, int y) {
        this.targetQueue.add(new Target(x - this.xOff, y - this.yOff));
    }

    public double dist(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }
}


// import java.awt.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.util.List;

public class GameClient extends JFrame implements MouseInputListener, ActionListener, KeyListener {

    private int width = 600;
    private int height = 600;
    private int mouseX, pMouseX = 0;
    private int mouseY, pMouseY = 0;
    private int key = -1;

    private GameMap gm = new GameMap(1800, 1800);

    private List<Worker> workers = new ArrayList<>();
    private Worker selectedWorker;

    private boolean mouseIsPressed = false;
    private boolean keyIsPressed = false;

    private Timer timer;
    private final int DELAY = 25;

    GameClient() {

        addMouseMotionListener(this);
        addKeyListener(this);
        addMouseListener(this);

        setSize(600, 600);
        setLayout(null);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        timer = new Timer(DELAY, this);
        timer.start();
        for (int i = 0; i < 5; i++) {
            workers.add(new Worker((int) (Math.random() * 100 + 150), (int) (Math.random() * 100 + 150), 3, gm));
        }
    }

    @Override
    public void paint(Graphics g) {
        this.width = this.getWidth();
        this.height = this.getHeight();

        BufferedImage bufferedImage = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        g2d.setColor(new Color(0, 0, 0));
        g2d.fillRect(0, 0, this.width, this.height);

        gm.display(g2d);
        for (Worker w : this.workers) {
            w.display(g2d);
        }

        Graphics2D g2dComponent = (Graphics2D) g;
        g2dComponent.drawImage(bufferedImage, null, 0, 0);
    }

    public void displayMenu() {
        Graphics2D g = (Graphics2D) getGraphics();

        g.setColor(new Color(100, 100, 100));
        g.fillRect(0, 0, this.width, this.height);

        Button b = new Button((int) (this.width * 0.25), (int) (this.height * 0.8), (int) (this.width * 0.5),
                (int) (this.height * 0.1));
        b.watch(this.mouseX, this.mouseY);
        b.display(g);
    }

    public static void main(String[] args) {
        new GameClient();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            // label.setText("Left Click!");
            for (Worker w : this.workers) {
                if (dist(mouseX, mouseY, w.absolutePosX, w.absolutePosY) < w.r / 2) {

                    if(this.selectedWorker != null)
                        this.selectedWorker.toggleSelected();
                    w.toggleSelected();
                    this.selectedWorker = w;
                }
            }
        }
        if (e.getButton() == MouseEvent.BUTTON2) {
            // label.setText("Middle Click!");
        }
        if (e.getButton() == MouseEvent.BUTTON3) {
            // label.setText("Right Click!");
            if(key == 16) {
                this.selectedWorker.addTargetToQueue(this.mouseX, this.mouseY);
            } else {
                this.selectedWorker.setTarget(this.mouseX, this.mouseY);
            }
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {

        this.mouseIsPressed = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.mouseIsPressed = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        gm.moveCamera(this.mouseX - this.pMouseX, this.mouseY - this.pMouseY);
        for (Worker w : this.workers) {
            w.moveCamera(this.mouseX - this.pMouseX, this.mouseY - this.pMouseY);
        }
        this.pMouseX = this.mouseX;
        this.pMouseY = this.mouseY;

        this.mouseX = e.getX();
        this.mouseY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        this.pMouseX = this.mouseX;
        this.pMouseY = this.mouseY;

        this.mouseX = e.getX();
        this.mouseY = e.getY();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    public double dist(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        this.key = e.getKeyCode();
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        this.key = -1;        
    }

}

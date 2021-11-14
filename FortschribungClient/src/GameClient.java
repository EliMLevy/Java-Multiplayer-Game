
// import java.awt.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.MouseInputListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import java.io.*;
import java.net.*;

public class GameClient extends JFrame implements MouseInputListener, ActionListener, KeyListener {

    private int width = 600;
    private int height = 600;
    private int mouseX, pMouseX = 0;
    private int mouseY, pMouseY = 0;
    private int absMouseX, absMouseY = 0;
    private int cameraOffSetX, cameraOffSetY = 0;

    private int key = -1;
    private Set<Integer> keypresses = new HashSet<>();

    private GameMap gm = new GameMap(1800, 1800);

    private List<Worker> workers = new ArrayList<>();
    private List<Worker> yourWorkers = new ArrayList<>();
    private Map<Integer, Worker> enemyWorkerIDs = new HashMap<>();
    private List<Worker> enemyWorkers = new ArrayList<>();
    private Worker selectedWorker;

    private boolean mouseIsPressed = false;
    private boolean keyIsPressed = false;

    private Timer timer;
    private final int DELAY = 50;

    private PrintWriter toServer;
    private NonblockingBufferedReader fromServerNonblocking;
    private StringBuffer pendingToServer = new StringBuffer();

    private int playerID;

    GameClient(String hostName) {

        addMouseMotionListener(this);
        addKeyListener(this);
        addMouseListener(this);

        setSize(600, 600);
        setLayout(null);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        openSocket(hostName);

        timer = new Timer(DELAY, this);
        timer.start();

        // @TODO The server need to generate the workers and tell both clients where
        // they are

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

    public void openSocket(String hostName) {
        // String hostName = "localhost";
        int portNumber = 3000;
        try {

            Socket socket = new Socket(hostName, portNumber);
            PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader fromServerStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            this.playerID = Integer.parseInt(fromServerStream.readLine());
            String yourWorkersRaw = fromServerStream.readLine();
            String enemyWorkersRaw = fromServerStream.readLine();

            String[] yourWorkersParsed = yourWorkersRaw.split(" ");
            String[] enemyWorkersParsed = enemyWorkersRaw.split(" ");
            for (int i = 0; i < yourWorkersParsed.length - 2; i += 3) {
                int x1 = Integer.parseInt(yourWorkersParsed[i + 1]);
                int y1 = Integer.parseInt(yourWorkersParsed[i + 2]);
                Worker yourWorker = new Worker(x1, y1, 3, gm, false, i);

                int x2 = Integer.parseInt(enemyWorkersParsed[i + 1]);
                int y2 = Integer.parseInt(enemyWorkersParsed[i + 2]);
                Worker enemyWorker = new Worker(x2, y2, 3, gm, true, i);

                workers.add(yourWorker);
                workers.add(enemyWorker);

                yourWorkers.add(yourWorker);

                enemyWorkerIDs.put(i, enemyWorker);

            }

            this.fromServerNonblocking = new NonblockingBufferedReader(fromServerStream);
            this.toServer = toServer;

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            // System.err.println(e.toString());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: GameClient <hostName>");
            return;
        }
        new GameClient(args[0]);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            // label.setText("Left Click!");
            for (Worker w : this.yourWorkers) {
                if (dist(mouseX, mouseY, w.absolutePosX, w.absolutePosY) < w.r / 2) {
                    if (this.selectedWorker != null)
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
            if (key == 16) {
                this.selectedWorker.addTargetToQueue(this.absMouseX, this.absMouseY);
                this.pendingToServer.append(
                        "addTargetPos " + this.selectedWorker.id + " " + this.absMouseX + " " + this.absMouseY + " ");
            } else {
                this.selectedWorker.setTarget(this.absMouseX, this.absMouseY);
                this.pendingToServer.append(
                        "targetPos " + this.selectedWorker.id + " " + this.absMouseX + " " + this.absMouseY + " ");
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

        this.cameraOffSetX += this.pMouseX - this.mouseX;
        this.cameraOffSetY += this.pMouseY - this.mouseY;

        this.absMouseX = this.mouseX + this.cameraOffSetX;
        this.absMouseY = this.mouseY + this.cameraOffSetY;

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

        this.absMouseX = this.mouseX + this.cameraOffSetX;
        this.absMouseY = this.mouseY + this.cameraOffSetY;

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
        try {
            String incoming = this.fromServerNonblocking.readLine();
            if (incoming != null)
                handleServerData(incoming);
        } catch (IOException error) {
            error.printStackTrace();
        }
        if (this.pendingToServer.length() > 0) {
            this.toServer.println(this.pendingToServer.toString());
            this.pendingToServer = new StringBuffer();
        }
    }

    public void handleServerData(String data) {
        System.out.println(data);
        String[] parsedMsgs = data.split(" ");

        for (int i = 0; i < parsedMsgs.length; i++) {
            int x, y;
            switch (parsedMsgs[i]) {
            case "targetPos": // targetPos <worker id> <posX> <posY>
                x = Integer.parseInt(parsedMsgs[i + 2]);
                y = Integer.parseInt(parsedMsgs[i + 3]);
                this.enemyWorkerIDs.get(Integer.parseInt(parsedMsgs[i + 1])).setTarget(x, y);
                break;
            case "targetObj": // targetObj <worker id> <objID>
                break;

            case "addTargetPos": // addTargetPos <worker id> <posX> <posY>
                x = Integer.parseInt(parsedMsgs[i + 2]);
                y = Integer.parseInt(parsedMsgs[i + 3]);
                this.enemyWorkerIDs.get(Integer.parseInt(parsedMsgs[i + 1])).addTargetToQueue(x, y);
                break;
            case "addTargetObj": // addTargetObj <worker id> <objID>

                break;
            default:
                break;
            }
        }
    }

    public double dist(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    @Override
    public void keyTyped(KeyEvent e) {

        if (this.keypresses.contains(88)) {

            if (this.keypresses.contains(16)) {
                this.selectedWorker.addTargetToQueue(this.absMouseX, this.absMouseY);
                this.pendingToServer.append(
                        "addTargetPos " + this.selectedWorker.id + " " + this.absMouseX + " " + this.absMouseY + " ");
            } else {
                this.selectedWorker.setTarget(this.absMouseX, this.absMouseY);
                this.pendingToServer.append(
                        "targetPos " + this.selectedWorker.id + " " + this.absMouseX + " " + this.absMouseY + " ");
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

        // if(this.key == 16 && e.getKeyCode() == 88) {

        // }
        this.key = e.getKeyCode();
        this.keypresses.add(e.getKeyCode());
        // System.out.println(Arrays.toString(this.keypresses.toArray()));

    }

    @Override
    public void keyReleased(KeyEvent e) {
        this.key = -1;
        this.keypresses.removeIf(i -> (i == e.getKeyCode()));
    }

}

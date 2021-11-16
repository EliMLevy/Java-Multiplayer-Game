
// import java.awt.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.MouseInputListener;

import java.util.ArrayList;
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
    private GameObject mouseHovering;

    private int key = -1;
    private Set<Integer> keypresses = new HashSet<>();

    private GameMap gm = new GameMap(1800, 1800);

    private int playerID;
    private List<Worker> workers = new ArrayList<>();
    private List<Worker> yourWorkers = new ArrayList<>();
    private Map<Integer, Worker> enemyWorkerIDs = new HashMap<>();
    private Map<Integer, GameObject> gameObjectIDs = new HashMap<>();
    private Worker selectedWorker;

    private List<GameObject> gameObjects = new ArrayList<>();
    private List<Worker> enemyWorkers = new ArrayList<>();

    private Timer timer;
    private final int DELAY = 50;

    private PrintWriter toServer;
    private NonblockingBufferedReader fromServerNonblocking;
    private StringBuffer pendingToServer = new StringBuffer();

    GameClient(String hostName) {

        addMouseMotionListener(this);
        addKeyListener(this);
        addMouseListener(this);

        setSize(1020, 780);
        setLayout(null);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        GameObject gen = new Generator(300, 200, 10, 1, this);
        GameObject gen2 = new Generator(1400, 1400, 10, 2, this);
        this.gameObjects.add(gen);
        this.gameObjectIDs.put(1, gen);
        this.gameObjects.add(gen2);
        this.gameObjectIDs.put(2, gen2);

        openSocket(hostName);

        timer = new Timer(DELAY, this);
        timer.start();

    }

    @Override
    public void paint(Graphics g) {
        this.width = this.getWidth();
        this.height = this.getHeight();

        BufferedImage bufferedImage = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        g2d.setColor(new Color(0, 0, 0));
        g2d.fillRect(0, 0, this.width, this.height);

        gm.display(g2d, this.cameraOffSetX, this.cameraOffSetY);
        for (Worker w : this.workers) {
            w.display(g2d, this.cameraOffSetX, this.cameraOffSetY);
        }
        for (GameObject obj : this.gameObjects) {
            obj.display(g2d, this.cameraOffSetX, this.cameraOffSetY);
        }

        Graphics2D g2dComponent = (Graphics2D) g;
        g2dComponent.drawImage(bufferedImage, null, 0, 0);
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
                Worker yourWorker = new Worker(x1, y1, 3, gm, false, i / 3);

                int x2 = Integer.parseInt(enemyWorkersParsed[i + 1]);
                int y2 = Integer.parseInt(enemyWorkersParsed[i + 2]);
                System.out.println("initializing with worker id " + (i / 3));
                Worker enemyWorker = new Worker(x2, y2, 3, gm, true, i / 3);

                workers.add(yourWorker);
                workers.add(enemyWorker);

                yourWorkers.add(yourWorker);
                
                this.enemyWorkers.add(enemyWorker);
                enemyWorkerIDs.put(i / 3, enemyWorker);

            }

            if (this.playerID == 1) {
                this.cameraOffSetX += 1200;
                this.cameraOffSetY += 1200;

                this.absMouseX = this.mouseX + this.cameraOffSetX;
                this.absMouseY = this.mouseY + this.cameraOffSetY;
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
        System.out.println("end of main");
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            // label.setText("Left Click!");
            for (Worker w : this.yourWorkers) {
                if (dist(this.cameraOffSetX + mouseX, this.cameraOffSetY + mouseY, w.pos.x, w.pos.y) < w.r / 2) {
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

            if (this.mouseHovering != null) {
                if (this.key == 16) {
                    this.selectedWorker.addObjectToQueue(this.mouseHovering);
                    this.pendingToServer
                            .append("addTargetObj " + this.selectedWorker.id + " " + this.mouseHovering.id + " ");
                } else {
                    this.selectedWorker.setTarget(this.mouseHovering);
                    this.pendingToServer
                            .append("setTargetObj " + this.selectedWorker.id + " " + this.mouseHovering.id + " ");
                }
            } else {

                if (key == 16) {
                    this.selectedWorker.addTargetToQueue(this.absMouseX, this.absMouseY);
                    this.pendingToServer.append("addTargetPos " + this.selectedWorker.id + " " + this.absMouseX + " "
                            + this.absMouseY + " ");
                } else {
                    this.selectedWorker.setTarget(this.absMouseX, this.absMouseY);
                    this.pendingToServer.append(
                            "targetPos " + this.selectedWorker.id + " " + this.absMouseX + " " + this.absMouseY + " ");
                }
            }
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
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

        this.mouseHovering = null;
        for (GameObject obj : this.gameObjects) {
            obj.hovering = false;

            double d = dist(this.cameraOffSetX + this.mouseX, this.cameraOffSetY + this.mouseY, (int) obj.pos.x,
                    (int) obj.pos.y);

            if (d < obj.r / 2) {
                this.mouseHovering = obj;
                obj.hovering = true;
            }
        }
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
                i += 3;
                break;
            case "targetObj": // targetObj <worker id> <objID>
                break;

            case "addTargetPos": // addTargetPos <worker id> <posX> <posY>
                x = Integer.parseInt(parsedMsgs[i + 2]);
                y = Integer.parseInt(parsedMsgs[i + 3]);
                this.enemyWorkerIDs.get(Integer.parseInt(parsedMsgs[i + 1])).addTargetToQueue(x, y);
                i += 3;
                break;
            case "addTargetObj": // addTargetObj <worker id> <objID>

                break;
            case "newWorker":
                this.workerGenerated(Integer.parseInt(parsedMsgs[i + 1]), Integer.parseInt(parsedMsgs[i + 2]),
                        Integer.parseInt(parsedMsgs[i + 3]), Integer.parseInt(parsedMsgs[i + 4]), true);
                i += 4;
                break;
            default:
                break;
            }
        }
    }

    public double dist(float x1, float y1, float x2, float y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static double linearMap(double val, double start, double end, double a, double b) {
        return a + ((b - a) / (end - start)) * (val - start);

    }

    @Override
    public void keyTyped(KeyEvent e) {

        if (this.keypresses.contains(88)) {
            if (this.mouseHovering != null) {
                if (this.keypresses.contains(16)) {
                    this.selectedWorker.addObjectToQueue(this.mouseHovering);
                    this.pendingToServer
                            .append("addTargetObj " + this.selectedWorker.id + " " + this.mouseHovering.id + " ");
                } else {
                    this.selectedWorker.setTarget(this.mouseHovering);
                    this.pendingToServer
                            .append("targetObj " + this.selectedWorker.id + " " + this.mouseHovering.id + " ");
                }
            } else {
                if (this.keypresses.contains(16)) {
                    this.selectedWorker.addTargetToQueue(this.absMouseX, this.absMouseY);
                    this.pendingToServer.append("addTargetPos " + this.selectedWorker.id + " " + this.absMouseX + " "
                            + this.absMouseY + " ");
                } else {
                    this.selectedWorker.setTarget(this.absMouseX, this.absMouseY);
                    this.pendingToServer.append(
                            "targetPos " + this.selectedWorker.id + " " + this.absMouseX + " " + this.absMouseY + " ");
                }
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        this.key = e.getKeyCode();
        this.keypresses.add(e.getKeyCode());

    }

    @Override
    public void keyReleased(KeyEvent e) {
        this.key = -1;
        this.keypresses.removeIf(i -> (i == e.getKeyCode()));
    }

    public void workerGenerated(int x, int y, int targetX, int targetY, boolean enemy) {
        if (enemy) {
            int id = this.enemyWorkers.size();
            Worker newWorker = new Worker(x, y, 3, this.gm, enemy, id);
            newWorker.setTarget(targetX, targetY);
            System.out.println("putting new worker with id " + id);
            enemyWorkerIDs.put(id, newWorker);
            this.workers.add(newWorker);
            this.enemyWorkers.add(newWorker);

        } else {
            int id = this.yourWorkers.size();
            Worker newWorker = new Worker(x, y, 3, this.gm, enemy, id );
            newWorker.setTarget(targetX, targetY);
            this.yourWorkers.add(newWorker);
            this.workers.add(newWorker);
            this.pendingToServer.append("newWorker " + x + " " + y + " " + targetX + " " + targetY + " " + id);
        }
    }

}

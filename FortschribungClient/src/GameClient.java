
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

import java.io.*;
import java.net.*;

public class GameClient extends JFrame implements MouseInputListener, ActionListener, KeyListener {

    private int width = 600;
    private int height = 600;
    private int mouseX, pMouseX = 0;
    private int mouseY, pMouseY = 0;
    private int key = -1;

    private GameMap gm = new GameMap(1800, 1800);

    private List<Worker> workers = new ArrayList<>();
    private List<Worker> yourWorkers = new ArrayList<>();
    private Map<Integer, Worker> workerIDs = new HashMap<>();
    private List<Worker> enemyWorkers = new ArrayList<>();
    private Worker selectedWorker;

    private boolean mouseIsPressed = false;
    private boolean keyIsPressed = false;

    private Timer timer;
    private final int DELAY = 25;

    private PrintWriter toServer;
    private NonblockingBufferedReader fromServerNonblocking;
    private StringBuffer pendingToServer = new StringBuffer();

    private int playerID;

    GameClient() {

        addMouseMotionListener(this);
        addKeyListener(this);
        addMouseListener(this);

        setSize(600, 600);
        setLayout(null);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        openSocket();

        timer = new Timer(DELAY, this);
        timer.start();

        // @TODO The server need to generate the workers and tell both clients where they are 
        for (int i = 0; i < 5; i++) {
            Worker temp = new Worker((int) (Math.random() * 100 + 150 + (this.playerID * 1600)),
                    (int) (Math.random() * 100 + 150 + (this.playerID * 1600)), 3, gm, false);
            workers.add(temp);
            yourWorkers.add(temp);
        }

        int otherPlayerID = (this.playerID + 1) % 2;
        for (int i = 0; i < 5; i++) {
            Worker temp = new Worker((int) (Math.random() * 100 + 150 + (otherPlayerID * 1600)),
                    (int) (Math.random() * 100 + 150 + (otherPlayerID * 1600)), 3, gm, true);
            workers.add(temp);
            enemyWorkers.add(temp);
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

    public void openSocket() {
        String hostName = "localhost";
        int portNumber = 3000;
        try {
            
            Socket socket = new Socket(hostName, portNumber);
            PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader fromServerStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            // String fromServer;
            // String fromUser;

            this.playerID = Integer.parseInt(fromServerStream.readLine());

            this.fromServerNonblocking = new NonblockingBufferedReader(fromServerStream);
            this.toServer = toServer;

            // while ((fromServer = fromServerStream.readLine()) != null) {
            // System.out.println("Server: " + fromServer);
            // if (fromServer.equals("Bye."))
            // break;

            // if(!listening) {
            // System.out.println("waiting for user input");
            // fromUser = stdIn.readLine();
            // if (fromUser != null) {
            // System.out.println("Client: " + fromUser);
            // toServer.println(fromUser);
            // }
            // }
            // }
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
        new GameClient();
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
        try {
            String incoming = this.fromServerNonblocking.readLine();
            if(incoming != null)
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
        String[] parsedMsgs = data.split(" ");
        for (int i = 0; i < parsedMsgs.length; i++) {
            switch (parsedMsgs[i]) {
            case "targetPos": // targetPos <worker id> <posX> <posY>

                break;
            case "targetObj": // targetObj <worker id> <objID>
                break;

            case "addTargetPos": // addTargetPos <worker id> <posX> <posY>

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

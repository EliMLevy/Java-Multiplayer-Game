import java.awt.Graphics2D;

public abstract class GameObject extends Target {

    public int id;

    public Vector2D pos;

    public boolean occupied = false;
    public boolean hovering = false;
    public int scl;

    public int r;

    public GameObject(int x, int y, int scl, int id) {
        super(x, y);

        this.pos = new Vector2D(x, y);
        this.scl = scl;
        this.id = id;
    }

    public void display(Graphics2D g2d, int offSetX, int offSetY) {

    }

}

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.MouseInputListener;


public class Button {

    private int x;
    private int y;
    private int h;
    private int w;

    private Integer mouseX = 0;
    private Integer mouseY = 0;

    private String msg;

    private Color fill = new Color(255, 255, 255);

    public Button(int x, int y, int w, int h) {
        
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setFill(Color c) {
        this.fill = c;
    }

    public void display(Graphics2D g) {
        

        g.setColor(new Color(0, 0, 0));
        BasicStroke stroke;
        int offSet = 0;
        if(mouseX > x && mouseX < x + w && mouseY > y && mouseY < y + h) {
            stroke = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 2.0f, null, 0.0f);
            offSet = 5;
        } else {
            stroke = new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 2.0f, null, 0.0f);

        }
        g.setStroke(stroke);
        g.drawRect(x + 3, y + 3, w, h);

        stroke = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 2.0f, null, 0.0f);
        g.setColor(this.fill);
        g.fillRect(x + offSet, y + offSet, w, h);



    }

    public void watch(Integer x, Integer y) {
        this.mouseX = x;
        this.mouseY = y;
    }

}

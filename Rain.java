import java.awt.*;  
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

class Rain {
    public static void main(String args[]) {  
        System.setProperty("sun.java2d.opengl", "true");
        Frame f = new Frame();  
        f.start();
    }   
}

class Frame extends JFrame implements ActionListener {  
    private int DEFAULT_HEIGHT = 750;
    private int DEFAULT_WIDTH = 750;
    private Panel panel = new Panel();
    private Timer timer = new Timer(33, null);
    private static Color color = Color.BLUE;

    public Frame() {
        timer.addActionListener(this);
        initializeFrame();
    }

    public void start() {
        timer.start();
    }

    public static void setColor () {
        color = new Color((int)(Math.random() * 0x1000000));
    }

    public void initializeFrame() {
        this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Rain");
        this.setBackground(Color.BLACK);
        this.setResizable(true);
        this.setContentPane(panel);
        this.addMouseListener(new mouseField());

        this.setVisible(true);
    }

    public void paint(Graphics g) {
        g.clearRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(color);
        panel.paint(g);
    }

    public void actionPerformed(ActionEvent e) {
        repaint();
        getToolkit().sync();
    }

}

class Drop {
    public static Wind wind = new Wind();
    protected int velocity, x, y, z, length;
    private int rX, rY;
    private final double cdValue = 40;
    private double cd;
    // private double repelScale = 0.5;

    public Drop(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        velocity = 9 + (20 - z)/2;
        length = 5 + (20 - z)/2;
    }

    public void reset(int x) {
        this.x = x;
        this.y = (int) (Math.random() * 100 - 100);
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // public int getX() {
    //     return x;
    // }

    // public int getY() {
    //     return y;
    // }

    private void repelled(double r) {
        cd = cdValue;
        r += (Math.random() - 0.5)/10 - r/5; 
        //To make the drops drop left and right more as
        //it doesn't look nice dropping up and down over again.
    
        if (r < 0) {
            rX = (int) ( (length) * (1 + r));
        }
        else {
            rX = (int) -( (length) * (1 - r));
        }
        rY = (int) ( -length * Math.abs(r));
    }

    public void paint(Graphics g) {
        if (mouseField.check(x, y)) {
            repelled(mouseField.repel(x, y));
        }
       
        double s = cd/cdValue;
        int windFactor = (int) (wind.windFactor() * (1 - s));
        int len = (int) (length * (1 - s));
        if (cd > 0) {
            --cd;
            g.drawLine(x, y, x + (int) (rX * s) + windFactor, y + (int) (rY * s) + len);
            x += (int) (rX * s);
            y += (int) (rY * s);
        }
        else {
            g.drawLine(x, y, x + windFactor, y + length);
        }
            x += (int) (windFactor * (1 - s));
            y += (int) (velocity * (1 - s));
    }
}

class mouseField implements MouseListener{
    static boolean active;
    static PointerInfo pointer = MouseInfo.getPointerInfo();
    static Point mouseLocation = pointer.getLocation();
    private static int range = 50;
    static int mouseX = mouseLocation.x;
    static int mouseY = mouseLocation.y;

    public void mousePressed(MouseEvent e) {
        active = !active;
        Frame.setColor();
    } 
    
    public void mouseExited(MouseEvent e) {}

    public void mouseClicked(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}

    static boolean check(int checkX, int checkY) {
        if(!active) {
            return false;
        }
        int x = Math.abs(mouseX - checkX);
        int y = mouseY - checkY;
        if (y < 0) {
            return false;
        }
        else if (x*x + y*y <= range * range) {
            return true;
        }
        else {
            return false;
        }
    }

    static double repel(int x, int y) {
        int negative = 1;
        if(mouseX - x < 0) {
            negative = -1;
        }
        return (double) (negative * (mouseY - y))/range;
    }

    public static void update() {
        if (!active) {
            return;
        }
        pointer = MouseInfo.getPointerInfo();
        mouseLocation = pointer.getLocation();
        mouseX = mouseLocation.x;
        mouseY = mouseLocation.y;
    }
}

class Wind {
    protected int scale;
    //0 - 6 scale
    public int windFactor(){
        return (int) (-3 * Math.cos(Math.toRadians(scale/2)) + 3);
    }

    public void reset(){
        scale = 0;
    }

    public void update(){
        scale++;
        if(scale >= 720)
            scale = 0;
    }
}

class Splash {
    protected int x, y;
    //boolean isRight;

    public Splash(int x, int y) {
        this.x = x;
        this.y = y;
        //this.isRight = isRight;
    }

    public void paint(Graphics g) {
            g.drawLine(x, y, x + 4, y - 4);
            g.drawLine(x, y, x - 4, y - 4);
            g.drawRect(x + (int)(10 * Math.random()), y + (int)(10 * Math.random()), 1, 2);
            g.drawRect(x + (int)(10 * Math.random() - 20), y + (int)(10 * Math.random()), 1, 2);
    }
}

class Panel extends JPanel {
    private int DEFAULT_HEIGHT = 750;
    private int DEFAULT_WIDTH = 750;

    private ArrayList<Drop> drops = new ArrayList<>();

    public Panel() {
        createPanel();
        createDrops();
    }

    public void createPanel() {
        this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.setBackground(Color.BLACK);
    }

    private void createDrops() {
        for(int i = 0; i <(this.getWidth() + this.getHeight())/2; i++) {
            drops.add(genDrop());
        }
    }

    private Drop genDrop() {
        int rX = (int) (Math.random() * this.getWidth());
        int rY = (int) (Math.random() * 200 - 100);
        int rZ = (int) (Math.random() * 20);
        return new Drop(rX, rY, rZ);
    }

    private void changeDrops(int i) {
        drops.get(i).reset((int) (Math.random() * this.getWidth() - Drop.wind.windFactor() * 20));
    }

    public void paint(Graphics g) {
        if(drops.size() != (this.getHeight() + this.getWidth())/2) {
            drops.clear();
            createDrops();
            Drop.wind.reset();
        }

        Drop.wind.update();
        mouseField.update();

        for(int i = 0; i < drops.size(); i++) {
            Drop d = drops.get(i);
            d.paint(g);

            if(d.y + d.z >= this.getHeight() - 15) {
                Splash splash = new Splash(d.x, d.y);
                splash.paint(g);
            }
            if(d.y + d.z>= this.getHeight()) {
                changeDrops(i);
            }
        }
        
    }
}
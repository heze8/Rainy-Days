import java.awt.*;  
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

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

    public Frame() {
        timer.addActionListener(this);
        initializeFrame();
    }

    public void start() {
        timer.start();
    }
    public void initializeFrame() {
        this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Rain");
        this.setBackground(Color.BLACK);
        this.setResizable(true);
        this.setContentPane(panel);

        this.setVisible(true);
    }

    public void paint(Graphics g) {
        g.clearRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.WHITE);
        panel.paint(g);
    }

    public void actionPerformed(ActionEvent e) {
        repaint();
    }

}

class Drop {
    public static Wind wind = new Wind();
    protected int velocity, x, y, z, length;

    public Drop(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        velocity = 6 + (15 - z)/2;
        length = 8 + (15 - z)/2;
    }

    public void paint(Graphics g) {
        int windFactor = wind.windFactor();
        g.drawLine(x, y, x + windFactor, y + length);
        x += windFactor;
        y += length + velocity;
    }
}

class Wind {
    protected int scale;

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
        int rZ = (int) (Math.random() * 15);
        return new Drop(rX, rY, rZ);
    }

    private void changeDrops(int i) {
        drops.set(i, genDrop());
    }

    public void paint(Graphics g) {
        if(drops.size() != (this.getHeight() + this.getWidth())/2) {
            drops.clear();
            createDrops();
            Drop.wind.reset();
        }

        Drop.wind.update();

        for(int i = 0; i < drops.size(); i++) {
            Drop d = drops.get(i);
            d.paint(g);

            if(d.y >= this.getHeight() - 15) {
                Splash splash = new Splash(d.x, d.y);
                splash.paint(g);
            }
            if(d.y >= this.getHeight()) {
                changeDrops(i);
            }
        }
    }
}
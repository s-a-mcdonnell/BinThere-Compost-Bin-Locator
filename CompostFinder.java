import javax.swing.*;
import java.awt.*;

public class CompostFinder extends JPanel {
    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;
    public static final int FPS = 60;
    public static final double CENTERX = WIDTH / 2.0;
    public static final double CENTERY = HEIGHT / 2.0;

    public CompostFinder() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    public void Go() {
        while (true) {
            //__ update

            repaint();
            try {
                Thread.sleep(1000 / FPS);
            } catch (InterruptedException e) {
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Compost Finder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        CompostFinder world = new CompostFinder();
        frame.setContentPane(world);
        frame.pack();
        frame.setVisible(true);
        world.Go();
    }

    public void paintComponent(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(100, 100, 100, 100);
    }
}

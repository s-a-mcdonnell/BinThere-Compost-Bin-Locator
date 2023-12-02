import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class CompostFinder extends JPanel {
    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;
    public static final int FPS = 60;
    public static final double CENTERX = WIDTH / 2.0;
    public static final double CENTERY = HEIGHT / 2.0;

    BufferedImage image;

    public CompostFinder() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        try {
            this.image = ImageIO.read(new File("ACHigherQ.png"));
            
            int w = this.image.getWidth();
            int h = this.image.getHeight();
            double proportion = ((double) w) / (double) h;
            this.image = toBufferedImage(image.getScaledInstance((int) (HEIGHT * proportion), HEIGHT, java.awt.Image.SCALE_SMOOTH));

        } catch (Exception e) {
            System.err.println("Error");
        }

    }

    public void Go() {
        while (true) {
            //TODO: Write update methods for whatever needs to be updated

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
        //TODO: Scale
        g.drawImage(this.image, 0, 0, null);
    }

    // From https://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
}

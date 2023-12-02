import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class CompostFinder extends JPanel implements MouseListener {
    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;
    public static final int FPS = 60;
    public static final double CENTERX = WIDTH / 2.0;
    public static final double CENTERY = HEIGHT / 2.0;

    private BufferedImage image;

    private BufferedImage scaledImage;

    private Pair mouse = null;

    public CompostFinder() {
        // Setup
        addMouseListener(this);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        try {
            this.image = ImageIO.read(new File("ACHigherQ.png"));
        } catch (Exception e) {
        }

        int w = this.image.getWidth();
        int h = this.image.getHeight();
        double proportion = ((double) w) / (double) h;
        this.scaledImage = toBufferedImage(image.getScaledInstance(WIDTH, (int) (WIDTH / proportion), java.awt.Image.SCALE_SMOOTH));

    }

    public void Go() {
        while (true) {
            //TODO: Write update methods for whatever needs to be updated

            repaint();
            try {
                Thread.sleep(1000 / FPS);
            } catch (InterruptedException e) {
            }

            /*// Reset mouse
            this.mouse = null;*/
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
        g.drawImage(this.scaledImage, 0, 0, null);

        // TODO: Make clicking the mouse do something useful (currently, it just draws a square at the location of the click
        if (this.mouse != null) {
            g.setColor(Color.RED);
            g.fillRect((int) mouse.x - 5, (int) mouse.y - 5, 10, 10);
        }
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

    @Override
    public void mouseClicked(MouseEvent click) {
        Point location = click.getPoint();

        // Save the location at which the mouse clicks (if the click was on the display area)
        if (location.x >= 0 && location.x <= WIDTH && location.y >= 0 && location.y <= HEIGHT)
            this.mouse = new Pair(location.x, location.y);
    }

    @Override
    public void mouseExited(MouseEvent click) {
    }

    @Override
    public void mouseEntered(MouseEvent click) {
    }

    @Override
    public void mouseReleased(MouseEvent click) {
    }

    @Override
    public void mousePressed(MouseEvent click) {
    }
}

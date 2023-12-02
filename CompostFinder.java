import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CompostFinder extends JPanel implements MouseListener {
    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;
    public static final int FPS = 60;
    public static final double CENTERX = WIDTH / 2.0;
    public static final double CENTERY = HEIGHT / 2.0;

    BufferedImage image;

    private Pair mouse = null;

    public CompostFinder() {
        // Setup
        addMouseListener(this);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        try {
            this.image = ImageIO.read(new File("ACHigherQ.png"));

            int w = this.image.getWidth();
            int h = this.image.getHeight();
            double proportion = ((double) w) / (double) h;
            this.image = toBufferedImage(image.getScaledInstance(WIDTH, (int) (WIDTH / proportion), java.awt.Image.SCALE_SMOOTH))
            ;

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
        g.drawImage(this.image, 0, 0, null);

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

    public static List<double[]> readCSVCoordinates(String filePath) {
        List<double[]> data = new ArrayList<>();
        String line = "";
        String delimiter = ","; // CSV delimiter, usually a comma

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                // Split each line into an array of strings
                String[] stringValues = line.split(delimiter);

                // Parse the string values into doubles
                double[] doubleValues = new double[stringValues.length];
                for (int i = 0; i < stringValues.length; i++) {
                    doubleValues[i] = Double.parseDouble(stringValues[i].trim());
                }

                // Add the double array to the list
                data.add(doubleValues);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Error parsing a double value: " + e.getMessage());
        }

        return data;
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

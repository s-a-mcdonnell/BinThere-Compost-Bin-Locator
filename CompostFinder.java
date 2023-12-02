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
import java.util.Scanner;


public class CompostFinder extends JPanel implements MouseListener {
    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;
    public static final int FPS = 60;
    public static final int CENTERX = WIDTH / 2;
    public static final int CENTERY = HEIGHT / 2;

    private BufferedImage baseImage;
    private BufferedImage scaledImage;

    private Pair mouse;
    //__ private Pair anchor = new Pair(CENTERX, CENTERY - 50);
    private Pair anchor = null;
    private Pair topLeftCorner = new Pair(0, 0);

    // 2 = zoom in, 1 = don't zoom, 0.5 = zoom out
    private double zoomMode = 1;

    public CompostFinder() {
        // Setup
        addMouseListener(this);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        try {
            BufferedImage image = ImageIO.read(new File("ACHigherQ.png"));

            int w = image.getWidth();
            int h = image.getHeight();
            double proportion = ((double) w) / (double) h;
            this.baseImage = toBufferedImage(image.getScaledInstance(WIDTH, (int) (WIDTH / proportion), java.awt.Image.SCALE_SMOOTH))
            ;

            defaultSettings();
        } catch (Exception e) {
            System.err.println("Error");
        }

    }

    private void defaultSettings() {
        scaledImage = toBufferedImage(baseImage);
        zoomMode = 1;
        mouse = null;
        /*anchor.x = CENTERX;
        anchor.y = CENTERY - 50;*/
        anchor = null;
        topLeftCorner.x = 0;
        topLeftCorner.y = 0;
    }

    public void Go() {
        while (true) {
            update();
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
        Scanner scan = new Scanner(System.in);

    }

    public void update() {
        // TODO: Write update method

        if (this.mouse != null) {
            //__
            this.anchor = this.mouse;

            /*if (zoomMode == 2) {
                topLeftCorner.x -= anchor.x - topLeftCorner.x;
                topLeftCorner.y -= anchor.y - topLeftCorner.y;
            } else if (zoomMode == 0.5) {
                topLeftCorner.x += 0.5 * (anchor.x - topLeftCorner.x);
                topLeftCorner.y += 0.5 * (anchor.y - topLeftCorner.y);
            } else {
                //__
            }*/

            zoom(zoomMode);
        }
    }

    // TODO: Refactor to take no parameters and just directly access zoomMode
    public void zoom(double scale) {
        // Don't zoom out past min size
        if (scale < 1 && (scaledImage.getWidth() <= baseImage.getWidth() || scaledImage.getHeight() <= baseImage.getHeight())) {
            zoomMode = 1;
            return;
        }

        scaledImage = toBufferedImage(scaledImage.getScaledInstance((int) (scaledImage.getWidth() * scale), (int) (scaledImage.getHeight() * scale), java.awt.Image.SCALE_SMOOTH))
        ;
    }

    public void paintComponent(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        //__g.drawImage(this.scaledImage, (int) anchor.x - this.scaledImage.getWidth() / 2, (int) anchor.y - this.scaledImage.getHeight() / 2, null);
        // __g.drawImage(this.scaledImage, CENTERX - (int) (zoomMode * anchor.x), CENTERY - (int) (zoomMode * anchor.y), null);
        g.drawImage(this.scaledImage, (int) topLeftCorner.x, (int) topLeftCorner.y, null);

        // TODO: Make clicking the mouse do something useful (currently, it just draws a square at the location of the click
        if (this.mouse != null) {
            g.setColor(Color.RED);
            g.fillRect((int) mouse.x - 5, (int) mouse.y - 5, 10, 10);

        }

        // __ testing
        if (button(g, 100, 100, HEIGHT - 60, 50, "Zoom In")) {
            System.out.println("zoom in");
            topLeftCorner.x -= mouse.x - topLeftCorner.x;
            topLeftCorner.y -= mouse.y - topLeftCorner.y;
            zoomMode = 2;
            this.mouse = null;
        } else if (button(g, 300, 100, HEIGHT - 60, 50, "Zoom Out")) {
            topLeftCorner.x += 0.5 * (mouse.x - topLeftCorner.x);
            topLeftCorner.y += 0.5 * (mouse.y - topLeftCorner.y);
            System.out.println("zoom out");
            zoomMode = 0.5;
            this.mouse = null;
        } else if (button(g, 500, 100, HEIGHT - 60, 50, "Pan")) {
            zoomMode = 1;
            this.mouse = null;
        } else if (button(g, 700, 100, HEIGHT - 60, 50, "Reset")) {
            defaultSettings();
        }

        // __ this.mouse = null;

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

    // Takes __
    // Returns true of the click was inside the box, otherwise returns false
    public boolean button(Graphics g, int x, int dx, int y, int dy, String words) {
        g.setColor(Color.BLACK);
        g.fillRect(x, y, dx, dy);

        g.setColor(Color.RED);
        g.drawString(words, (int) (x + 0.2 * dx), (int) (y + 0.5 * dy));

        if (this.mouse != null && (mouse.x >= x && mouse.x <= x + dx && mouse.y >= y && mouse.y <= y + dy)) {
            //__this.mouse = null;
            //__ testing
            System.out.println("Button " + words + " pressed");
            return true;
        } else {
            return false;
        }

        //__


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

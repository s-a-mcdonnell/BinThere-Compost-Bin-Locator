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

    public static double longitude;
    public static double latitude;

    public static final int CENTERX = WIDTH / 2;
    public static final int CENTERY = HEIGHT / 2;

    private BufferedImage baseImage;
    private BufferedImage[] scaledImages;
    private int scale = 1;

    private BufferedImage binIcon;

    private Pair mouse = null;
    private Pair mouseToDrag = null;
    private Pair topLeftCorner = new Pair(0, 0);

    // TODO: set this to useful values
    private ArrayList<Pair> pointsToTrack = new ArrayList<Pair>();

    // 2 = zoom in, 1 = don't zoom (pan), 0.5 = zoom out, 0 = do nothing
    private double zoomMode = 0;

    public CompostFinder() {
        // Setup
        addMouseListener(this);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        try {
            BufferedImage binImage = ImageIO.read(new File("CompostBinPlaceholder.png"));
            this.binIcon = toBufferedImage(binImage.getScaledInstance(20, (int) (20.0 / (binImage.getWidth() / binImage.getHeight())), java.awt.Image.SCALE_SMOOTH));


            BufferedImage image = ImageIO.read(new File("ACHigherQ.png"));

            int w = image.getWidth();
            int h = image.getHeight();
            double topLat = 42.377579;
            double topLong = -72.529270;
            double botLat = 42.366916;
            double botLong = -72.505895;
            double latDiff = botLat - topLat;
            double longDiff = botLong - topLong;


            double proportion = ((double) w) / (double) h;
            this.baseImage = toBufferedImage(image.getScaledInstance(WIDTH, (int) (WIDTH / proportion), java.awt.Image.SCALE_SMOOTH))
            ;

            defaultSettings();
        } catch (Exception e) {
            System.err.println("Error");
        }

        setScaledImages();

        // TODO: Change from test values to useful values (compost bin locations)
        // this one is approx over Val
        pointsToTrack.add(new Pair(580, 280));
        // this one is approx over Frost
        pointsToTrack.add(new Pair(520, 335));

    }

    private void defaultSettings() {
        scale = 1;
        zoomMode = 0;
        mouse = null;
        topLeftCorner.x = 0;
        topLeftCorner.y = 0;
    }

    private void setScaledImages() {
        // length depends on scales to be scored
        // images are stored at the index of their scale (ex. scaledImages[1] is the standard size, scaledImages[4] is four times as large, etc)
        scaledImages = new BufferedImage[5];

        // Preload with zoom factors 1, 2, and 4
        int s = 1;
        for (int i = 0; s <= 4; s = (int) Math.pow(2, i)) {
            scaledImages[s] = toBufferedImage(baseImage.getScaledInstance((baseImage.getWidth() * s), (baseImage.getHeight() * s), java.awt.Image.SCALE_SMOOTH));
            i++;
        }

    }

    public void Go() {
        while (true) {
            // Update and paint
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
        try {
            longitude = scan.nextInt();
            latitude = scan.nextInt();
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter numbers");
        }
        scan.close();
    }

    // Overloaded zoom
    public void zoom() {
        zoom(zoomMode);
    }

    public void zoom(double s) {
        // For testing:
        System.out.println("zoom " + s);
        System.out.println("mouse x: " + mouse.x + ", mouse y: " + mouse.y);

        if (zoomMode == 0) return;

        if (this.scale * zoomMode <= 4) this.scale *= zoomMode;

        this.mouse = null;
    }

    // Updates and paints
    public void paintComponent(Graphics g) {
        // Background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.drawImage(scaledImages[scale], (int) topLeftCorner.x, (int) topLeftCorner.y, null);

        g.setColor(Color.RED);
        // TODO: Make points look as desired (currently 15 by 15 red squares)
        for (Pair p : pointsToTrack) {
            // This scales the box alone with the drawing
            // g.fillRect((int) (topLeftCorner.x + scale * p.x), (int) (topLeftCorner.y + scale * p.y), 15 * scale, 15 * scale);

            // This just transposes
            // g.fillRect((int) (topLeftCorner.x + scale * p.x), (int) (topLeftCorner.y + scale * p.y), 15, 15);

            g.drawImage(binIcon, (int) (topLeftCorner.x + scale * p.x), (int) (topLeftCorner.y + scale * p.y), null);

        }
        // Printing for testing
        if (button(g, 100, 100, HEIGHT - 60, 50, "Zoom In")) {
            System.out.println("zoom in");
            mouse = null;
            zoomMode = 2;
        } else if (button(g, 300, 100, HEIGHT - 60, 50, "Zoom Out")) {
            System.out.println("zoom out");
            mouse = null;
            zoomMode = 0.5;
        } else if (button(g, 500, 100, HEIGHT - 60, 50, "Pan")) {
            mouse = null;
            zoomMode = 1;
        } else if (button(g, 700, 100, HEIGHT - 60, 50, "Reset")) {
            defaultSettings();
        } else if (this.mouse != null && (mouse.x >= 0 && mouse.x <= WIDTH && mouse.y >= 0 && mouse.y <= HEIGHT)) {
            if (zoomMode == 2 && scale < 4) {
                topLeftCorner.x -= mouse.x - topLeftCorner.x;
                topLeftCorner.y -= mouse.y - topLeftCorner.y;
                zoom();
            } else if (zoomMode == 0.5 && scale > 1) {
                topLeftCorner.x += 0.5 * (mouse.x - topLeftCorner.x);
                topLeftCorner.y += 0.5 * (mouse.y - topLeftCorner.y);
                zoom();
            } // panning is handled with mousePressed and mouseReleased

            this.mouse = null;
        }

    }

    // From https://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), java.awt.image.BufferedImage.TYPE_INT_ARGB);

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

    // Takes a Graphics element, the dimensions of a button to draw, and the words to draw inside that button
    // Returns true of the click was inside the box, otherwise returns false
    public boolean button(Graphics g, int x, int dx, int y, int dy, String words) {
        g.setColor(Color.BLACK);
        g.fillRect(x, y, dx, dy);

        g.setColor(Color.RED);
        g.drawString(words, (int) (x + 0.2 * dx), (int) (y + 0.5 * dy));

        if (this.mouse != null && (mouse.x >= x && mouse.x <= x + dx && mouse.y >= y && mouse.y <= y + dy)) {
            // Reset mouse
            this.mouse = null;
            // For testing:
            System.out.println("Button " + words + " pressed");
            return true;
        } else {
            return false;
        }


    }

    @Override
    public void mouseExited(MouseEvent click) {
    }

    @Override
    public void mouseEntered(MouseEvent click) {
    }

    @Override
    public void mouseReleased(MouseEvent click) {
        // For use when panning
        if (zoomMode == 1 && this.mouseToDrag != null) {
            System.out.println("Released mouse x: " + click.getPoint().x + ", y: " + click.getPoint().y);
            Pair clickLoc = new Pair();
            clickLoc.matchPoint(click.getPoint());
            this.topLeftCorner.x += clickLoc.x - this.mouseToDrag.x;
            this.topLeftCorner.y += clickLoc.y - this.mouseToDrag.y;
            this.mouseToDrag = null;
        }

    }

    @Override
    public void mousePressed(MouseEvent click) {
        // For use when panning
        if (zoomMode == 1) {
            System.out.println("Held mouse x: " + click.getPoint().x + ", y: " + click.getPoint().y);
            this.mouseToDrag = new Pair();
            mouseToDrag.matchPoint(click.getPoint());
        }
    }
}

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
    private BufferedImage binIconBold;

    private BufferedImage mammoth;

    private Pair mouse = null;
    private Pair mouseToDrag = null;
    private Pair topLeftCorner = new Pair(0, 0);

    // TODO: set this to useful values
    private ArrayList<PairWithText> pointsToTrack = new ArrayList<PairWithText>();

    private ArrayList<Pair> usercreated = new ArrayList<Pair>();
    // Is true when user is dropping new points to be added to usercreated
    private boolean dropPoint = false;

    // Is true when user is clicking on compost icons to see their information
    //private boolean reader = false;

    // 2 = zoom in, 1 = don't zoom (pan), 0.5 = zoom out, 0 = do nothing
    private double zoomMode = 0;

    public CompostFinder() {
        // Setup
        addMouseListener(this);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        try {
            BufferedImage binImage = ImageIO.read(new File("compostBin.png"));
            this.binIcon = toBufferedImage(binImage.getScaledInstance(20, (int) (20.0 / (binImage.getWidth() / binImage.getHeight())), java.awt.Image.SCALE_SMOOTH));

            BufferedImage binImageBold = ImageIO.read(new File("obnoxiousBin.png"));
            this.binIconBold = toBufferedImage(binImageBold.getScaledInstance(20, (int) (20.0 / (binImageBold.getWidth() / binImageBold.getHeight())), java.awt.Image.SCALE_SMOOTH));

            BufferedImage mamImage = ImageIO.read(new File("mammoth.png"));
            //__
            this.mammoth = toBufferedImage(mamImage.getScaledInstance(20, (int) (20.0 / (mamImage.getWidth() / mamImage.getHeight())), java.awt.Image.SCALE_SMOOTH));

            BufferedImage image = ImageIO.read(new File("ACHigherQ.png"));

            int w = image.getWidth();
            int h = image.getHeight();

            double proportion = ((double) w) / (double) h;
            this.baseImage = toBufferedImage(image.getScaledInstance(WIDTH, (int) (WIDTH / proportion), java.awt.Image.SCALE_SMOOTH));

            List<double[]> coordinates = readCSVCoordinates("BinLocations.csv");
            for (int i = 0; i < coordinates.size(); i++) {
                convertAndAdd(coordinates.get(i), proportion);
            }

            usercreated = new ArrayList<Pair>();
            defaultSettings();
        } catch (Exception e) {
            System.err.println("Error");
        }

        setScaledImages();

        // TODO: Change from test values to useful values (compost bin locations)
        // this one is approx over Val
        pointsToTrack.add(new PairWithText(580, 280, "Val: By the checker stand and the further stairs"));
        // this one is approx over Frost
        pointsToTrack.add(new PairWithText(520, 335, "Frost: Front door, Periodicals Reading Room, 3rd floor, C-Level"));

    }

    private void convertAndAdd(double[] coordinates, double proportion) {
        double topLat = 42.37747909759732; //42.37747909759732, -72.5289558884478
        double topLong = -72.5289558884478;
        double botLat = 42.36708626458876; //42.367386655249895, -72.50576946133151
        double botLong = -72.50576946133151;
        double latDiff = botLat - topLat; // 42.374660288597745, -72.50579222182994
        double longDiff = botLong - topLong;

        int downLoc = (int) Math.round((coordinates[0] - topLat) / latDiff * WIDTH / proportion);
        int rightLoc = (int) Math.round((coordinates[1] - topLong) / longDiff * WIDTH);
        pointsToTrack.add(new PairWithText(rightLoc, downLoc));
    }

    private void defaultSettings() {
        scale = 1;
        zoomMode = 0;
        mouse = null;
        topLeftCorner.x = 0;
        topLeftCorner.y = 0;
        dropPoint = false;
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

        for (PairWithText p : pointsToTrack) {
            // This scales the box alone with the drawing
            // g.fillRect((int) (topLeftCorner.x + scale * p.x), (int) (topLeftCorner.y + scale * p.y), 15 * scale, 15 * scale);

            // This just transposes
            // g.fillRect((int) (topLeftCorner.x + scale * p.x), (int) (topLeftCorner.y + scale * p.y), 15, 15);

            g.drawImage(p.bold ? binIconBold : binIcon, (int) (topLeftCorner.x + scale * p.x), (int) (topLeftCorner.y + scale * p.y), null);
            if (p.showDescription && p.description != null)
                g.drawString(p.description, (int) (topLeftCorner.x + scale * p.x), (int) (topLeftCorner.y + scale * p.y));
        }

        // TODO: Make dropped points look as desired (currently 15 by 15 red squares)
        g.setColor(Color.RED);
        for (Pair p : usercreated) {
            g.drawImage(mammoth, (int) (topLeftCorner.x + scale * p.x - 0.5 * mammoth.getWidth()), (int) (topLeftCorner.y + scale * p.y - 0.5 * mammoth.getWidth()), null);
            // __g.fillRect((int) (topLeftCorner.x + scale * p.x), (int) (topLeftCorner.y + scale * p.y), 15, 15);
            // For testing:
            // System.out.println("Drawing point " + p.x + ", " + p.y);
        }


        // Printing for testing
        if (allButtons(g)) {
            // All action is handled from within allButtons
        } else if (checkCanClicker(g)) {
            //__
        } else if (this.mouse != null && (mouse.x >= 0 && mouse.x <= WIDTH && mouse.y >= 0 && mouse.y <= HEIGHT)) {
            if (zoomMode == 2 && scale < 4) {
                topLeftCorner.x -= mouse.x - topLeftCorner.x;
                topLeftCorner.y -= mouse.y - topLeftCorner.y;
                zoom();
            } else if (zoomMode == 0.5 && scale > 1) {
                topLeftCorner.x += 0.5 * (mouse.x - topLeftCorner.x);
                topLeftCorner.y += 0.5 * (mouse.y - topLeftCorner.y);
                zoom();
            } else if (zoomMode == 0 && dropPoint) {
                usercreated.add(new Pair((mouse.x - topLeftCorner.x) / scale, (mouse.y - topLeftCorner.y) / scale));
                // For testing:
                // System.out.println("Adding point " + usercreated.get(usercreated.size() - 1).x + ", " + usercreated.get(usercreated.size() - 1).y);
            } // panning is handled with mousePressed and mouseReleased

            this.mouse = null;
        }

    }

    // Draws all buttons and sets fields accordingly
    // Returns true if any of the buttons were pressed
    // else returns false
    public boolean allButtons(Graphics g) {
        if (button(g, 30, 100, HEIGHT - 60, 50, "Zoom In")) {
            System.out.println("zoom in");
            mouse = null;
            zoomMode = 2;
            dropPoint = false;
            return true;
        } else if (button(g, 150, 100, HEIGHT - 60, 50, "Zoom Out")) {
            System.out.println("zoom out");
            mouse = null;
            zoomMode = 0.5;
            dropPoint = false;
            return true;
        } else if (button(g, 270, 100, HEIGHT - 60, 50, "Pan")) {
            mouse = null;
            zoomMode = 1;
            dropPoint = false;
            return true;
        } else if (button(g, 390, 100, HEIGHT - 60, 50, "Reset")) {
            defaultSettings();
            usercreated = new ArrayList<Pair>();
            // Hide descriptions of bins and un-bold them
            for (PairWithText p : pointsToTrack) {
                p.showDescription = false;
                p.bold = false;
            }
            //reader = false;
            return true;
        } else if (button(g, 510, 100, HEIGHT - 60, 50, "Recenter")) {
            // Resets but without erasing user-created points
            defaultSettings();
        } else if (button(g, 630, 100, HEIGHT - 60, 50, "Drop Point")) {
            mouse = null;
            zoomMode = 0;
            dropPoint = true;
            //reader = false;
            return true;
        } else if (usercreated.size() > 0 && button(g, 750, 100, HEIGHT - 60, 50, "Find Bin")) {
            mouse = null;
            zoomMode = 0;
            dropPoint = false;

            //__
            double dist = 9999999;
            int binIndex = -1;
            for (Pair user : usercreated) {
                for (int i = 0; i < pointsToTrack.size(); i++) {
                    if (pointsToTrack.get(i).distanceTo(user) < dist) {
                        dist = pointsToTrack.get(i).distanceTo(user);
                        binIndex = i;
                    }
                }
            }

            if (binIndex == -1) {
                System.err.println("Error: No bin found.");
            }

            pointsToTrack.get(binIndex).bold = true;

        }

        return false;
    }

    private boolean checkCanClicker(Graphics g) {
        for (PairWithText p : pointsToTrack) {
            if (this.mouse != null && inside(this.mouse, topLeftCorner.x + scale * p.x, binIcon.getWidth(), topLeftCorner.y + scale * p.y, binIcon.getHeight())) {
                this.mouse = null;
                p.showDescription = !p.showDescription;
            }
        }
        //__ placeholder
        return false;
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

    // Overloaded button with default colors
    public boolean button(Graphics g, int x, int dx, int y, int dy, String words) {
        return button(g, x, dx, y, dy, words, Color.BLACK, Color.RED);
    }

    // Takes a Graphics element, the dimensions of a button to draw, and the words to draw inside that button
    // Returns true of the click was inside the box, otherwise returns false
    public boolean button(Graphics g, int x, int dx, int y, int dy, String words, Color color1, Color color2) {
        g.setColor(color1);
        g.fillRect(x, y, dx, dy);

        g.setColor(color2);
        g.drawString(words, (int) (x + 0.2 * dx), (int) (y + 0.5 * dy));

        if (this.mouse != null && inside(mouse, x, dx, y, dy)) {
            // Reset mouse
            this.mouse = null;
            // For testing:
            System.out.println("Button " + words + " pressed");
            return true;
        } else {
            return false;
        }
    }

    // Returns true if given Pair is inside given bounds
    // Otherwise returns false
    private boolean inside(Pair p, double x, double dx, double y, double dy) {
        // Validity check
        if (dx < 0 || dy < 0) {
            System.err.println("Error: invalid box");
            return false;
        }

        return p.x >= x && p.x <= x + dx && p.y >= y && p.y <= y + dy;
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

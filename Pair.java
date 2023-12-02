import java.awt.*;

public class Pair {
    public double x;
    public double y;

    public Pair(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Pair() {
        this.x = 0;
        this.y = 0;
    }

    public void matchPoint(Point p) {
        this.x = p.x;
        this.y = p.y;
    }
}

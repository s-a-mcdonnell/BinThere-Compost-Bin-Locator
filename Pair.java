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

    public void matchPoint(Point other) {
        this.x = other.x;
        this.y = other.y;
    }

    public double distanceTo(Pair other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }
}

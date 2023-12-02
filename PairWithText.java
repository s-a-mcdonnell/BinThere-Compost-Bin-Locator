public class PairWithText extends Pair {
    String description;
    boolean showDescription = false;

    public PairWithText(int x, int y, String s) {
        super(x, y);
        description = s;
    }

    public PairWithText(int x, int y) {
        super(x, y);
    }
}

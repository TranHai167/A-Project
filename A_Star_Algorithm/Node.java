package A_Star_simulation;

public class Node {
    Node previous;
    private double gValue;
    private double hValue;
    int x, y;

    public Node(int x, int y, double gValue, double hValue) {
        this.gValue = gValue;
        this.hValue = hValue;
        this.x = x;
        this.y = y;
    }

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public double get_gValue() {
        return gValue;
    }

    public double get_fValue() {
        return gValue + hValue;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

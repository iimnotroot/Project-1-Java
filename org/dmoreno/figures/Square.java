public class Square extends SimpleFigure {
    public int size;
    /**
     * Method to create a square
     * @param p Bottom corner point of the square
     * @param num Size number of the square
     */

    public Square(Point p, int num) {
        super(p);
        size = num;
    }
    @Override
    public String toString() {
        Point max = new Point(pos.x, pos.y);
        max.move(size,size);
        return " Square: [" + pos + "," + max +"]";
    }
}

public final class Rect extends SimpleFigure{
    public int sizex;
    public int sizey;

    /**
     * Method to create a Rectangle with a min Point and max Point
     */

    public Rect(Point pmin, Point pmax) {
        super(pmin);
        sizex = pmax.x - pmin.x;
        sizey = pmax.y - pmin.y;
    }

    @Override
    public String toString() {
        Point max = new Point(pos.x, pos.y);
        max.move(sizex,sizey);
        return " Rect: [" + pos + "," + max + "]";
    }
}

package org.dmoreno.figures;

public final class Rect extends SimpleFigure {
    private final int sizex;
    private final int sizey;

    /**
     * Method to create a Rectangle with a min org.dmoreno.figures.Point and max org.dmoreno.figures.Point
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

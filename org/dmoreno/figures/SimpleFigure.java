package org.dmoreno.figures;

public class SimpleFigure extends Figure {
    Point pos;

    /**
     * Creates new org.dmoreno.figures.Point from x and y
     */

    public SimpleFigure(int x, int y) {
        pos = new Point(x,y);
    }

    /**
     * Creates new org.dmoreno.figures.Point from a org.dmoreno.figures.Point
     * @param pos org.dmoreno.figures.Point for the org.dmoreno.figures.SimpleFigure
     */

    public SimpleFigure(Point pos){
        this(pos.x, pos.y);
    }

    /**
     * Returns the org.dmoreno.figures.Point of the org.dmoreno.figures.SimpleFigure
     */
    @Override
    public Point pos() {
        return new Point(pos.x,pos.y);
    }

    /**
     * Moves the position of the org.dmoreno.figures.Point
     * @param dx Value to displace the org.dmoreno.figures.Point in x-axis
     * @param dy Value to displace the org.dmoreno.figures.Point in y-axis
     */
    @Override
    public void move(int dx, int dy) {
        pos.x += dx;
        pos.y += dy;
    }
    @Override
    public String toString(){
        return "fig:" + pos.toString();
    }

    @Override
    public String BuilderString() {
        return pos.toString();
    }

}

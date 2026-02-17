package org.dmoreno.figures;

public class Line extends SimpleFigure {
    private final int lengthx;
    private final int lengthy;

    public Line(Point pmin, Point pmax){
        super(pmin);
        lengthx = pmax.x - pmin.x;
        lengthy = pmax.y - pmin.y;
    }

    public double getLenght() {
        double length;
        length = Math.hypot(lengthx, lengthy);
        return length;
    }

    @Override
    public String toString(){
        Point max = new Point(pos.x, pos.y);
        max.move(lengthx,lengthy);
        return " Line: [" + pos + "," + max + "]";
    }
}

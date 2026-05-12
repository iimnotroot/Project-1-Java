package org.dmoreno.figures;

import java.io.BufferedReader;

public class Line extends SimpleFigure {
    private final int lengthx;
    private final int lengthy;

    public Line(Point pmin, Point pmax){
        super(pmin);
        lengthx = pmax.x - pmin.x;
        lengthy = pmax.y - pmin.y;
    }

    public Line(String[] args) {
        this(
                parsePoint(args, 1,2),
                parsePoint(args, 3 ,4)
        );
    }

    private static Point parsePoint(String[] args, int idx1, int idx2) {
        try {
            if (args.length != 5) {
                throw new IllegalArgumentException("Line Xmin Ymin Xmax Ymax");
            }
            int x = Integer.parseInt(args[idx1]);
            int y = Integer.parseInt(args[idx2]);
            return new Point(x,y);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("X and Y must be Integers");
        }
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
        return "Line: " + "Figure ID : " + id +  " [" + pos + "," + max + "]";
    }


    public String BuilderString(){
        Point max = new Point(pos.x, pos.y);
        max.move(lengthx,lengthy);
        return "Line " + pos.x + " " + pos.y + " " + max.x + " " + max.y;
    }

    public static Figure parse(String[] args, BufferedReader rd) {
        return new Line(args);
    }

}

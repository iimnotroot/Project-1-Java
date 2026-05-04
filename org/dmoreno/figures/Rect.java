package org.dmoreno.figures;

import java.io.BufferedReader;

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

    public Rect(String[] args) {
        this (
                parsePoint(args, 1, 2),
                parsePoint(args, 3 ,4)
        );
    }

    private static Point parsePoint(String[] args, int idx1, int idx2) {
        try {
            if (args.length != 5) {
                throw new IllegalArgumentException("Rect Xmin Ymin Xmax Ymax");
            }
            int x = Integer.parseInt(args[idx1]);
            int y = Integer.parseInt(args[idx2]);
            return new Point(x,y);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("X and Y must be Integers");
        }
    }



    @Override
    public String toString() {
        Point max = new Point(pos.x, pos.y);
        max.move(sizex,sizey);
        return "ID: " + id + " Rect: [" + pos + "," + max + "]";
    }

    public String BuilderString() {
        Point max = new Point(pos.x, pos.y);
        max.move(sizex,sizey);
        return "Rect " + pos.x + " " + pos.y + " " + max.x + " " + max.y;
    }

    public static Figure parse(String[] args, BufferedReader rd) {
        return new Rect(args);
    }
}

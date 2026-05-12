package org.dmoreno.figures;

import java.io.BufferedReader;

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

    public Square(String[] args) {
        this(
            parasePoint(args),
            parseSize(args)
        );
    }

    private static Point parasePoint(String[] args) {
        try {
            if (args.length != 4) {
                throw new IllegalArgumentException("Square X Y size");
            }
            int x = Integer.parseInt(args[1]);
            int y = Integer.parseInt(args[2]);
            return new Point(x,y);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("X and Y must be integers");
        }
    }

    private static int parseSize(String[] args) {
        try {
            int num = Integer.parseInt(args[3]);
            if (num <= 0) {
                throw new IllegalArgumentException("Size of square must not be a negative number or zero");
            }
            return num;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Size must be an integer");
        }
    }

    @Override
    public String toString() {
        Point max = new Point(pos.x, pos.y);
        max.move(size,size);
        return "Square: " + "Figure ID: " + id  + " [" + pos + "," + max +"]";
    }

    public String BuilderString(){
        Point max = new Point(pos.x, pos.y);
        max.move(size,size);
        return "Square " + pos.x + " " + pos.y + " " + size;
    }

    public static Figure parse(String[] args, BufferedReader rd) {
        return new Square(args);
    }
}

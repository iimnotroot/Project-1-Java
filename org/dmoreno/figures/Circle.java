package org.dmoreno.figures;

import java.io.BufferedReader;

public class Circle extends SimpleFigure{
    private int radius;

    /**
     * Creates a new Circle given a center point and a radius
     * @param p Point representing the center of the Circle
     * @param r Radius of the Circle
     */

    public Circle(Point p, int r) {
        super(p);
        if (r<=0){
            System.err.println("error: radius can not be a negative number or zero");
            System.exit(1);
        }
        radius = r;
    }

    public Circle(String[] args) {
        this(
                parsePoint(args),
                parseSize(args)
        );
    }

    private static Point parsePoint(String[] args) {
        if (args.length != 4) {
            throw new IllegalArgumentException("Circle X Y radius");
        }
        try {
            int x = Integer.parseInt(args[1]);
            int y = Integer.parseInt(args[2]);
            return new Point(x,y);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("X and Y must be integer numbers");
        }
    }

    private static int parseSize(String[] args){
        try {
            int num = Integer.parseInt(args[3]);
            if (num <= 0) {
                throw new IllegalArgumentException("Radius must be a positive number");
            }
            return num;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Radius must be an integer number");
        }
    }

    public int getRadius() {
        return radius;
    }

    public void modify_radius(int new_r) {
        if (new_r <= 0){
            System.err.println("error: radius can not be a negative number or zero");
            System.exit(1);
        }
        radius = new_r;
    }

    @Override
    public String toString() {
        if (this.color != null || this.name != null) {
            return " Circle: [" + pos + "," + "rad=" + radius + "]" + "Attributes:" + this.color + " " + this.name.toString();
        }
        return " Circle: [" + pos + "," + "rad=" + radius + "]";
    }

    public String BuilderString(){
        return "Circle " + pos.x + " " + pos.y + " " + radius;
    }

    public static Figure parse(String[] args, BufferedReader rd) {
        return new Circle(args);
    }
}

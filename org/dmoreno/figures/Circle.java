package org.dmoreno.figures;

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
        return " Circle: [" + pos + "," + "rad=" + radius + "]";
    }
}

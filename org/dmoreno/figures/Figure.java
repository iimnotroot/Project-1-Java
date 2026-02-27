package org.dmoreno.figures;

import org.dmoreno.FigureAttributes.Color;
import org.dmoreno.FigureAttributes.Name;

public abstract class Figure {
    public Color color;
    public Name name;

    /**
     * Returns the main org.dmoreno.figures.Point of the org.dmoreno.figures.SimpleFigure
     */

    public abstract Point pos();

    /**
     * Displacement of the point
     */

    public abstract void move(int dx, int dy);

    /**
     * Print line
     */

    public abstract String toString();

    /**
     * Method returns a String with builder format
     */

    public abstract String BuilderString();

    /**
     * Factory to create Figures by a string given
     * List Figures:
     * {Circle X Y radius}
     * {Rect Xmin Ymin Xmax Ymax}
     * {Line Xmin Ymin Xmax Ymax}
     * {Square X Y size}
     * @param str {TypeFigure pos params}
     * @return Figure
     */
    public static Figure parse(String str) {
        if (str == null) {
            return null;
        }
        String[] args = str.split("\\s");
        switch (args[0]) {
                case "Circle":
                    return new Circle(args);

                case "Rect":
                    return new Rect(args);

                case "Line":
                    return new Line(args);

                case "Square":
                    return new Square(args);

                default:
                    System.err.println("error: Figure name is not recognize");
                    throw new RuntimeException("Figure name unknown");
        }
    }

    /**
     * Set an RGB color to a Figure
     * @param r Red Color
     * @param g Green Color
     * @param b Blue Color
     */
    public void setColor(int r, int g, int b){
        try {
            color = new Color(r,g,b);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setName(String str) {
        try {
            if (str == null) {
                throw new RuntimeException("error: string can not be null");
            }
            name = new Name(str);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}

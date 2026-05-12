package org.dmoreno.figures;

import org.dmoreno.FigureAttributes.Color;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Figure {
    public Color color;
    protected int id;


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

    public void setId(Integer id) {
        try {
            if (id < 0) {
                throw new RuntimeException("error: id can not be negative");
            }
            this.id = id;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public int getId() {
        return id;
    }



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
        Parser p = parsers.getOrDefault(args[0], null);
        if (p == null) {
            throw new RuntimeException("error: figure unknown");
        }
        return p.parse(args, null);
    }

    public static ArrayList<Figure> ReadAllFrom(BufferedReader rd){
        ArrayList<Figure> fig_arr= new ArrayList<Figure>();
        try{
            var line = rd.readLine();
            while(line != null){
                var args = line.split(" ");
                Parser p = parsers.getOrDefault(args[0], null);
                if (p==null) {
                    throw new RuntimeException("error: figure unknown");
                }
                fig_arr.add(p.parse(args, rd));
                line = rd.readLine();
            }
        } catch  (Exception e){
            throw new RuntimeException("error reading line");
        }
        return fig_arr;
    }

    public static Figure Readfrom(BufferedReader rd) {
        try {
            var line = rd.readLine();
            if (line == null) {
                return null;
            }
            var args = line.split(" ");
             Parser p = parsers.getOrDefault(args[0], null);
             if (p==null) {
                 throw new RuntimeException("error: figure unknown");
             }
             return p.parse(args, rd);
        } catch (Exception e) {
            throw new RuntimeException("error reading file");
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


    interface Parser {
        Figure parse(String[] args, BufferedReader rd);
    }

    public static HashMap<String, Parser> parsers;

    static {
        parsers = new HashMap<>();
        parsers.put("Circle", Circle::parse);
        parsers.put("Square", Square::parse);
        parsers.put("Line", Line::parse);
        parsers.put("Rect", Rect::parse);
    }

}

package org.dmoreno.figures;

public abstract class Figure {

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

}

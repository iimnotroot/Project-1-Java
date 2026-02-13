public abstract class Figure {

    /**
     * Returns the main Point of the SimpleFigure
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

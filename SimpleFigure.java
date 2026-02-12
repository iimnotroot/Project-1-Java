public class SimpleFigure extends Figure{
    Point pos;

    /**
     * Creates new Point from x and y
     */

    public SimpleFigure(int x, int y) {
        pos = new Point(x,y);
    }

    /**
     * Creates new Point from a Point
     * @param pos Point for the SimpleFigure
     */

    public SimpleFigure(Point pos){
        this(pos.x, pos.y);
    }

    /**
     * Returns the Point of the SimpleFigure
     */
    @Override
    public Point pos() {
        return new Point(pos.x,pos.y);
    }

    /**
     * Moves the position of the Point
     * @param dx Value to displace the Point in x-axis
     * @param dy Value to displace the Point in y-axis
     */
    @Override
    public void move(int dx, int dy) {
        pos.x += dx;
        pos.y += dy;
    }
    @Override
    public String toString(){
        return "fig:" + pos.toString();
    }
}

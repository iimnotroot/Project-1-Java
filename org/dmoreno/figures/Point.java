package org.dmoreno.figures;

public class Point {
    public int x;
    public int y;

    /**
     * moves point adding delta x and delta y
     */

    public Point(int ux, int uy) {
        x = ux;
        y = uy;
    }

    public void move(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    public String toString() {
        return "["+x+", "+y+"]";
    }

}

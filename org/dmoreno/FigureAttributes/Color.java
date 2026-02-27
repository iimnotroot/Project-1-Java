package org.dmoreno.FigureAttributes;

public class Color {
    private int red;
    private int blue;
    private int green;
    public int color;



    public Color(int r, int g, int b) {
        red = r & 0xFF;
        green = g & 0xFF;
        blue = b & 0xFF;

        color = (red<<16 | green << 8 | blue);

    }

    public int getColor(){
        return color;
    }

    public void changeColor(int r, int b, int g){
        red = r & 0xFF;
        green = g & 0xFF;
        blue = b & 0xFF;

        color = (red<<16 | green << 8 | blue);
    }

    public int getRed(){
        return red;
    }

    public int getBlue(){
        return blue;
    }

    public int getGreen(){
        return green;
    }

    public String toString(){
        return String.format("RGB(%d, %d, %d) [#%06X]", red, green, blue, getColor());
    }

}

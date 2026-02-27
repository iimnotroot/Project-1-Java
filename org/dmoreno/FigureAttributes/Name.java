package org.dmoreno.FigureAttributes;

public class Name {
    public String name;
    public Name(String str) {
        name = str;
    }
    public void ChangeName(String newstr) {
        name = newstr;
    }

    public String toString() {
        return name;
    }

}

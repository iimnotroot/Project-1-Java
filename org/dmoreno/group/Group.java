package org.dmoreno.group;

import org.dmoreno.figures.Figure;
import org.dmoreno.figures.Point;

import java.util.Arrays;

public class Group extends Figure {
    Figure[] group;
    Point pos_group;
    int current_position = 0;
    public Group() { // Ask if there is any dynamic allocation in Java for arrays
        group = new Figure[1];
    }
    @Override
    public Point pos(){
        if (pos_group != null) {
            System.err.println("error: there is no org.dmoreno.figures.Figure in the group");
        }
        return pos_group;
    }
    @Override
    public void move(int dx, int dy) { //How can I implement?? Move first org.dmoreno.figures.Figure and upload pos_group? Move only pos_group?

    }

    public void add(Figure fig){
        if (current_position == 0) {
            pos_group = fig.pos();
        } else {
            group = Arrays.copyOf(group, (group.length + 1));
        }
        group[current_position] = fig;
        current_position++;
    }

    public void drop(int position) { //For me, it feels more accurate to drop by name instead of by position
        int i;
        int u;

        if (position < group.length) {
            for (i=position; i < group.length; i++) {
                if (i == (group.length - 1) ) {
                    group = Arrays.copyOf(group, i);
                } else {
                    u = i + 1;
                    group[i] = group[u];
                }
            }
        } else {
            System.err.println("error: position exceeds the length of the array group");
        }

    }
    @Override
    public String toString(){
        String result = "Group: ";
        int i;
        for (i=0; i < group.length; i++) {
            if (group[i] != null) {
                if (i != (group.length - 1) ){
                    result += group[i].toString() + ",";
                } else {
                    result += group[i].toString();
                }
            }
        }
        return result;
    }
}

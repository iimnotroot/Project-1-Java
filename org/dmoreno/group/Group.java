package org.dmoreno.group;

import org.dmoreno.figures.Figure;
import org.dmoreno.figures.Point;

import java.util.Arrays;

public class Group extends Figure {
    Figure[] group;
    Point pos_group;
    public String name;
    int current_position = 0;

    /**
     * Creates a group of Figures, named as you want, also it can be created by giving an array of Figures or empty
     * @param group_name Name of the group
     * @param args Array of Figures
     */
    public Group(String group_name, Figure... args ) {
        group = new Figure[0];
        name = group_name;
        for (Figure f : args) {
            add(f);
        }
    }
    @Override
    public Point pos(){
        if (pos_group != null) {
            System.err.println("error: there is no Figure in the group");
        }
        return pos_group;
    }
    @Override
    public void move(int dx, int dy) { //How can I implement?? Move Figure and upload pos_group? Move only pos_group?
        pos_group.x += dx;
        pos_group.y += dy;
    }

    public void add(Figure fig){
        if (group == null) {
            System.err.println("error: Figure can not be added because Group is null");
        } else {
            if (current_position == 0) {
                pos_group = fig.pos();
            }
            group = Arrays.copyOf(group, (group.length + 1));
            group[current_position] = fig;
            current_position++;
        }
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
        String result = "Group " + name;
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

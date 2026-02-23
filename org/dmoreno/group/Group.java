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
        if (pos_group == null) {
            System.err.println("error: there is no Figure in the group");
            System.exit(1);
        }
        return pos_group;
    }
    @Override
    public void move(int dx, int dy) {
        int i;
        for (i=0; i < group.length; i++) {
            group[i].move(dx,dy);
        }
        pos_group.move(dx,dy);
    }

    public void add(Figure fig){
        if (group == null) {
            System.err.println("error: Figure can not be added because Group is null");
            System.exit(1);
        } else {
            if (current_position == 0) {
                pos_group = new Point(fig.pos().x,fig.pos().y);
            }
            group = Arrays.copyOf(group, (group.length + 1));
            group[current_position] = fig;
            current_position++;
        }
    }

    public void drop(int position) {
        int i;

        if (position < group.length && position >= 0) {
            for (i=position; i < group.length; i++) {
                if (i != (group.length - 1)) {
                    group[i] = group[i+1];
                }
            }
            group = Arrays.copyOf(group, group.length - 1);
            current_position--;

            if (group.length == 0) {
                pos_group = null;
            } else if (position == 0) {
                pos_group = new Point(group[0].pos().x, group[0].pos().y);
            }

        } else {
            System.err.println("error: position exceeds the length of the array group");
            System.exit(1);
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

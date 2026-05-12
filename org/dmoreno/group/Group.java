package org.dmoreno.group;

import org.dmoreno.figures.Figure;
import org.dmoreno.figures.Point;

import java.util.ArrayList;

public class Group extends Figure {
    public ArrayList<Figure> group_figures;
    Point pos_group;
    /**
     * Creates a group of Figures, named as you want, also it can be created by giving an array of Figures or empty
     * @param id ID of the group
     * @param args Array of Figures
     */
    public Group(int id, Figure... args ) {
        group_figures = new ArrayList<>();
        setId(id);
        for (Figure f : args) {
            add(f);
        }
    }
    @Override
    public Point pos(){
        try {
            if (pos_group == null) {
                throw new RuntimeException("error: there is no position in the group");
            }
            return pos_group;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public void move(int dx, int dy) {
        for (Figure fig: group_figures) {
            fig.move(dx,dy);
        }
        if (pos_group != null) {
            pos_group.move(dx, dy);
        }
    }

    private boolean collision(Figure fig_toadd) {

        try {
            for (Figure fig: group_figures) {
                if (fig_toadd.pos().x == fig.pos().x && fig_toadd.pos().y == fig.pos().y) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("error: comparison has failed");
        }
    }

    public void add(Figure fig){
        try {
            if (fig == null) {
                throw new RuntimeException("error: Figure can not be added because is null");
            } else {
                if (group_figures.isEmpty()) {
                    pos_group = new Point(fig.pos().x,fig.pos().y);
                }
                if (collision(fig)) {
                    throw new RuntimeException("error: figure position crash with another of the group");
                }
                group_figures.add(fig);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }

    public void drop(Figure figtodrop) {
        try {
            if (figtodrop == null) {
                throw new RuntimeException("error: Figure can not be drop because is null");
            }
            boolean removed = group_figures.remove(figtodrop);
            if (!removed) {
                throw new RuntimeException("error: figure not found in group");
            }

            if (group_figures.isEmpty()) {
                pos_group = null;
            } else {
                Figure firstFig = group_figures.getFirst();
                pos_group = new Point(firstFig.pos().x,firstFig.pos().y);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean contains(Figure fig) {
        return group_figures.contains(fig);
    }
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Group ID: ").append(id).append(" ");
        result.append("Figures in Group:\n");
        result.append(" -");

        for (int idx = 0; idx < group_figures.size(); idx++) {
            Figure fig = group_figures.get(idx);
            if ( fig != null) {
                result.append(fig.toString());

                if (idx != group_figures.size() - 1) {
                    result.append("\n");
                    result.append(" -");
                }
            }
        }

        return result.toString();
    }

    public String BuilderString(){
        return Integer.toString(id);
    }

}

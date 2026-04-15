package org.dmoreno.draw;

import org.dmoreno.FigureAttributes.Name;
import org.dmoreno.figures.Figure;
import org.dmoreno.group.Group;

import java.io.*;
import java.util.ArrayList;

public class Draw {
    ArrayList<Figure> figureList = new ArrayList<Figure>();
    File file;
    public Name name;

    /**
     * Create a draw giving only the path of the file
     * @param path path of the file
     */
    public Draw(String path){
        try {
            file = new File(path);
        } catch (Exception e) {
            throw new RuntimeException("error: file does not exist");
        }
        ;
    };

    /**
     * This method Draws all the Figures in the different group
     * @param grp Group of Figures to draw
     */
    public Draw(Group grp, String path){
        try {
            file = new File(path);
        } catch (Exception e) {
            throw new RuntimeException("error: file does not exist");
        }
        add(grp);
    }

    /**
     * This method is used to Draw only the Figure is passed
     * @param fig Figure to draw
     */

    public Draw(Figure fig, String path) {
        try {
            file = new File(path);
        } catch (Exception e) {
            throw new RuntimeException("error: file does not exist");
        }
        add(fig);
    }

    private boolean collision(Figure fig_toadd) {
        int idx = 0;
        try {
            for (idx=0; idx < figureList.size(); idx++) {
                Figure fig = figureList.get(idx);
                if (fig_toadd.pos().x == fig.pos().x && fig_toadd.pos().y == fig.pos().y) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("error: comparison has failed");
        }
    }

    public void add(Figure fig) {
        try {
            if (fig == null) {
                throw new RuntimeException("error: figure is null");
            }
            if (collision(fig)) {
                throw new RuntimeException("error: collision with the position of another figure in the draw");
            }
            figureList.add(fig);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void add(Group grp){
        int idx;
        try {
            if (grp == null || grp.group_figures==null) {
                throw new RuntimeException("error: group is null");
            }
            for (idx = 0; idx < grp.group_figures.length; idx++) {
                    if (collision(grp.group_figures[idx])) {
                        throw new RuntimeException("error: collision with the position of other figure in the draw");
                    }
                    figureList.add(grp.group_figures[idx]);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public void drop(Figure fig) {
        try {
            if (fig==null) {
                throw new RuntimeException("error: figure is null");
            }
            figureList.remove(fig);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void sketch() {
        BufferedWriter wr = null;
        try {
            if (file == null) {
                throw new RuntimeException("error: file is null");
            }
            OutputStream out = new FileOutputStream(file);
            wr = new BufferedWriter(new OutputStreamWriter(out));
            int idx;
            for (idx=0; idx < figureList.size(); idx++) {
                Figure fig = figureList.get(idx);
                if (fig == null) {
                    throw new RuntimeException("error: figure is null");
                }
                wr.write(fig.toString());
                System.out.println("Drawing figure: " + fig.toString());
                wr.newLine();
                wr.flush();
            }
            wr.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (wr != null) {
                try {
                    wr.close();
                } catch (Exception e) {
                    System.out.println("error: writer can not be closed");
                    System.exit(1);
                }
            }
        }
    }

    public void setName(String str){
        name = new Name(str);
    }



}

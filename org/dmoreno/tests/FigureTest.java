package org.dmoreno.tests;

import org.dmoreno.draw.Draw;
import org.dmoreno.figures.*;
import org.junit.jupiter.api.Test;
import org.dmoreno.group.Group;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class FigureTest {

    @Test
    public void testCreateGroup() {
        Figure fig1 = Figure.parse("Square 1 3 5");
        Figure fig2 = new Circle(new Point(1,2), 4);
        Figure fig3 = Figure.parse("Line 1 4 5 8");

        Group group1 = new Group("Figures1", fig1, fig2, fig3);
    }

    @Test
    public void testCreateFigures() {
        Figure fig1 = new Circle(new Point(3,2),5);
        Figure fig2 = new Line(new Point(1,2), new Point(3,4));
        Figure fig3 = new Square(new Point(1,2),3);
        Figure fig4 = new Rect(new Point(4,3), new Point(5,6));

        Group group1 = new Group("figures1", fig1, fig2, fig3, fig4);
        group1.move(3,4);

        Figure fig5 = Figure.parse("Circle 3 2 5");
        Figure fig6 = Figure.parse("Line 1 2 3 4");
        Figure fig7 = Figure.parse("Square 1 2 3");
        Figure fig8 = Figure.parse("Rect 4 3 5 6");

        Group group2 = new Group("figures1", fig5, fig6, fig7, fig8);
        group2.move(3,4);
        if (!Objects.equals(group1.toString(), group2.toString())) {
            System.err.println("Strings are not equal");
            System.exit(1);
        }
        System.out.println(group1.toString());
        System.out.println(group2.toString());

    }

    @Test
    public void testFigStr() {
        Square sqr1 = new Square(new Point(1,2),5);
        var s = sqr1.BuilderString();
        System.out.println(s);
        Figure fignew = Figure.parse(s);
        if (fignew instanceof Circle) {
            System.out.println("fig is a Circle");
        } else {
            System.out.println("fig is not a Circle");
        }

        assertEquals("Square 1 2 5", fignew.BuilderString());
    }

    @Test
    public void FigureColor() {
        Figure fig1 = new Square(new Point(1,2),5);
        fig1.setColor(255,15,55);

        Square sq1 = new Square(new Point(4,4),5);
        sq1.setColor(4,4,4);


        System.out.println(sq1.color.toString());
        System.out.println(fig1.color.toString());
    }

    @Test
    public void FigureName() {
        Figure fig1 = new Square(new Point(1,2),5);
        fig1.setName("Cuadrado");

        System.out.println(fig1.name.toString());

    }

    @Test
    public void AllFiguresCreationbyFileTest() {
        File file = new File("/home/dontlookatme/IdeaProjects/Pj1/Figures.txt");
        BufferedReader rd = null;
        try {
            InputStream in = new FileInputStream(file);
            rd = new BufferedReader(new InputStreamReader(in));
            ArrayList<Figure> arr_fig = new ArrayList<Figure>(Figure.ReadAllFrom(rd));
            for (Figure fig: arr_fig){
                System.out.println(fig.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (rd != null) {
                try {
                    rd.close();;
                } catch (Exception e) {
                    System.exit(1);
                }
            }
        }
    }

    @Test
    public void FIgureCreationbyFileTest() {
        File file = new File("/home/dontlookatme/IdeaProjects/Pj1/Figures.txt");
        BufferedReader rd = null;
        try {
            InputStream in = new FileInputStream(file);
            rd = new BufferedReader(new InputStreamReader(in));
            while (true) {
                Figure fig = Figure.Readfrom(rd);
                if (fig == null) {
                    break;
                }
                System.out.println(fig.toString());
            }
            rd.close();;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (rd != null) {
                try {
                    rd.close();
                } catch (Exception e) {
                    System.exit(1);
                }
            }
        }

    }

    @Test
    public void DrawTest() {
        Figure fig1 = new Circle(new Point(3,2),5);
        Figure fig2 = new Line(new Point(1,2), new Point(3,4));
        Figure fig3 = new Square(new Point(1,2),3);
        Figure fig4 = new Rect(new Point(4,3), new Point(5,6));

        Group group1 = new Group("figures1", fig1, fig2, fig3, fig4);
        Draw draw1 = new Draw(group1, "/home/dontlookatme/IdeaProjects/Pj1/draw1.txt");
        draw1.sketch();
        Figure fig5 = new Line(new Point(4,3), new Point(4,6));
        draw1.add(fig5);
        draw1.sketch();

    }

    @Test
    public void attributesTest() {
        Figure fig1 = new Circle(new Point(1,5),6);
        fig1.setName("figura1");
        fig1.setColor(50,255,0);
        System.out.println(fig1.toString());
    }
}

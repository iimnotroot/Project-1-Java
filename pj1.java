import org.dmoreno.figures.Figure;
import org.dmoreno.figures.Point;
import org.dmoreno.figures.Rect;
import org.dmoreno.figures.Square;
import org.dmoreno.figures.Line;
import org.dmoreno.figures.Circle;
import org.dmoreno.group.Group;

public class pj1 {
    public static void main (String[] args) {
        Point p = new Point(1,2);
        Point pmin = new Point(2,3);
        Point pmax = new Point(4,6);
        Figure fig1 = new Square(p,4);
        Figure fig2 = new Rect(pmin,pmax);
        Figure fig3 = new Line(pmin,pmax);
        int rad = 3;
        Figure fig4 = new Circle(p,rad);

        Group group1 = new Group("figures1");
        group1.add(fig1);
        group1.add(fig2);
        group1.add(fig3);
        group1.add(fig4);
        System.out.println(group1.toString());
        group1.drop(3);
        System.out.println(group1.toString());


        group1.drop(1);
        System.out.println(group1.toString());

        Figure fig5 = new Square(p,7);
        Figure fig6 = new Rect(pmin,pmax);
        fig4.move(6,7);
        Figure fig7 = Figure.parse("Circle 4 5 5");

        Group group2 = new Group("figures2",fig5, fig6);
        group2.add(fig7);
        System.out.println(group2.toString());

        group2.drop(1);
        System.out.println(group2.toString());

    }
}

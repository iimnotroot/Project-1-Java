public class pj1 {
    public static void main (String[] args) {
        Point p = new Point(1,2);
        Point pmin = new Point(2,3);
        Point pmax = new Point(4,6);
        Figure fig1 = new Square(p,4);
        Figure fig2 = new Rect(pmin,pmax);

        Group group = new Group();
        group.add(fig1);
        group.add(fig2);
        System.out.println(group.toString());

        group.drop(1);
        System.out.println(group.toString());

    }
}

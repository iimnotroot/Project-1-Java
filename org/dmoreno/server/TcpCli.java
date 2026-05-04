package org.dmoreno.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.dmoreno.figures.Figure;
import org.dmoreno.server.util.Addr;
import org.dmoreno.server.Msg;

import java.net.StandardSocketOptions;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.dmoreno.server.Msg.*;

public class TcpCli {
    SocketChannel sck;
    Req req = new Req();
    AtomicBoolean running = new AtomicBoolean(true);
    int msg_tag = 0;
    public TcpCli(String addr) {
        Addr a = new Addr(addr);
        try {
            sck = SocketChannel.open();
            sck.connect(new InetSocketAddress(a.inet(), a.port));

            sck.setOption(StandardSocketOptions.TCP_NODELAY, true);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean verifyAct(String action, int maxnum) {
        try {
            int num = Integer.parseInt(action);
            if ((num < 0) ||  (num > maxnum)) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;

    }

    private boolean isValidID(String str) {
        if (str == null || str.isBlank()) {
                return false;
        }
        if (str.equals("*")) {
            return true;
        }
        String[] parts = str.split("-");
        try {
            for (String p : parts) {
                if (p.isBlank()) return false;
                Integer.parseInt(p);
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private Msg handleDraw(Scanner sc, int msg_tag) {
        while (true) {
            System.out.println("Draw actions:");
            System.out.println("1. New Draw");
            System.out.println("2. Add Figure to Draw");
            System.out.println("3. Delete Draw");
            System.out.println("4. Sketch Draw");
            System.out.println("5. List Draw");
            System.out.println("6. Go back");
            System.out.print("> ");

            String opt = sc.nextLine();
            if (!verifyAct(opt, 6)) {
                System.err.println("error: please select one of the options writing the number associate to");
                continue;
            }
            switch (opt) {
                case "1":
                    System.out.println("Enter the draw ID:");
                    System.out.print("> ");
                    opt = sc.nextLine();
                    if (!isValidID(opt)) {
                        System.err.println("error: please write a correct ID for the draw");
                        continue;
                    }
                    return new Tnewdib(opt, msg_tag, null);
                case "2":
                    System.out.println("Enter the draw ID and figure IDs (separated by '-'). Example: 1 0-5-6");
                    System.out.print("> ");
                    opt = sc.nextLine();
                    return new Tadddib(opt, msg_tag, null);
                case "3":
                    System.out.println("Enter the ID of the draw to delete:");
                    System.out.print("> ");
                    opt = sc.nextLine();
                    if (!isValidID(opt)) {
                        System.err.println("error: please write a correct ID of the draw");
                        continue;
                    }
                    return new Tdeldib(Integer.parseInt(opt), msg_tag, null);
                case "4":
                    System.out.println("Enter the ID of the draw to sketch:");
                    System.out.print("> ");
                    opt = sc.nextLine();
                    if (!isValidID(opt)) {
                        System.err.println("error: please write a correct ID of the draw\"");
                        continue;
                    }
                    return new Tsketchdib(Integer.parseInt(opt), msg_tag, null);
                case "5":
                    System.out.println("Enter the ID of the draw to list:");
                    System.out.print("> ");
                    opt = sc.nextLine();
                    if (!isValidID(opt)) {
                        System.err.println("error: please write a correct ID of the draw\"");
                        continue;
                    }
                    return new Tlistdib(Integer.parseInt(opt), msg_tag, null);
                case "6":
                    return null;
                default:
                    System.err.println("Invalid draw option");
                    continue;
            }
        }
    }

    private Msg handleFigure(Scanner sc, int msg_tag) {
        while (true) {
            System.out.println("Figure actions:");
            System.out.println("1. New Figure");
            System.out.println("2. List Figures");
            System.out.println("3. Delete Figure");
            System.out.println("4. Go back");
            System.out.print("> ");

            String opt = sc.nextLine();
            if (!verifyAct(opt, 4)) {
                System.err.println("error: please select one of the options writing the number associate to");
                continue;
            }
            switch (opt) {
                case "1":
                    System.out.println("Enter the figure type and its parameters. Examples:\nCircle 10 20 4\nSquare 5 10 4\nRect 10 5 20 3");
                    System.out.print("> ");
                    String args = sc.nextLine();
                    Figure fig;
                    try {
                        fig = Figure.parse(args);
                    } catch (RuntimeException e) {
                        System.err.println("Invalid figure type or parameters.");
                        continue;
                    }

                    return new Tnewfig(fig.BuilderString(), msg_tag, null);
                case "2":
                    System.out.println("Enter the IDs of the figure to list (* to list all, 1-5-6 to list specific IDs):");
                    System.out.print("> ");
                    String arg = sc.nextLine();
                    if (!isValidID(arg)) {
                        System.err.println("error: please write a correct ID for the figure/s\"");
                        continue;
                    }
                    return new Tlistfigs(arg, msg_tag, null);
                case "3":
                    System.out.println("Enter the ID of the figure to delete:");
                    System.out.print("> ");
                    String id = sc.nextLine();
                    if (!isValidID(opt)) {
                        System.err.println("error: please write a correct ID for the figure\"");
                        continue;
                    }
                    continue;
                case "4":
                    return null;
                default:
                    System.err.println("Invalid figure option");
                    continue;
            }
        }

    }

    public Msg handleAction(int msg_tag, Scanner sc) { // new dib del dib add dib new fig list fig ...
        while (true) {
            System.out.println("1. Draw");
            System.out.println("2. Figure");
            System.out.println("3. Exit");
            System.out.print("> ");
            String opt = sc.nextLine();

            if (!verifyAct(opt, 3)) {
                System.err.println("error: please select one of the options writing the number associate to");
                continue;

            }
            Msg m;
            switch (opt) {
                case "1":
                    m = handleDraw(sc, msg_tag);
                    if (m==null) {
                        continue;
                    }
                    return m;
                case "2":
                    m = handleFigure(sc, msg_tag);
                    if(m==null) {
                        continue;
                    }
                    return m;

                case "3":
                    return new Texit(msg_tag, null);
                default:
                    System.err.println("Invalid option");
                    continue;
            }
        }
    }




    public void run() {
        Scanner sc = new Scanner(System.in);
        Thread listener = new Thread(this::listener);
        listener.start();
        try {
            while (running.get()) {
                System.out.println("Select an action to send to the server (choose Exit to quit):");
                req.r = handleAction(msg_tag, sc);
                if (req.r.kind == Texit) {
                    running.set(false);
                    req.r.writeTo(sck);
                    try {
                        sck.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }
                req.r.writeTo(sck);
                msg_tag += 1;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (sck != null && sck.isOpen()) {
                    sck.close();
                }
            } catch (IOException e) {
                if (running.get()) {
                    e.printStackTrace();
                }

            }
            sc.close();
        }

    }

    private void listener() {
        try {
            while (running.get() && sck.isOpen()) {
                req.readFrom(sck);
                if (req.m != null) {
                    System.out.println("\nMSG SERVER: " + req.m.toString());
                    System.out.print("> ");
                } else {
                    System.out.println("\nINFO: server has finished the connection");
                    sck.close();
                    running.set(false);
                    System.exit(0);
                    return;
                }
            }
        } catch (Exception e) {
            if (running.get()) {
                e.printStackTrace();
            }
            System.out.println("Connection finished");
            running.set(false);
        }
    }
}

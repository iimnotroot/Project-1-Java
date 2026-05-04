package org.dmoreno.server;

import static org.dmoreno.server.Msg.*;

import org.dmoreno.draw.Draw;
import org.dmoreno.figures.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DibSvc implements Svc{

    private final HashMap<Integer, Draw> dibs;
    private static final String PATH = "/home/dontlookatme/IdeaProjects/Pj1/src/org/dmoreno/server/dibs/";
    private final HashMap<Integer, Figure> figs;
    private final AtomicInteger id_figs = new AtomicInteger(0);
    private final AtomicInteger id_dibs = new AtomicInteger(0);
    public DibSvc() {
        dibs = new HashMap<Integer, Draw>();
        figs = new HashMap<Integer, Figure>();
    }

    public void init(){

    }
    public void term(){

    }
    public void newClient(String name) {

    }
    public void closeClient(String name) {

    }

    private Integer verifyID(String str) {
        Integer id;
        if (str == null || !str.matches("\\d+")) {
            return null;
        }
        try {
            id = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
        return id;
    }

    private Integer[] parseFigureIds(String figs) { // Examples: 1-5-6 or 1-4-7-5 or 4
        String[] id_str = figs.split("-");
        Integer[] figs_ids = new Integer[id_str.length];
        int i;
        try {
            for (i=0; i<id_str.length; i++) {
                figs_ids[i] = Integer.parseInt(id_str[i]);
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
        return figs_ids;
    }

    private Integer newDib(String request) {
        String[] request_splited = request.split(" "); // figures id from hashmap to include in draw
        Draw dib;
        Integer dibid;
        try {
            dibid = id_dibs.get();
            dib = new Draw(PATH + "Dib" + dibid);
        } catch (Exception e) {
            return -1;
        }
        synchronized (dibs) {
            dibs.put(id_dibs.getAndIncrement(), dib);
            dib.setName(String.format("Dib%d", dibid));
        }
        return dibid;

    }


    private Integer newFig(String request) { // Tnewfig <Figure> <arguments>
        Figure fig;
        Integer figid;
        try {
            fig = Figure.parse(request);
        } catch (Exception e) {
            return -1;
        }
        if (fig != null) {
            synchronized (figs) {
                figid = id_figs.get();
                fig.setId(figid);
                figs.put(id_figs.getAndIncrement(), fig);
            }
            return figid;
        } else {
            return -1;
        }
    }

    private List<Figure> listFigs(String request) {
        Integer[] figs_ids;
        List<Figure> lFigs = new ArrayList<>();
        if ("*".equals(request)) {
            synchronized (figs) {
                return new ArrayList<>(figs.values());
            }
        }
        try {
            figs_ids = parseFigureIds(request);
        } catch (RuntimeException e) {
            System.err.println("WARNING: invalid ID");
            return null;
        }

        synchronized (figs){
            int i;
            for (i=0; i< figs_ids.length; i++) {
                boolean exists = figs.containsKey(figs_ids[i]);
                if (!exists) {
                    System.err.println("WARNING: Figure with ID "+ figs_ids[i] + " does not exists");
                    return null;
                }
                lFigs.add(figs.get(figs_ids[i]));
            }
        }

        return lFigs;

    }

    private Integer delDib(String request) {
        String[] args = request.split(" ");
        Integer dib = verifyID(args[0]);
        if (dib == null) {
            return -1;
        }
        synchronized (dibs) {
            boolean exists = dibs.containsKey(dib);
            if (!exists) {
                return -1;
            }
            dibs.remove(dib);
        }

        return dib;
    }

    private Integer delFig(String request) {
        String[] args = request.split(" ");
        Integer fig = verifyID(args[0]);
        if (fig == null) {
            return -1;
        }
        synchronized (figs) {
            boolean exists = figs.containsKey(fig);
            if (!exists) {
                return -1;
            }
            figs.remove(fig);
        }

        return fig;
    }


    private Integer addDib(String request) {
        String[] args = request.split(" ");
        Integer dib = verifyID(args[0]);
        if (dib == null) {
            return -1;
        }
        List<Figure> toAdd = new ArrayList<>();
        Integer[] figs_ids;
        try {
            figs_ids = parseFigureIds(args[1]);
        } catch (RuntimeException e) {
            return -1;
        }
        Draw draw;
        synchronized (dibs) {
            boolean exists = dibs.containsKey(dib);
            if (!exists) {
                return -1;
            }
            draw = dibs.get(dib);
        }

        synchronized (figs){
            int i;
            for (i=0; i< figs_ids.length; i++) {
                boolean exists = figs.containsKey(figs_ids[i]);
                if (!exists) {
                    return -1;
                }
                toAdd.add(figs.get(figs_ids[i]));
            }
        }

        synchronized (draw) {
            for (Figure f : toAdd) {
                draw.add(f);
            }
        }

        return dib;
    }

    private Integer sketchdib(String request) {
        String[] args = request.split(" ");
        Integer dib = verifyID(args[0]);
        Draw draw;
        if (dib == null) {
            return -1;
        }

        synchronized (dibs) {
            boolean exists = dibs.containsKey(dib);
            if (!exists) {
                return -1;
            }
            draw = dibs.get(dib);
            draw.sketch();
        }

        return dib;
    }

    private static StringBuilder Readfrom(File file) {
        StringBuilder list = new StringBuilder();
        BufferedReader rd = null;
        try {
            InputStream in = new FileInputStream(file);
            rd = new BufferedReader(new InputStreamReader(in));
            while (true) {
                try {
                    var line = rd.readLine();
                    if (line == null) {
                        break;
                    }
                    list.append(line);
                    list.append('\n');

                } catch (Exception e) {
                    return null;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (rd != null) {
                try {
                    rd.close();
                } catch (Exception e) {
                    throw new RuntimeException("error: Buffer Reader could not finish");
                }
            }
        }
        return list;
    }

    private StringBuilder listDib(String request) {
        String[] args = request.split(" ");
        Integer dib = verifyID(args[0]);
        Draw draw;
        StringBuilder list;
        if (dib == null) {
            return null;
        }

        synchronized (dibs) {
            boolean exists = dibs.containsKey(dib);
            if (!exists) {
                return null;
            }
            draw = dibs.get(dib);;
            String dib_path = PATH + draw.getName();
            File file = new File(dib_path);
            list = Readfrom(file);
        }
        return list;
    }


    public Msg handle(Msg req) {
        Integer id;
        switch (req.kind) {
            case Tnewdib:
                id = newDib(req.getStr());
                if (id < 0) {
                    return new Rerror("error: draw creation failed", req.buf);
                }
                return new Rnewdib(id, req.buf);
            case Tnewfig:
                id = newFig(req.getStr());
                if (id < 0) {
                    return new Rerror("error: figure creation failed", req.buf);
                }
                return new Rnewfig(id, req.buf);
            case Tlistfigs:
                List<Figure> lFigs = listFigs(req.getStr());

                if (lFigs == null) {
                    return new Rerror("error: invalid figure list", req.buf);
                }

                StringBuilder msg = new StringBuilder();

                for (int i = 0; i < lFigs.size(); i++) {
                    msg.append(lFigs.get(i).toString());
                    if (i < lFigs.size() - 1) {
                        msg.append(", ");
                    }
                }

                return new Rlistfigs(msg.toString(), req.buf);
            case Tdeldib:
                id = delDib(req.getStr());
                if (id < 0) {
                    return new Rerror("error: draw do not exists", req.buf);
                }
                return new Rdeldib(id, req.buf);
            case Tadddib:
                id = addDib(req.getStr());
                if (id < 0) {
                    return new Rerror("error: could not add figs to the draw", req.buf);
                }
                return new Radddib(id, req.buf);
            case Tsketchdib:
                id = sketchdib(req.getStr());
                if (id < 0) {
                    return new Rerror("error: could not sketch draw", req.buf);
                }
                return new Rsketchdib(id, req.buf);
            case Tlistdib:
                StringBuilder list = listDib(req.getStr());
                if (list == null) {
                    return new Rerror("error: could not list draw", req.buf);
                }
                return new Rlistdib(list.toString(), req.buf);
            case Tdelfig:
                id = delFig(req.getStr());
                if (id < 0) {
                    return new Rerror("error: draw do not exists", req.buf);
                }
                return new Rdelfig(id, req.buf);
            case Texit:
                return new Texit(0, req.buf);
            default:
                return new Rerror("error: command not supported", req.buf);
        }
    }
}

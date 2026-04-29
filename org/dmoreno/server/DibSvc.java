package org.dmoreno.server;

import static org.dmoreno.server.Msg.*;

import org.dmoreno.draw.Draw;
import org.dmoreno.figures.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DibSvc implements Svc{

    private final HashMap<Integer, Draw> dibs;
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

    private Integer newDib(String request) {
        String[] request_splited = request.split(" "); // figures id from hashmap to include in draw
        Draw dib;
        Integer dibid;
        try {
            dib = new Draw(".");
        } catch (Exception e) {
            return -1;
        }
        if (dib != null) {
            synchronized (dibs) {
                dibid = id_dibs.get();
                dibs.put(id_dibs.getAndIncrement(), dib);
            }
            return dibid;
        } else {
            return -1;
        }

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
                figs.put(id_figs.getAndIncrement(), fig); // maybe client should say which id is the key
            }
            return figid;
        } else {
            return -1;
        }
    }

    private void listFigs(String request) {
        Figure[] figs;


    }

    private Integer delDib(String request) {
        String[] args = request.split(" ");
        Integer dib = Integer.parseInt(args[0]);
        synchronized (dibs) {
            boolean exists = dibs.containsKey(dib);
            if (!exists) {
                return -1;
            }
            dibs.remove(dib);
        }

        return dib;
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

    private Integer addDib(String request) {
        String[] args = request.split(" ");
        Integer dib = Integer.parseInt(args[0]);
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
                listFigs(req.getStr());
                return new Rlistfigs("Figs: circle1 10 20 5, Rect1 10 15 50 20", req.buf);
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
            case Texit:
                return new Texit(0, req.buf);
            default:
                return new Rerror("error: command not supported", req.buf);
        }
    }
}

package org.dmoreno.server;

import static org.dmoreno.server.Msg.*;

import org.dmoreno.draw.Draw;
import org.dmoreno.figures.*;
import org.dmoreno.group.Group;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DibSvc implements Svc{

    private final HashMap<Integer, Draw> dibs;
    private static final String PATH = "/home/dontlookatme/IdeaProjects/Pj1/src/org/dmoreno/server/dibs/";
    private final HashMap<Integer, Figure> figs;
    private final HashMap<Integer, Group> grps;
    private final AtomicInteger id_figs = new AtomicInteger(0);
    private final AtomicInteger id_dibs = new AtomicInteger(0);
    private final AtomicInteger id_grps = new AtomicInteger(0);
    private final Object stateLock = new Object(); // To avoid race conditions


    public DibSvc() {
        dibs = new HashMap<>();
        figs = new HashMap<>();
        grps = new HashMap<>();
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
            throw e;
        }
        return id;
    }

    private Integer[] parseFigureIds(String figs) { // Examples: 1-5-6 or 1-4-7-5 or 4
        String[] id_str = figs.split("-");
        Integer[] figs_ids = new Integer[id_str.length];
        int i;
        for (i=0; i<id_str.length; i++) {
            figs_ids[i] = Integer.parseInt(id_str[i]);
        }
        return figs_ids;
    }

    private Integer newDib() {
        synchronized (stateLock) {
            try {
                Integer draw_id = id_dibs.get();
                Draw draw = new Draw(PATH + "Dib" + draw_id);
                dibs.put(id_dibs.getAndIncrement(), draw);
                draw.setName(String.format("Dib%d", draw_id));
                return draw_id;
            } catch (Exception e) {
                throw e;
            }
        }

    }


    private Integer newFig(String request) { // Tnewfig <Figure> <arguments>
        try {
            synchronized (stateLock) {
                Figure fig = Figure.parse(request);
                if (fig != null) {
                    Integer figid = id_figs.get();
                    fig.setId(figid);
                    figs.put(id_figs.getAndIncrement(), fig);
                    return figid;
                } else {
                    throw new RuntimeException("error: fig is null");
                }
            }
        } catch (RuntimeException e) {
            throw e;
        }

    }

    private List<Figure> listFigs(String request) {
        try {
            synchronized (stateLock) {
                List<Figure> lFigs = new ArrayList<>();
                if ("*".equals(request)) {
                    return new ArrayList<>(figs.values());
                }
                Integer[] figs_ids = parseFigureIds(request);
                int idx;
                for (idx=0; idx< figs_ids.length; idx++) {
                    boolean exists = figs.containsKey(figs_ids[idx]);
                    if (!exists) {
                        throw new RuntimeException("error: Figure with ID "+ Integer.toString(figs_ids[idx]) + " does not exists");
                    }
                    lFigs.add(figs.get(figs_ids[idx]));
                }
                return lFigs;
            }
        } catch (RuntimeException e) {
            throw e;
        }
    }

    private List<Group> listGrps(String request) {
        try {
            synchronized (stateLock) {
                if ("*".equals(request)) {
                    return new ArrayList<>(grps.values());
                }
                Integer[] grps_ids = parseFigureIds(request);
                List<Group> lGrps = new ArrayList<>();
                int i;
                for (i=0; i< grps_ids.length; i++) {
                    boolean exists = grps.containsKey(grps_ids[i]);
                    if (!exists) {
                        throw new RuntimeException("error: Group with ID "+ Integer.toString(grps_ids[i]) + " does not exists");
                    }
                    lGrps.add(grps.get(grps_ids[i]));
                }
                return lGrps;
            }
        } catch (RuntimeException e) {
            throw e;
        }
    }

    private Integer delDib(String request) {
        try {
            synchronized (stateLock) {
                String[] args = request.split(" ");
                Integer dib = verifyID(args[0]);
                if (dib == null) {
                    throw new RuntimeException("error: ID draw incorrect");
                }
                boolean exists = dibs.containsKey(dib);
                if (!exists) {
                    throw new RuntimeException("error: draw does not exists");
                }
                dibs.remove(dib);
                return dib;
            }
        } catch (RuntimeException e) {
            throw e;
        }

    }

    private Integer delFig(String request) {
        try {
            synchronized (stateLock) {
                String[] args = request.split(" ");
                Integer fig_id = verifyID(args[0]);
                Figure fig;
                if (fig_id == null) {
                    throw new RuntimeException("error: Figure ID is incorrect");
                }
                boolean exists = figs.containsKey(fig_id);
                if (!exists) {
                    throw new RuntimeException("error: figure does not exists");
                }
                fig = figs.get(fig_id);
                figs.remove(fig_id);
                for (Draw dib: dibs.values()) {
                    if (dib.contains(fig)) {
                        dib.drop(fig);
                    }
                }
                for (Group grp: grps.values()) {
                    if (grp.contains(fig)) {
                        grp.drop(fig);
                    }
                }
                return fig_id;
            }
        } catch (RuntimeException e) {
            throw e;
        }
    }

    private Integer delGrp(String request) {
        try {
            synchronized (stateLock) {
                String[] args = request.split(" ");
                Integer grp_id = verifyID(args[0]);
                Group grp;
                if (grp_id == null) {
                    throw new RuntimeException("error: Group ID is incorrect");
                }
                boolean exists = grps.containsKey(grp_id);
                if (!exists) {
                    throw new RuntimeException("error: group does not exists");
                }
                grp = grps.get(grp_id);
                grps.remove(grp_id);
                for (Draw dib: dibs.values()) {
                    if (dib.contains(grp)) {
                        dib.drop(grp);
                    }
                }
                return grp_id;
            }
        } catch (RuntimeException e) {
            throw e;
        }
    }


    private Integer addFigDib(String request) {
        try {
            synchronized (stateLock) {
                String[] args = request.split(" ");
                Integer draw_id = verifyID(args[0]);
                if (draw_id == null) {
                    throw new RuntimeException("error: Draw ID is incorrect");
                }
                List<Figure> toAdd = new ArrayList<>();
                Integer[] figs_ids = parseFigureIds(args[1]);
                boolean exists = dibs.containsKey(draw_id);
                if (!exists) {
                    throw new RuntimeException("error: Draw does not exists");
                }
                Draw draw = dibs.get(draw_id);
                int idx;
                for (idx=0; idx< figs_ids.length; idx++) {
                    boolean figexists = figs.containsKey(figs_ids[idx]);
                    if (!figexists) {
                        throw new RuntimeException("error: Figure with ID "+ Integer.toString(figs_ids[idx]) + "does not exists");
                    }
                    toAdd.add(figs.get(figs_ids[idx]));
                }
                for (Figure f : toAdd) {
                    draw.add(f);
                }
                return draw_id;
            }
        } catch (RuntimeException e) {
            throw e;
        }
    }

    private Integer addGrpDib(String request) {
        try {
            synchronized (stateLock) {
                String[] args = request.split(" ");
                Integer draw_id = verifyID(args[0]);
                if (draw_id == null) {
                    throw new RuntimeException("error: Draw ID is incorrect");
                }
                List<Group> toAdd = new ArrayList<>();
                Integer[] grps_ids = parseFigureIds(args[1]);
                boolean drawexists = dibs.containsKey(draw_id);
                if (!drawexists) {
                    throw new RuntimeException("error: Draw does not exists");
                }
                Draw draw = dibs.get(draw_id);
                int idx;
                for (idx=0; idx< grps_ids.length; idx++) {
                    boolean grpexists = grps.containsKey(grps_ids[idx]);
                    if (!grpexists) {
                        throw new RuntimeException("error: Group with ID " + Integer.toString(grps_ids[idx]) + " does not exists");
                    }
                    toAdd.add(grps.get(grps_ids[idx]));
                }
                for (Group grp : toAdd) {
                    draw.add(grp);
                }
                return draw_id;
            }
        } catch (RuntimeException e) {
            throw e;
        }
    }

    private Integer addFigGrp(String request) {
        try {
            synchronized (stateLock) {
                String[] args = request.split(" ");
                Integer grp_id = verifyID(args[0]);
                if (grp_id == null) {
                    throw new RuntimeException("error: Group ID is incorrect");
                }
                List<Figure> toAdd = new ArrayList<>();
                Integer[] figs_ids = parseFigureIds(args[1]);
                boolean grpexists = grps.containsKey(grp_id);
                if (!grpexists) {
                    throw new RuntimeException("error: Group does not exists");
                }
                Group group = grps.get(grp_id);
                int idx;
                for (idx=0; idx< figs_ids.length; idx++) {
                    boolean figexists = figs.containsKey(figs_ids[idx]);
                    if (!figexists) {
                        throw new RuntimeException("error: Figure with ID " + Integer.toString(figs_ids[idx]) + " does not exists");
                    }
                    toAdd.add(figs.get(figs_ids[idx]));
                }
                for (Figure fig: toAdd) {
                    group.add(fig);
                }
                return grp_id;

            }
        } catch (RuntimeException e) {
            throw e;
        }
    }



    private Integer sketchdib(String request) {
        try {
            synchronized (stateLock) {
                String[] args = request.split(" ");
                Integer draw_id = verifyID(args[0]);
                Draw draw;
                if (draw_id == null) {
                    throw new RuntimeException("error: Draw ID is incorrect");
                }

                boolean exists = dibs.containsKey(draw_id);
                if (!exists) {
                    throw new RuntimeException("error: Draw does not exists");
                }
                draw = dibs.get(draw_id);
                draw.sketch();

                return draw_id;
            }
        } catch (RuntimeException e) {
            throw e;
        }

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
                    list.append('-');
                    list.append(line);
                    list.append('\n');

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (rd != null) {
                try {
                    rd.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return list;
    }

    private StringBuilder listDib(String request) {
        try {
            synchronized (stateLock) {
                String[] args = request.split(" ");
                Integer draw_id = verifyID(args[0]);
                Draw draw;
                StringBuilder list;
                if (draw_id == null) {
                    throw new RuntimeException("error: Draw ID is incorrect");
                }
                boolean exists = dibs.containsKey(draw_id);
                if (!exists) {
                    throw new RuntimeException("error: Draw does not exists");
                }
                draw = dibs.get(draw_id);
                String dib_path = PATH + draw.getName();
                File file = new File(dib_path);
                list = Readfrom(file);

                return list;
            }
        } catch (RuntimeException e) {
            throw e;
        }
    }

    private Integer newGrp(String request) {
        try {
            synchronized (stateLock) {
                List<Figure> lFigs = new ArrayList<>();
                if ("*".equals(request)) {
                    lFigs = new ArrayList<>(figs.values());
                } else {
                    Integer[] figs_ids = parseFigureIds(request);
                    int idx;
                    for (idx=0; idx< figs_ids.length; idx++) {
                        boolean exists = figs.containsKey(figs_ids[idx]);
                        if (!exists) {
                            throw new RuntimeException("error: figure" + Integer.toString(figs_ids[idx]) + "do not exists");
                        }
                        lFigs.add(figs.get(figs_ids[idx]));
                    }
                }
                Integer id_grp = id_grps.get();
                Group grp = new Group(id_grp, lFigs.toArray(new Figure[0]));
                grp.setId(id_grp);
                grps.put(id_grps.getAndIncrement(), grp);

                return id_grp;
            }
        } catch (RuntimeException e) {
            throw e;
        }
    }

    public Msg handle(Msg req, ByteBuffer repbuf) {
        Integer id;
        try {
            switch (req.kind) {
                case Tnewdib:
                    id = newDib();
                    if (id < 0) {
                        return new Rerror("error: draw creation failed", repbuf);
                    }
                    return new Rnewdib(id, req.buf);
                case Tnewfig:
                    id = newFig(req.getStr());
                    if (id < 0) {
                        return new Rerror("error: figure creation failed", repbuf);
                    }
                    return new Rnewfig(id, req.buf);
                case Tlistfigs:
                    List<Figure> lFigs = listFigs(req.getStr());

                    StringBuilder msg = new StringBuilder();

                    for (int i = 0; i < lFigs.size(); i++) {
                        msg.append(lFigs.get(i).toString());
                        if (i < lFigs.size() - 1) {
                            msg.append("\n");
                        }
                    }

                    return new Rlistfigs(msg.toString(), repbuf);
                case Tdeldib:
                    id = delDib(req.getStr());
                    if (id < 0) {
                        return new Rerror("error: draw do not exists", repbuf);
                    }
                    return new Rdeldib(id, repbuf);
                case Taddfigdib:
                    id = addFigDib(req.getStr());
                    if (id < 0) {
                        return new Rerror("error: could not add figs to the draw", repbuf);
                    }
                    return new Raddfigdib(id, repbuf);
                case Taddgrpdib:
                    id = addGrpDib(req.getStr());
                    if (id < 0) {
                        return new Rerror("error: could not add figs to the draw", repbuf);
                    }
                    return new Raddgrpdib(id, repbuf);
                case Taddfiggrp:
                    id = addFigGrp(req.getStr());
                    if (id < 0) {
                        return new Rerror("error: could not add figs to the group", repbuf);
                    }
                    return new Raddfiggrp(id, repbuf);
                case Tsketchdib:
                    id = sketchdib(req.getStr());
                    if (id < 0) {
                        return new Rerror("error: could not sketch draw", repbuf);
                    }
                    return new Rsketchdib(id, repbuf);
                case Tlistdib:
                    StringBuilder list = listDib(req.getStr());
                    return new Rlistdib(list.toString(), repbuf);
                case Tdelfig:
                    id = delFig(req.getStr());
                    if (id < 0) {
                        return new Rerror("error: draw do not exists", repbuf);
                    }
                    return new Rdelfig(id, repbuf);
                case Tnewgrp:
                    id = newGrp(req.getStr());
                    if (id < 0) {
                        return new Rerror("error: could not created new group", repbuf);
                    }
                    return new Rnewgrp(id, repbuf);
                case Tdelgrp:
                    id = delGrp(req.getStr());
                    if (id < 0) {
                        return new Rerror("error: could not delete the group", repbuf);
                    }
                    return new Rdelgrp(id, repbuf);
                case Tlistgrp:
                    List<Group> lGrps = listGrps(req.getStr());

                    StringBuilder msg_grp = new StringBuilder();

                    for (int i = 0; i < lGrps.size(); i++) {
                        msg_grp.append(lGrps.get(i).toString());
                        if (i < lGrps.size() - 1) {
                            msg_grp.append("\n");
                        }
                    }

                    return new Rlistgrp(msg_grp.toString(), repbuf);
                case Texit:
                    return new Texit(0, repbuf);
                default:
                    return new Rerror("error: command not supported", repbuf);
            }
        } catch (RuntimeException e) {
            return new Rerror(e.getMessage(),repbuf);
        }
    }
}

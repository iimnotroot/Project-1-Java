package org.dmoreno.server;

import static org.dmoreno.server.Msg.Rerror;
import static org.dmoreno.server.Msg.Tnewdib;

import org.dmoreno.draw.Draw;
import org.dmoreno.figures.*;

import java.util.HashMap;
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

    private void newDib(String request) {

    }


    private void newFig(String request) { // Tnewfig <Figure> <arguments>
        Figure fig;
        try {
            fig = Figure.parse(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (fig != null) {
            synchronized (figs) {
                figs.put(id_figs.getAndIncrement(), fig); // maybe client should say which id is the key
            }
        }
    }

    private void getArgs(String request) {
        int idx = request.indexOf(" ");
        if (idx != -1) {
            request = request.substring(idx + 1).trim();
            System.out.println(request);
        } else {
            throw new RuntimeException("command not supported");
        }
    }

    public Msg handle(Msg req) {
        String request = req.getStr();
        String comm = request.split(" ")[0];
        try {
            getArgs(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        switch (comm) {
            case "Tnewdib":
                newDib(request);
            case "Tnewfig":
                newFig(request);
            default:
                return new Rerror("error: command not supported", req.buf);
        }
    }
}

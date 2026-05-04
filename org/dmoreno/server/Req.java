package org.dmoreno.server;

import java.nio.channels.SocketChannel;

public class Req {
    public int tag;
    public Msg m; // request msg
    public Msg r; // reply msg


    public Req(){}

    public Req(Msg m) {
        this.m = m;
        tag = m.getTag();
    }

    public void readFrom(SocketChannel ch) {
        Msg req = Msg.readFrom(ch);
        if (req != null) {
            m = req;
            tag = m.getTag();
        }
    }

    public void reply(Msg rep) {
        if (r != null) {
            return;
        }
        rep.setTag(tag);
        this.r = rep;

    }

    public void reset() {
        this.m = null;
        this.r = null;
    }

    public void error(String s) {
        reply(new Msg.Rerror(s, r.buf));
    }
}

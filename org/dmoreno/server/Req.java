package org.dmoreno.server;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;

public class Req {
    public int tag;
    public Msg m; // request msg
    public Msg r; // reply msg

    public Req(){
        ByteBuffer bufM = ByteBuffer.allocate(Msg.BUFFSIZE);
        this.m = new Msg(bufM, false);
        ByteBuffer bufR = ByteBuffer.allocate(Msg.BUFFSIZE);
        this.r = new Msg(bufR, false);

        bufM.order(ByteOrder.LITTLE_ENDIAN);
        bufR.order(ByteOrder.LITTLE_ENDIAN);
    }

    public Req(Msg m) {
        this.m = m;
        tag = m.getTag();
    }

    public void readFrom(SocketChannel ch) {
        try {
            Msg req = Msg.readFrom(ch, m);
            if (req != null) {
                m = req;
                tag = m.getTag();
            } else {
                m = null;
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }

    public void reply(Msg rep) {
        rep.setTag(tag);
        this.r = rep;

    }

    public void reset() {
        this.tag = 0;
        if (m!=null) {
            m.reset();
        }
        if (r!=null) {
            r.reset();
        }
    }
}

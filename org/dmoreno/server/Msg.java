package org.dmoreno.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

public class Msg {

    public static final int BUFFSIZE = 4096;

    public static final int Tflush = 20; // req[4]
    public static final int Rflush = 21; //

    public static final int Texit = 22;

    public static final int Tvers = 34; // vers[s] nick[s] buffsize[int]
    public static final int Rvers = 35; // vers[s] buffsize[int]
    public static final int Rerror = 41; // msg[s]
    public static final int Tnewdib = 50; // name[s]
    public static final int Rnewdib = 51; // id[4] 4 bytes en little endian
    public static final int Tdeldib = 52; // id[4]
    public static final int Rdeldib = 53; //
    public static final int Tlistdib = 54; // pos[4] n[4]
    public static final int Rlistdib = 55; // n[4] name[s] ...
    public static final int Tlistfigs = 56; // dib[4] pos[4] n[4]
    public static final int Rlistfigs = 57; // n[4] id[4] ...
    public static final int Tnewfig = 58; // args[s]
    public static final int Rnewfig = 59; // id[4]
    public static final int Tadddib = 60; //id_dib[4] figs[s](id_figs split with '-')
    public static final int Radddib = 61;


    public int tag;
    public int kind;
    public int dsize;

    private static final int TAGOFF = 0;
    private static final int KINDOFF = TAGOFF + 4;
    private static final int DSIZEOFF = KINDOFF + 4;
    private static final int HDRSIZE = DSIZEOFF + 4;

    ByteBuffer buf;

    public Msg() {
        buf = ByteBuffer.allocate(BUFFSIZE);
        buf.order(ByteOrder.LITTLE_ENDIAN);
    }

    protected Msg(ByteBuffer buf, boolean unpack) {
        if (buf == null) {
            buf = ByteBuffer.allocate(BUFFSIZE);
            buf.order(ByteOrder.LITTLE_ENDIAN);
        }
        this.buf = buf;
        buf.clear();
        buf.order(ByteOrder.LITTLE_ENDIAN);
        if (unpack) {
            getTag();
            getKind();
            getDsize();
        }
    }


    public void setTag(int tag) {
        this.tag = tag;
        buf.putInt(TAGOFF, tag);
    }

    public int getTag() {
        this.tag = buf.getInt(TAGOFF);
        return this.tag;
    }

    public void setKind(int kind) {
        this.kind = kind;
        buf.putInt(KINDOFF, kind);
    }

    public int getKind() {
        this.kind = buf.getInt(KINDOFF);
        return this.kind;
    }

    public void setDsize(int dsize) {
        this.dsize = dsize;
        buf.putInt(DSIZEOFF, dsize);
    }

    public int getDsize() {
        this.dsize = buf.getInt(DSIZEOFF);
        return this.dsize;
    }

    protected void makeHdr(int newtag, int kind) {
        buf.clear();
        setTag(newtag);
        setKind(kind);
        setDsize(0);
        buf.position(HDRSIZE);
        buf.limit(buf.capacity());

    }

    protected void addStr(String s) {
        byte[] b = s.getBytes();
        buf.putInt(b.length);
        buf.put(b);
    }


    protected void rdMode() {
        buf.position(0);
        buf.limit(HDRSIZE + dsize);
    }

    protected void dataDone() {
        int size = buf.position() - HDRSIZE;
        setDsize(size);
        rdMode();
    }

    public static int readn(ReadableByteChannel ch, ByteBuffer buf, int n) {
        buf.limit(buf.position() + n);
        try {
            int total = 0;
            while (total < n) {
                int br = ch.read(buf);
                if (br < 0) {
                    break;
                }
                total += br;
            }
            return total;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    boolean readHdr(ReadableByteChannel ch) {
        rdMode();
        int br = readn(ch, buf, HDRSIZE);
        if (br <= 0) {
            return false;
        }
        if (br != HDRSIZE) {
            throw new RuntimeException("error: truncated header");
        }
        getTag();
        getKind();
        getDsize();
        return true;
    }

    void readData(ReadableByteChannel ch) {
        if (dsize > 0) {
            buf.position(HDRSIZE);
            int nr = readn(ch, buf, dsize);
            if (nr != dsize) {
                throw new RuntimeException("truncated msg body in readmsg");
            }
            rdMode();
        }
    }


    public String getStr() {
        buf.position(HDRSIZE);
        int str_size = buf.getInt();
        byte[] b = new byte[str_size];
        if (str_size > 0) {
            buf.get(b);
            return new String(b);
        } else {
            return null;
        }
    }


    public static Msg readFrom(ReadableByteChannel ch) {
        Msg msg = new Msg();
        if (!msg.readHdr(ch)) {
            return null;
        }
        msg.readData(ch);
        switch (msg.kind) {
            case Rerror:
                return new Rerror(msg.buf);
            case Tnewdib:
                return new Tnewdib(msg.buf);
            case Rnewdib:
                return new Rnewdib(msg.buf);
            case Tnewfig:
                return new Tnewfig(msg.buf);
            case Rnewfig:
                return new Rnewfig(msg.buf);
            case Tlistfigs:
                return new Tlistfigs(msg.buf);
            case Rlistfigs:
                return new Rlistfigs(msg.buf);
            case Tdeldib:
                return new Tdeldib(msg.buf);
            case Rdeldib:
                return new Rdeldib(msg.buf);
            case Tadddib:
                return new Tadddib(msg.buf);
            case Radddib:
                return new Radddib(msg.buf);
            case Texit:
                return new Texit(msg.buf);
            default:
                throw new RuntimeException("unknown msg kind");
        }
    }


    public void writeTo(WritableByteChannel ch) {
        rdMode();
        try {
            ch.write(buf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            rdMode();
        }
    }


    public String toString() {
        return tag + " " + kind + " " + dsize;
    }


    public static class Rerror extends Msg {

        String msg;

        public Rerror(String msg, ByteBuffer bufa) {
            super(bufa, false);
            buf.clear();
            this.msg = msg;
            makeHdr(0, Rerror);
            addStr(msg);
            dataDone();
        }

        public Rerror(ByteBuffer buf) {
            super(buf, true);
            try {
                rdMode();
                buf.position(HDRSIZE);
                msg = getStr();
            } finally {
                rdMode();
            }

        }

        public String toString() {
            return super.toString() + " " + msg;
        }

    }

    public static class Tnewdib extends Msg {
        String name;

        public Tnewdib(String name, int msg_tag, ByteBuffer bufa) {
            super(bufa, false);
            buf.clear();
            this.name = name;
            makeHdr(msg_tag, Tnewdib);
            addStr(name);
            dataDone();
        }

        public Tnewdib(ByteBuffer bufa) {
            super(bufa, true);
            try {
                rdMode();
                buf.position(HDRSIZE);
                name = getStr();
            } finally {
                rdMode();
            }
        }

        public String toString() {
            return super.toString() + " " + name;
        }
    }

    public static class Rnewdib extends Msg {
        int id;

        public Rnewdib(int id, ByteBuffer bufa) {
            super(bufa, false);
            buf.clear();
            this.id = id;
            makeHdr(0, Rnewdib);
            addStr(Integer.toString(id));

            dataDone();
        }

        public Rnewdib(ByteBuffer buf) {
            super(buf, true);
            try {
                rdMode();
                buf.position(HDRSIZE);
                id = Integer.parseInt(getStr());
            } finally {
                rdMode();
            }
        }

        public String toString() {
            return super.toString() + " " + Integer.toString(id);
        }
    }

    public static class Texit extends Msg {

        public Texit(int msg_tag, ByteBuffer bufa) {
            super(bufa, false);
            buf.clear();
            makeHdr(msg_tag, Texit);
            dataDone();
        }

        public Texit(ByteBuffer buf) {
            super(buf, true);
            try {
                rdMode();
                buf.position(HDRSIZE);
            } finally {
                rdMode();
            }
        }

        public String toString() {
            return super.toString();
        }
    }

    public static class Tnewfig extends Msg {
        String name;

        public Tnewfig(String name, int msg_tag, ByteBuffer bufa) {
            super(bufa, false);
            buf.clear();
            this.name = name;
            makeHdr(msg_tag, Tnewfig);
            addStr(name);
            dataDone();
        }

        public Tnewfig(ByteBuffer bufa) {
            super(bufa, true);
            try {
                rdMode();
                buf.position(HDRSIZE);
                name = getStr();
            } finally {
                rdMode();
            }
        }

        public String toString() {
            return super.toString() + " " + name;
        }


    }

    public static class Rnewfig extends Msg {
        int id;

        public Rnewfig(int id, ByteBuffer bufa) {
            super(bufa, false);
            buf.clear();
            this.id = id;

            makeHdr(0, Rnewfig);
            addStr(Integer.toString(id));
            dataDone();
        }

        public Rnewfig(ByteBuffer bufa) {
            super(bufa, true);
            try {
                rdMode();
                buf.position(HDRSIZE);
                id = Integer.parseInt(getStr());
            } finally {
                rdMode();
            }
        }

        public String toString() {
            return super.toString() + " " + id;
        }

    }

    public static class Tlistfigs extends Msg {
        String args;

        public Tlistfigs(String args, int msg_tag, ByteBuffer bufa) {
            super(bufa, false);
            buf.clear();
            makeHdr(msg_tag, Tlistfigs);
            this.args = args;
            addStr(args);
            dataDone();
        }

        public Tlistfigs(ByteBuffer bufa) {
            super(bufa, true);
            try {
                rdMode();
                buf.position(HDRSIZE);
                args = getStr();
            } finally {
                rdMode();
            }
        }

        public String toString() {
            return super.toString() + " " + args;
        }

    }

    public static class Rlistfigs extends Msg {
        String msg;

        public Rlistfigs(String msg, ByteBuffer bufa) {
            super(bufa, false);
            buf.clear();
            this.msg = msg;
            makeHdr(0, Rlistfigs);
            addStr(msg);
            dataDone();
        }

        public Rlistfigs(ByteBuffer bufa) {
            super(bufa, true);
            try {
                rdMode();
                buf.position(HDRSIZE);
                msg = getStr();
            } finally {
                rdMode();
            }
        }

        public String toString() {
            return super.toString() + " " + msg;
        }

    }

    public static class Tdeldib extends Msg {
        int id;

        public Tdeldib(int id, int msg_tag, ByteBuffer bufa) {
            super(bufa, false);
            buf.clear();
            this.id = id;
            makeHdr(msg_tag, Tdeldib);
            addStr(Integer.toString(id));
            dataDone();
        }

        public Tdeldib(ByteBuffer bufa) {
            super(bufa, true);
            try {
                rdMode();
                buf.position(HDRSIZE);
                id = Integer.parseInt(getStr());
                ;
            } finally {
                rdMode();
            }
        }

        public String toString() {
            return super.toString() + " " + id;
        }


    }

    public static class Rdeldib extends Msg {
        int id;

        public Rdeldib(int id, ByteBuffer bufa) {
            super(bufa, false);
            buf.clear();
            this.id = id;
            makeHdr(0, Rdeldib);
            addStr(Integer.toString(id));
            dataDone();
        }

        public Rdeldib(ByteBuffer bufa) {
            super(bufa, true);
            try {
                rdMode();
                buf.position(HDRSIZE);
                id = Integer.parseInt(getStr());
                ;
            } finally {
                rdMode();
            }
        }

        public String toString() {
            return super.toString() + " " + id;
        }


    }

    public static class Tadddib extends Msg {
        String args;
        public Tadddib(String args, int msg_tag, ByteBuffer bufa) {
            super(bufa, false);
            buf.clear();
            makeHdr(msg_tag, Tadddib);
            this.args = args;
            addStr(args);
            dataDone();
        }

        public Tadddib(ByteBuffer bufa) {
            super(bufa, true);
            try {
                rdMode();
                buf.position(HDRSIZE);
                args = getStr();
            } finally {
                rdMode();
            }
        }

        public String toString() {
            return super.toString() + " " + args;

        }

    }

    public static class Radddib extends Msg {
        int id;

        public Radddib(int id, ByteBuffer bufa) {
            super(bufa, false);
            buf.clear();
            this.id = id;
            makeHdr(0, Radddib);
            addStr(Integer.toString(id));
            dataDone();
        }

        public Radddib(ByteBuffer bufa) {
            super(bufa, true);
            try {
                rdMode();
                buf.position(HDRSIZE);
                id = Integer.parseInt(getStr());
            } finally {
                rdMode();
            }
        }

        public String toString() {
            return super.toString() + " " + "figures deleted of the dib " +id;
        }


    }


}


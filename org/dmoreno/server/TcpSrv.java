package org.dmoreno.server;

import org.dmoreno.server.Msg;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.dmoreno.server.Msg.*;


public class TcpSrv {
    ServerSocketChannel srv;
    final AtomicBoolean halting;
    Thread listener;
    protected Svc svc;
    static final HashMap<SocketChannel, Integer> hashcli;
    private final AtomicInteger id_client = new AtomicInteger(1);
    static final Queue<Req> buffsAval;

    static {
        hashcli = new HashMap<>();
        buffsAval = new LinkedList<>();
    }

    public TcpSrv(Svc svc, int port) {
        this.svc = svc;
        halting = new AtomicBoolean(false);
        try {
            srv = ServerSocketChannel.open();
            srv.bind(new InetSocketAddress(port));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void run() {
        System.out.println("Initializing server...");
        System.out.println("Waiting for connections...");
        try {
            listener = new Thread(this::listenerLoop); // Threads can be named with setName
            listener.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void closeSrv() {
        try {
            srv.close();
        } catch (Exception e) {
            //ignored
        }
    }

    public void hangup() {
        if (srv != null) {
            closeSrv();
            if (listener != null) {
                listener.interrupt();
            }
        }
    }

    public synchronized void halt() {
        halting.set(true);
        hangup();
    }

    public synchronized Integer addClient(SocketChannel sck) {
        int id = id_client.get();
        hashcli.put(sck, id_client.getAndIncrement());
        return id;
    }

    public void listenerLoop() {
        try {
            while (!halting.get()) {
                SocketChannel sck = srv.accept();
                if (halting.get()) {
                    sck.close();
                    break;
                }
                int id_c = addClient(sck);
                System.out.println("INFO: new connection accepted " + id_c);
                Client cli = new Client(sck, srv, id_c);
                cli.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeSrv();
        }
    }

    public synchronized void saveReq(Req req) {
        req.reset();
        buffsAval.offer(req);
    }

    public synchronized Req getReq() {
        if (buffsAval.isEmpty()) {
            return new Req();
        } else {
            return buffsAval.poll();
        }
    }

    private class Client extends Thread {
        ServerSocketChannel srv;
        SocketChannel sck;
        int id_c;
        public Client(SocketChannel sck, ServerSocketChannel srv, int id_c) {
            this.sck = sck;
            this.srv = srv;
            this.id_c = id_c;
        }

        public void run() {
            try {
                handle();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public void endCli() {
            try {
                System.out.println("INFO: finishing connection with " + id_c + " ...");
                sck.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void handle() {
            try {
                Req req = getReq();
                while(!halting.get()) {
                    try {
                        req.readFrom(sck);
                        if (req.m == null) {
                            saveReq(req);
                            return;
                        }
                        req.reply(svc.handle(req.m));
                        if (req.r.kind == Texit) {
                            saveReq(req);
                            return;
                        }
                        System.out.println("INFO: sending to client " + id_c + " reply: " + req.r.toString());
                        req.r.writeTo(sck);
                        req.reset();

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } finally {
                endCli();
            }
        }
    }
}


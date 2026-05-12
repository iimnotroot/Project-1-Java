package org.dmoreno.server;

import java.io.IOException;
import java.net.InetSocketAddress;
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
        synchronized (hashcli) {
            for (SocketChannel sck : hashcli.keySet()) {
                try {
                    sck.close();
                } catch (IOException e)
                {
                    //ignored
                }
            }
        }
        hashcli.clear();
    }

    private synchronized void removeClient(SocketChannel sck) {
        hashcli.remove(sck);
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
                System.out.println("INFO: new connection accepted client" + id_c);
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
                System.out.println("INFO: finishing connection with client" + id_c + " ...");
                removeClient(sck);
                if (sck.isOpen()) {
                    sck.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void handle() {
            Req req = getReq();
            try {
                while(!halting.get()) {
                    try {
                        req.readFrom(sck);
                        if (req.m == null) {
                            return;
                        }
                        req.reply(svc.handle(req.m, req.r.buf));
                        if (req.r.kind == Texit) {
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
                saveReq(req);
                endCli();
            }
        }
    }
}


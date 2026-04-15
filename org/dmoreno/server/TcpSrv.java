package org.dmoreno.server;

import org.dmoreno.server.Msg;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static jdk.internal.net.http.common.Utils.close;


public class TcpSrv {
    ServerSocketChannel srv;
    final AtomicBoolean halting;
    Thread listener;
    protected Svc svc;

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
        try {
            listener = new Thread(this::listenerLoop);
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

    public void listenerLoop() {
        try {
            while (!halting.get()) {
                SocketChannel sck = srv.accept();
                if (halting.get()) {
                    close(sck);
                    break;
                }
                Client cli = new Client(sck, srv);
                cli.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeSrv();
        }
    }

    private class Client extends Thread {
        ServerSocketChannel srv;
        SocketChannel sck;

        public Client(SocketChannel sck, ServerSocketChannel srv) {
            this.sck = sck;
            this.srv = srv;
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
                sck.finishConnect();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void handle() {
            try {
                Req r = new Req();
                while(!halting.get()) {
                    try {
                        r.readFrom(sck);
                        if (r.m == null) {
                            endCli();
                        }
                        r.reply(svc.handle(r.m));
                        r.r.writeTo(sck);
                    } catch (Exception e) {
                        //reply error? close connect?
                    }
                }
            } finally {
                endCli();
            }
        }
    }
}


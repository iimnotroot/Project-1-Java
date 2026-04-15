package org.dmoreno.server;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static jdk.internal.net.http.common.Utils.close;

public class EchoSrv {

    ServerSocket socket;
    final AtomicBoolean halting;
    Thread listener;
    private static HashMap<String, Socket> clients;

    static {
        clients = new HashMap<>();
    }

    public EchoSrv(int port) {
        halting = new AtomicBoolean(false);
        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void run() {
        if (listener != null) {
            throw new RuntimeException("error: listener is already started");
        }
        listener = new Thread(()->{listener();});
        listener.start();
    }

    public void hangup() {
        if (socket != null) {
            closesocket();
            if (listener != null) {
                listener.interrupt();
            }
        }
    }

    public synchronized void halt(){
        halting.set(true);
        hangup();
    }

    private synchronized void closesocket() {
        try {
            clients.remove(socket.toString());
            socket.close();
        } catch (Exception e) {
            //ignored
        }
    }

    void listener() {
        try {
            while(!halting.get()) {
                Socket socket_client = socket.accept();
                if (halting.get()) {
                    close(socket_client);
                    break;
                }
                Client client = new Client(this, socket_client);
                synchronized (clients) {
                    clients.put(socket_client.toString(), socket_client);
                }
                client.run();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closesocket();
        }

    }

    private class Client extends Thread {
        EchoSrv server;
        Socket socket_client;
        BufferedReader reader;
        Writer writer;


        public Client(EchoSrv server, Socket socket_client) {
            this.server = server;
            this.socket_client = socket_client;
            try {
                socket_client.setTcpNoDelay(true);
            } catch (SocketException e) {
                //ignored
            }
        }

        public void run() {
            try {
                handle();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                close();
                synchronized (clients) {
                    clients.remove(socket_client.toString());
                }
            }
        }

        public void handle() throws IOException {
            while (!halting.get()) {

            }
        }
    }
}

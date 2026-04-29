package org.dmoreno.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import org.dmoreno.server.util.Addr;
import org.dmoreno.server.Msg;

import java.net.StandardSocketOptions;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class TcpCli {
    SocketChannel sck;
    Req req = new Req();
    AtomicBoolean running = new AtomicBoolean(true);
    int msg_tag = 0;
    public TcpCli(String addr) {
        Addr a = new Addr(addr);
        try {
            sck = SocketChannel.open();
            sck.connect(new InetSocketAddress(a.inet(), a.port));

            sck.setOption(StandardSocketOptions.TCP_NODELAY, true);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void run() {
        Scanner sc = new Scanner(System.in);
        Thread listener = new Thread(this::listener);
        listener.start();
        try {
            System.out.print("> Input commands to send to the server (exit to finish)\n");
            while (running.get()) {
                System.out.print("> ");
                String comm = sc.nextLine();
                String[] args = comm.split(" ");
                switch (args[0]) {
                    case "exit":
                        running.set(false);
                        req.r = new Msg.Texit(msg_tag,null);
                        req.r.writeTo(sck);
                        try {
                            sck.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        return;
                    case "Tnewdib":
                        req.r = new Msg.Tnewdib("Dib 1", msg_tag,null);
                        req.r.writeTo(sck);
                        msg_tag += 1;
                        break;
                    case "Tnewfig":
                        req.r = new Msg.Tnewfig("Circle 10 20 5", msg_tag,null);
                        req.r.writeTo(sck);
                        msg_tag += 1;
                        break;
                    case "Tlistfigs":
                        req.r = new Msg.Tlistfigs("1 0 2", msg_tag,null);
                        req.r.writeTo(sck);
                        msg_tag += 1;
                        break;
                    case "Tdeldib":
                        req.r = new Msg.Tdeldib(0, msg_tag, null);
                        req.r.writeTo(sck);
                        msg_tag += 1;
                        break;
                    case "Tadddib":
                        req.r = new Msg.Tadddib("0 0-2", msg_tag, null);
                        req.r.writeTo(sck);
                        msg_tag += 1;
                        break;
                    default:
                        break;
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (sck != null && sck.isOpen()) {
                    sck.close();
                }
            } catch (IOException e) {
                if (running.get()) {
                    e.printStackTrace();
                }

            }
            sc.close();
        }

    }

    private void listener() {
        try {
            while (running.get()) {
                req.readFrom(sck);
                if (req.m != null) {
                    System.out.println("\nMSG SERVER: " + req.m.toString());
                    System.out.print("> ");
                } else {
                    System.out.println("\nINFO: server has finished the connection");
                    sck.close();
                    running.set(false);
                    return;
                }
            }
        } catch (Exception e) {
            if (running.get()) {
                e.printStackTrace();
            }
        }
    }
}

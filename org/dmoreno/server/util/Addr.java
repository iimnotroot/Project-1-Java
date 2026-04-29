package org.dmoreno.server.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Network addresses of the form
 * <pre>
 * "protocol!address!service", "address!service"
 * "address:service"
 * name@address
 * </pre>
 */
public class Addr {

    protected static Set<String> localAddrs;
    public final String proto;
    public final String host;
    public int port;
    public String name;
    private String addr;

    private Addr(String name, String addr, String proto, String host, int port) {
        this.name =  name;
        this.addr = addr;
        this.proto = proto;
        this.host = host;
        this.port = port;
    }

    public static boolean isproto(String p) {
        if (p != null) {
            switch (p) {
                case "tcp":
                case "tls":
                case "udp":
                case "unix":
                    return true;
            }
        }
        return false;
    }
    /**
     * Create an adress to use later addr.proto, addr.host addr.port, ...
     */
    public Addr(String addr) {
        name = null;
        String[] parts;
        int i = addr.indexOf('@');
        if (i > 0) {
            name = addr.substring(0, i);
            addr = addr.substring(i+1);
        }
        this.addr = addr;
        parts = addr.split("!");
        if (parts.length == 1) {
            parts = addr.split(":");
        }
        switch (parts.length) {
            case 1:
                proto = "tcp";
                host = parts[0];
                port = 0;
                break;
            case 2:
                if (isproto(parts[0])) {
                    proto = parts[0];
                    host = parts[1];
                    port = Integer.parseInt(parts[2]);
                } else {
                    proto = "tcp";
                    host = parts[0];
                    port = Integer.parseInt(parts[1]);
                }
                if (port <= 0) {
                    throw new RuntimeException("bad port in "+addr);
                }
                break;
            case 3:
                proto = parts[0];
                if (!isproto(proto)) {
                    throw new RuntimeException("unknown proto in "+addr);
                }
                host = parts[1];
                port = Integer.parseInt(parts[2]);
                if (port <= 0) {
                    throw new RuntimeException("bad port in "+addr);
                }
                break;
            default:
                throw new RuntimeException("bad address "+addr);
        }
    }

    public Addr copy() {
        Addr dup = new Addr(name, addr, proto, host, port);
        return dup;
    }

    /**
     * Return true if this looks like a network address.
     */
    public static boolean isNetAddr(String addr) {
        String[] parts = addr.split("!");
        if (parts.length == 1) {
            parts = addr.split(":");
        }
        switch (parts.length) {
            case 2:
                return Integer.parseInt(parts[1]) > 0;
            case 3:
                if (!parts[0].equals("tcp")) {
                    return false;
                }
                return Integer.parseInt(parts[2]) > 0;
            default:
                return false;
        }
    }

    protected static void initAddrs() {
        if (localAddrs != null) {
            return;
        }
        localAddrs = new HashSet<String>();
        localAddrs.add("127.0.0.1");  // just in case we fail...
        try {
            Enumeration<NetworkInterface> ifcs = NetworkInterface.getNetworkInterfaces();
            NetworkInterface ifc;
            while ((ifc = ifcs.nextElement()) != null) {
                Enumeration<InetAddress> addrs = ifc.getInetAddresses();
                InetAddress addr;
                while ((addr = addrs.nextElement()) != null) {
                    localAddrs.add(addr.toString());
                }
            }
        } catch (Exception e) {
            localAddrs.add("localhost");
            // and ignore. nothing we could do here.
        }
    }

    public void setPort(int port) {
        this.port = port;
        addr = host + "!" + port;
        if (proto != null && !proto.equals("tcp")) {
            addr = proto + "!" + addr;
        }
        if (name != null) {
            addr = name+"@"+addr;
        }
    }

    public InetAddress inet() {
        try {
            return InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public String toString() {
        return addr;
    }

    /**
     * See if the address is a local address
     */
    public boolean isLocal() {
        initAddrs();
        if (localAddrs.contains(host)) {
            return true;
        }
        try {
            return localAddrs.contains(InetAddress.getByName(host).toString());
        } catch (UnknownHostException e) {
            return false;
        }
    }
}

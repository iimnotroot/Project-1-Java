import org.dmoreno.server.*;

public class srv {
    public static void main (String[] args) {
        DibSvc dib = new DibSvc();
        TcpSrv server = new TcpSrv(dib, 8000);
        server.run();
    }
}
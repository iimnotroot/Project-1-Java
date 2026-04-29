import org.dmoreno.server.*;

public class client {
    public static void main (String[] args) {
        TcpCli cli = new TcpCli("localhost:8000");
        cli.run();
    }
}

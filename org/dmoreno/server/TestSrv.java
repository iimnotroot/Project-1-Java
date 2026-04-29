package org.dmoreno.server;

import org.junit.jupiter.api.Test;

public class TestSrv {
    @Test
    public void TestDib() {
        DibSvc dib = new DibSvc();
        TcpSrv server = new TcpSrv(dib, 8000);
        server.run();


    }

    @Test
    public void TestCli() {
        DibSvc dib = new DibSvc();
        TcpSrv server = new TcpSrv(dib, 8000);
        server.run();
        TcpCli cli = new TcpCli("localhost:8000");
        cli.run();
    }
}

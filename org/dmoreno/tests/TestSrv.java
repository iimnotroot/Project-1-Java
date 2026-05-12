package org.dmoreno.tests;

import org.dmoreno.server.DibSvc;
import org.dmoreno.server.TcpCli;
import org.dmoreno.server.TcpSrv;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

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
        //cli.run()
        server.halt();
    }

}


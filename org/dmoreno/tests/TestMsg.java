package org.dmoreno.tests;

import org.dmoreno.server.Msg;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.channels.Pipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestMsg {

    @Test
    public void msgTest() {
        Msg r = new Msg.Rerror("hello there", null);
        System.out.println(r.toString());
    }

    @Test
    public void testEOF() throws IOException {
        var p = Pipe.open();
        var wrc = p.sink();
        var rdc = p.source();
        var m = new Msg.Rerror("msg1", null);
        m.writeTo(wrc);
        wrc.close();
        int n = 0;
        Msg r;
        while ((r = Msg.readFrom(rdc, null)) != null) {
            System.err.printf("did read %s\n", r);
            n++;
        }
        assertEquals(1, n);
    }

    static void testPack(Msg t) throws IOException {
        var p = Pipe.open();
        var wrc = p.sink();
        var rdc = p.source();
        System.err.printf("writing %s\n", t);
        t.writeTo(wrc);
        t.writeTo(wrc);
        for (int i = 0; i < 2; i++) {
            var r = Msg.readFrom(rdc, null);
            System.err.printf("readed %s\n", r);
            assertNotNull(r);
            assertEquals(t.toString(), r.toString());
        }
    }

    @Test
    public void testRerror() throws IOException {
        testPack(new Msg.Rerror("hi there" ,null));
    }

    @Test
    public void testNewdib() throws IOException {
        testPack(new Msg.Tnewdib("Dib1", 5,null));
    }

    @Test
    public void testNewfig() throws IOException {
        testPack(new Msg.Tnewfig("Circle 10 20 5", 0,null));
    }

    @Test
    public void TestdelGrp() throws IOException {
        testPack(new Msg.Tdelgrp(0, 0, null));
    }



}
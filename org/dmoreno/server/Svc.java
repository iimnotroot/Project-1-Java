package org.dmoreno.server;

import java.nio.ByteBuffer;

/**
 * Service for a TCP Server
 */
public interface Svc {
    void init();
    void term();
    void newClient(String name);
    void closeClient(String name);
    Msg handle(Msg req, ByteBuffer buf);
}

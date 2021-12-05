package com.company;

import java.io.IOException;

public class Demultiplexer implements AutoCloseable {

    private TaggedConnection tc;

    public Demultiplexer(TaggedConnection conn) {
        this.tc= conn;
    }

    public void start() {

    }

    public void send(TaggedConnection.Frame frame) throws IOException {
        this.tc.send(frame);
    }

    public void send(int tag, byte[] data) throws IOException {
        tc.send(tag, data);
    }
    public byte[] receive(int tag) throws IOException, InterruptedException {

    }

    public void close() throws IOException { ... }
}

package com.company;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TaggedConnection implements AutoCloseable {
    public static class Frame {
        public final int tag;
        public final byte[] data;
        public Frame(int tag, byte[] data) { this.tag = tag; this.data = data; }
    }

    private Lock lockSend= new ReentrantLock();
    private Lock lockReceive= new ReentrantLock();
    private DataInputStream in;
    private DataOutputStream out;

    public TaggedConnection(Socket socket) throws IOException {
        this.in= new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.out= new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    public void send(Frame frame) throws IOException {
        try {
            this.lockSend.lock();
            this.out.writeInt(frame.tag);
            this.out.writeInt(frame.data.length);
            this.out.write(frame.data);
            this.out.flush();
        }
        finally {
            this.lockSend.unlock();
        }
    }
    public void send(int tag, byte[] data) throws IOException {
        this.send(new Frame(tag, data));
    }
    public Frame receive() throws IOException {
        try {
            this.lockReceive.lock();
            int tag = this.in.readInt();
            int size = this.in.readInt();
            byte[] info = new byte[size];
            this.in.readFully(info);
            return new Frame(tag, info);
        } finally {
            this.lockReceive.unlock();
        }
    }

    public void close() throws IOException {
        try {
            this.lockSend.lock();
            this.lockReceive.lock();
            this.out.close();
            this.in.close();
        }
        finally {
            this.lockSend.unlock();
            this.lockReceive.unlock();
        }
    }
}

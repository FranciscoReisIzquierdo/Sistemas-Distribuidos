package com.company;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FramedConnection implements AutoCloseable {
    private Lock lockSend= new ReentrantLock();
    private Lock lockReceive= new ReentrantLock();

    private DataInputStream in;
    private DataOutputStream out;


    public FramedConnection(Socket socket) throws IOException {
        this.in= new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.out= new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }


    public void send(byte[] data) throws IOException {
        try{
            this.lockSend.lock();
            this.out.writeInt(data.length);
            this.out.write(data);
            this.out.flush();
        }
        finally {
            this.lockSend.unlock();
        }
    }
    public byte[] receive() throws IOException {
        try{
            this.lockReceive.lock();
            int size= this.in.readInt();
            byte[] info= new byte[size];
            this.in.readFully(info);
            return info;
        }
        finally {
            this.lockReceive.unlock();
        }
    }
    public void close() throws IOException {
        this.in.close();
        this.out.close();
    }
}

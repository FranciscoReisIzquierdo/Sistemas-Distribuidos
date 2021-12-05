package com.company;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class Barrier {
    private int N;
    private ReentrantLock lock;
    private int counter;
    private final Condition waitForLastThread= lock.newCondition();

    public Barrier (int N) {
        this.N= N;
        this.lock= new ReentrantLock();
        this.waitForLastThread= this.lock.newCondition();
    }


    void await() throws InterruptedException {
        lock.lock();
        this.counter++;


        if(counter< N) {
            while (this.counter < N) {
                System.out.println("")
                this.waitForLastThread.await();
            }
        }
        else {

            this.waitForLastThread.signalAll();
        }


        lock.unlock();

    }
}


public class Main {

    public static void main(String[] args) {
        int N= 10;

        Thread[] t= new Thread[N];
        Barrier b= new Barrier(N);

    }
}

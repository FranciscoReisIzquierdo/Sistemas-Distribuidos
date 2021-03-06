package com.company;

class Increment implements Runnable{
    public void run(){
        final long I=100;

        for (long i = 0; i < I; i++)
            System.out.println(i);
    }
}

public class Main {
    public static void main(String[] args) throws InterruptedException{
        final int N= 10;
        Thread threads[]= new Thread[N];

        for(int i= 0; i< N; i++) threads[i]= new Thread(new Increment()); //Cria N threads e "guarda-as" no array 'threads'

        for(int i= 0; i< N; i++) threads[i].start(); //Invoca cada uma das N threads

        for(int i= 0; i< N; i++) threads[i].join(); //Main thread aguarda por cada uma das N threads executarem

        System.out.println("Fim!\n");
    }
}

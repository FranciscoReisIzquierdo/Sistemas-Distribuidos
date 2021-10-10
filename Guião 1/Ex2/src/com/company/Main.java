package com.company;

class Bank{
    private static class Account{
        //Variáveis de instância
        private int balance;

        //Construtor parameterizado
        Account(int balance){
            this.balance = balance;
        }
        //Método que devolve o saldo da conta
        int balance(){
            return balance;
        }
        //Método que deposita um dado montante na conta
        boolean deposit(int value) {
            balance += value;
            return true;
        }
    }
    //Our single account, for now
    private Account savings = new Account(0);

    // Account balance
    public int balance() {
        return savings.balance();
    }

    // Deposit
    boolean deposit(int value) {
        return savings.deposit(value);
    }
}

class Deposit implements Runnable{
    //Variáveis de instância
    private Bank banco;
    private final int v= 100;

    //Construtor parameterizado
    public Deposit(Bank b){
        this.banco= b;
    }

    public void run(){
        final int I= 1000;
        for(int i= 0; i< I; i++) this.banco.deposit(v);
    }
}

public class Main{
    public static void main(String[] args) throws InterruptedException{
        final int N= 10;
        Bank banco= new Bank();
        Thread threads[]= new Thread[N];

        for(int i= 0; i< N; i++) threads[i]= new Thread(new Deposit(banco)); //Cria N threads e "guarda-as" no array 'threads'

        for(int i= 0; i< N; i++) threads[i].start(); //Invoca cada uma das N threads

        for (int i= 0; i< N; i++) threads[i].join(); //Main thread aguarda por cada uma das N threads executarem

        System.out.println("Saldo da conta: "+ banco.balance());
    }
}

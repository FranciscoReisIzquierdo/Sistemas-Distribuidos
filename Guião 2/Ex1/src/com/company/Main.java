package com.company;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

class Bank {

    private static class Account {
        private int balance;
        private ReentrantLock lock;
        
        Account(int balance) { this.balance = balance; }
        int balance() { return balance; }
        boolean deposit(int value) {
            balance += value;
            return true;
        }
        boolean withdraw(int value) {
            if (value > balance)
                return false;
            balance -= value;
            return true;
        }
    }

    private Map<Integer, Account> map = new HashMap<Integer, Account>();
    private int nextId = 0;
    private ReentrantLock lock;

    // create account and return account id
    public int createAccount(int balance) {
        //////////////////////////////////
        Account c = new Account(balance);
        int id = nextId;
        nextId += 1;
        map.put(id, c);
        return id;
    }

    // close account and return balance, or 0 if no such account
    public int closeAccount(int id) {
        this.lock.lock();

        Account c = map.remove(id);
        if (c == null) {
            this.lock.unlock();
            return 0;
        }

        c.lock.lock();
        this.lock.unlock();
        int balance= c.balance();
        c.lock.unlock();
        return balance;
    }

    // account balance; 0 if no such account
    public int balance(int id) {
        this.lock.lock();
        Account c = map.get(id);
        if (c == null) {
            this.lock.unlock();
            return 0;
        }

        c.lock.lock();
        this.lock.unlock();
        int bal= c.balance();
        c.lock.unlock();
        return bal;
    }

    // deposit; fails if no such account
    public boolean deposit(int id, int value) {
        this.lock.lock();
        Account c = map.get(id);
        if (c == null){
            this.lock.unlock();
            return false;
        }

        c.lock.lock();
        this.lock.unlock();
        boolean result= c.deposit(value);
        c.lock.unlock();
        return result;
    }

    // withdraw; fails if no such account or insufficient balance
    public boolean withdraw(int id, int value) {
        this.lock.lock();
        Account c = map.get(id);
        if (c == null)
            return false;
        c.lock.lock();
        this.lock.unlock();
        boolean result= c.withdraw(value);
        this.lock.unlock();
        return result;
    }

    // transfer value between accounts;
    // fails if either account does not exist or insufficient balance
    public boolean transfer(int from, int to, int value) {
        this.lock.lock();
        Account cfrom, cto;
        cfrom = map.get(from);
        cto = map.get(to);

        cfrom.lock.lock();
        cto.lock.lock();

        if (cfrom == null || cto ==  null) {
            this.lock.unlock();
            return false;
        }
        try {
            return cfrom.withdraw(value) && cto.deposit(value);
        }
        finally {
            cfrom.lock.unlock();
            cto.lock.unlock();
        }
    }

    // sum of balances in set of accounts; 0 if some does not exist
    public int totalBalance(int[] ids){
        Account[] lockedAccounts= new Account[ids.length];
        this.lock.lock();
        int total = 0, nextLockedAccount= 0;
        for (int i= 0; i< ids.length; i++) {
            Account c = map.get(i);
            if (c == null) {
                this.lock.unlock();
                return 0;
            }
            lockedAccounts[nextLockedAccount++]= c;
        }
        this.lock.unlock();

        for(Account c: lockedAccounts){
            total+= c.balance();
            c.lock.unlock();
        }
        return total;
    }

}

public class Main {

    public static void main(String[] args) {
	// write your code here
    }
}

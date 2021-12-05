package com.company;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


class Warehouse {
    private Map<String, Product> map =  new HashMap<String, Product>();
    Lock lock= new ReentrantLock();


    private class Product {
        Condition c=  lock.newCondition();
        int quantity = 0;
    }

    private Product get(String item) {
        Product p = map.get(item);
        if (p != null) return p;
        p = new Product();
        map.put(item, p);
        return p;
    }

    public void supply(String item, int quantity) {
        try {
            lock.lock();
            Product product = get(item);
            product.quantity += quantity;
            //Must signal the others threads
            product.c.signalAll();
        }
        finally{ lock.unlock(); }
    }

    // Errado se faltar algum produto...
    public void consume(Set<String> items) {
        lock.lock();
        try {
            for (String s : items){
                Product product= map.get(s);
                //If quantity is 0, must wait!
                while(product.quantity== 0) product.c.await();
                get(s).quantity--;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally{ lock.unlock(); }
    }
}

public class Main {

    public static void main(String[] args) {
	// write your code here
    }
}
